package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.*
import com.capachica.turismokotlin.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _conversacionActual = MutableStateFlow<Conversacion?>(null)
    val conversacionActual: StateFlow<Conversacion?> = _conversacionActual.asStateFlow()

    private val _mensajes = MutableStateFlow<List<MensajeChat>>(emptyList())
    val mensajes: StateFlow<List<MensajeChat>> = _mensajes.asStateFlow()

    init {
        loadConversaciones()
    }

    fun loadConversaciones() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            chatRepository.getConversaciones()
                .onSuccess { conversaciones ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        conversaciones = conversaciones
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                }
        }
    }

    fun abrirConversacion(conversacionId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingMensajes = true)

            // Cargar detalles de la conversación
            chatRepository.getConversacion(conversacionId)
                .onSuccess { conversacion ->
                    _conversacionActual.value = conversacion
                    // Marcar como leído
                    marcarComoLeido(conversacionId)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(error = exception.message)
                }

            // Cargar mensajes
            loadMensajes(conversacionId)
        }
    }

    private fun loadMensajes(conversacionId: Long, pagina: Int = 0) {
        viewModelScope.launch {
            chatRepository.getMensajes(conversacionId, pagina)
                .onSuccess { nuevosMensajes ->
                    if (pagina == 0) {
                        _mensajes.value = nuevosMensajes
                    } else {
                        // Agregar mensajes más antiguos al principio
                        _mensajes.value = nuevosMensajes + _mensajes.value
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoadingMensajes = false,
                        puedeCargarMasMensajes = nuevosMensajes.size >= 20
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingMensajes = false,
                        error = exception.message
                    )
                }
        }
    }

    fun enviarMensaje(mensaje: String) {
        val conversacionId = _conversacionActual.value?.id ?: return
        if (mensaje.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isEnviandoMensaje = true)

            val request = EnviarMensajeRequest(
                conversacionId = conversacionId,
                mensaje = mensaje.trim(),
                tipo = TipoMensaje.TEXTO
            )

            chatRepository.enviarMensaje(request)
                .onSuccess { nuevoMensaje ->
                    // Agregar el nuevo mensaje a la lista
                    _mensajes.value = _mensajes.value + nuevoMensaje

                    // Actualizar la conversación actual
                    _conversacionActual.value = _conversacionActual.value?.let { conversacion ->
                        conversacion.copy(
                            ultimoMensaje = nuevoMensaje,
                            fechaUltimoMensaje = nuevoMensaje.fechaEnvio,
                            mensajesRecientes = conversacion.mensajesRecientes ?: emptyList()
                        )
                    }

                    _uiState.value = _uiState.value.copy(
                        isEnviandoMensaje = false,
                        successMessage = null
                    )

                    // Recargar lista de conversaciones para actualizar el último mensaje
                    loadConversaciones()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isEnviandoMensaje = false,
                        error = exception.message
                    )
                }
        }
    }

    // Método corregido para iniciar conversación
    fun iniciarConversacion(emprendedorId: Long, mensajeInicial: String, reservaId: Long? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Primero iniciar la conversación
            chatRepository.iniciarConversacion(emprendedorId, reservaId)
                .onSuccess { nuevaConversacion ->
                    _conversacionActual.value = nuevaConversacion

                    // Luego enviar el mensaje inicial si no está vacío
                    if (mensajeInicial.isNotBlank()) {
                        val request = EnviarMensajeRequest(
                            conversacionId = nuevaConversacion.id,
                            mensaje = mensajeInicial.trim(),
                            tipo = TipoMensaje.TEXTO
                        )

                        chatRepository.enviarMensaje(request)
                            .onSuccess { mensajeEnviado ->
                                _mensajes.value = listOf(mensajeEnviado)
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    conversacionCreada = nuevaConversacion,
                                    successMessage = "Conversación iniciada"
                                )
                            }
                            .onFailure { exception ->
                                // Aunque falló el mensaje, la conversación se creó
                                _mensajes.value = emptyList()
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    conversacionCreada = nuevaConversacion,
                                    error = "Conversación creada pero error al enviar mensaje: ${exception.message}"
                                )
                            }
                    } else {
                        // Solo crear conversación sin mensaje inicial
                        _mensajes.value = emptyList()
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            conversacionCreada = nuevaConversacion,
                            successMessage = "Conversación iniciada"
                        )
                    }

                    // Recargar lista de conversaciones
                    loadConversaciones()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                }
        }
    }

    fun cargarMasMensajes() {
        val conversacionId = _conversacionActual.value?.id ?: return
        val paginaActual = (_mensajes.value.size / 20)

        if (_uiState.value.puedeCargarMasMensajes && !_uiState.value.isLoadingMensajes) {
            loadMensajes(conversacionId, paginaActual)
        }
    }

    private fun marcarComoLeido(conversacionId: Long) {
        viewModelScope.launch {
            chatRepository.marcarComoLeido(conversacionId)
                .onSuccess {
                    // Actualizar la conversación en la lista
                    val conversacionesActualizadas = _uiState.value.conversaciones.map { conversacion ->
                        if (conversacion.id == conversacionId) {
                            conversacion.copy(mensajesNoLeidos = 0)
                        } else {
                            conversacion
                        }
                    }

                    _uiState.value = _uiState.value.copy(conversaciones = conversacionesActualizadas)
                }
        }
    }

    fun cerrarConversacion() {
        val conversacionId = _conversacionActual.value?.id ?: return

        viewModelScope.launch {
            chatRepository.cerrarConversacion(conversacionId)
                .onSuccess { conversacionCerrada ->
                    _conversacionActual.value = conversacionCerrada
                    loadConversaciones() // Recargar lista
                }
        }
    }

    fun limpiarConversacionActual() {
        _conversacionActual.value = null
        _mensajes.value = emptyList()
        _uiState.value = _uiState.value.copy(
            conversacionCreada = null,
            isLoadingMensajes = false,
            puedeCargarMasMensajes = false
        )
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}

data class ChatUiState(
    val isLoading: Boolean = false,
    val isLoadingMensajes: Boolean = false,
    val isEnviandoMensaje: Boolean = false,
    val conversaciones: List<Conversacion> = emptyList(),
    val conversacionCreada: Conversacion? = null,
    val puedeCargarMasMensajes: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)