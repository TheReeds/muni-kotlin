package com.capachica.turismokotlin.data.model

enum class EstadoServicio {
    ACTIVO, INACTIVO, MANTENIMIENTO
}

data class Servicio(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val duracionHoras: Int,
    val capacidadMaxima: Int,
    val tipo: TipoServicio?,
    val estado: EstadoServicio,
    val ubicacion: String,
    val latitud: Double,
    val longitud: Double,
    val requisitos: String? = null,
    val incluye: String? = null,
    val noIncluye: String? = null,
    val imagenUrl: String? = null,
    val emprendedor: Emprendedor
)