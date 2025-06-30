package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.CreateReservaCarritoRequest
import com.capachica.turismokotlin.data.model.MetodoPago
import com.capachica.turismokotlin.data.model.ReservaCarrito
import com.capachica.turismokotlin.data.repository.CartRepository
import com.capachica.turismokotlin.data.repository.ReservasCarritoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val reservasRepository: ReservasCarritoRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    fun crearReserva(
        observaciones: String? = null,
        contactoEmergencia: String? = null,
        telefonoEmergencia: String? = null,
        metodoPago: MetodoPago = MetodoPago.EFECTIVO
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val request = CreateReservaCarritoRequest(
                observaciones = observaciones,
                contactoEmergencia = contactoEmergencia,
                telefonoEmergencia = telefonoEmergencia,
                metodoPago = metodoPago
            )

            reservasRepository.createReservaFromCart(request)
                .onSuccess { reserva ->
                    // Limpiar carrito después de crear la reserva
                    cartRepository.clearCart()

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        reservaCreada = reserva,
                        successMessage = "Reserva creada exitosamente"
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

data class CheckoutUiState(
    val isLoading: Boolean = false,
    val reservaCreada: ReservaCarrito? = null,
    val error: String? = null,
    val successMessage: String? = null
)