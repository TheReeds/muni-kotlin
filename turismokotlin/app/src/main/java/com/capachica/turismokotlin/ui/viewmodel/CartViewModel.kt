package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.CartRemoto
import com.capachica.turismokotlin.data.model.Plan
import com.capachica.turismokotlin.data.model.Servicio
import com.capachica.turismokotlin.data.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    val itemCount = _uiState.map { it.cart?.totalItems ?: 0 }
    val cartTotal = _uiState.map { it.cart?.totalCarrito ?: 0.0 }

    init {
        loadCart()
    }

    fun loadCart() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            cartRepository.getCarrito()
                .onSuccess { cart ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        cart = cart
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

    fun addToCart(servicioId: Long, cantidad: Int = 1, fechaServicio: String, notasEspeciales: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOperating = true, error = null)

            cartRepository.addToCart(servicioId, cantidad, fechaServicio, notasEspeciales)
                .onSuccess { cart ->
                    _uiState.value = _uiState.value.copy(
                        isOperating = false,
                        cart = cart,
                        successMessage = "Servicio agregado al carrito"
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

    fun updateCartItem(itemId: Long, cantidad: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOperating = true, error = null)

            cartRepository.updateCartItem(itemId, cantidad)
                .onSuccess { cart ->
                    _uiState.value = _uiState.value.copy(
                        isOperating = false,
                        cart = cart
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

    fun removeCartItem(itemId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOperating = true, error = null)

            cartRepository.removeCartItem(itemId)
                .onSuccess { cart ->
                    _uiState.value = _uiState.value.copy(
                        isOperating = false,
                        cart = cart,
                        successMessage = "Servicio eliminado del carrito"
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

    fun clearCart() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOperating = true, error = null)

            cartRepository.clearCart()
                .onSuccess {
                    loadCart() // Recargar carrito vacío
                    _uiState.value = _uiState.value.copy(
                        isOperating = false,
                        successMessage = "Carrito limpiado"
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

data class CartUiState(
    val isLoading: Boolean = false,
    val isOperating: Boolean = false,
    val cart: CartRemoto? = null,
    val error: String? = null,
    val successMessage: String? = null
)