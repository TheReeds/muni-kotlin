package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.Emprendedor
import com.capachica.turismokotlin.data.model.UsuarioDetallado
import com.capachica.turismokotlin.data.repository.AdminEmprendedoresRepository
import com.capachica.turismokotlin.data.repository.AdminUsuariosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminUsuariosViewModel @Inject constructor(
    private val adminUsuariosRepository: AdminUsuariosRepository,
    private val adminEmprendedoresRepository: AdminEmprendedoresRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUsuariosUiState())
    val uiState: StateFlow<AdminUsuariosUiState> = _uiState.asStateFlow()

    init {
        loadUsuarios()
        loadEmprendedoresDisponibles()
    }

    fun loadUsuarios() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            adminUsuariosRepository.getAllUsuarios()
                .onSuccess { usuarios ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        usuarios = usuarios
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

    fun loadUsuariosPorRol(rol: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            adminUsuariosRepository.getUsuariosPorRol(rol)
                .onSuccess { usuarios ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        usuariosFiltrados = usuarios
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

    fun loadUsuariosSinEmprendedor() {
        viewModelScope.launch {
            adminUsuariosRepository.getUsuariosSinEmprendedor()
                .onSuccess { usuarios ->
                    _uiState.value = _uiState.value.copy(usuariosSinEmprendedor = usuarios)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(error = exception.message)
                }
        }
    }

    fun asignarRol(usuarioId: Long, rol: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOperating = true, error = null)

            adminUsuariosRepository.asignarRol(usuarioId, rol)
                .onSuccess { mensaje ->
                    _uiState.value = _uiState.value.copy(
                        isOperating = false,
                        successMessage = mensaje
                    )
                    loadUsuarios() // Recargar lista
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isOperating = false,
                        error = exception.message
                    )
                }
        }
    }

    fun quitarRol(usuarioId: Long, rol: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOperating = true, error = null)

            adminUsuariosRepository.quitarRol(usuarioId, rol)
                .onSuccess { mensaje ->
                    _uiState.value = _uiState.value.copy(
                        isOperating = false,
                        successMessage = mensaje
                    )
                    loadUsuarios() // Recargar lista
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isOperating = false,
                        error = exception.message
                    )
                }
        }
    }

    fun resetearRoles(usuarioId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOperating = true, error = null)

            adminUsuariosRepository.resetearRoles(usuarioId)
                .onSuccess { mensaje ->
                    _uiState.value = _uiState.value.copy(
                        isOperating = false,
                        successMessage = mensaje
                    )
                    loadUsuarios() // Recargar lista
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isOperating = false,
                        error = exception.message
                    )
                }
        }
    }

    fun asignarEmprendedor(usuarioId: Long, emprendedorId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOperating = true, error = null)

            adminUsuariosRepository.asignarEmprendedor(usuarioId, emprendedorId)
                .onSuccess { mensaje ->
                    _uiState.value = _uiState.value.copy(
                        isOperating = false,
                        successMessage = mensaje
                    )
                    loadUsuarios() // Recargar lista
                    loadEmprendedoresDisponibles() // Actualizar disponibles
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isOperating = false,
                        error = exception.message
                    )
                }
        }
    }

    fun desasignarEmprendedor(usuarioId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOperating = true, error = null)

            adminUsuariosRepository.desasignarEmprendedor(usuarioId)
                .onSuccess { mensaje ->
                    _uiState.value = _uiState.value.copy(
                        isOperating = false,
                        successMessage = mensaje
                    )
                    loadUsuarios() // Recargar lista
                    loadEmprendedoresDisponibles() // Actualizar disponibles
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isOperating = false,
                        error = exception.message
                    )
                }
        }
    }

    fun cambiarEmprendedor(usuarioId: Long, nuevoEmprendedorId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOperating = true, error = null)

            adminUsuariosRepository.cambiarEmprendedor(usuarioId, nuevoEmprendedorId)
                .onSuccess { mensaje ->
                    _uiState.value = _uiState.value.copy(
                        isOperating = false,
                        successMessage = mensaje
                    )
                    loadUsuarios() // Recargar lista
                    loadEmprendedoresDisponibles() // Actualizar disponibles
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isOperating = false,
                        error = exception.message
                    )
                }
        }
    }

    fun loadEmprendedoresDisponibles() {
        viewModelScope.launch {
            adminEmprendedoresRepository.getAllEmprendedores()
                .onSuccess { emprendedores ->
                    _uiState.value = _uiState.value.copy(emprendedoresDisponibles = emprendedores)
                }
                .onFailure { exception ->
                    // No mostramos error aquí, es opcional
                }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}

data class AdminUsuariosUiState(
    val isLoading: Boolean = false,
    val isOperating: Boolean = false, // Para operaciones individuales
    val usuarios: List<UsuarioDetallado> = emptyList(),
    val usuariosFiltrados: List<UsuarioDetallado> = emptyList(),
    val usuariosSinEmprendedor: List<UsuarioDetallado> = emptyList(),
    val emprendedoresDisponibles: List<Emprendedor> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)