package com.capachica.turismokotlin.data.model

data class CreateReservaPlanRequest(
    val planId: Long,
    val fechaInicio: String, // Format: "YYYY-MM-DD"
    val numeroPersonas: Int,
    val observaciones: String? = null,
    val solicitudesEspeciales: String? = null,
    val contactoEmergencia: String? = null,
    val telefonoEmergencia: String? = null,
    val metodoPago: Any? = MetodoPago.EFECTIVO,
    val serviciosPersonalizados: List<ServicioPersonalizadoRequest> = emptyList()
)

data class ServicioPersonalizadoRequest(
    val servicioPlanId: Long,
    val incluido: Boolean = true,
    val precioPersonalizado: Double? = null,
    val observaciones: String? = null,
    val estado: EstadoServicioPersonalizado = EstadoServicioPersonalizado.INCLUIDO
)

enum class EstadoServicioPersonalizado {
    INCLUIDO, EXCLUIDO, MODIFICADO
}