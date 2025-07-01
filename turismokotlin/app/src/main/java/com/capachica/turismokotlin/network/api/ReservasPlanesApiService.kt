package com.capachica.turismokotlin.network.api

import com.capachica.turismokotlin.data.model.CreateReservaPlanRequest
import com.capachica.turismokotlin.data.model.ReservaPlan
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ReservasPlanesApiService {

    // Usar el endpoint correcto de la documentación
    @POST("reservas")
    suspend fun createReservaPlan(@Body request: CreateReservaPlanRequest): Response<ReservaPlan>

    @GET("reservas/mis-reservas")
    suspend fun getMisReservasPlanes(): Response<List<ReservaPlan>>

    @GET("reservas/{id}")
    suspend fun getReservaPlanById(@Path("id") reservaId: Long): Response<ReservaPlan>

    @GET("reservas/plan/{planId}")
    suspend fun getReservasByPlan(@Path("planId") planId: Long): Response<List<ReservaPlan>>

    @PATCH("reservas/{id}/confirmar")
    suspend fun confirmarReservaPlan(@Path("id") reservaId: Long): Response<ReservaPlan>

    @PATCH("reservas/{id}/cancelar")
    suspend fun cancelarReservaPlan(
        @Path("id") reservaId: Long,
        @Query("motivo") motivo: String
    ): Response<ReservaPlan>

    @PATCH("reservas/{id}/completar")
    suspend fun completarReservaPlan(@Path("id") reservaId: Long): Response<ReservaPlan>
}