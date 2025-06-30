// GestionServiciosViewModel.kt
package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.*
import com.capachica.turismokotlin.data.repository.ServiciosRepository
import com.capachica.turismokotlin.data.repository.ReservasCarritoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GestionServiciosViewModel @Inject constructor(
    private val serviciosRepository: ServiciosRepository,
    private val reservasRepository: ReservasCarritoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GestionServiciosUiState())
    val uiState: StateFlow<GestionServiciosUiState> = _uiState.asStateFlow()

    init {
        loadMisServicios()
        loadReservasEmprendedor()
    }

    fun loadMisServicios() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingServicios = true, error = null)

            serviciosRepository.getMisServicios()
                .onSuccess { servicios ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingServicios = false,
                        misServicios = servicios
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingServicios = false,
                        error = exception.message
                    )
                }
        }
    }

    fun loadReservasEmprendedor() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingReservas = true, error = null)

            reservasRepository.getReservasEmprendedor()
                .onSuccess { reservas ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingReservas = false,
                        reservasEmprendedor = reservas
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingReservas = false,
                        error = exception.message
                    )
                }
        }
    }

    fun confirmarReserva(reservaId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOperating = true)

            reservasRepository.confirmarReserva(reservaId)
                .onSuccess { reservaActualizada ->
                    val reservasActualizadas = _uiState.value.reservasEmprendedor.map { reserva ->
                        if (reserva.id == reservaId) reservaActualizada else reserva
                    }
                    _uiState.value = _uiState.value.copy(
                        isOperating = false,
                        reservasEmprendedor = reservasActualizadas,
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

    fun completarReserva(reservaId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOperating = true)

            reservasRepository.completarReserva(reservaId)
                .onSuccess { reservaActualizada ->
                    val reservasActualizadas = _uiState.value.reservasEmprendedor.map { reserva ->
                        if (reserva.id == reservaId) reservaActualizada else reserva
                    }
                    _uiState.value = _uiState.value.copy(
                        isOperating = false,
                        reservasEmprendedor = reservasActualizadas,
                        successMessage = "Reserva completada exitosamente"
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

data class GestionServiciosUiState(
    val isLoadingServicios: Boolean = false,
    val isLoadingReservas: Boolean = false,
    val isOperating: Boolean = false,
    val misServicios: List<Servicio> = emptyList(),
    val reservasEmprendedor: List<ReservaCarrito> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)