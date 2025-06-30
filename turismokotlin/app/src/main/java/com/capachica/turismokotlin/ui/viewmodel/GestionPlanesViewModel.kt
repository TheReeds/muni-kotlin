package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.EstadoPlan
import com.capachica.turismokotlin.data.model.Plan
import com.capachica.turismokotlin.data.model.ReservaPlan
import com.capachica.turismokotlin.data.repository.PlanesRepository
import com.capachica.turismokotlin.data.repository.ReservasPlanesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GestionPlanesViewModel @Inject constructor(
    private val planesRepository: PlanesRepository,
    private val reservasPlanesRepository: ReservasPlanesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GestionPlanesUiState())
    val uiState: StateFlow<GestionPlanesUiState> = _uiState.asStateFlow()

    init {
        loadMisPlanes()
        loadReservasPlanes()
    }

    fun loadMisPlanes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingPlanes = true, error = null)

            planesRepository.getMisPlanes()
                .onSuccess { planes ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingPlanes = false,
                        misPlanes = planes
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingPlanes = false,
                        error = exception.message
                    )
                }
        }
    }

    fun loadReservasPlanes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingReservas = true, error = null)

            reservasPlanesRepository.getMisReservasPlanes()
                .onSuccess { reservas ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingReservas = false,
                        reservasPlanes = reservas
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

    fun cambiarEstadoPlan(planId: Long, nuevoEstado: EstadoPlan) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOperating = true)

            planesRepository.cambiarEstadoPlan(planId, nuevoEstado)
                .onSuccess { planActualizado ->
                    val planesActualizados = _uiState.value.misPlanes.map { plan ->
                        if (plan.id == planId) planActualizado else plan
                    }
                    _uiState.value = _uiState.value.copy(
                        isOperating = false,
                        misPlanes = planesActualizados,
                        successMessage = "Estado del plan actualizado"
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

    fun confirmarReservaPlan(reservaId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOperating = true)

            reservasPlanesRepository.confirmarReservaPlan(reservaId)
                .onSuccess { reservaActualizada ->
                    val reservasActualizadas = _uiState.value.reservasPlanes.map { reserva ->
                        if (reserva.id == reservaId) reservaActualizada else reserva
                    }
                    _uiState.value = _uiState.value.copy(
                        isOperating = false,
                        reservasPlanes = reservasActualizadas,
                        successMessage = "Reserva de plan confirmada"
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

data class GestionPlanesUiState(
    val isLoadingPlanes: Boolean = false,
    val isLoadingReservas: Boolean = false,
    val isOperating: Boolean = false,
    val misPlanes: List<Plan> = emptyList(),
    val reservasPlanes: List<ReservaPlan> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)