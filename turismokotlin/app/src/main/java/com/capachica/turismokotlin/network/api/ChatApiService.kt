package com.capachica.turismokotlin.network.api

import com.capachica.turismokotlin.data.model.Conversacion
import com.capachica.turismokotlin.data.model.EnviarMensajeRequest
import com.capachica.turismokotlin.data.model.MensajeChat
import retrofit2.Response
import retrofit2.http.*

interface ChatApiService {

    @GET("chat/conversaciones")
    suspend fun getConversaciones(): Response<List<Conversacion>>

    @GET("chat/conversacion/{conversacionId}")
    suspend fun getConversacion(@Path("conversacionId") conversacionId: Long): Response<Conversacion>

    @GET("chat/conversacion/{conversacionId}/mensajes")
    suspend fun getMensajes(
        @Path("conversacionId") conversacionId: Long,
        @Query("pagina") pagina: Int = 0,
        @Query("tamaño") tamaño: Int = 20
    ): Response<List<MensajeChat>>

    // Endpoint corregido para iniciar conversación
    @POST("chat/conversacion/iniciar")
    suspend fun iniciarConversacion(
        @Query("emprendedorId") emprendedorId: Long,
        @Query("reservaId") reservaId: Long? = null
    ): Response<Conversacion>

    @POST("chat/mensaje")
    suspend fun enviarMensaje(@Body request: EnviarMensajeRequest): Response<MensajeChat>

    @PATCH("chat/conversacion/{conversacionId}/marcar-leido")
    suspend fun marcarComoLeido(@Path("conversacionId") conversacionId: Long): Response<Unit>

    @PATCH("chat/conversacion/{conversacionId}/cerrar")
    suspend fun cerrarConversacion(@Path("conversacionId") conversacionId: Long): Response<Conversacion>
}