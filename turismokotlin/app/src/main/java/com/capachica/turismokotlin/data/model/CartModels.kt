package com.capachica.turismokotlin.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime

// ========== CARRITO MODELS ==========

@Parcelize
data class CarritoResponse(
    val id: Long,
    val usuarioId: Long,
    val fechaCreacion: String,
    val fechaActualizacion: String,
    val totalCarrito: Double,
    val totalItems: Int,
    val items: List<CarritoItemResponse>
) : Parcelable

@Parcelize
data class CarritoItemResponse(
    val id: Long,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double,
    val fechaServicio: String,
    val notasEspeciales: String?,
    val fechaAgregado: String,
    val servicio: ServicioTuristicoBasicResponse
) : Parcelable

@Parcelize
data class CarritoItemRequest(
    val servicioId: Long,
    val cantidad: Int,
    val fechaServicio: String,
    val notasEspeciales: String? = null
) : Parcelable

@Parcelize
data class ServicioTuristicoBasicResponse(
    val id: Long,
    val nombre: String,
    val precio: Double,
    val tipo: TipoServicio,
    val emprendedor: EmprendedorBasic
) : Parcelable

@Parcelize
data class CarritoTotalResponse(
    val totalItems: Int,
    val totalCarrito: Double
) : Parcelable

@Parcelize
data class CarritoContarResponse(
    val cantidadItems: Int
) : Parcelable

// ========== RESERVAS DESDE CARRITO MODELS ==========

@Parcelize
data class ReservaCarritoResponse(
    val id: Long,
    val codigoReserva: String,
    val montoTotal: Double,
    val montoDescuento: Double,
    val montoFinal: Double,
    val estado: EstadoReservaCarrito,
    val metodoPago: MetodoPago?,
    val fechaCreacion: String,
    val fechaInicio: String,
    val fechaFin: String,
    val numeroPersonas: Int,
    val observaciones: String?,
    val items: List<ReservaCarritoItemResponse>,
    val pagos: List<PagoCarritoResponse>
) : Parcelable

@Parcelize
data class ReservaCarritoItemResponse(
    val id: Long,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double,
    val fechaServicio: String,
    val notasEspeciales: String?,
    val servicio: ServicioTuristicoBasicResponse
) : Parcelable

@Parcelize
data class PagoCarritoResponse(
    val id: Long,
    val monto: Double,
    val metodoPago: MetodoPago,
    val estado: EstadoPago,
    val fechaPago: String,
    val numeroTransaccion: String?
) : Parcelable

@Parcelize
data class ReservaCarritoRequest(
    val fechaInicio: String,
    val fechaFin: String,
    val numeroPersonas: Int,
    val metodoPago: MetodoPago?,
    val observaciones: String? = null
) : Parcelable

@Parcelize
data class CancelacionReservaRequest(
    val motivo: String
) : Parcelable

enum class EstadoReservaCarrito {
    PENDIENTE,
    CONFIRMADA,
    PAGADA,
    EN_PROCESO,
    COMPLETADA,
    CANCELADA
}

@Parcelize
data class EstadisticasReservaResponse(
    val totalReservas: Int,
    val reservasPendientes: Int,
    val reservasConfirmadas: Int,
    val reservasCompletadas: Int,
    val reservasCanceladas: Int,
    val montoTotalGastado: Double
) : Parcelable