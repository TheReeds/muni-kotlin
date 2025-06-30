package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.Emprendedor
import com.capachica.turismokotlin.data.model.Servicio
import com.capachica.turismokotlin.data.repository.CategoriasRepository
import com.capachica.turismokotlin.data.repository.EmprendedoresRepository
import com.capachica.turismokotlin.data.repository.ServiciosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmprendedorViewModel @Inject constructor(
    private val emprendedoresRepository: EmprendedoresRepository,
    private val serviciosRepository: ServiciosRepository,
    private val categoriasRepository: CategoriasRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmprendedorUiState())
    val uiState: StateFlow<EmprendedorUiState> = _uiState.asStateFlow()

    fun loadEmprendedorDetails(emprendedorId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val emprendedorDeferred = async { emprendedoresRepository.getEmprendedorById(emprendedorId) }
            val serviciosDeferred = async { serviciosRepository.getServiciosByEmprendedor(emprendedorId) }

            val emprendedorResult = emprendedorDeferred.await()
            val serviciosResult = serviciosDeferred.await()

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                emprendedor = emprendedorResult.getOrNull(),
                servicios = serviciosResult.getOrElse { emptyList() },
                error = when {
                    emprendedorResult.isFailure -> emprendedorResult.exceptionOrNull()?.message
                    serviciosResult.isFailure -> serviciosResult.exceptionOrNull()?.message
                    else -> null
                }
            )
        }
    }

    fun loadEmprendedoresByCategoria(categoriaId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            emprendedoresRepository.getEmprendedoresByCategoria(categoriaId)
                .onSuccess { emprendedores ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        emprendedoresByCategoria = emprendedores
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

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class EmprendedorUiState(
    val isLoading: Boolean = false,
    val emprendedor: Emprendedor? = null,
    val servicios: List<Servicio> = emptyList(),
    val emprendedoresByCategoria: List<Emprendedor> = emptyList(),
    val error: String? = null
)