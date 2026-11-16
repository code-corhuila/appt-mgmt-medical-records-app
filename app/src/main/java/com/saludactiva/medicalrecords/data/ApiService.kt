package com.saludactiva.medicalrecords.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/estudios")
    suspend fun getStudies(): Response<List<MedicalStudy>>

    @POST("api/estudios")
    suspend fun createStudy(@Body request: CreateStudyRequest): Response<MedicalStudy>

    @DELETE("api/estudios/{id}")
    suspend fun deleteStudy(@Path("id") id: String): Response<Unit>
}
