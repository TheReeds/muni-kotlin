package com.capachica.turismokotlin.data.model

enum class EstadoPlan {
    BORRADOR, ACTIVO, INACTIVO, AGOTADO, SUSPENDIDO
}

enum class NivelDificultad {
    FACIL, MODERADO, DIFICIL, EXTREMO
}

data class ServicioPlan(
    val id: Long,
    val diaDelPlan: Int,
    val ordenEnElDia: Int,
    val horaInicio: String,
    val horaFin: String,
    val precioEspecial: Double,
    val notas: String? = null,
    val esOpcional: Boolean = false,
    val esPersonalizable: Boolean = false,
    val servicio: Servicio
)

data class Plan(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val precioTotal: Double,
    val duracionDias: Int,
    val capacidadMaxima: Int,
    val estado: EstadoPlan,
    val nivelDificultad: NivelDificultad,
    val imagenPrincipalUrl: String? = null,
    val itinerario: String? = null,
    val incluye: String? = null,
    val noIncluye: String? = null,
    val recomendaciones: String? = null,
    val requisitos: String? = null,
    val fechaCreacion: String,
    val fechaActualizacion: String,
    val municipalidad: Municipalidad,
    val usuarioCreador: Usuario,
    val servicios: List<ServicioPlan>,
    val totalReservas: Int = 0
)