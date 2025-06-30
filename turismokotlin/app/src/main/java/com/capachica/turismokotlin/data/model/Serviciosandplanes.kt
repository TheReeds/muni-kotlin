package com.capachica.turismokotlin.data.model

data class CrearServicioRequest(
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val duracionHoras: Int,
    val capacidadMaxima: Int,
    val tipo: TipoServicio,
    val ubicacion: String,
    val latitud: Double?,
    val longitud: Double?,
    val requisitos: String? = null,
    val incluye: String? = null,
    val noIncluye: String? = null,
    val imagenUrl: String? = null
)

data class ActualizarServicioRequest(
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val duracionHoras: Int,
    val capacidadMaxima: Int,
    val tipo: TipoServicio,
    val ubicacion: String,
    val latitud: Double?,
    val longitud: Double?,
    val requisitos: String? = null,
    val incluye: String? = null,
    val noIncluye: String? = null,
    val imagenUrl: String? = null
)

data class ServicioPlanRequest(
    val servicioId: Long,
    val diaDelPlan: Int,
    val ordenEnElDia: Int,
    val horaInicio: String,
    val horaFin: String,
    val precioEspecial: Double,
    val notas: String? = null,
    val esOpcional: Boolean = false,
    val esPersonalizable: Boolean = false
)

data class CrearPlanRequest(
    val nombre: String,
    val descripcion: String,
    val duracionDias: Int,
    val capacidadMaxima: Int,
    val nivelDificultad: NivelDificultad,
    val imagenPrincipalUrl: String? = null,
    val itinerario: String? = null,
    val incluye: String? = null,
    val noIncluye: String? = null,
    val recomendaciones: String? = null,
    val requisitos: String? = null,
    val municipalidadId: Long,
    val servicios: List<ServicioPlanRequest> = emptyList()
)

data class ActualizarPlanRequest(
    val nombre: String,
    val descripcion: String,
    val duracionDias: Int,
    val capacidadMaxima: Int,
    val nivelDificultad: NivelDificultad,
    val imagenPrincipalUrl: String? = null,
    val itinerario: String? = null,
    val incluye: String? = null,
    val noIncluye: String? = null,
    val recomendaciones: String? = null,
    val requisitos: String? = null,
    val municipalidadId: Long,
    val servicios: List<ServicioPlanRequest> = emptyList()
)