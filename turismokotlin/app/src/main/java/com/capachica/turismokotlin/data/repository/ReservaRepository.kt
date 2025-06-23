package com.capachica.turismokotlin.data.repository

import com.capachica.turismokotlin.data.api.ApiService
import com.capachica.turismokotlin.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReservaRepository @Inject constructor(
    private val apiService: ApiService
) {
    
    fun getAllReservas(): Flow<Result<List<Reserva>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getAllReservas()
            if (response.isSuccessful) {
                emit(Result.Success(response.body() ?: emptyList()))
            } else {
                emit(Result.Error("Error al obtener reservas: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun getReservaById(id: Long): Flow<Result<Reserva>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getReservaById(id)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Error("Reserva no encontrada"))
            } else {
                emit(Result.Error("Error al obtener reserva: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun getReservaByCodigo(codigo: String): Flow<Result<Reserva>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getReservaByCodigo(codigo)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Error("Reserva no encontrada"))
            } else {
                emit(Result.Error("Error al obtener reserva: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun getMisReservas(): Flow<Result<List<Reserva>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getMisReservas()
            if (response.isSuccessful) {
                emit(Result.Success(response.body() ?: emptyList()))
            } else {
                emit(Result.Error("Error al obtener mis reservas: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun getReservasByPlan(planId: Long): Flow<Result<List<Reserva>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getReservasByPlan(planId)
            if (response.isSuccessful) {
                emit(Result.Success(response.body() ?: emptyList()))
            } else {
                emit(Result.Error("Error al obtener reservas: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun getReservasByMunicipalidad(municipalidadId: Long): Flow<Result<List<Reserva>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getReservasByMunicipalidad(municipalidadId)
            if (response.isSuccessful) {
                emit(Result.Success(response.body() ?: emptyList()))
            } else {
                emit(Result.Error("Error al obtener reservas: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun createReserva(request: ReservaRequest): Flow<Result<Reserva>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.createReserva(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Error("Error al crear reserva"))
            } else {
                emit(Result.Error("Error al crear reserva: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun confirmarReserva(id: Long): Flow<Result<Reserva>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.confirmarReserva(id)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Error("Error al confirmar reserva"))
            } else {
                emit(Result.Error("Error al confirmar reserva: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun cancelarReserva(id: Long, motivo: String): Flow<Result<Reserva>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.cancelarReserva(id, motivo)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Error("Error al cancelar reserva"))
            } else {
                emit(Result.Error("Error al cancelar reserva: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun completarReserva(id: Long): Flow<Result<Reserva>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.completarReserva(id)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Error("Error al completar reserva"))
            } else {
                emit(Result.Error("Error al completar reserva: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
}