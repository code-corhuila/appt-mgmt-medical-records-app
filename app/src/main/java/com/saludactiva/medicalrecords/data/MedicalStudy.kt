package com.saludactiva.medicalrecords.data

import com.google.gson.annotations.SerializedName

data class MedicalStudy(
    @SerializedName(value = "id", alternate = ["_id"])
    val id: String,
    val titulo: String,
    val fecha: String,
    val tipo: String,
    val resultadoBreve: String,
    val urlDocumento: String? = null,
    val notas: String? = null
)

data class CreateStudyRequest(
    val titulo: String,
    val fecha: String,
    val tipo: String,
    val resultadoBreve: String,
    val urlDocumento: String?
)

data class LoginRequest(
    val email: String,
    @SerializedName("contrasena")
    val password: String
)

data class LoginResponse(val token: String)
