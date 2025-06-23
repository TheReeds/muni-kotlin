package com.capachica.turismokotlin.data.repository

import com.capachica.turismokotlin.data.api.ApiService
import com.capachica.turismokotlin.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PagoRepository @Inject constructor(
    private val apiService: ApiService
) {
    
    fun getAllPagos(): Flow<Result<List<Pago>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getAllPagos()
            if (response.isSuccessful) {
                emit(Result.Success(response.body() ?: emptyList()))
            } else {
                emit(Result.Error("Error al obtener pagos: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun getPagoById(id: Long): Flow<Result<Pago>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getPagoById(id)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Error("Pago no encontrado"))
            } else {
                emit(Result.Error("Error al obtener pago: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun getPagoByCodigo(codigo: String): Flow<Result<Pago>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getPagoByCodigo(codigo)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Error("Pago no encontrado"))
            } else {
                emit(Result.Error("Error al obtener pago: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun getPagosByReserva(reservaId: Long): Flow<Result<List<Pago>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getPagosByReserva(reservaId)
            if (response.isSuccessful) {
                emit(Result.Success(response.body() ?: emptyList()))
            } else {
                emit(Result.Error("Error al obtener pagos: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun getMisPagos(): Flow<Result<List<Pago>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getMisPagos()
            if (response.isSuccessful) {
                emit(Result.Success(response.body() ?: emptyList()))
            } else {
                emit(Result.Error("Error al obtener mis pagos: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun getPagosByMunicipalidad(municipalidadId: Long): Flow<Result<List<Pago>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getPagosByMunicipalidad(municipalidadId)
            if (response.isSuccessful) {
                emit(Result.Success(response.body() ?: emptyList()))
            } else {
                emit(Result.Error("Error al obtener pagos: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun registrarPago(request: PagoRequest): Flow<Result<Pago>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.registrarPago(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Error("Error al registrar pago"))
            } else {
                emit(Result.Error("Error al registrar pago: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun confirmarPago(id: Long): Flow<Result<Pago>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.confirmarPago(id)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Error("Error al confirmar pago"))
            } else {
                emit(Result.Error("Error al confirmar pago: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun rechazarPago(id: Long, motivo: String): Flow<Result<Pago>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.rechazarPago(id, motivo)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Error("Error al rechazar pago"))
            } else {
                emit(Result.Error("Error al rechazar pago: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
}