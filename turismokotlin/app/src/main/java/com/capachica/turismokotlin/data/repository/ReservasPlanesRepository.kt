package com.capachica.turismokotlin.data.repository

import com.capachica.turismokotlin.data.model.CreateReservaPlanRequest
import com.capachica.turismokotlin.data.model.ReservaPlan
import com.capachica.turismokotlin.network.api.ReservasPlanesApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReservasPlanesRepository @Inject constructor(
    private val reservasPlanesApiService: ReservasPlanesApiService
) {
    suspend fun createReservaPlan(request: CreateReservaPlanRequest): Result<ReservaPlan> {
        return try {
            val response = reservasPlanesApiService.createReservaPlan(request)
            if (response.isSuccessful) {
                response.body()?.let { reserva ->
                    Result.success(reserva)
                } ?: Result.failure(Exception("Error al crear reserva de plan"))
            } else {
                Result.failure(Exception("Error al crear reserva de plan: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMisReservasPlanes(): Result<List<ReservaPlan>> {
        return try {
            val response = reservasPlanesApiService.getMisReservasPlanes()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener reservas de planes: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReservaPlanById(reservaId: Long): Result<ReservaPlan> {
        return try {
            val response = reservasPlanesApiService.getReservaPlanById(reservaId)
            if (response.isSuccessful) {
                response.body()?.let { reserva ->
                    Result.success(reserva)
                } ?: Result.failure(Exception("Reserva de plan no encontrada"))
            } else {
                Result.failure(Exception("Error al obtener reserva de plan: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun confirmarReservaPlan(reservaId: Long): Result<ReservaPlan> {
        return try {
            val response = reservasPlanesApiService.confirmarReservaPlan(reservaId)
            if (response.isSuccessful) {
                response.body()?.let { reserva ->
                    Result.success(reserva)
                } ?: Result.failure(Exception("Error al confirmar reserva de plan"))
            } else {
                Result.failure(Exception("Error al confirmar reserva de plan: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelarReservaPlan(reservaId: Long, motivo: String): Result<ReservaPlan> {
        return try {
            val response = reservasPlanesApiService.cancelarReservaPlan(reservaId, motivo)
            if (response.isSuccessful) {
                response.body()?.let { reserva ->
                    Result.success(reserva)
                } ?: Result.failure(Exception("Error al cancelar reserva de plan"))
            } else {
                Result.failure(Exception("Error al cancelar reserva de plan: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun ReservasPlanesRepository.completarReservaPlan(reservaId: Long): Result<ReservaPlan> {
        return try {
            val response = reservasPlanesApiService.completarReservaPlan(reservaId)
            if (response.isSuccessful) {
                response.body()?.let { reserva ->
                    Result.success(reserva)
                } ?: Result.failure(Exception("Error al completar reserva de plan"))
            } else {
                Result.failure(Exception("Error al completar reserva de plan: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}