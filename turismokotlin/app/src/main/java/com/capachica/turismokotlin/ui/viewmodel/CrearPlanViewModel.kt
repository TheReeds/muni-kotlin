// CrearPlanViewModel.kt
package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.*
import com.capachica.turismokotlin.data.repository.AdminMunicipalidadesRepository
import com.capachica.turismokotlin.data.repository.PlanesRepository
import com.capachica.turismokotlin.data.repository.ServiciosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CrearPlanViewModel @Inject constructor(
    private val planesRepository: PlanesRepository,
    private val serviciosRepository: ServiciosRepository,
    private val municipalidadesRepository: AdminMunicipalidadesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CrearPlanUiState())
    val uiState: StateFlow<CrearPlanUiState> = _uiState.asStateFlow()

    fun loadMunicipalidades() {
        viewModelScope.launch {
            municipalidadesRepository.getAllMunicipalidades()
                .onSuccess { municipalidades ->
                    _uiState.value = _uiState.value.copy(municipalidades = municipalidades)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(error = exception.message)
                }
        }
    }

    fun loadServiciosDisponibles() {
        viewModelScope.launch {
            serviciosRepository.getAllServicios()
                .onSuccess { servicios ->
                    _uiState.value = _uiState.value.copy(serviciosDisponibles = servicios)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(error = exception.message)
                }
        }
    }

    fun crearPlan(
        nombre: String,
        descripcion: String,
        duracionDias: Int,
        capacidadMaxima: Int,
        nivelDificultad: NivelDificultad,
        municipalidadId: Long,
        imagenPrincipalUrl: String?,
        itinerario: String?,
        incluye: String?,
        noIncluye: String?,
        recomendaciones: String?,
        requisitos: String?,
        servicios: List<ServicioPlanRequest>
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val request = CrearPlanRequest(
                nombre = nombre,
                descripcion = descripcion,
                duracionDias = duracionDias,
                capacidadMaxima = capacidadMaxima,
                nivelDificultad = nivelDificultad,
                municipalidadId = municipalidadId,
                imagenPrincipalUrl = imagenPrincipalUrl,
                itinerario = itinerario,
                incluye = incluye,
                noIncluye = noIncluye,
                recomendaciones = recomendaciones,
                requisitos = requisitos,
                servicios = servicios
            )

            planesRepository.crearPlan(request)
                .onSuccess { plan ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        planCreado = plan,
                        successMessage = "Plan creado exitosamente"
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

data class CrearPlanUiState(
    val isLoading: Boolean = false,
    val planCreado: Plan? = null,
    val municipalidades: List<MunicipalidadDetallada> = emptyList(),
    val serviciosDisponibles: List<Servicio> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)