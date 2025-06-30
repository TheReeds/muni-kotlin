package com.capachica.turismokotlin.network.api

import com.capachica.turismokotlin.data.model.AuthResponse
import com.capachica.turismokotlin.data.model.LoginRequest
import com.capachica.turismokotlin.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.*

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("auth/init")
    suspend fun initAuth(): Response<String>
}