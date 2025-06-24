package com.capachica.turismokotlin.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Usuario(
    val id: Long,
    val username: String,
    val email: String,
    val roles: List<String>
) : Parcelable

@Parcelize
data class AuthResponse(
    val token: String,
    val tokenType: String,
    val id: Long,
    val username: String,
    val email: String,
    val roles: List<String>
) : Parcelable

@Parcelize
data class LoginRequest(
    val username: String,
    val password: String
) : Parcelable

@Parcelize
data class RegisterRequest(
    val nombre: String,
    val apellido: String,
    val username: String,
    val email: String,
    val password: String,
    val roles: List<String>? = null
) : Parcelable

@Parcelize
data class EmprendedorBasic(
    val id: Long,
    val nombreEmpresa: String,
    val rubro: String
) : Parcelable

@Parcelize
data class EmprendedorWithMunicipalidad(
    val id: Long,
    val nombreEmpresa: String,
    val rubro: String,
    val municipalidad: MunicipalidadBasic
) : Parcelable

@Parcelize
data class MunicipalidadBasic(
    val id: Long,
    val nombre: String,
    val distrito: String
) : Parcelable

@Parcelize
data class Municipalidad(
    val id: Long,
    val nombre: String,
    val departamento: String,
    val provincia: String,
    val distrito: String,
    val direccion: String?,
    val telefono: String?,
    val sitioWeb: String?,
    val descripcion: String?,
    val usuarioId: Long,
    val emprendedores: List<EmprendedorBasic> = emptyList()
) : Parcelable

@Parcelize
data class MunicipalidadRequest(
    val nombre: String,
    val departamento: String,
    val provincia: String,
    val distrito: String,
    val direccion: String? = null,
    val telefono: String? = null,
    val sitioWeb: String? = null,
    val descripcion: String? = null
) : Parcelable

@Parcelize
data class Emprendedor(
    val id: Long,
    val nombreEmpresa: String,
    val rubro: String,
    val direccion: String?,
    val telefono: String?,
    val email: String?,
    val sitioWeb: String?,
    val descripcion: String?,
    val productos: String?,
    val servicios: String?,
    val usuarioId: Long,
    val municipalidad: MunicipalidadBasic? = null,
    val categoria: CategoriaBasic? = null
) : Parcelable

@Parcelize
data class EmprendedorRequest(
    val nombreEmpresa: String,
    val rubro: String,
    val direccion: String? = null,
    val telefono: String? = null,
    val email: String? = null,
    val sitioWeb: String? = null,
    val descripcion: String? = null,
    val productos: String? = null,
    val servicios: String? = null,
    val municipalidadId: Long,
    val categoriaId: Long? = null
) : Parcelable
@Parcelize
data class Categoria(
    val id: Long,
    val nombre: String,
    val descripcion: String?,
    val cantidadEmprendedores: Int
) : Parcelable

@Parcelize
data class CategoriaBasic(
    val id: Long,
    val nombre: String
) : Parcelable

@Parcelize
data class CategoriaRequest(
    val nombre: String,
    val descripcion: String? = null
) : Parcelable

// ========== NUEVAS ENTIDADES PARA EL MÓDULO DE TURISMO ==========

@Parcelize
data class ServicioTuristico(
    val id: Long,
    val nombre: String,
    val descripcion: String?,
    val precio: Double,
    val duracionHoras: Int,
    val capacidadMaxima: Int,
    val tipo: TipoServicio,
    val estado: EstadoServicio,
    val ubicacion: String?,
    val requisitos: String?,
    val incluye: String?,
    val noIncluye: String?,
    val imagenUrl: String?,
    val emprendedor: EmprendedorWithMunicipalidad
) : Parcelable

@Parcelize
data class ServicioTuristicoRequest(
    val nombre: String,
    val descripcion: String? = null,
    val precio: Double,
    val duracionHoras: Int,
    val capacidadMaxima: Int,
    val tipo: TipoServicio,
    val ubicacion: String? = null,
    val requisitos: String? = null,
    val incluye: String? = null,
    val noIncluye: String? = null,
    val imagenUrl: String? = null
) : Parcelable

enum class TipoServicio {
    ALOJAMIENTO,
    TRANSPORTE,
    ALIMENTACION,
    GUIA_TURISTICO,
    ACTIVIDAD_RECREATIVA,
    TOUR,
    AVENTURA,
    CULTURAL,
    GASTRONOMICO,
    WELLNESS,
    OTRO
}

enum class EstadoServicio {
    ACTIVO,
    INACTIVO,
    AGOTADO,
    MANTENIMIENTO
}

@Parcelize
data class PlanTuristico(
    val id: Long,
    val nombre: String,
    val descripcion: String?,
    val precioTotal: Double,
    val duracionDias: Int,
    val capacidadMaxima: Int,
    val estado: EstadoPlan,
    val nivelDificultad: NivelDificultad,
    val imagenPrincipalUrl: String?,
    val itinerario: String?,
    val incluye: String?,
    val noIncluye: String?,
    val recomendaciones: String?,
    val requisitos: String?,
    val fechaCreacion: String,
    val fechaActualizacion: String?,
    val municipalidad: MunicipalidadBasic,
    val usuarioCreador: UsuarioBasic,
    val servicios: List<ServicioPlan>,
    val totalReservas: Int
) : Parcelable

@Parcelize
data class PlanTuristicoBasic(
    val id: Long,
    val nombre: String,
    val descripcion: String?,
    val precioTotal: Double,
    val duracionDias: Int,
    val capacidadMaxima: Int,
    val estado: EstadoPlan,
    val nivelDificultad: NivelDificultad,
    val imagenPrincipalUrl: String?,
    val municipalidad: MunicipalidadBasic
) : Parcelable

