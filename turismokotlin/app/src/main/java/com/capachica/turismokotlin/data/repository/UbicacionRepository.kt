package com.capachica.turismokotlin.data.repository

import com.capachica.turismokotlin.data.api.ApiService
import com.capachica.turismokotlin.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UbicacionRepository(private val apiService: ApiService) {

    fun getUbicacionesEmprendedores(): Flow<Result<List<EmprendedorUbicacion>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getUbicacionesEmprendedores()
            if (response.isSuccessful) {
                response.body()?.let { ubicaciones ->
                    emit(Result.Success(ubicaciones))
                } ?: emit(Result.Error("No se encontraron ubicaciones"))
            } else {
                emit(Result.Error("Error al obtener ubicaciones: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun getUbicacionesServicios(): Flow<Result<List<ServicioUbicacion>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getUbicacionesServicios()
            if (response.isSuccessful) {
                response.body()?.let { ubicaciones ->
                    emit(Result.Success(ubicaciones))
                } ?: emit(Result.Error("No se encontraron ubicaciones"))
            } else {
                emit(Result.Error("Error al obtener ubicaciones: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun buscarCercanos(
        latitud: Double,
        longitud: Double,
        radio: Double = 10.0,
        tipo: String? = null
    ): Flow<Result<BusquedaCercanosResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.buscarCercanos(latitud, longitud, radio, tipo)
            if (response.isSuccessful) {
                response.body()?.let { resultado ->
                    emit(Result.Success(resultado))
                } ?: emit(Result.Error("No se encontraron resultados"))
            } else {
                emit(Result.Error("Error en búsqueda: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun calcularDistancia(
        latitudOrigen: Double,
        longitudOrigen: Double,
        latitudDestino: Double,
        longitudDestino: Double
    ): Flow<Result<DistanciaResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.calcularDistancia(latitudOrigen, longitudOrigen, latitudDestino, longitudDestino)
            if (response.isSuccessful) {
                response.body()?.let { distancia ->
                    emit(Result.Success(distancia))
                } ?: emit(Result.Error("Error al calcular distancia"))
            } else {
                emit(Result.Error("Error al calcular distancia: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun validarCoordenadas(latitud: Double, longitud: Double): Flow<Result<ValidacionCoordenadaResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.validarCoordenadas(latitud, longitud)
            if (response.isSuccessful) {
                response.body()?.let { validacion ->
                    emit(Result.Success(validacion))
                } ?: emit(Result.Error("Error al validar coordenadas"))
            } else {
                emit(Result.Error("Error al validar coordenadas: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun actualizarUbicacionEmprendedor(
        emprendedorId: Long,
        request: UbicacionRequest
    ): Flow<Result<UbicacionResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.actualizarUbicacionEmprendedor(emprendedorId, request)
            if (response.isSuccessful) {
                response.body()?.let { ubicacion ->
                    emit(Result.Success(ubicacion))
                } ?: emit(Result.Error("Error al actualizar ubicación"))
            } else {
                emit(Result.Error("Error al actualizar ubicación: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun actualizarUbicacionServicio(
        servicioId: Long,
        request: UbicacionRequest
    ): Flow<Result<UbicacionResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.actualizarUbicacionServicio(servicioId, request)
            if (response.isSuccessful) {
                response.body()?.let { ubicacion ->
                    emit(Result.Success(ubicacion))
                } ?: emit(Result.Error("Error al actualizar ubicación"))
            } else {
                emit(Result.Error("Error al actualizar ubicación: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }
}