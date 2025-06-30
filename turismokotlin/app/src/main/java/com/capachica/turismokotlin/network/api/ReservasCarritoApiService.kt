package com.capachica.turismokotlin.network.api

import com.capachica.turismokotlin.data.model.CreateReservaCarritoRequest
import com.capachica.turismokotlin.data.model.ReservaCarrito
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ReservasCarritoApiService {
    @POST("reservas-carrito/crear")
    suspend fun createReservaFromCart(@Body request: CreateReservaCarritoRequest): Response<ReservaCarrito>

    @GET("reservas-carrito/mis-reservas")
    suspend fun getMisReservas(): Response<List<ReservaCarrito>>

    @GET("reservas-carrito/{id}")
    suspend fun getReservaById(@Path("id") reservaId: Long): Response<ReservaCarrito>

    @PATCH("reservas-carrito/{id}/confirmar")
    suspend fun confirmarReserva(@Path("id") reservaId: Long): Response<ReservaCarrito>

    @PATCH("reservas-carrito/{id}/cancelar")
    suspend fun cancelarReserva(
        @Path("id") reservaId: Long,
        @Query("motivo") motivo: String
    ): Response<ReservaCarrito>

    @PATCH("reservas-carrito/{id}/completar")
    suspend fun completarReserva(@Path("id") reservaId: Long): Response<ReservaCarrito>

    @GET("reservas-carrito/estado/{estado}")
    suspend fun getReservasByEstado(@Path("estado") estado: String): Response<List<ReservaCarrito>>

    @GET("reservas-carrito/emprendedor/reservas")
    suspend fun getReservasEmprendedor(): Response<List<ReservaCarrito>>
}