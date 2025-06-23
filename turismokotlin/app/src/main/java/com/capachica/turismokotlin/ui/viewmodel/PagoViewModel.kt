package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.*
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.data.repository.PagoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PagoViewModel(
    private val repository: PagoRepository
) : ViewModel() {
    
    private val _pagosState = MutableStateFlow<Result<List<Pago>>>(Result.Loading)
    val pagosState: StateFlow<Result<List<Pago>>> = _pagosState
    
    private val _pagoState = MutableStateFlow<Result<Pago>>(Result.Loading)
    val pagoState: StateFlow<Result<Pago>> = _pagoState
    
    private val _registrarState = MutableStateFlow<Result<Pago>>(Result.Loading)
    val registrarState: StateFlow<Result<Pago>> = _registrarState
    
    private val _confirmarState = MutableStateFlow<Result<Pago>>(Result.Loading)
    val confirmarState: StateFlow<Result<Pago>> = _confirmarState
    
    private val _rechazarState = MutableStateFlow<Result<Pago>>(Result.Loading)
    val rechazarState: StateFlow<Result<Pago>> = _rechazarState
    
    fun getAllPagos() {
        viewModelScope.launch {
            repository.getAllPagos()
                .catch { e ->
                    _pagosState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _pagosState.value = result
                }
        }
    }
    
    fun getPagoById(id: Long) {
        viewModelScope.launch {
            repository.getPagoById(id)
                .catch { e ->
                    _pagoState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _pagoState.value = result
                }
        }
    }
    
    fun getPagoByCodigo(codigo: String) {
        viewModelScope.launch {
            repository.getPagoByCodigo(codigo)
                .catch { e ->
                    _pagoState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _pagoState.value = result
                }
        }
    }
    
    fun getPagosByReserva(reservaId: Long) {
        viewModelScope.launch {
            repository.getPagosByReserva(reservaId)
                .catch { e ->
                    _pagosState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _pagosState.value = result
                }
        }
    }
    
    fun getMisPagos() {
        viewModelScope.launch {
            repository.getMisPagos()
                .catch { e ->
                    _pagosState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _pagosState.value = result
                }
        }
    }
    
    fun getPagosByMunicipalidad(municipalidadId: Long) {
        viewModelScope.launch {
            repository.getPagosByMunicipalidad(municipalidadId)
                .catch { e ->
                    _pagosState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _pagosState.value = result
                }
        }
    }
    
    fun registrarPago(request: PagoRequest) {
        viewModelScope.launch {
            repository.registrarPago(request)
                .catch { e ->
                    _registrarState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _registrarState.value = result
                }
        }
    }
    
    fun confirmarPago(id: Long) {
        viewModelScope.launch {
            repository.confirmarPago(id)
                .catch { e ->
                    _confirmarState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _confirmarState.value = result
                }
        }
    }
    
    fun rechazarPago(id: Long, motivo: String) {
        viewModelScope.launch {
            repository.rechazarPago(id, motivo)
                .catch { e ->
                    _rechazarState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _rechazarState.value = result
                }
        }
    }
    
    fun clearStates() {
        _registrarState.value = Result.Loading
        _confirmarState.value = Result.Loading
        _rechazarState.value = Result.Loading
    }
}