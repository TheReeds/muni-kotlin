package com.capachica.turismokotlin.data.model

data class UsuarioDetallado(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val username: String,
    val email: String,
    val roles: List<String>,
    val emprendedor: EmprendedorBasico? = null
)

data class CreateEmprendedorRequest(
    val nombreEmpresa: String,
    val rubro: String,
    val direccion: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val direccionCompleta: String? = null,
    val telefono: String,
    val email: String,
    val sitioWeb: String? = null,
    val descripcion: String? = null,
    val productos: String? = null,
    val servicios: String? = null,
    val municipalidadId: Long,
    val categoriaId: Long
)

data class UpdateEmprendedorRequest(
    val nombreEmpresa: String,
    val rubro: String,
    val direccion: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val direccionCompleta: String? = null,
    val telefono: String,
    val email: String,
    val sitioWeb: String? = null,
    val descripcion: String? = null,
    val productos: String? = null,
    val servicios: String? = null,
    val municipalidadId: Long,
    val categoriaId: Long
)

data class CreateCategoriaRequest(
    val nombre: String,
    val descripcion: String? = null
)

data class UpdateCategoriaRequest(
    val nombre: String,
    val descripcion: String? = null
)

data class MunicipalidadDetallada(
    val id: Long,
    val nombre: String,
    val departamento: String,
    val provincia: String,
    val distrito: String,
    val direccion: String? = null,
    val telefono: String? = null,
    val sitioWeb: String? = null,
    val descripcion: String? = null,
    val usuarioId: Long,
    val emprendedores: List<EmprendedorBasico>
)

data class CreateMunicipalidadRequest(
    val nombre: String,
    val departamento: String,
    val provincia: String,
    val distrito: String,
    val direccion: String? = null,
    val telefono: String? = null,
    val sitioWeb: String? = null,
    val descripcion: String? = null
)

data class UpdateMunicipalidadRequest(
    val nombre: String,
    val departamento: String,
    val provincia: String,
    val distrito: String,
    val direccion: String? = null,
    val telefono: String? = null,
    val sitioWeb: String? = null,
    val descripcion: String? = null
)