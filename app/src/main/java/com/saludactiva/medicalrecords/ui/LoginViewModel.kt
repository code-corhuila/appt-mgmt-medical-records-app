package com.saludactiva.medicalrecords.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.saludactiva.medicalrecords.data.MedicalRecordsRepository
import com.saludactiva.medicalrecords.data.NetworkModule
import com.saludactiva.medicalrecords.data.SessionManager
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MedicalRecordsRepository(NetworkModule.createApi(application))
    private val sessionManager = SessionManager(application)
    private val _state = MutableLiveData<LoginState>(LoginState.Idle)
    val state: LiveData<LoginState> = _state

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = LoginState.Error("Ingresa tu correo y contraseña.")
            return
        }
        _state.value = LoginState.Loading
        viewModelScope.launch {
            try {
                val token = repository.login(email.trim(), password)
                sessionManager.saveToken(token)
                _state.value = LoginState.Authenticated
            } catch (error: Exception) {
                _state.value = LoginState.Error(error.messageForUser())
            }
        }
    }
}

sealed interface LoginState {
    data object Idle : LoginState
    data object Loading : LoginState
    data object Authenticated : LoginState
    data class Error(val message: String) : LoginState
}

internal fun Exception.messageForUser(): String = when (this) {
    is IOException, is HttpException -> "No se pudo conectar con el servidor. Intenta de nuevo."
    else -> message ?: "No se pudo completar la solicitud. Intenta de nuevo."
}
