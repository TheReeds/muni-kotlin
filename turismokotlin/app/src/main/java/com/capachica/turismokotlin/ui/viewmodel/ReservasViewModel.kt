package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.ReservaCarrito
import com.capachica.turismokotlin.data.repository.ReservasCarritoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservasViewModel @Inject constructor(
    private val reservasRepository: ReservasCarritoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReservasUiState())
    val uiState: StateFlow<ReservasUiState> = _uiState.asStateFlow()

    fun loadMisReservas() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            reservasRepository.getMisReservas()
                .onSuccess { reservas ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        reservas = reservas
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

    fun cancelarReserva(reservaId: Long, motivo: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOperating = true, error = null)

            reservasRepository.cancelarReserva(reservaId, motivo)
                .onSuccess { reservaActualizada ->
                    // Actualizar la lista de reservas
                    val reservasActualizadas = _uiState.value.reservas.map { reserva ->
                        if (reserva.id == reservaId) reservaActualizada else reserva
                    }
                    _uiState.value = _uiState.value.copy(
                        isOperating = false,
                        reservas = reservasActualizadas,
                        successMessage = "Reserva cancelada exitosamente"
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isOperating = false,
                        error = exception.message
                    )
                }
        }
    }

    fun confirmarReserva(reservaId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOperating = true, error = null)

            reservasRepository.confirmarReserva(reservaId)
                .onSuccess { reservaActualizada ->
                    // Actualizar la lista de reservas
                    val reservasActualizadas = _uiState.value.reservas.map { reserva ->
                        if (reserva.id == reservaId) reservaActualizada else reserva
                    }
                    _uiState.value = _uiState.value.copy(
                        isOperating = false,
                        reservas = reservasActualizadas,
                        successMessage = "Reserva confirmada exitosamente"
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isOperating = false,
                        error = exception.message
                    )
                }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}

data class ReservasUiState(
    val isLoading: Boolean = false,
    val isOperating: Boolean = false,
    val reservas: List<ReservaCarrito> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)