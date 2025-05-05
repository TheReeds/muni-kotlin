package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.Emprendedor
import com.capachica.turismokotlin.data.model.EmprendedorRequest
import com.capachica.turismokotlin.data.repository.EmprendedorRepository
import com.capachica.turismokotlin.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EmprendedorViewModel(private val repository: EmprendedorRepository) : ViewModel() {

    private val _emprendedoresState = MutableStateFlow<Result<List<Emprendedor>>>(Result.Loading)
    val emprendedoresState: StateFlow<Result<List<Emprendedor>>> = _emprendedoresState

    private val _emprendedorState = MutableStateFlow<Result<Emprendedor>>(Result.Loading)
    val emprendedorState: StateFlow<Result<Emprendedor>> = _emprendedorState

    // Cambio crítico: inicializamos con un estado inicial (no Loading)
    private val _createUpdateState = MutableStateFlow<Result<Emprendedor>?>(null)
    val createUpdateState: StateFlow<Result<Emprendedor>?> = _createUpdateState

    // Cambio crítico: inicializamos con un estado inicial (no Loading)
    private val _deleteState = MutableStateFlow<Result<Boolean>?>(null)
    val deleteState: StateFlow<Result<Boolean>?> = _deleteState

    // Método para resetear estados cuando se inicia un formulario
    fun resetStates() {
        _createUpdateState.value = null
        _deleteState.value = null
    }

    fun getAllEmprendedores() {
        _emprendedoresState.value = Result.Loading
        viewModelScope.launch {
            repository.getAllEmprendedores().collect {
                _emprendedoresState.value = it
            }
        }
    }

    fun getEmprendedorById(id: Long) {
        _emprendedorState.value = Result.Loading
        viewModelScope.launch {
            repository.getEmprendedorById(id).collect {
                _emprendedorState.value = it
            }
        }
    }

    fun getEmprendedoresByMunicipalidad(municipalidadId: Long) {
        _emprendedoresState.value = Result.Loading
        viewModelScope.launch {
            repository.getEmprendedoresByMunicipalidad(municipalidadId).collect {
                _emprendedoresState.value = it
            }
        }
    }

    fun getEmprendedoresByRubro(rubro: String) {
        _emprendedoresState.value = Result.Loading
        viewModelScope.launch {
            repository.getEmprendedoresByRubro(rubro).collect {
                _emprendedoresState.value = it
            }
        }
    }

    fun getMiEmprendedor() {
        _emprendedorState.value = Result.Loading
        viewModelScope.launch {
            repository.getMiEmprendedor().collect {
                _emprendedorState.value = it
            }
        }
    }

    fun createEmprendedor(request: EmprendedorRequest) {
        _createUpdateState.value = Result.Loading
        viewModelScope.launch {
            repository.createEmprendedor(request).collect {
                _createUpdateState.value = it
            }
        }
    }

    fun updateEmprendedor(id: Long, request: EmprendedorRequest) {
        _createUpdateState.value = Result.Loading
        viewModelScope.launch {
            repository.updateEmprendedor(id, request).collect {
                _createUpdateState.value = it
            }
        }
    }

    fun deleteEmprendedor(id: Long) {
        _deleteState.value = Result.Loading
        viewModelScope.launch {
            repository.deleteEmprendedor(id).collect {
                _deleteState.value = it
            }
        }
    }
}