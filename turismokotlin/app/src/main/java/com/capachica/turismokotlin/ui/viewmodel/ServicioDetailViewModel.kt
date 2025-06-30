package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.Servicio
import com.capachica.turismokotlin.data.repository.ServiciosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServicioDetailViewModel @Inject constructor(
    private val serviciosRepository: ServiciosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServicioDetailUiState())
    val uiState: StateFlow<ServicioDetailUiState> = _uiState.asStateFlow()

    fun loadServicioDetails(servicioId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            serviciosRepository.getServicioById(servicioId)
                .onSuccess { servicio ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        servicio = servicio
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

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class ServicioDetailUiState(
    val isLoading: Boolean = false,
    val servicio: Servicio? = null,
    val error: String? = null
)