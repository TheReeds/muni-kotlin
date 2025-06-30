package com.capachica.turismokotlin.data.model

import com.google.gson.annotations.SerializedName

// Enums para Chat
enum class EstadoConversacion {
    @SerializedName("ACTIVA")
    ACTIVA,

    @SerializedName("CERRADA")
    CERRADA,

    @SerializedName("PAUSADA")
    PAUSADA;

    companion object {
        fun fromString(value: String?): EstadoConversacion {
            return when (value?.uppercase()) {
                "ACTIVA" -> ACTIVA
                "CERRADA" -> CERRADA
                "PAUSADA" -> PAUSADA
                else -> ACTIVA
            }
        }
    }
}

enum class TipoMensaje {
    @SerializedName("TEXTO")
    TEXTO,

    @SerializedName("IMAGEN")
    IMAGEN,

    @SerializedName("ARCHIVO")
    ARCHIVO,

    @SerializedName("UBICACION")
    UBICACION,

    @SerializedName("SISTEMA")
    SISTEMA;

    companion object {
        fun fromString(value: String?): TipoMensaje {
            return when (value?.uppercase()) {
                "TEXTO" -> TEXTO
                "IMAGEN" -> IMAGEN
                "ARCHIVO" -> ARCHIVO
                "UBICACION" -> UBICACION
                "SISTEMA" -> SISTEMA
                else -> TEXTO
            }
        }
    }
}

// Modelo de Conversación
data class Conversacion(
    val id: Long,
    val usuarioId: Long,
    val emprendedorId: Long,
    val reservaId: Long? = null,
    val reservaCarritoId: Long? = null,
    val codigoReservaAsociada: String? = null,
    val fechaCreacion: String,
    val fechaUltimoMensaje: String,
    val estado: EstadoConversacion,
    val usuario: UsuarioBasico,
    val emprendedor: EmprendedorBasico,
    val ultimoMensaje: MensajeChat? = null,
    val mensajesNoLeidos: Int = 0,
    val mensajesRecientes: List<MensajeChat> = emptyList()
)

// Modelo de Mensaje
data class MensajeChat(
    val id: Long,
    val conversacionId: Long,
    val mensaje: String,
    val tipo: TipoMensaje,
    val fechaEnvio: String,
    val leido: Boolean,
    val esDeEmprendedor: Boolean,
    val remitenteId: Long,
    val remitenteNombre: String,
    val archivoUrl: String? = null,
    val archivoNombre: String? = null,
    val archivoTipo: String? = null
)

// Modelo básico de Emprendedor para Chat
data class EmprendedorBasico(
    val id: Long,
    val nombreEmpresa: String,
    val rubro: String,
    val telefono: String,
    val email: String,
    val municipalidad: MunicipalidadBasica
)

// Requests para enviar mensajes
data class EnviarMensajeRequest(
    val conversacionId: Long,
    val mensaje: String,
    val tipo: TipoMensaje = TipoMensaje.TEXTO
)

data class CrearConversacionRequest(
    val emprendedorId: Long,
    val mensaje: String,
    val reservaId: Long? = null,
    val reservaCarritoId: Long? = null
)