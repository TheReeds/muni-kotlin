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
    @POST("reservas-planes/crear")
    suspend fun createReservaPlan(@Body request: CreateReservaPlanRequest): Response<ReservaPlan>

    @GET("reservas-planes/mis-reservas")
    suspend fun getMisReservasPlanes(): Response<List<ReservaPlan>>

    @GET("reservas-planes/{id}")
    suspend fun getReservaPlanById(@Path("id") reservaId: Long): Response<ReservaPlan>

    @PATCH("reservas-planes/{id}/confirmar")
    suspend fun confirmarReservaPlan(@Path("id") reservaId: Long): Response<ReservaPlan>

    @PATCH("reservas-planes/{id}/cancelar")
    suspend fun cancelarReservaPlan(
        @Path("id") reservaId: Long,
        @Query("motivo") motivo: String
    ): Response<ReservaPlan>

    @PATCH("reservas-planes/{id}/completar")
    suspend fun completarReservaPlan(@Path("id") reservaId: Long): Response<ReservaPlan>

    @GET("reservas-planes/estado/{estado}")
    suspend fun getReservasPlanByEstado(@Path("estado") estado: String): Response<List<ReservaPlan>>
}