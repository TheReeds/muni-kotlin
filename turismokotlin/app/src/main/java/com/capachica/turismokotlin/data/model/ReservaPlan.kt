package com.capachica.turismokotlin.data.model

data class ReservaPlan(
    val id: Long,
    val codigoReserva: String,
    val fechaInicio: String,
    val fechaFin: String,
    val numeroPersonas: Int,
    val montoTotal: Double,
    val montoDescuento: Double,
    val montoFinal: Double,
    val estado: EstadoReserva,
    val metodoPago: MetodoPago,
    val observaciones: String? = null,
    val solicitudesEspeciales: String? = null,
    val contactoEmergencia: String? = null,
    val telefonoEmergencia: String? = null,
    val fechaReserva: String,
    val fechaConfirmacion: String? = null,
    val fechaCancelacion: String? = null,
    val motivoCancelacion: String? = null,
    val plan: PlanBasico,
    val usuario: UsuarioBasico,
    val serviciosPersonalizados: List<ServicioPersonalizado> = emptyList(),
    val pagos: List<PagoPlan> = emptyList()
)
data class ServicioPersonalizado(
    val id: Long,
    val incluido: Boolean,
    val precioPersonalizado: Double? = null,
    val observaciones: String? = null,
    val estado: EstadoServicioPersonalizado,
    val servicioPlan: ServicioPlanDetalle
)
data class ServicioPlanDetalle(
    val id: Long,
    val diaDelPlan: Int,
    val ordenEnElDia: Int,
    val horaInicio: String,
    val horaFin: String,
    val precioEspecial: Double,
    val notas: String? = null,
    val esOpcional: Boolean = false,
    val esPersonalizable: Boolean = false,
    val servicio: ServicioDetalle
)

data class ServicioDetalle(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val duracionHoras: Int,
    val capacidadMaxima: Int,
    val tipo: TipoServicio,
    val estado: EstadoServicio,
    val ubicacion: String,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val requisitos: String? = null,
    val incluye: String? = null,
    val noIncluye: String? = null,
    val imagenUrl: String? = null,
    val emprendedor: EmprendedorBasico? = null
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