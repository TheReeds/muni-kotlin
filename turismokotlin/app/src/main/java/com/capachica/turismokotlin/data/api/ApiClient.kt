package com.capachica.turismokotlin.data.api

import android.util.Log
import com.capachica.turismokotlin.BuildConfig
import com.capachica.turismokotlin.data.local.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = BuildConfig.API_BASE_URL
    private const val TAG = "ApiClient"

    fun getApiService(sessionManager: SessionManager): ApiService {
        return buildRetrofit(sessionManager).create(ApiService::class.java)
    }

    private fun buildRetrofit(sessionManager: SessionManager): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getOkHttpClient(sessionManager))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getOkHttpClient(sessionManager: SessionManager): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(getAuthInterceptor(sessionManager))
            .addInterceptor(getLoggingInterceptor())
            .build()
    }

    private fun getAuthInterceptor(sessionManager: SessionManager): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()

            // Agregar token de autenticación si está disponible
            val token = runBlocking {
                try {
                    sessionManager.getAuthToken().first()
                } catch (e: Exception) {
                    Log.e(TAG, "Error obteniendo token: ${e.message}", e)
                    null
                }
            }

            val request = if (!token.isNullOrEmpty()) {
                Log.d(TAG, "Agregando token de autenticación a la solicitud")
                originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            } else {
                Log.d(TAG, "No hay token disponible, enviando solicitud sin autenticación")
                originalRequest
            }

            chain.proceed(request)
        }
    }

    private fun getLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
}