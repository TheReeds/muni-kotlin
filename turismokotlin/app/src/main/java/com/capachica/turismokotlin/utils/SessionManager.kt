package com.capachica.turismokotlin.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.capachica.turismokotlin.data.model.AuthResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class SessionManager(private val context: Context) {

    private object PreferencesKeys {
        val TOKEN = stringPreferencesKey("auth_token")
        val USER_ID = longPreferencesKey("user_id")
        val USERNAME = stringPreferencesKey("username")
        val EMAIL = stringPreferencesKey("email")
        val ROLES = stringPreferencesKey("roles")
    }

    // Guardar información de sesión
    suspend fun saveAuthInfo(authResponse: AuthResponse) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TOKEN] = authResponse.token
            preferences[PreferencesKeys.USER_ID] = authResponse.id
            preferences[PreferencesKeys.USERNAME] = authResponse.username
            preferences[PreferencesKeys.EMAIL] = authResponse.email
            preferences[PreferencesKeys.ROLES] = authResponse.roles.joinToString(",")
        }
    }

    // Borrar información de sesión
    suspend fun clearAuthInfo() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.TOKEN)
            preferences.remove(PreferencesKeys.USER_ID)
            preferences.remove(PreferencesKeys.USERNAME)
            preferences.remove(PreferencesKeys.EMAIL)
            preferences.remove(PreferencesKeys.ROLES)
        }
    }

    // Obtener token de autenticación
    fun getAuthToken(): String? {
        val tokenFlow: Flow<String?> = context.dataStore.data
            .map { preferences ->
                preferences[PreferencesKeys.TOKEN]
            }

        var token: String? = null
        tokenFlow.map {
            token = it
        }

        return token
    }

    // Obtener ID del usuario
    fun getUserId(): Flow<Long?> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.USER_ID]
        }

    // Obtener nombre de usuario
    fun getUsername(): Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.USERNAME]
        }

    // Obtener email
    fun getEmail(): Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.EMAIL]
        }

    // Obtener roles
    fun getRoles(): Flow<List<String>> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.ROLES]?.split(",") ?: emptyList()
        }

    // Verificar si hay sesión activa
    fun isLoggedIn(): Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.TOKEN] != null
        }

    // Verificar si el usuario tiene un rol específico
    fun hasRole(role: String): Flow<Boolean> = getRoles()
        .map { roles ->
            roles.contains(role)
        }
}