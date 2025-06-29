package com.capachica.turismokotlin.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// ========== CHAT MODELS ==========

@Parcelize
data class ConversacionResponse(
    val id: Long,
    val usuarioId: Long,
    val emprendedorId: Long,
    val reservaCarritoId: Long?,
    val fechaCreacion: String,
    val ultimoMensaje: MensajeResponse?,
    val mensajesNoLeidos: Int,
    val emprendedor: EmprendedorBasic,
    val usuario: UsuarioBasic
) : Parcelable

@Parcelize
data class MensajeResponse(
    val id: Long,
    val contenido: String,
    val fechaEnvio: String,
    val leido: Boolean,
    val tipoMensaje: TipoMensaje,
    val emisor: UsuarioBasic,
    val conversacionId: Long
) : Parcelable

@Parcelize
data class MensajeRequest(
    val conversacionId: Long,
    val contenido: String,
    val tipoMensaje: TipoMensaje = TipoMensaje.TEXTO
) : Parcelable

@Parcelize
data class IniciarConversacionCarritoRequest(
    val emprendedorId: Long,
    val mensaje: String
) : Parcelable

@Parcelize
data class MensajeRapidoRequest(
    val mensaje: String
) : Parcelable

@Parcelize
data class MensajesNoLeidosResponse(
    val cantidadNoLeidos: Int
) : Parcelable

enum class TipoMensaje {
    TEXTO,
    IMAGEN,
    ARCHIVO,
    UBICACION,
    SISTEMA
}