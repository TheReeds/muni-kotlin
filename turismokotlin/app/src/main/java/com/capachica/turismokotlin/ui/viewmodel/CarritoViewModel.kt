package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.*
import com.capachica.turismokotlin.data.repository.CarritoRepository
import com.capachica.turismokotlin.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CarritoViewModel(private val repository: CarritoRepository) : ViewModel() {

    private val _carritoState = MutableStateFlow<Result<CarritoResponse>>(Result.Loading)
    val carritoState: StateFlow<Result<CarritoResponse>> = _carritoState

    private val _totalState = MutableStateFlow<Result<CarritoTotalResponse>>(Result.Loading)
    val totalState: StateFlow<Result<CarritoTotalResponse>> = _totalState

    private val _contarState = MutableStateFlow<Result<CarritoContarResponse>>(Result.Loading)
    val contarState: StateFlow<Result<CarritoContarResponse>> = _contarState

    private val _operationState = MutableStateFlow<Result<String>?>(null)
    val operationState: StateFlow<Result<String>?> = _operationState

    // Estados para reservas desde carrito
    private val _crearReservaState = MutableStateFlow<Result<ReservaCarritoResponse>?>(null)
    val crearReservaState: StateFlow<Result<ReservaCarritoResponse>?> = _crearReservaState

    private val _misReservasCarritoState = MutableStateFlow<Result<List<ReservaCarritoResponse>>>(Result.Loading)
    val misReservasCarritoState: StateFlow<Result<List<ReservaCarritoResponse>>> = _misReservasCarritoState

    private val _reservaCarritoDetailState = MutableStateFlow<Result<ReservaCarritoResponse>>(Result.Loading)
    val reservaCarritoDetailState: StateFlow<Result<ReservaCarritoResponse>> = _reservaCarritoDetailState

    private val _confirmarReservaState = MutableStateFlow<Result<ReservaCarritoResponse>?>(null)
    val confirmarReservaState: StateFlow<Result<ReservaCarritoResponse>?> = _confirmarReservaState

    private val _completarReservaState = MutableStateFlow<Result<ReservaCarritoResponse>?>(null)
    val completarReservaState: StateFlow<Result<ReservaCarritoResponse>?> = _completarReservaState

    private val _cancelarReservaState = MutableStateFlow<Result<ReservaCarritoResponse>?>(null)
    val cancelarReservaState: StateFlow<Result<ReservaCarritoResponse>?> = _cancelarReservaState

    init {
        cargarCarrito()
        cargarTotal()
        cargarContador()
    }

    fun cargarCarrito() {
        viewModelScope.launch {
            repository.getCarrito().collect { result ->
                _carritoState.value = result
            }
        }
    }

    fun cargarTotal() {
        viewModelScope.launch {
            repository.getTotalCarrito().collect { result ->
                _totalState.value = result
            }
        }
    }

    fun cargarContador() {
        viewModelScope.launch {
            repository.contarItemsCarrito().collect { result ->
                _contarState.value = result
            }
        }
    }

    fun agregarItem(servicioId: Long, cantidad: Int, fechaServicio: String, notasEspeciales: String? = null) {
        viewModelScope.launch {
            _operationState.value = Result.Loading
            val request = CarritoItemRequest(
                servicioId = servicioId,
                cantidad = cantidad,
                fechaServicio = fechaServicio,
                notasEspeciales = notasEspeciales
            )
            
            repository.agregarItemAlCarrito(request).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _carritoState.value = result
                        _operationState.value = Result.Success("Item agregado al carrito")
                        cargarTotal()
                        cargarContador()
                    }
                    is Result.Error -> {
                        _operationState.value = Result.Error(result.message)
                    }
                    is Result.Loading -> {
                        // No hacer nada, ya está en loading
                    }
                }
            }
        }
    }

    fun actualizarCantidad(itemId: Long, cantidad: Int) {
        viewModelScope.launch {
            _operationState.value = Result.Loading
            repository.actualizarCantidadItem(itemId, cantidad).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _carritoState.value = result
                        _operationState.value = Result.Success("Cantidad actualizada")
                        cargarTotal()
                        cargarContador()
                    }
                    is Result.Error -> {
                        _operationState.value = Result.Error(result.message)
                    }
                    is Result.Loading -> {
                        // No hacer nada
                    }
                }
            }
        }
    }

    fun eliminarItem(itemId: Long) {
        viewModelScope.launch {
            _operationState.value = Result.Loading
            repository.eliminarItemDelCarrito(itemId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _carritoState.value = result
                        _operationState.value = Result.Success("Item eliminado del carrito")
                        cargarTotal()
                        cargarContador()
                    }
                    is Result.Error -> {
                        _operationState.value = Result.Error(result.message)
                    }
                    is Result.Loading -> {
                        // No hacer nada
                    }
                }
            }
        }
    }

    fun limpiarCarrito() {
        viewModelScope.launch {
            _operationState.value = Result.Loading
            repository.limpiarCarrito().collect { result ->
                when (result) {
                    is Result.Success -> {
                        _carritoState.value = result
                        _operationState.value = Result.Success("Carrito limpiado")
                        cargarTotal()
                        cargarContador()
                    }
                    is Result.Error -> {
                        _operationState.value = Result.Error(result.message)
                    }
                    is Result.Loading -> {
                        // No hacer nada
                    }
                }
            }
        }
    }

    fun clearOperationState() {
        _operationState.value = null
    }

    // ========== MÉTODOS PARA RESERVAS DESDE CARRITO ==========

    fun crearReservaDesdeCarrito(request: ReservaCarritoRequest) {
        viewModelScope.launch {
            _crearReservaState.value = Result.Loading
            repository.crearReservaDesdeCarrito(request).collect { result ->
                _crearReservaState.value = result
                if (result is Result.Success) {
                    // Recargar carrito después de crear la reserva
                    cargarCarrito()
                    cargarTotal()
                    cargarContador()
                }
            }
        }
    }

    fun cargarMisReservasCarrito() {
        viewModelScope.launch {
            repository.getMisReservasCarrito().collect { result ->
                _misReservasCarritoState.value = result
            }
        }
    }

    fun cargarReservasCarritoPorEstado(estado: EstadoReservaCarrito) {
        viewModelScope.launch {
            repository.getReservasCarritoPorEstado(estado).collect { result ->
                _misReservasCarritoState.value = result
            }
        }
    }

    fun cargarReservaCarritoById(id: Long) {
        viewModelScope.launch {
            _reservaCarritoDetailState.value = Result.Loading
            repository.getReservaCarritoById(id).collect { result ->
                _reservaCarritoDetailState.value = result
            }
        }
    }

    fun confirmarReservaCarrito(id: Long) {
        viewModelScope.launch {
            _confirmarReservaState.value = Result.Loading
            repository.confirmarReservaCarrito(id).collect { result ->
                _confirmarReservaState.value = result
                if (result is Result.Success) {
                    // Recargar detalle de la reserva
                    cargarReservaCarritoById(id)
                    // Recargar lista de reservas
                    cargarMisReservasCarrito()
                }
            }
        }
    }

    fun completarReservaCarrito(id: Long) {
        viewModelScope.launch {
            _completarReservaState.value = Result.Loading
            repository.completarReservaCarrito(id).collect { result ->
                _completarReservaState.value = result
                if (result is Result.Success) {
                    // Recargar detalle de la reserva
                    cargarReservaCarritoById(id)
                    // Recargar lista de reservas
                    cargarMisReservasCarrito()
                }
            }
        }
    }

    fun cancelarReservaCarrito(id: Long, request: CancelacionReservaRequest) {
        viewModelScope.launch {
            _cancelarReservaState.value = Result.Loading
            repository.cancelarReservaCarrito(id, request).collect { result ->
                _cancelarReservaState.value = result
                if (result is Result.Success) {
                    // Recargar detalle de la reserva
                    cargarReservaCarritoById(id)
                    // Recargar lista de reservas
                    cargarMisReservasCarrito()
                }
            }
        }
    }

    fun clearReservaStates() {
        _crearReservaState.value = null
        _confirmarReservaState.value = null
        _completarReservaState.value = null
        _cancelarReservaState.value = null
    }
}