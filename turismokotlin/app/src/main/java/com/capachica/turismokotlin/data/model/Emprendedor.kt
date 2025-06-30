package com.capachica.turismokotlin.data.model

data class Emprendedor(
    val id: Long,
    val nombreEmpresa: String,
    val rubro: String,
    val direccion: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val direccionCompleta: String? = null,
    val telefono: String,
    val email: String,
    val sitioWeb: String? = null,
    val descripcion: String? = null,
    val productos: String? = null,
    val servicios: String? = null,
    val usuarioId: Long,
    val municipalidad: Municipalidad,
    val categoria: Categoria
)