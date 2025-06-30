package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.Categoria
import com.capachica.turismokotlin.data.model.CreateCategoriaRequest
import com.capachica.turismokotlin.data.model.UpdateCategoriaRequest
import com.capachica.turismokotlin.data.repository.AdminCategoriasRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminCategoriasViewModel @Inject constructor(
    private val adminCategoriasRepository: AdminCategoriasRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminCategoriasUiState())
    val uiState: StateFlow<AdminCategoriasUiState> = _uiState.asStateFlow()

    init {
        loadCategorias()
    }

    fun loadCategorias() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            adminCategoriasRepository.getAllCategorias()
                .onSuccess { categorias ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        categorias = categorias
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

    fun createCategoria(request: CreateCategoriaRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreating = true, error = null)

            adminCategoriasRepository.createCategoria(request)
                .onSuccess { categoria ->
                    _uiState.value = _uiState.value.copy(
                        isCreating = false,
                        successMessage = "Categoría creada exitosamente"
                    )
                    loadCategorias() // Recargar lista
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isCreating = false,
                        error = exception.message
                    )
                }
        }
    }

    fun updateCategoria(categoriaId: Long, request: UpdateCategoriaRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdating = true, error = null)

            adminCategoriasRepository.updateCategoria(categoriaId, request)
                .onSuccess { categoria ->
                    _uiState.value = _uiState.value.copy(
                        isUpdating = false,
                        successMessage = "Categoría actualizada exitosamente"
                    )
                    loadCategorias() // Recargar lista
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isUpdating = false,
                        error = exception.message
                    )
                }
        }
    }

    fun deleteCategoria(categoriaId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(error = null)

            adminCategoriasRepository.deleteCategoria(categoriaId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Categoría eliminada exitosamente"
                    )
                    loadCategorias() // Recargar lista
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

data class AdminCategoriasUiState(
    val isLoading: Boolean = false,
    val isCreating: Boolean = false,
    val isUpdating: Boolean = false,
    val categorias: List<Categoria> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)