@Parcelize
data class PlanTuristicoRequest(
    val nombre: String,
    val descripcion: String? = null,
    val duracionDias: Int,
    val capacidadMaxima: Int,
    val nivelDificultad: NivelDificultad,
    val imagenPrincipalUrl: String? = null,
    val itinerario: String? = null,
    val incluye: String? = null,
    val noIncluye: String? = null,
    val recomendaciones: String? = null,
    val requisitos: String? = null,
    val servicios: List<ServicioPlanRequest>
) : Parcelable

enum class EstadoPlan {
    BORRADOR,
    ACTIVO,
    INACTIVO,
    AGOTADO,
    SUSPENDIDO
}

enum class NivelDificultad {
    FACIL,
    MODERADO,
    DIFICIL,
    EXTREMO
}

@Parcelize
data class ServicioPlan(
    val id: Long,
    val diaDelPlan: Int,
    val ordenEnElDia: Int,
    val horaInicio: String?,
    val horaFin: String?,
    val precioEspecial: Double?,
    val notas: String?,
    val esOpcional: Boolean,
    val esPersonalizable: Boolean,
    val servicio: ServicioTuristico
) : Parcelable

@Parcelize
data class ServicioPlanRequest(
    val servicioId: Long,
    val diaDelPlan: Int,
    val ordenEnElDia: Int,
    val horaInicio: String? = null,
    val horaFin: String? = null,
    val precioEspecial: Double? = null,
    val notas: String? = null,
    val esOpcional: Boolean,
    val esPersonalizable: Boolean
) : Parcelable

@Parcelize
data class Reserva(
    val id: Long,
    val codigoReserva: String,
    val fechaInicio: String,
    val fechaFin: String,
    val numeroPersonas: Int,
    val montoTotal: Double,
    val montoDescuento: Double?,
    val montoFinal: Double,
    val estado: EstadoReserva,
    val metodoPago: MetodoPago?,
    val observaciones: String?,
    val solicitudesEspeciales: String?,
    val contactoEmergencia: String?,
    val telefonoEmergencia: String?,
    val fechaReserva: String,
    val fechaConfirmacion: String?,
    val fechaCancelacion: String?,
    val motivoCancelacion: String?,
    val plan: PlanTuristicoBasic,
    val usuario: UsuarioBasic,
    val serviciosPersonalizados: List<ReservaServicio>?,
    val pagos: List<Pago>?
) : Parcelable

@Parcelize
data class ReservaRequest(
    val planId: Long,
    val fechaInicio: String,
    val numeroPersonas: Int,
    val observaciones: String? = null,
    val solicitudesEspeciales: String? = null,
    val contactoEmergencia: String? = null,
    val telefonoEmergencia: String? = null,
    val metodoPago: MetodoPago? = null,
    val serviciosPersonalizados: List<ReservaServicioRequest>? = null
) : Parcelable

enum class EstadoReserva {
    PENDIENTE,
    CONFIRMADA,
    PAGADA,
    EN_PROCESO,
    COMPLETADA,
    CANCELADA,
    NO_SHOW
}

enum class MetodoPago {
    EFECTIVO,
    TARJETA_CREDITO,
    TARJETA_DEBITO,
    TRANSFERENCIA,
    PAGO_MOVIL,
    PAYPAL,
    OTRO
}

@Parcelize
data class ReservaServicio(
    val id: Long,
    val incluido: Boolean,
    val precioPersonalizado: Double?,
    val observaciones: String?,
    val estado: EstadoServicioReserva,
    val servicioPlan: ServicioPlan
) : Parcelable

@Parcelize
data class ReservaServicioRequest(
    val servicioPlanId: Long,
    val incluido: Boolean,
    val precioPersonalizado: Double? = null,
    val observaciones: String? = null,
    val estado: EstadoServicioReserva
) : Parcelable

enum class EstadoServicioReserva {
    INCLUIDO,
    EXCLUIDO,
    PERSONALIZADO,
    PENDIENTE_CONFIRMACION
}

@Parcelize
data class Pago(
    val id: Long,
    val codigoPago: String,
    val reserva: ReservaBasic,
    val monto: Double,
    val tipo: TipoPago,
    val estado: EstadoPago,
    val metodoPago: MetodoPago,
    val numeroTransaccion: String?,
    val numeroAutorizacion: String?,
    val observaciones: String?,
    val fechaPago: String,
    val fechaConfirmacion: String?
) : Parcelable

@Parcelize
data class PagoRequest(
    val reservaId: Long,
    val monto: Double,
    val tipo: TipoPago,
    val metodoPago: MetodoPago,
    val numeroTransaccion: String? = null,
    val numeroAutorizacion: String? = null,
    val observaciones: String? = null
) : Parcelable

enum class TipoPago {
    SEÑA,
    PAGO_COMPLETO,
    PAGO_PARCIAL,
    SALDO_PENDIENTE
}

enum class EstadoPago {
    PENDIENTE,
    PROCESANDO,
    CONFIRMADO,
    FALLIDO,
    REEMBOLSADO,
    CANCELADO
}

@Parcelize
data class UsuarioBasic(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val username: String,
    val email: String
) : Parcelable

@Parcelize
data class UsuarioResponse(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val username: String,
    val email: String,
    val roles: List<String>,
    val emprendedor: EmprendedorBasic? = null
) : Parcelable

@Parcelize
data class ReservaBasic(
    val id: Long,
    val codigoReserva: String,
    val fechaInicio: String,
    val numeroPersonas: Int,
    val montoFinal: Double,
    val estado: EstadoReserva,
    val plan: PlanTuristicoBasic,
    val usuario: UsuarioBasic
) : Parcelable