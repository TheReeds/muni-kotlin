package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.UsuarioResponse
import com.capachica.turismokotlin.data.repository.UserRepository
import com.capachica.turismokotlin.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _usuariosState = MutableStateFlow<Result<List<UsuarioResponse>>>(Result.Loading)
    val usuariosState: StateFlow<Result<List<UsuarioResponse>>> = _usuariosState

    private val _usuarioState = MutableStateFlow<Result<UsuarioResponse>>(Result.Loading)
    val usuarioState: StateFlow<Result<UsuarioResponse>> = _usuarioState

    private val _usuariosSinEmprendedorState = MutableStateFlow<Result<List<UsuarioResponse>>>(Result.Loading)
    val usuariosSinEmprendedorState: StateFlow<Result<List<UsuarioResponse>>> = _usuariosSinEmprendedorState

    private val _usuariosPorRolState = MutableStateFlow<Result<List<UsuarioResponse>>>(Result.Loading)
    val usuariosPorRolState: StateFlow<Result<List<UsuarioResponse>>> = _usuariosPorRolState

    private val _operacionState = MutableStateFlow<Result<String>?>(null)
    val operacionState: StateFlow<Result<String>?> = _operacionState

    // Método para resetear estados
    fun resetOperacionState() {
        _operacionState.value = null
    }

    fun getAllUsuarios() {
        _usuariosState.value = Result.Loading
        viewModelScope.launch {
            repository.getAllUsuarios().collect {
                _usuariosState.value = it
            }
        }
    }

    fun getUsuarioById(id: Long) {
        _usuarioState.value = Result.Loading
        viewModelScope.launch {
            repository.getUsuarioById(id).collect {
                _usuarioState.value = it
            }
        }
    }

    fun getUsuariosSinEmprendedor() {
        _usuariosSinEmprendedorState.value = Result.Loading
        viewModelScope.launch {
            repository.getUsuariosSinEmprendedor().collect {
                _usuariosSinEmprendedorState.value = it
            }
        }
    }

    fun getUsuariosPorRol(rol: String) {
        _usuariosPorRolState.value = Result.Loading
        viewModelScope.launch {
            repository.getUsuariosPorRol(rol).collect {
                _usuariosPorRolState.value = it
            }
        }
    }

    fun asignarRolAUsuario(usuarioId: Long, rol: String) {
        _operacionState.value = Result.Loading
        viewModelScope.launch {
            repository.asignarRolAUsuario(usuarioId, rol).collect {
                _operacionState.value = it
            }
        }
    }

    fun quitarRolAUsuario(usuarioId: Long, rol: String) {
        _operacionState.value = Result.Loading
        viewModelScope.launch {
            repository.quitarRolAUsuario(usuarioId, rol).collect {
                _operacionState.value = it
            }
        }
    }

    fun resetearRolesUsuario(usuarioId: Long) {
        _operacionState.value = Result.Loading
        viewModelScope.launch {
            repository.resetearRolesUsuario(usuarioId).collect {
                _operacionState.value = it
            }
        }
    }

    fun asignarUsuarioAEmprendedor(usuarioId: Long, emprendedorId: Long) {
        _operacionState.value = Result.Loading
        viewModelScope.launch {
            repository.asignarUsuarioAEmprendedor(usuarioId, emprendedorId).collect {
                _operacionState.value = it
            }
        }
    }

    fun cambiarUsuarioDeEmprendedor(usuarioId: Long, emprendedorId: Long) {
        _operacionState.value = Result.Loading
        viewModelScope.launch {
            repository.cambiarUsuarioDeEmprendedor(usuarioId, emprendedorId).collect {
                _operacionState.value = it
            }
        }
    }

    fun desasignarUsuarioDeEmprendedor(usuarioId: Long) {
        _operacionState.value = Result.Loading
        viewModelScope.launch {
            repository.desasignarUsuarioDeEmprendedor(usuarioId).collect {
                _operacionState.value = it
            }
        }
    }
}