package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.*
import com.capachica.turismokotlin.data.repository.ChatRepository
import com.capachica.turismokotlin.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val repository: ChatRepository) : ViewModel() {

    private val _conversacionesState = MutableStateFlow<Result<List<ConversacionResponse>>>(Result.Loading)
    val conversacionesState: StateFlow<Result<List<ConversacionResponse>>> = _conversacionesState

    private val _mensajesNoLeidosState = MutableStateFlow<Result<MensajesNoLeidosResponse>>(Result.Loading)
    val mensajesNoLeidosState: StateFlow<Result<MensajesNoLeidosResponse>> = _mensajesNoLeidosState

    private val _enviarMensajeState = MutableStateFlow<Result<MensajeResponse>?>(null)
    val enviarMensajeState: StateFlow<Result<MensajeResponse>?> = _enviarMensajeState

    private val _iniciarConversacionState = MutableStateFlow<Result<ConversacionResponse>?>(null)
    val iniciarConversacionState: StateFlow<Result<ConversacionResponse>?> = _iniciarConversacionState

    private val _mensajeRapidoState = MutableStateFlow<Result<List<MensajeResponse>>?>(null)
    val mensajeRapidoState: StateFlow<Result<List<MensajeResponse>>?> = _mensajeRapidoState

    private val _conversacionesPorReservaState = MutableStateFlow<Result<List<ConversacionResponse>>?>(null)
    val conversacionesPorReservaState: StateFlow<Result<List<ConversacionResponse>>?> = _conversacionesPorReservaState

    // Para manejo de mensajes en tiempo real
    private val _mensajesConversacionActual = MutableStateFlow<List<MensajeResponse>>(emptyList())
    val mensajesConversacionActual: StateFlow<List<MensajeResponse>> = _mensajesConversacionActual

    private val _conversacionActual = MutableStateFlow<ConversacionResponse?>(null)
    val conversacionActual: StateFlow<ConversacionResponse?> = _conversacionActual

    init {
        cargarConversaciones()
        cargarMensajesNoLeidos()
    }

    fun cargarConversaciones() {
        viewModelScope.launch {
            repository.getConversaciones().collect { result ->
                _conversacionesState.value = result
            }
        }
    }

    fun cargarMensajesNoLeidos() {
        viewModelScope.launch {
            repository.getMensajesNoLeidos().collect { result ->
                _mensajesNoLeidosState.value = result
            }
        }
    }

    fun enviarMensaje(conversacionId: Long, contenido: String, tipoMensaje: TipoMensaje = TipoMensaje.TEXTO) {
        viewModelScope.launch {
            _enviarMensajeState.value = Result.Loading
            val request = MensajeRequest(
                conversacionId = conversacionId,
                contenido = contenido,
                tipoMensaje = tipoMensaje
            )
            
            repository.enviarMensaje(request).collect { result ->
                _enviarMensajeState.value = result
                
                // Si el mensaje se envió correctamente, agregarlo a la lista local
                if (result is Result.Success) {
                    val mensajesActuales = _mensajesConversacionActual.value.toMutableList()
                    mensajesActuales.add(result.data)
                    _mensajesConversacionActual.value = mensajesActuales
                    
                    // Recargar conversaciones para actualizar último mensaje
                    cargarConversaciones()
                }
            }
        }
    }

    fun iniciarConversacionConEmprendedor(emprendedorId: Long, mensaje: String) {
        viewModelScope.launch {
            _iniciarConversacionState.value = Result.Loading
            val request = IniciarConversacionCarritoRequest(
                emprendedorId = emprendedorId,
                mensaje = mensaje
            )
            
            repository.iniciarConversacionCarrito(request).collect { result ->
                _iniciarConversacionState.value = result
                
                // Si se creó la conversación, recargar lista de conversaciones
                if (result is Result.Success) {
                    cargarConversaciones()
                    _conversacionActual.value = result.data
                }
            }
        }
    }

    fun enviarMensajeRapido(reservaCarritoId: Long, mensaje: String) {
        viewModelScope.launch {
            _mensajeRapidoState.value = Result.Loading
            val request = MensajeRapidoRequest(mensaje = mensaje)
            
            repository.enviarMensajeRapido(reservaCarritoId, request).collect { result ->
                _mensajeRapidoState.value = result
                
                // Si se enviaron mensajes, actualizarlos en la conversación actual
                if (result is Result.Success) {
                    _mensajesConversacionActual.value = result.data
                    cargarConversaciones()
                }
            }
        }
    }

    fun cargarConversacionesPorReserva(reservaCarritoId: Long) {
        viewModelScope.launch {
            repository.getConversacionesPorReserva(reservaCarritoId).collect { result ->
                _conversacionesPorReservaState.value = result
            }
        }
    }

    fun seleccionarConversacion(conversacion: ConversacionResponse) {
        _conversacionActual.value = conversacion
        // Aquí podrías cargar los mensajes de la conversación si tuvieras un endpoint para eso
        // Por ahora, inicializamos con el último mensaje
        _mensajesConversacionActual.value = if (conversacion.ultimoMensaje != null) {
            listOf(conversacion.ultimoMensaje)
        } else {
            emptyList()
        }
    }

    fun limpiarConversacionActual() {
        _conversacionActual.value = null
        _mensajesConversacionActual.value = emptyList()
    }

    fun clearStates() {
        _enviarMensajeState.value = null
        _iniciarConversacionState.value = null
        _mensajeRapidoState.value = null
        _conversacionesPorReservaState.value = null
    }

    // Función para simular mensajes recibidos (en una implementación real, esto vendría de WebSockets o polling)
    fun agregarMensajeRecibido(mensaje: MensajeResponse) {
        val mensajesActuales = _mensajesConversacionActual.value.toMutableList()
        mensajesActuales.add(mensaje)
        _mensajesConversacionActual.value = mensajesActuales
        
        // Actualizar contador de no leídos
        cargarMensajesNoLeidos()
        cargarConversaciones()
    }

    // Función para marcar mensajes como leídos (simular)
    fun marcarMensajesComoLeidos(conversacionId: Long) {
        // En una implementación real, esto haría una llamada al backend
        // Por ahora, solo recargamos los datos
        cargarMensajesNoLeidos()
        cargarConversaciones()
    }
}