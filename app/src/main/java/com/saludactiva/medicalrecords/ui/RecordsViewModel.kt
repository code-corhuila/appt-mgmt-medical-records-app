package com.saludactiva.medicalrecords.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.saludactiva.medicalrecords.data.ApiException
import com.saludactiva.medicalrecords.data.CreateStudyRequest
import com.saludactiva.medicalrecords.data.MedicalRecordsRepository
import com.saludactiva.medicalrecords.data.MedicalStudy
import com.saludactiva.medicalrecords.data.NetworkModule
import com.saludactiva.medicalrecords.data.SessionManager
import com.saludactiva.medicalrecords.data.StudyFilter
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Locale

class RecordsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MedicalRecordsRepository(NetworkModule.createApi(application))
    private val sessionManager = SessionManager(application)
    private var allStudies: List<MedicalStudy> = emptyList()
    private var searchQuery = ""

    private val _studies = MutableLiveData<List<MedicalStudy>>(emptyList())
    val studies: LiveData<List<MedicalStudy>> = _studies
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading
    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message
    private val _sessionExpired = MutableLiveData(false)
    val sessionExpired: LiveData<Boolean> = _sessionExpired

    fun loadStudies() {
        _loading.value = true
        viewModelScope.launch {
            try {
                allStudies = repository.getStudies()
                publishFilteredStudies()
            } catch (error: Exception) {
                handleError(error)
            } finally {
                _loading.value = false
            }
        }
    }

    fun search(query: String) {
        searchQuery = query
        publishFilteredStudies()
    }

    fun createStudy(title: String, type: String, date: String, result: String, documentUrl: String) {
        if (listOf(title, type, date, result).any { it.isBlank() }) {
            _message.value = "Completa nombre, tipo, fecha y resultado."
            return
        }
        if (!date.isValidIsoDate()) {
            _message.value = "La fecha debe ser válida y tener el formato AAAA-MM-DD."
            return
        }
        val document = documentUrl.trim().ifEmpty { null }
        if (document != null && !document.isValidWebUrl()) {
            _message.value = "El enlace debe comenzar con http:// o https://."
            return
        }

        viewModelScope.launch {
            _loading.value = true
            try {
                repository.createStudy(
                    CreateStudyRequest(
                        titulo = title.trim(),
                        fecha = date.trim(),
                        tipo = type.trim(),
                        resultadoBreve = result.trim(),
                        urlDocumento = document
                    )
                )
                loadStudies()
            } catch (error: Exception) {
                handleError(error)
                _loading.value = false
            }
        }
    }

    fun deleteStudy(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteStudy(id)
                allStudies = allStudies.filterNot { it.id == id }
                publishFilteredStudies()
            } catch (error: Exception) {
                handleError(error)
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }

    fun signOut() {
        sessionManager.clear()
        _sessionExpired.value = true
    }

    private fun publishFilteredStudies() {
        _studies.value = StudyFilter.filter(allStudies, searchQuery)
    }

    private fun handleError(error: Exception) {
        if (error is ApiException && error.statusCode == 401) {
            signOut()
            return
        }
        _message.value = when (error) {
            is IOException -> "No se pudo conectar con el servidor. Intenta de nuevo."
            else -> error.message ?: "No se pudo completar la solicitud. Intenta de nuevo."
        }
    }
}

private fun String.isValidIsoDate(): Boolean {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }
    val position = ParsePosition(0)
    val parsedDate = format.parse(this, position)
    return parsedDate != null && position.index == length
}

private fun String.isValidWebUrl(): Boolean =
    startsWith("https://", ignoreCase = true) || startsWith("http://", ignoreCase = true)
