package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.*
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.data.repository.ServicioTuristicoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ServicioTuristicoViewModel(
    private val repository: ServicioTuristicoRepository
) : ViewModel() {
    
    private val _serviciosState = MutableStateFlow<Result<List<ServicioTuristico>>>(Result.Loading)
    val serviciosState: StateFlow<Result<List<ServicioTuristico>>> = _serviciosState
    
    private val _servicioState = MutableStateFlow<Result<ServicioTuristico>>(Result.Loading)
    val servicioState: StateFlow<Result<ServicioTuristico>> = _servicioState
    
    private val _createUpdateState = MutableStateFlow<Result<ServicioTuristico>?>(null)
    val createUpdateState: StateFlow<Result<ServicioTuristico>?> = _createUpdateState
    
    private val _deleteState = MutableStateFlow<Result<Boolean>?>(null)
    val deleteState: StateFlow<Result<Boolean>?> = _deleteState
    
    private val _cambiarEstadoState = MutableStateFlow<Result<ServicioTuristico>?>(null)
    val cambiarEstadoState: StateFlow<Result<ServicioTuristico>?> = _cambiarEstadoState
    
    fun getAllServicios() {
        viewModelScope.launch {
            repository.getAllServicios()
                .catch { e ->
                    _serviciosState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _serviciosState.value = result
                }
        }
    }
    
    fun getServicioById(id: Long) {
        viewModelScope.launch {
            repository.getServicioById(id)
                .catch { e ->
                    _servicioState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _servicioState.value = result
                }
        }
    }
    
    fun getServiciosByEmprendedor(emprendedorId: Long) {
        viewModelScope.launch {
            repository.getServiciosByEmprendedor(emprendedorId)
                .catch { e ->
                    _serviciosState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _serviciosState.value = result
                }
        }
    }
    
    fun getServiciosByMunicipalidad(municipalidadId: Long) {
        viewModelScope.launch {
            repository.getServiciosByMunicipalidad(municipalidadId)
                .catch { e ->
                    _serviciosState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _serviciosState.value = result
                }
        }
    }
    
    fun getServiciosByTipo(tipo: TipoServicio) {
        viewModelScope.launch {
            repository.getServiciosByTipo(tipo)
                .catch { e ->
                    _serviciosState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _serviciosState.value = result
                }
        }
    }
    
    fun getServiciosByEstado(estado: EstadoServicio) {
        viewModelScope.launch {
            repository.getServiciosByEstado(estado)
                .catch { e ->
                    _serviciosState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _serviciosState.value = result
                }
        }
    }
    
    fun searchServicios(termino: String) {
        viewModelScope.launch {
            repository.searchServicios(termino)
                .catch { e ->
                    _serviciosState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _serviciosState.value = result
                }
        }
    }
    
    fun getMisServicios() {
        viewModelScope.launch {
            repository.getMisServicios()
                .catch { e ->
                    _serviciosState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _serviciosState.value = result
                }
        }
    }
    
    fun createServicio(request: ServicioTuristicoRequest) {
        _createUpdateState.value = Result.Loading
        viewModelScope.launch {
            repository.createServicio(request)
                .catch { e ->
                    _createUpdateState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _createUpdateState.value = result
                }
        }
    }
    
    fun updateServicio(id: Long, request: ServicioTuristicoRequest) {
        _createUpdateState.value = Result.Loading
        viewModelScope.launch {
            repository.updateServicio(id, request)
                .catch { e ->
                    _createUpdateState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _createUpdateState.value = result
                }
        }
    }
    
    fun deleteServicio(id: Long) {
        _deleteState.value = Result.Loading
        viewModelScope.launch {
            repository.deleteServicio(id)
                .catch { e ->
                    _deleteState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _deleteState.value = result
                }
        }
    }
    
    fun cambiarEstadoServicio(id: Long, estado: EstadoServicio) {
        _cambiarEstadoState.value = Result.Loading
        viewModelScope.launch {
            repository.cambiarEstadoServicio(id, estado)
                .catch { e ->
                    _cambiarEstadoState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _cambiarEstadoState.value = result
                }
        }
    }
    
    fun clearStates() {
        _createUpdateState.value = null
        _deleteState.value = null
        _cambiarEstadoState.value = null
    }
}