package com.capachica.turismokotlin.data.model

data class CreateReservaPlanRequest(
    val planId: Long,
    val cantidad: Int,
    val fechaInicio: String,
    val observaciones: String? = null,
    val contactoEmergencia: String? = null,
    val telefonoEmergencia: String? = null,
    val metodoPago: MetodoPago = MetodoPago.EFECTIVO
)