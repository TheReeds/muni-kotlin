package com.capachica.turismokotlin.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import com.capachica.turismokotlin.data.model.AuthResponse
import com.capachica.turismokotlin.data.model.LoginRequest
import com.capachica.turismokotlin.data.model.RegisterRequest
import com.capachica.turismokotlin.network.api.AuthApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val dataStore: DataStore<Preferences>
) {
    private val TOKEN_KEY = stringPreferencesKey("auth_token")
    private val USERNAME_KEY = stringPreferencesKey("username")
    private val ROLES_KEY = stringPreferencesKey("user_roles")

    val authToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    val userRoles: Flow<List<String>> = dataStore.data.map { preferences ->
        val rolesString = preferences[ROLES_KEY] ?: ""
        if (rolesString.isEmpty()) emptyList() else rolesString.split(",")
    }

    suspend fun login(username: String, password: String): Result<AuthResponse> {
        return try {
            val response = authApiService.login(LoginRequest(username, password))
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    saveAuthData(authResponse)
                    Result.success(authResponse)
                } ?: Result.failure(Exception("No auth data received"))
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(
        nombre: String,
        apellido: String,
        username: String,
        email: String,
        password: String,
        roles: List<String> = listOf("ROLE_USER")
    ): Result<AuthResponse> {
        return try {
            val request = RegisterRequest(nombre, apellido, username, email, password, roles)
            val response = authApiService.register(request)
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    saveAuthData(authResponse)
                    Result.success(authResponse)
                } ?: Result.failure(Exception("No auth data received"))
            } else {
                Result.failure(Exception("Registration failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun saveAuthData(authResponse: AuthResponse) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = authResponse.token
            preferences[USERNAME_KEY] = authResponse.username
            preferences[ROLES_KEY] = authResponse.roles.joinToString(",")
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    fun hasRole(role: String): Flow<Boolean> = userRoles.map { roles ->
        roles.contains(role)
    }

    fun isAdmin(): Flow<Boolean> = hasRole("ROLE_ADMIN")
    fun isEmprendedor(): Flow<Boolean> = hasRole("ROLE_EMPRENDEDOR")
    fun isMunicipalidad(): Flow<Boolean> = hasRole("ROLE_MUNICIPALIDAD")
}