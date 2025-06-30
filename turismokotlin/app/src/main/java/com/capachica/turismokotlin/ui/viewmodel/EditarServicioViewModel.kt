package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.*
import com.capachica.turismokotlin.data.repository.ServiciosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditarServicioViewModel @Inject constructor(
    private val serviciosRepository: ServiciosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditarServicioUiState())
    val uiState: StateFlow<EditarServicioUiState> = _uiState.asStateFlow()

    fun loadServicio(servicioId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingServicio = true, error = null)

            serviciosRepository.getServicioById(servicioId)
                .onSuccess { servicio ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingServicio = false,
                        servicio = servicio
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingServicio = false,
                        error = exception.message
                    )
                }
        }
    }

    fun actualizarServicio(
        servicioId: Long,
        nombre: String,
        descripcion: String,
        precio: Double,
        duracionHoras: Int,
        capacidadMaxima: Int,
        tipo: TipoServicio,
        ubicacion: String,
        latitud: Double?,
        longitud: Double?,
        requisitos: String?,
        incluye: String?,
        noIncluye: String?,
        imagenUrl: String?
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val request = ActualizarServicioRequest(
                nombre = nombre,
                descripcion = descripcion,
                precio = precio,
                duracionHoras = duracionHoras,
                capacidadMaxima = capacidadMaxima,
                tipo = tipo,
                ubicacion = ubicacion,
                latitud = latitud,
                longitud = longitud,
                requisitos = requisitos,
                incluye = incluye,
                noIncluye = noIncluye,
                imagenUrl = imagenUrl
            )

            serviciosRepository.actualizarServicio(servicioId, request)
                .onSuccess { servicio ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        servicioActualizado = servicio,
                        successMessage = "Servicio actualizado exitosamente"
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

    fun eliminarServicio(servicioId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            serviciosRepository.eliminarServicio(servicioId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        servicioEliminado = true,
                        successMessage = "Servicio eliminado exitosamente"
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

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}

data class EditarServicioUiState(
    val isLoadingServicio: Boolean = false,
    val isLoading: Boolean = false,
    val servicio: Servicio? = null,
    val servicioActualizado: Servicio? = null,
    val servicioEliminado: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)