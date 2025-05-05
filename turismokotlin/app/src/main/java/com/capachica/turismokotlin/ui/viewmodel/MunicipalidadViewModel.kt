package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.Municipalidad
import com.capachica.turismokotlin.data.model.MunicipalidadRequest
import com.capachica.turismokotlin.data.repository.MunicipalidadRepository
import com.capachica.turismokotlin.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class MunicipalidadViewModel(private val repository: MunicipalidadRepository) : ViewModel() {

    private val _municipalidadesState = MutableStateFlow<Result<List<Municipalidad>>>(Result.Loading)
    val municipalidadesState: StateFlow<Result<List<Municipalidad>>> = _municipalidadesState

    private val _municipalidadState = MutableStateFlow<Result<Municipalidad>>(Result.Loading)
    val municipalidadState: StateFlow<Result<Municipalidad>> = _municipalidadState

    // Estado INITIAL: Estado inicial antes de que el usuario inicie una acción
    private val _createUpdateState = MutableStateFlow<Result<Municipalidad>?>(null)
    val createUpdateState: StateFlow<Result<Municipalidad>?> = _createUpdateState

    private val _deleteState = MutableStateFlow<Result<Boolean>?>(null)
    val deleteState: StateFlow<Result<Boolean>?> = _deleteState

    // Método para resetear estados cuando se inicia un formulario
    fun resetStates() {
        _createUpdateState.value = null
        _deleteState.value = null
    }

    fun getAllMunicipalidades() {
        _municipalidadesState.value = Result.Loading
        viewModelScope.launch {
            repository.getAllMunicipalidades().collect {
                _municipalidadesState.value = it
            }
        }
    }

    fun getMunicipalidadById(id: Long) {
        _municipalidadState.value = Result.Loading
        viewModelScope.launch {
            repository.getMunicipalidadById(id).collect {
                _municipalidadState.value = it
            }
        }
    }

    fun getMunicipalidadesByDepartamento(departamento: String) {
        _municipalidadesState.value = Result.Loading
        viewModelScope.launch {
            repository.getMunicipalidadesByDepartamento(departamento).collect {
                _municipalidadesState.value = it
            }
        }
    }

    // Eliminamos los métodos no implementados:
    // getMunicipalidadesByProvincia
    // getMunicipalidadesByDistrito

    fun getMiMunicipalidad() {
        _municipalidadState.value = Result.Loading
        viewModelScope.launch {
            repository.getMiMunicipalidad().collect {
                _municipalidadState.value = it
            }
        }
    }

    fun createMunicipalidad(request: MunicipalidadRequest) {
        _createUpdateState.value = Result.Loading
        viewModelScope.launch {
            repository.createMunicipalidad(request).collect {
                _createUpdateState.value = it
            }
        }
    }

    fun updateMunicipalidad(id: Long, request: MunicipalidadRequest) {
        _createUpdateState.value = Result.Loading
        viewModelScope.launch {
            repository.updateMunicipalidad(id, request).collect {
                _createUpdateState.value = it
            }
        }
    }

    fun deleteMunicipalidad(id: Long) {
        _deleteState.value = Result.Loading
        viewModelScope.launch {
            repository.deleteMunicipalidad(id).collect {
                _deleteState.value = it
            }
        }
    }
}
