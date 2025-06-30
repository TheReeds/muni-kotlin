package com.capachica.turismokotlin.data.model

data class CartRemoto(
    val id: Long,
    val usuarioId: Long,
    val fechaCreacion: String,
    val fechaActualizacion: String,
    val totalCarrito: Double,
    val totalItems: Int,
    val items: List<CartItemRemoto>
)

data class CartItemRemoto(
    val id: Long,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double,
    val fechaServicio: String,
    val notasEspeciales: String? = null,
    val fechaAgregado: String,
    val servicio: ServicioCarrito
)

data class ServicioCarrito(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val duracionHoras: Int,
    val tipo: TipoServicio,
    val imagenUrl: String? = null,
    val ubicacion: UbicacionServicio,
    val emprendedor: EmprendedorCarrito
)

data class UbicacionServicio(
    val latitud: Double?,
    val longitud: Double?,
    val direccionCompleta: String? = null,
    val tieneUbicacionValida: Boolean = false
)

data class EmprendedorCarrito(
    val id: Long,
    val nombreEmpresa: String,
    val rubro: String,
    val telefono: String,
    val email: String,
    val municipalidad: MunicipalidadBasica
)

data class MunicipalidadBasica(
    val id: Long,
    val nombre: String,
    val departamento: String,
    val provincia: String,
    val distrito: String
)

data class AddToCartRequest(
    val servicioId: Long,
    val cantidad: Int,
    val fechaServicio: String,
    val notasEspeciales: String? = null
)


enum class TipoPago {
    SEÑA, TOTAL, COMPLEMENTO
}

enum class EstadoPago {
    PENDIENTE, CONFIRMADO, RECHAZADO
}

data class CreateReservaCarritoRequest(
    val observaciones: String? = null,
    val contactoEmergencia: String? = null,
    val telefonoEmergencia: String? = null,
    val metodoPago: MetodoPago = MetodoPago.EFECTIVO
)

data class ReservaCarrito(
    val id: Long,
    val codigoReserva: String,
    val montoTotal: Double,
    val montoDescuento: Double,
    val montoFinal: Double,
    val estado: EstadoReserva,
    val metodoPago: MetodoPago,
    val observaciones: String? = null,
    val contactoEmergencia: String? = null,
    val telefonoEmergencia: String? = null,
    val fechaReserva: String,
    val fechaConfirmacion: String? = null,
    val fechaCancelacion: String? = null,
    val motivoCancelacion: String? = null,
    val usuario: UsuarioBasico,
    val items: List<ReservaItem>,
    val pagos: List<PagoReserva>
)

data class UsuarioBasico(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val username: String,
    val email: String
)

data class ReservaItem(
    val id: Long,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double,
    val fechaServicio: String,
    val notasEspeciales: String? = null,
    val estado: EstadoReserva,
    val servicio: ServicioCarrito
)

data class PagoReserva(
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