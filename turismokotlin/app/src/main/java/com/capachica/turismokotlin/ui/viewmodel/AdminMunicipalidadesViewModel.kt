package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.CreateMunicipalidadRequest
import com.capachica.turismokotlin.data.model.MunicipalidadDetallada
import com.capachica.turismokotlin.data.model.UpdateMunicipalidadRequest
import com.capachica.turismokotlin.data.repository.AdminMunicipalidadesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminMunicipalidadesViewModel @Inject constructor(
    private val adminMunicipalidadesRepository: AdminMunicipalidadesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminMunicipalidadesUiState())
    val uiState: StateFlow<AdminMunicipalidadesUiState> = _uiState.asStateFlow()

    init {
        loadMunicipalidades()
    }

    fun loadMunicipalidades() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            adminMunicipalidadesRepository.getAllMunicipalidades()
                .onSuccess { municipalidades ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        municipalidades = municipalidades
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

    fun createMunicipalidad(request: CreateMunicipalidadRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreating = true, error = null)

            adminMunicipalidadesRepository.createMunicipalidad(request)
                .onSuccess { municipalidad ->
                    _uiState.value = _uiState.value.copy(
                        isCreating = false,
                        successMessage = "Municipalidad creada exitosamente"
                    )
                    loadMunicipalidades() // Recargar lista
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isCreating = false,
                        error = exception.message
                    )
                }
        }
    }

    fun updateMunicipalidad(municipalidadId: Long, request: UpdateMunicipalidadRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdating = true, error = null)

            adminMunicipalidadesRepository.updateMunicipalidad(municipalidadId, request)
                .onSuccess { municipalidad ->
                    _uiState.value = _uiState.value.copy(
                        isUpdating = false,
                        successMessage = "Municipalidad actualizada exitosamente"
                    )
                    loadMunicipalidades() // Recargar lista
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isUpdating = false,
                        error = exception.message
                    )
                }
        }
    }

    fun deleteMunicipalidad(municipalidadId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(error = null)

            adminMunicipalidadesRepository.deleteMunicipalidad(municipalidadId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Municipalidad eliminada exitosamente"
                    )
                    loadMunicipalidades() // Recargar lista
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(error = exception.message)
                }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}

data class AdminMunicipalidadesUiState(
    val isLoading: Boolean = false,
    val isCreating: Boolean = false,
    val isUpdating: Boolean = false,
    val municipalidades: List<MunicipalidadDetallada> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)