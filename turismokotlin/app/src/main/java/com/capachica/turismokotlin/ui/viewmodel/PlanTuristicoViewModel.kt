package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.*
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.data.repository.PlanTuristicoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PlanTuristicoViewModel(
    private val repository: PlanTuristicoRepository
) : ViewModel() {
    
    private val _planesState = MutableStateFlow<Result<List<PlanTuristico>>>(Result.Loading)
    val planesState: StateFlow<Result<List<PlanTuristico>>> = _planesState
    
    private val _planState = MutableStateFlow<Result<PlanTuristico>>(Result.Loading)
    val planState: StateFlow<Result<PlanTuristico>> = _planState
    
    private val _createUpdateState = MutableStateFlow<Result<PlanTuristico>?>(null)
    val createUpdateState: StateFlow<Result<PlanTuristico>?> = _createUpdateState
    
    private val _deleteState = MutableStateFlow<Result<Boolean>?>(null)
    val deleteState: StateFlow<Result<Boolean>?> = _deleteState
    
    private val _cambiarEstadoState = MutableStateFlow<Result<PlanTuristico>>(Result.Loading)
    val cambiarEstadoState: StateFlow<Result<PlanTuristico>> = _cambiarEstadoState
    
    fun getAllPlanes() {
        viewModelScope.launch {
            repository.getAllPlanes()
                .catch { e ->
                    _planesState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _planesState.value = result
                }
        }
    }
    
    fun getPlanById(id: Long) {
        viewModelScope.launch {
            repository.getPlanById(id)
                .catch { e ->
                    _planState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _planState.value = result
                }
        }
    }
    
    fun getPlanesByMunicipalidad(municipalidadId: Long) {
        viewModelScope.launch {
            repository.getPlanesByMunicipalidad(municipalidadId)
                .catch { e ->
                    _planesState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _planesState.value = result
                }
        }
    }
    
    fun getPlanesByEstado(estado: EstadoPlan) {
        viewModelScope.launch {
            repository.getPlanesByEstado(estado)
                .catch { e ->
                    _planesState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _planesState.value = result
                }
        }
    }
    
    fun getPlanesByNivelDificultad(nivel: NivelDificultad) {
        viewModelScope.launch {
            repository.getPlanesByNivelDificultad(nivel)
                .catch { e ->
                    _planesState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _planesState.value = result
                }
        }
    }
    
    fun getPlanesByDuracion(duracionMin: Int, duracionMax: Int) {
        viewModelScope.launch {
            repository.getPlanesByDuracion(duracionMin, duracionMax)
                .catch { e ->
                    _planesState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _planesState.value = result
                }
        }
    }
    
    fun getPlanesByPrecio(precioMin: Double, precioMax: Double) {
        viewModelScope.launch {
            repository.getPlanesByPrecio(precioMin, precioMax)
                .catch { e ->
                    _planesState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _planesState.value = result
                }
        }
    }
    
    fun searchPlanes(termino: String) {
        viewModelScope.launch {
            repository.searchPlanes(termino)
                .catch { e ->
                    _planesState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _planesState.value = result
                }
        }
    }
    
    fun getMisPlanes() {
        viewModelScope.launch {
            repository.getMisPlanes()
                .catch { e ->
                    _planesState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _planesState.value = result
                }
        }
    }
    
    fun getPlanesMasPopulares() {
        viewModelScope.launch {
            repository.getPlanesMasPopulares()
                .catch { e ->
                    _planesState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _planesState.value = result
                }
        }
    }
    
    fun createPlan(request: PlanTuristicoRequest) {
        _createUpdateState.value = Result.Loading
        viewModelScope.launch {
            repository.createPlan(request)
                .catch { e ->
                    _createUpdateState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _createUpdateState.value = result
                }
        }
    }
    
    fun updatePlan(id: Long, request: PlanTuristicoRequest) {
        _createUpdateState.value = Result.Loading
        viewModelScope.launch {
            repository.updatePlan(id, request)
                .catch { e ->
                    _createUpdateState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _createUpdateState.value = result
                }
        }
    }
    
    fun deletePlan(id: Long) {
        _deleteState.value = Result.Loading
        viewModelScope.launch {
            repository.deletePlan(id)
                .catch { e ->
                    _deleteState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _deleteState.value = result
                }
        }
    }
    
    fun cambiarEstadoPlan(id: Long, estado: EstadoPlan) {
        viewModelScope.launch {
            repository.cambiarEstadoPlan(id, estado)
                .catch { e ->
                    _cambiarEstadoState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _cambiarEstadoState.value = result
                }
        }
    }
    
    fun resetStates() {
        _createUpdateState.value = null
        _deleteState.value = null
        _cambiarEstadoState.value = Result.Loading
    }
    
    fun resetDeleteState() {
        _deleteState.value = null
    }
}