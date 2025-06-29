package com.capachica.turismokotlin.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// ========== UBICACION MODELS ==========

@Parcelize
data class UbicacionRequest(
    val latitud: Double,
    val longitud: Double,
    val direccionCompleta: String? = null
) : Parcelable

@Parcelize
data class UbicacionResponse(
    val latitud: Double,
    val longitud: Double,
    val direccionCompleta: String?,
    val tieneUbicacionValida: Boolean
) : Parcelable

@Parcelize
data class EmprendedorUbicacion(
    val id: Long,
    val nombreEmpresa: String,
    val rubro: String,
    val latitud: Double,
    val longitud: Double,
    val direccionCompleta: String?,
    val municipalidad: MunicipalidadBasic
) : Parcelable

@Parcelize
data class ServicioUbicacion(
    val id: Long,
    val nombre: String,
    val tipo: TipoServicio,
    val precio: Double,
    val latitud: Double,
    val longitud: Double,
    val direccionCompleta: String?,
    val emprendedor: EmprendedorBasic
) : Parcelable

@Parcelize
data class BusquedaCercanosRequest(
    val latitud: Double,
    val longitud: Double,
    val radio: Double = 10.0, // km
    val tipo: String? = null // "emprendedor" o "servicio"
) : Parcelable

@Parcelize
data class BusquedaCercanosResponse(
    val emprendedores: List<EmprendedorUbicacion>,
    val servicios: List<ServicioUbicacion>
) : Parcelable

@Parcelize
data class DistanciaRequest(
    val latitudOrigen: Double,
    val longitudOrigen: Double,
    val latitudDestino: Double,
    val longitudDestino: Double
) : Parcelable

@Parcelize
data class DistanciaResponse(
    val distanciaKm: Double,
    val distanciaTexto: String
) : Parcelable

@Parcelize
data class ValidacionCoordenadaResponse(
    val esValida: Boolean,
    val mensaje: String?
) : Parcelable