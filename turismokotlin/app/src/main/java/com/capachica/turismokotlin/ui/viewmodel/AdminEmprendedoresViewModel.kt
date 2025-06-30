package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.Categoria
import com.capachica.turismokotlin.data.model.CreateEmprendedorRequest
import com.capachica.turismokotlin.data.model.Emprendedor
import com.capachica.turismokotlin.data.model.MunicipalidadDetallada
import com.capachica.turismokotlin.data.model.UpdateEmprendedorRequest
import com.capachica.turismokotlin.data.repository.AdminCategoriasRepository
import com.capachica.turismokotlin.data.repository.AdminEmprendedoresRepository
import com.capachica.turismokotlin.data.repository.AdminMunicipalidadesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminEmprendedoresViewModel @Inject constructor(
    private val adminEmprendedoresRepository: AdminEmprendedoresRepository,
    private val adminCategoriasRepository: AdminCategoriasRepository,
    private val adminMunicipalidadesRepository: AdminMunicipalidadesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminEmprendedoresUiState())
    val uiState: StateFlow<AdminEmprendedoresUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        loadEmprendedores()
        loadCategorias()
        loadMunicipalidades()
    }

    fun loadEmprendedores() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            adminEmprendedoresRepository.getAllEmprendedores()
                .onSuccess { emprendedores ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        emprendedores = emprendedores
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

    private fun loadCategorias() {
        viewModelScope.launch {
            adminCategoriasRepository.getAllCategorias()
                .onSuccess { categorias ->
                    _uiState.value = _uiState.value.copy(categorias = categorias)
                }
        }
    }

    private fun loadMunicipalidades() {
        viewModelScope.launch {
            adminMunicipalidadesRepository.getAllMunicipalidades()
                .onSuccess { municipalidades ->
                    _uiState.value = _uiState.value.copy(municipalidades = municipalidades)
                }
        }
    }

    fun createEmprendedor(request: CreateEmprendedorRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreating = true, error = null)

            adminEmprendedoresRepository.createEmprendedor(request)
                .onSuccess { emprendedor ->
                    _uiState.value = _uiState.value.copy(
                        isCreating = false,
                        successMessage = "Emprendedor creado exitosamente"
                    )
                    loadEmprendedores() // Recargar lista
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isCreating = false,
                        error = exception.message
                    )
                }
        }
    }

    fun updateEmprendedor(emprendedorId: Long, request: UpdateEmprendedorRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdating = true, error = null)

            adminEmprendedoresRepository.updateEmprendedor(emprendedorId, request)
                .onSuccess { emprendedor ->
                    _uiState.value = _uiState.value.copy(
                        isUpdating = false,
                        successMessage = "Emprendedor actualizado exitosamente"
                    )
                    loadEmprendedores() // Recargar lista
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isUpdating = false,
                        error = exception.message
                    )
                }
        }
    }

    fun deleteEmprendedor(emprendedorId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(error = null)

            adminEmprendedoresRepository.deleteEmprendedor(emprendedorId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Emprendedor eliminado exitosamente"
                    )
                    loadEmprendedores() // Recargar lista
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(error = exception.message)
                }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}

data class AdminEmprendedoresUiState(
    val isLoading: Boolean = false,
    val isCreating: Boolean = false,
    val isUpdating: Boolean = false,
    val emprendedores: List<Emprendedor> = emptyList(),
    val categorias: List<Categoria> = emptyList(),
    val municipalidades: List<MunicipalidadDetallada> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)