package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.*
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.data.repository.ReservaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ReservaViewModel(
    private val repository: ReservaRepository
) : ViewModel() {
    
    private val _reservasState = MutableStateFlow<Result<List<Reserva>>?>(null)
    val reservasState: StateFlow<Result<List<Reserva>>?> = _reservasState
    
    private val _reservaState = MutableStateFlow<Result<Reserva>?>(null)
    val reservaState: StateFlow<Result<Reserva>?> = _reservaState
    
    private val _createState = MutableStateFlow<Result<Reserva>?>(null)
    val createState: StateFlow<Result<Reserva>?> = _createState
    
    private val _confirmarState = MutableStateFlow<Result<Reserva>?>(null)
    val confirmarState: StateFlow<Result<Reserva>?> = _confirmarState
    
    private val _cancelarState = MutableStateFlow<Result<Reserva>?>(null)
    val cancelarState: StateFlow<Result<Reserva>?> = _cancelarState
    
    private val _completarState = MutableStateFlow<Result<Reserva>?>(null)
    val completarState: StateFlow<Result<Reserva>?> = _completarState
    
    fun getAllReservas() {
        viewModelScope.launch {
            repository.getAllReservas()
                .catch { e ->
                    _reservasState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _reservasState.value = result
                }
        }
    }
    
    fun getReservaById(id: Long) {
        viewModelScope.launch {
            repository.getReservaById(id)
                .catch { e ->
                    _reservaState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _reservaState.value = result
                }
        }
    }
    
    fun getReservaByCodigo(codigo: String) {
        viewModelScope.launch {
            repository.getReservaByCodigo(codigo)
                .catch { e ->
                    _reservaState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _reservaState.value = result
                }
        }
    }
    
    fun getMisReservas() {
        viewModelScope.launch {
            repository.getMisReservas()
                .catch { e ->
                    _reservasState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _reservasState.value = result
                }
        }
    }
    
    fun getReservasByPlan(planId: Long) {
        viewModelScope.launch {
            repository.getReservasByPlan(planId)
                .catch { e ->
                    _reservasState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _reservasState.value = result
                }
        }
    }
    
    fun getReservasByMunicipalidad(municipalidadId: Long) {
        viewModelScope.launch {
            repository.getReservasByMunicipalidad(municipalidadId)
                .catch { e ->
                    _reservasState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _reservasState.value = result
                }
        }
    }
    
    fun createReserva(request: ReservaRequest) {
        viewModelScope.launch {
            repository.createReserva(request)
                .catch { e ->
                    _createState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _createState.value = result
                }
        }
    }
    
    fun confirmarReserva(id: Long) {
        viewModelScope.launch {
            repository.confirmarReserva(id)
                .catch { e ->
                    _confirmarState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _confirmarState.value = result
                }
        }
    }
    
    fun cancelarReserva(id: Long, motivo: String) {
        viewModelScope.launch {
            repository.cancelarReserva(id, motivo)
                .catch { e ->
                    _cancelarState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _cancelarState.value = result
                }
        }
    }
    
    fun completarReserva(id: Long) {
        viewModelScope.launch {
            repository.completarReserva(id)
                .catch { e ->
                    _completarState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _completarState.value = result
                }
        }
    }
    
    fun clearStates() {
        _createState.value = null
        _confirmarState.value = null
        _cancelarState.value = null
        _completarState.value = null
    }
}