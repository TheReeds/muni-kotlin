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
class CrearServicioViewModel @Inject constructor(
    private val serviciosRepository: ServiciosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CrearServicioUiState())
    val uiState: StateFlow<CrearServicioUiState> = _uiState.asStateFlow()

    fun crearServicio(
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

            val request = CrearServicioRequest(
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

            serviciosRepository.crearServicio(request)
                .onSuccess { servicio ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        servicioCreado = servicio,
                        successMessage = "Servicio creado exitosamente"
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

data class CrearServicioUiState(
    val isLoading: Boolean = false,
    val servicioCreado: Servicio? = null,
    val error: String? = null,
    val successMessage: String? = null
)