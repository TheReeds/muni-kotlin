package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.CreateReservaPlanRequest
import com.capachica.turismokotlin.data.model.MetodoPago
import com.capachica.turismokotlin.data.model.ReservaPlan
import com.capachica.turismokotlin.data.model.ServicioPersonalizadoRequest
import com.capachica.turismokotlin.data.repository.ReservasPlanesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanReservaViewModel @Inject constructor(
    private val reservasPlanesRepository: ReservasPlanesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlanReservaUiState())
    val uiState: StateFlow<PlanReservaUiState> = _uiState.asStateFlow()

    fun crearReservaPlan(
        planId: Long,
        numeroPersonas: Int,
        fechaInicio: String,
        observaciones: String? = null,
        solicitudesEspeciales: String? = null,
        contactoEmergencia: String? = null,
        telefonoEmergencia: String? = null,
        metodoPago: Any? = MetodoPago.EFECTIVO,
        serviciosPersonalizados: List<ServicioPersonalizadoRequest> = emptyList()
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val request = CreateReservaPlanRequest(
                planId = planId,
                fechaInicio = fechaInicio,
                numeroPersonas = numeroPersonas,
                observaciones = observaciones,
                solicitudesEspeciales = solicitudesEspeciales,
                contactoEmergencia = contactoEmergencia,
                telefonoEmergencia = telefonoEmergencia,
                metodoPago = metodoPago,
                serviciosPersonalizados = serviciosPersonalizados
            )

            reservasPlanesRepository.createReservaPlan(request)
                .onSuccess { reserva ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        reservaCreada = reserva,
                        successMessage = "Reserva creada exitosamente con código: ${reserva.codigoReserva}"
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al crear la reserva"
                    )
                }
        }
    }

    fun confirmarReserva(reservaId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            reservasPlanesRepository.confirmarReservaPlan(reservaId)
                .onSuccess { reserva ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        reservaCreada = reserva,
                        successMessage = "Reserva confirmada exitosamente"
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al confirmar la reserva"
                    )
                }
        }
    }

    fun cancelarReserva(reservaId: Long, motivo: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            reservasPlanesRepository.cancelarReservaPlan(reservaId, motivo)
                .onSuccess { reserva ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        reservaCreada = reserva,
                        successMessage = "Reserva cancelada exitosamente"
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al cancelar la reserva"
                    )
                }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}

data class PlanReservaUiState(
    val isLoading: Boolean = false,
    val reservaCreada: ReservaPlan? = null,
    val error: String? = null,
    val successMessage: String? = null
)