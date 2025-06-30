package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.Emprendedor
import com.capachica.turismokotlin.data.model.Servicio
import com.capachica.turismokotlin.data.model.UpdateEmprendedorRequest
import com.capachica.turismokotlin.data.repository.AdminEmprendedoresRepository
import com.capachica.turismokotlin.data.repository.ServiciosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmprendedorDashboardViewModel @Inject constructor(
    private val adminEmprendedoresRepository: AdminEmprendedoresRepository,
    private val serviciosRepository: ServiciosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmprendedorDashboardUiState())
    val uiState: StateFlow<EmprendedorDashboardUiState> = _uiState.asStateFlow()

    init {
        loadMiEmprendedor()
    }

    fun loadMiEmprendedor() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val emprendedorDeferred = async { adminEmprendedoresRepository.getMiEmprendedor() }

            val emprendedorResult = emprendedorDeferred.await()

            emprendedorResult.onSuccess { emprendedor ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    miEmprendedor = emprendedor
                )
                // Cargar servicios del emprendedor
                loadMisServicios(emprendedor.id)
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = exception.message
                )
            }
        }
    }

    private fun loadMisServicios(emprendedorId: Long) {
        viewModelScope.launch {
            serviciosRepository.getServiciosByEmprendedor(emprendedorId)
                .onSuccess { servicios ->
                    _uiState.value = _uiState.value.copy(misServicios = servicios)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(error = exception.message)
                }
        }
    }

    fun updateMiEmprendedor(request: UpdateEmprendedorRequest) {
        viewModelScope.launch {
            val emprendedorId = _uiState.value.miEmprendedor?.id
            if (emprendedorId != null) {
                _uiState.value = _uiState.value.copy(isUpdating = true, error = null)

                adminEmprendedoresRepository.updateEmprendedor(emprendedorId, request)
                    .onSuccess { emprendedor ->
                        _uiState.value = _uiState.value.copy(
                            isUpdating = false,
                            miEmprendedor = emprendedor,
                            successMessage = "Información actualizada exitosamente"
                        )
                    }
                    .onFailure { exception ->
                        _uiState.value = _uiState.value.copy(
                            isUpdating = false,
                            error = exception.message
                        )
                    }
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}

data class EmprendedorDashboardUiState(
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val miEmprendedor: Emprendedor? = null,
    val misServicios: List<Servicio> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)