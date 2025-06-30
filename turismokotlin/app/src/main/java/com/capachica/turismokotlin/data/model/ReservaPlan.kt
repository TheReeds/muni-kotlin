package com.capachica.turismokotlin.data.model

data class ReservaPlan(
    val id: Long,
    val codigoReserva: String,
    val planId: Long,
    val planNombre: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val montoTotal: Double,
    val montoDescuento: Double,
    val montoFinal: Double,
    val estado: EstadoServicio,
    val metodoPago: MetodoPago,
    val fechaInicio: String,
    val fechaFin: String,
    val observaciones: String? = null,
    val contactoEmergencia: String? = null,
    val telefonoEmergencia: String? = null,
    val fechaReserva: String,
    val fechaConfirmacion: String? = null,
    val fechaCancelacion: String? = null,
    val motivoCancelacion: String? = null,
    val usuario: UsuarioBasico,
    val plan: PlanBasico,
    val pagos: List<PagoPlan> = emptyList()
)

data class PlanBasico(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val duracionDias: Int,
    val imagenPrincipalUrl: String? = null,
    val municipalidad: MunicipalidadBasica
)

data class PagoPlan(
    val id: Long,
    val codigoPago: String,
    val monto: Double,
    val tipo: TipoPago,
    val estado: EstadoPago,
    val metodoPago: MetodoPago,
    val numeroTransaccion: String? = null,
    val numeroAutorizacion: String? = null,
    val observaciones: String? = null,
    val fechaPago: String,
    val fechaConfirmacion: String? = null
)