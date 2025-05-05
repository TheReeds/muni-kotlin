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
    val municipalidad: MunicipalidadBasic? = null
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
    val municipalidadId: Long
) : Parcelable