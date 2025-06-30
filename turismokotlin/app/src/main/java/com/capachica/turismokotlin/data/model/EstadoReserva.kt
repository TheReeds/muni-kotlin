package com.capachica.turismokotlin.data.model

import com.google.gson.annotations.SerializedName

enum class EstadoReserva {
    @SerializedName("PENDIENTE")
    PENDIENTE,

    @SerializedName("CONFIRMADO")
    CONFIRMADA,

    @SerializedName("COMPLETADO")
    COMPLETADA,

    @SerializedName("CANCELADA")
    CANCELADA;

    companion object {
        fun fromString(value: String?): EstadoReserva {
            return when (value?.uppercase()) {
                "PENDIENTE" -> PENDIENTE
                "CONFIRMADO" -> CONFIRMADA
                "COMPLETADO" -> COMPLETADA
                "CANCELADO" -> CANCELADA
                else -> PENDIENTE // Valor por defecto
            }
        }
    }
}

enum class MetodoPago {
    @SerializedName("EFECTIVO")
    EFECTIVO,

    @SerializedName("TARJETA")
    TARJETA,

    @SerializedName("TRANSFERENCIA")
    TRANSFERENCIA,

    @SerializedName("YAPE")
    YAPE,

    @SerializedName("PLIN")
    PLIN;

    companion object {
        fun fromString(value: String?): MetodoPago {
            return when (value?.uppercase()) {
                "EFECTIVO" -> EFECTIVO
                "TARJETA" -> TARJETA
                "TRANSFERENCIA" -> TRANSFERENCIA
                "YAPE" -> YAPE
                "PLIN" -> PLIN
                else -> EFECTIVO // Valor por defecto
            }
        }
    }
}
enum class TipoServicio {
    @SerializedName("ALOJAMIENTO")
    ALOJAMIENTO,

    @SerializedName("TRANSPORTE")
    TRANSPORTE,

    @SerializedName("ALIMENTACION")
    ALIMENTACION,

    @SerializedName("GUIA_TURISTICO")
    GUIA_TURISTICO,

    @SerializedName("ACTIVIDAD_RECREATIVA")
    ACTIVIDAD_RECREATIVA,

    @SerializedName("CULTURAL")
    CULTURAL,

    @SerializedName("AVENTURA")
    AVENTURA,

    @SerializedName("WELLNESS")
    WELLNESS,

    @SerializedName("TOUR")
    TOUR,

    @SerializedName("GASTRONOMICO")
    GASTRONOMICO,

    @SerializedName("OTRO")
    OTRO;

    companion object {
        fun fromString(value: String?): TipoServicio {
            return when (value?.uppercase()) {
                "ALOJAMIENTO" -> ALOJAMIENTO
                "TRANSPORTE" -> TRANSPORTE
                "ALIMENTACION" -> ALIMENTACION
                "GUIA_TURISTICO" -> GUIA_TURISTICO
                "ACTIVIDAD_RECREATIVA" -> ACTIVIDAD_RECREATIVA
                "CULTURAL" -> CULTURAL
                "AVENTURA" -> AVENTURA
                "WELLNESS" -> WELLNESS
                "TOUR" -> TOUR
                "GASTRONOMICO" -> GASTRONOMICO
                "OTRO" -> OTRO
                else -> OTRO
            }
        }
    }
}
