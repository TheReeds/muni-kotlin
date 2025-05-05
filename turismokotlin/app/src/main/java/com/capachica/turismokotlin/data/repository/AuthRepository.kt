package com.capachica.turismokotlin.data.repository

import com.capachica.turismokotlin.data.api.ApiService
import com.capachica.turismokotlin.data.local.SessionManager
import com.capachica.turismokotlin.data.model.AuthResponse
import com.capachica.turismokotlin.data.model.LoginRequest
import com.capachica.turismokotlin.data.model.RegisterRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

class AuthRepository(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {
    suspend fun login(username: String, password: String): Flow<Result<AuthResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.login(LoginRequest(username, password))
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    // Guardar datos de sesión
                    sessionManager.saveAuthToken(authResponse.token)
                    sessionManager.saveUserId(authResponse.id)
                    sessionManager.saveUsername(authResponse.username)
                    sessionManager.saveUserRoles(authResponse.roles)

                    emit(Result.Success(authResponse))
                } ?: emit(Result.Error("Respuesta vacía del servidor"))
            } else {
                when (response.code()) {
                    401 -> emit(Result.Error("Credenciales incorrectas"))
                    404 -> emit(Result.Error("Usuario no encontrado"))
                    else -> emit(Result.Error("Error de autenticación: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            when (e) {
                is ConnectException, is UnknownHostException ->
                    emit(Result.Error("No se pudo conectar al servidor. Verifica tu conexión a internet."))
                is SocketTimeoutException ->
                    emit(Result.Error("La conexión al servidor ha excedido el tiempo de espera."))
                else ->
                    emit(Result.Error("Error en la solicitud: ${e.message ?: "Error desconocido"}"))
            }
        }
    }

    suspend fun register(request: RegisterRequest): Flow<Result<AuthResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.register(request)
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    // Guardar datos de sesión
                    sessionManager.saveAuthToken(authResponse.token)
                    sessionManager.saveUserId(authResponse.id)
                    sessionManager.saveUsername(authResponse.username)
                    sessionManager.saveUserRoles(authResponse.roles)

                    emit(Result.Success(authResponse))
                } ?: emit(Result.Error("Respuesta vacía del servidor"))
            } else {
                when (response.code()) {
                    400 -> emit(Result.Error("Datos de registro inválidos"))
                    409 -> emit(Result.Error("El nombre de usuario o email ya está en uso"))
                    else -> emit(Result.Error("Error en el registro: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            when (e) {
                is ConnectException, is UnknownHostException ->
                    emit(Result.Error("No se pudo conectar al servidor. Verifica tu conexión a internet."))
                is SocketTimeoutException ->
                    emit(Result.Error("La conexión al servidor ha excedido el tiempo de espera."))
                else ->
                    emit(Result.Error("Error en la solicitud: ${e.message ?: "Error desconocido"}"))
            }
        }
    }

    suspend fun logout() {
        sessionManager.clearSession()
    }

    suspend fun isUserLoggedIn(): Flow<Boolean> = flow {
        // Si hay un token almacenado, consideramos que el usuario está logueado
        try {
            val token = sessionManager.getAuthToken()?.first()
            emit(token != null)
        } catch (e: Exception) {
            emit(false)
        }
    }
}