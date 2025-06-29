package com.capachica.turismokotlin.data.repository

import com.capachica.turismokotlin.data.api.ApiService
import com.capachica.turismokotlin.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ReservaCarritoRepository(private val apiService: ApiService) {

    fun crearReservaDesdeCarrito(request: ReservaCarritoRequest): Flow<Result<ReservaCarritoResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.crearReservaDesdeCarrito(request)
            if (response.isSuccessful) {
                response.body()?.let { reserva ->
                    emit(Result.Success(reserva))
                } ?: emit(Result.Error("Error al crear reserva"))
            } else {
                emit(Result.Error("Error al crear reserva: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun getMisReservasCarrito(): Flow<Result<List<ReservaCarritoResponse>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getMisReservasCarrito()
            if (response.isSuccessful) {
                response.body()?.let { reservas ->
                    emit(Result.Success(reservas))
                } ?: emit(Result.Error("No se encontraron reservas"))
            } else {
                emit(Result.Error("Error al obtener reservas: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun getReservaCarritoById(id: Long): Flow<Result<ReservaCarritoResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getReservaCarritoById(id)
            if (response.isSuccessful) {
                response.body()?.let { reserva ->
                    emit(Result.Success(reserva))
                } ?: emit(Result.Error("Reserva no encontrada"))
            } else {
                emit(Result.Error("Error al obtener reserva: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun getReservaCarritoByCodigo(codigo: String): Flow<Result<ReservaCarritoResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getReservaCarritoByCodigo(codigo)
            if (response.isSuccessful) {
                response.body()?.let { reserva ->
                    emit(Result.Success(reserva))
                } ?: emit(Result.Error("Reserva no encontrada"))
            } else {
                emit(Result.Error("Error al obtener reserva: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun getReservasCarritoPorEstado(estado: EstadoReservaCarrito): Flow<Result<List<ReservaCarritoResponse>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getReservasCarritoPorEstado(estado)
            if (response.isSuccessful) {
                response.body()?.let { reservas ->
                    emit(Result.Success(reservas))
                } ?: emit(Result.Error("No se encontraron reservas"))
            } else {
                emit(Result.Error("Error al obtener reservas: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun getEstadisticasReservasCarrito(): Flow<Result<EstadisticasReservaResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getEstadisticasReservasCarrito()
            if (response.isSuccessful) {
                response.body()?.let { estadisticas ->
                    emit(Result.Success(estadisticas))
                } ?: emit(Result.Error("Error al obtener estadísticas"))
            } else {
                emit(Result.Error("Error al obtener estadísticas: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun getReservasParaEmprendedor(): Flow<Result<List<ReservaCarritoResponse>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getReservasParaEmprendedor()
            if (response.isSuccessful) {
                response.body()?.let { reservas ->
                    emit(Result.Success(reservas))
                } ?: emit(Result.Error("No se encontraron reservas"))
            } else {
                emit(Result.Error("Error al obtener reservas: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun confirmarReservaCarrito(id: Long): Flow<Result<ReservaCarritoResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.confirmarReservaCarrito(id)
            if (response.isSuccessful) {
                response.body()?.let { reserva ->
                    emit(Result.Success(reserva))
                } ?: emit(Result.Error("Error al confirmar reserva"))
            } else {
                emit(Result.Error("Error al confirmar reserva: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun completarReservaCarrito(id: Long): Flow<Result<ReservaCarritoResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.completarReservaCarrito(id)
            if (response.isSuccessful) {
                response.body()?.let { reserva ->
                    emit(Result.Success(reserva))
                } ?: emit(Result.Error("Error al completar reserva"))
            } else {
                emit(Result.Error("Error al completar reserva: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun cancelarReservaCarrito(id: Long, request: CancelacionReservaRequest): Flow<Result<ReservaCarritoResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.cancelarReservaCarrito(id, request)
            if (response.isSuccessful) {
                response.body()?.let { reserva ->
                    emit(Result.Success(reserva))
                } ?: emit(Result.Error("Error al cancelar reserva"))
            } else {
                emit(Result.Error("Error al cancelar reserva: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }
}