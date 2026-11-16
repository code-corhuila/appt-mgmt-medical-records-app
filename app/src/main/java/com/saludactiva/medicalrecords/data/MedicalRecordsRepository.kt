package com.saludactiva.medicalrecords.data

import retrofit2.Response

class MedicalRecordsRepository(private val api: ApiService) {
    suspend fun login(email: String, password: String): String {
        val response = api.login(LoginRequest(email, password))
        return response.requireBody("No fue posible iniciar sesión. Revisa tus credenciales.").token
    }

    suspend fun getStudies(): List<MedicalStudy> =
        api.getStudies().requireBody("No fue posible cargar tus estudios.")

    suspend fun createStudy(request: CreateStudyRequest): MedicalStudy =
        api.createStudy(request).requireBody("No fue posible guardar el estudio.")

    suspend fun deleteStudy(id: String) {
        val response = api.deleteStudy(id)
        if (!response.isSuccessful) {
            throw ApiException(response.code(), "No fue posible eliminar el estudio.")
        }
    }

    private fun <T> Response<T>.requireBody(errorMessage: String): T {
        if (!isSuccessful) throw ApiException(code(), errorMessage)
        return body() ?: throw ApiException(code(), "El servidor devolvió una respuesta vacía.")
    }
}

class ApiException(val statusCode: Int, message: String) : Exception(message)
