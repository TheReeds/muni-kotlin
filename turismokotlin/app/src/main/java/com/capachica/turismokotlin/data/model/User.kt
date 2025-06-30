package com.capachica.turismokotlin.data.model

data class Usuario(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val username: String,
    val email: String
)