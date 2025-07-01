// MisReservasPlanesViewModel.kt
package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.ReservaPlan
import com.capachica.turismokotlin.data.repository.ReservasPlanesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MisReservasPlanesViewModel @Inject constructor(
    private val reservasPlanesRepository: ReservasPlanesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MisReservasPlanesUiState())
    val uiState: StateFlow<MisReservasPlanesUiState> = _uiState.asStateFlow()

    fun loadMisReservasPlanes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            reservasPlanesRepository.getMisReservasPlanes()
                .onSuccess { reservas ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        reservasPlanes = reservas
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al cargar reservas"
                    )
                }
        }
    }

    fun cancelarReservaPlan(reservaId: Long, motivo: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOperating = true, error = null)

            reservasPlanesRepository.cancelarReservaPlan(reservaId, motivo)
                .onSuccess { reservaActualizada ->
                    val reservasActualizadas = _uiState.value.reservasPlanes.map { reserva ->
                        if (reserva.id == reservaId) reservaActualizada else reserva
                    }
                    _uiState.value = _uiState.value.copy(
                        isOperating = false,
                        reservasPlanes = reservasActualizadas,
                        successMessage = "Reserva cancelada exitosamente"
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isOperating = false,
                        error = exception.message ?: "Error al cancelar la reserva"
                    )
                }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}

data class MisReservasPlanesUiState(
    val isLoading: Boolean = false,
    val isOperating: Boolean = false,
    val reservasPlanes: List<ReservaPlan> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)