package com.capachica.turismokotlin.data.model

data class Municipalidad(
    val id: Long,
    val nombre: String,
    val departamento: String? = null,
    val provincia: String? = null,
    val distrito: String
)

data class Categoria(
    val id: Long,
    val nombre: String,
    val descripcion: String? = null,
    val cantidadEmprendedores: Int = 0
)