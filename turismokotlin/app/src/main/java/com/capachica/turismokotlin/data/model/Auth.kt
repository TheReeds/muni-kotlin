package com.capachica.turismokotlin.data.model

data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val nombre: String,
    val apellido: String,
    val username: String,
    val email: String,
    val password: String,
    val roles: List<String>
)

data class AuthResponse(
    val token: String,
    val tokenType: String,
    val id: Long,
    val username: String,
    val email: String,
    val roles: List<String>
)