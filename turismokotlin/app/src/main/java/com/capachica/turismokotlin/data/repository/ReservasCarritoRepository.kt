package com.capachica.turismokotlin.data.repository

import com.capachica.turismokotlin.data.model.CreateReservaCarritoRequest
import com.capachica.turismokotlin.data.model.ReservaCarrito
import com.capachica.turismokotlin.network.api.ReservasCarritoApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReservasCarritoRepository @Inject constructor(
    private val reservasCarritoApiService: ReservasCarritoApiService
) {
    suspend fun createReservaFromCart(request: CreateReservaCarritoRequest): Result<ReservaCarrito> {
        return try {
            val response = reservasCarritoApiService.createReservaFromCart(request)
            if (response.isSuccessful) {
                response.body()?.let { reserva ->
                    Result.success(reserva)
                } ?: Result.failure(Exception("Error al crear reserva"))
            } else {
                Result.failure(Exception("Error al crear reserva: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMisReservas(): Result<List<ReservaCarrito>> {
        return try {
            val response = reservasCarritoApiService.getMisReservas()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener reservas: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReservaById(reservaId: Long): Result<ReservaCarrito> {
        return try {
            val response = reservasCarritoApiService.getReservaById(reservaId)
            if (response.isSuccessful) {
                response.body()?.let { reserva ->
                    Result.success(reserva)
                } ?: Result.failure(Exception("Reserva no encontrada"))
            } else {
                Result.failure(Exception("Error al obtener reserva: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun confirmarReserva(reservaId: Long): Result<ReservaCarrito> {
        return try {
            val response = reservasCarritoApiService.confirmarReserva(reservaId)
            if (response.isSuccessful) {
                response.body()?.let { reserva ->
                    Result.success(reserva)
                } ?: Result.failure(Exception("Error al confirmar reserva"))
            } else {
                Result.failure(Exception("Error al confirmar reserva: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelarReserva(reservaId: Long, motivo: String): Result<ReservaCarrito> {
        return try {
            val response = reservasCarritoApiService.cancelarReserva(reservaId, motivo)
            if (response.isSuccessful) {
                response.body()?.let { reserva ->
                    Result.success(reserva)
                } ?: Result.failure(Exception("Error al cancelar reserva"))
            } else {
                Result.failure(Exception("Error al cancelar reserva: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getReservasEmprendedor(): Result<List<ReservaCarrito>> {
        return try {
            val response = reservasCarritoApiService.getReservasEmprendedor()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener reservas del emprendedor: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun completarReserva(reservaId: Long): Result<ReservaCarrito> {
        return try {
            val response = reservasCarritoApiService.completarReserva(reservaId)
            if (response.isSuccessful) {
                response.body()?.let { reserva ->
                    Result.success(reserva)
                } ?: Result.failure(Exception("Error al completar reserva"))
            } else {
                Result.failure(Exception("Error al completar reserva: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}