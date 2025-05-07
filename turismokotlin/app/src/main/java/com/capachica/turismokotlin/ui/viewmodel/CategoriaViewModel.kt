package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.Categoria
import com.capachica.turismokotlin.data.model.CategoriaRequest
import com.capachica.turismokotlin.data.repository.CategoriaRepository
import com.capachica.turismokotlin.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CategoriaViewModel(private val repository: CategoriaRepository) : ViewModel() {
    private val _categoriasState = MutableStateFlow<Result<List<Categoria>>>(Result.Loading)
    val categoriasState: StateFlow<Result<List<Categoria>>> = _categoriasState

    private val _categoriaState = MutableStateFlow<Result<Categoria>>(Result.Loading)
    val categoriaState: StateFlow<Result<Categoria>> = _categoriaState

    private val _createUpdateState = MutableStateFlow<Result<Categoria>>(Result.Loading)
    val createUpdateState: StateFlow<Result<Categoria>> = _createUpdateState

    private val _deleteState = MutableStateFlow<Result<Boolean>>(Result.Loading)
    val deleteState: StateFlow<Result<Boolean>> = _deleteState

    fun getAllCategorias() {
        viewModelScope.launch {
            repository.getAllCategorias()
                .catch { e ->
                    _categoriasState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _categoriasState.value = result
                }
        }
    }

    fun getCategoriaById(id: Long) {
        viewModelScope.launch {
            repository.getCategoriaById(id)
                .catch { e ->
                    _categoriaState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _categoriaState.value = result
                }
        }
    }

    fun createCategoria(request: CategoriaRequest) {
        viewModelScope.launch {
            repository.createCategoria(request)
                .catch { e ->
                    _createUpdateState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _createUpdateState.value = result
                }
        }
    }

    fun updateCategoria(id: Long, request: CategoriaRequest) {
        viewModelScope.launch {
            repository.updateCategoria(id, request)
                .catch { e ->
                    _createUpdateState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _createUpdateState.value = result
                }
        }
    }

    fun deleteCategoria(id: Long) {
        viewModelScope.launch {
            repository.deleteCategoria(id)
                .catch { e ->
                    _deleteState.value = Result.Error(e.message ?: "Error desconocido")
                }
                .collect { result ->
                    _deleteState.value = result
                }
        }
    }
}