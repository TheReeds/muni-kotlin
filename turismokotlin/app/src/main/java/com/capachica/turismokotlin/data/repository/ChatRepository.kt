package com.capachica.turismokotlin.data.repository

import com.capachica.turismokotlin.data.model.Conversacion
import com.capachica.turismokotlin.data.model.EnviarMensajeRequest
import com.capachica.turismokotlin.data.model.MensajeChat
import com.capachica.turismokotlin.network.api.ChatApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val chatApiService: ChatApiService
) {

    suspend fun getConversaciones(): Result<List<Conversacion>> {
        return try {
            val response = chatApiService.getConversaciones()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al cargar conversaciones: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getConversacion(conversacionId: Long): Result<Conversacion> {
        return try {
            val response = chatApiService.getConversacion(conversacionId)
            if (response.isSuccessful) {
                response.body()?.let { conversacion ->
                    Result.success(conversacion)
                } ?: Result.failure(Exception("Conversación no encontrada"))
            } else {
                Result.failure(Exception("Error al cargar conversación: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMensajes(conversacionId: Long, pagina: Int = 0): Result<List<MensajeChat>> {
        return try {
            val response = chatApiService.getMensajes(conversacionId, pagina)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al cargar mensajes: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun iniciarConversacion(
        emprendedorId: Long,
        reservaId: Long? = null
    ): Result<Conversacion> {
        return try {
            val response = chatApiService.iniciarConversacion(emprendedorId, reservaId)
            if (response.isSuccessful) {
                response.body()?.let { conversacion ->
                    Result.success(conversacion)
                } ?: Result.failure(Exception("Error al crear conversación"))
            } else {
                Result.failure(Exception("Error al iniciar conversación: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun enviarMensaje(request: EnviarMensajeRequest): Result<MensajeChat> {
        return try {
            val response = chatApiService.enviarMensaje(request)
            if (response.isSuccessful) {
                response.body()?.let { mensaje ->
                    Result.success(mensaje)
                } ?: Result.failure(Exception("Error al enviar mensaje"))
            } else {
                Result.failure(Exception("Error al enviar mensaje: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun marcarComoLeido(conversacionId: Long): Result<Unit> {
        return try {
            val response = chatApiService.marcarComoLeido(conversacionId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al marcar como leído: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cerrarConversacion(conversacionId: Long): Result<Conversacion> {
        return try {
            val response = chatApiService.cerrarConversacion(conversacionId)
            if (response.isSuccessful) {
                response.body()?.let { conversacion ->
                    Result.success(conversacion)
                } ?: Result.failure(Exception("Error al cerrar conversación"))
            } else {
                Result.failure(Exception("Error al cerrar conversación: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}