package com.capachica.turismokotlin.data.repository

import com.capachica.turismokotlin.data.api.ApiService
import com.capachica.turismokotlin.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChatRepository(private val apiService: ApiService) {

    fun getConversaciones(): Flow<Result<List<ConversacionResponse>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getConversaciones()
            if (response.isSuccessful) {
                response.body()?.let { conversaciones ->
                    emit(Result.Success(conversaciones))
                } ?: emit(Result.Error("No se encontraron conversaciones"))
            } else {
                emit(Result.Error("Error al obtener conversaciones: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun getMensajesNoLeidos(): Flow<Result<MensajesNoLeidosResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getMensajesNoLeidos()
            if (response.isSuccessful) {
                response.body()?.let { mensajes ->
                    emit(Result.Success(mensajes))
                } ?: emit(Result.Error("Error al obtener mensajes no leídos"))
            } else {
                emit(Result.Error("Error al obtener mensajes no leídos: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun enviarMensaje(request: MensajeRequest): Flow<Result<MensajeResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.enviarMensaje(request)
            if (response.isSuccessful) {
                response.body()?.let { mensaje ->
                    emit(Result.Success(mensaje))
                } ?: emit(Result.Error("Error al enviar mensaje"))
            } else {
                emit(Result.Error("Error al enviar mensaje: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun iniciarConversacionCarrito(request: IniciarConversacionCarritoRequest): Flow<Result<ConversacionResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.iniciarConversacionCarrito(request)
            if (response.isSuccessful) {
                response.body()?.let { conversacion ->
                    emit(Result.Success(conversacion))
                } ?: emit(Result.Error("Error al iniciar conversación"))
            } else {
                emit(Result.Error("Error al iniciar conversación: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun enviarMensajeRapido(
        reservaCarritoId: Long,
        request: MensajeRapidoRequest
    ): Flow<Result<List<MensajeResponse>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.enviarMensajeRapido(reservaCarritoId, request)
            if (response.isSuccessful) {
                response.body()?.let { mensajes ->
                    emit(Result.Success(mensajes))
                } ?: emit(Result.Error("Error al enviar mensaje rápido"))
            } else {
                emit(Result.Error("Error al enviar mensaje rápido: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun getConversacionesPorReserva(reservaCarritoId: Long): Flow<Result<List<ConversacionResponse>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getConversacionesPorReserva(reservaCarritoId)
            if (response.isSuccessful) {
                response.body()?.let { conversaciones ->
                    emit(Result.Success(conversaciones))
                } ?: emit(Result.Error("No se encontraron conversaciones para esta reserva"))
            } else {
                emit(Result.Error("Error al obtener conversaciones: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }
}