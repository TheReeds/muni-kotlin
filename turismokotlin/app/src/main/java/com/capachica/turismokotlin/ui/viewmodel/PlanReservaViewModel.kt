package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.CreateReservaPlanRequest
import com.capachica.turismokotlin.data.model.MetodoPago
import com.capachica.turismokotlin.data.model.ReservaPlan
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
        cantidad: Int,
        fechaInicio: String,
        observaciones: String? = null,
        contactoEmergencia: String? = null,
        telefonoEmergencia: String? = null,
        metodoPago: MetodoPago = MetodoPago.EFECTIVO
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val request = CreateReservaPlanRequest(
                planId = planId,
                cantidad = cantidad,
                fechaInicio = fechaInicio,
                observaciones = observaciones,
                contactoEmergencia = contactoEmergencia,
                telefonoEmergencia = telefonoEmergencia,
                metodoPago = metodoPago
            )

            reservasPlanesRepository.createReservaPlan(request)
                .onSuccess { reserva ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        reservaCreada = reserva,
                        successMessage = "Reserva de plan creada exitosamente"
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