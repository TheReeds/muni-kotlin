package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.Categoria
import com.capachica.turismokotlin.data.model.Emprendedor
import com.capachica.turismokotlin.data.model.Plan
import com.capachica.turismokotlin.data.model.Servicio
import com.capachica.turismokotlin.data.repository.CategoriasRepository
import com.capachica.turismokotlin.data.repository.EmprendedoresRepository
import com.capachica.turismokotlin.data.repository.PlanesRepository
import com.capachica.turismokotlin.data.repository.ServiciosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val planesRepository: PlanesRepository,
    private val serviciosRepository: ServiciosRepository,
    private val emprendedoresRepository: EmprendedoresRepository,
    private val categoriasRepository: CategoriasRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // Ejecutar todas las llamadas en paralelo
                val planesPopularesDeferred = async { planesRepository.getPlanesPopulares() }
                val serviciosDeferred = async { serviciosRepository.getAllServicios() }
                val emprendedoresDeferred = async { emprendedoresRepository.getAllEmprendedores() }
                val categoriasDeferred = async { categoriasRepository.getAllCategorias() }

                // Esperar resultados
                val planesPopularesResult = planesPopularesDeferred.await()
                val serviciosResult = serviciosDeferred.await()
                val emprendedoresResult = emprendedoresDeferred.await()
                val categoriasResult = categoriasDeferred.await()

                // Procesar resultados
                val planesPopulares = planesPopularesResult.getOrElse { emptyList() }
                val servicios = serviciosResult.getOrElse { emptyList() }
                val emprendedores = emprendedoresResult.getOrElse { emptyList() }
                val categorias = categoriasResult.getOrElse { emptyList() }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    planesPopulares = planesPopulares,
                    servicios = servicios,
                    emprendedores = emprendedores,
                    categorias = categorias,
                    error = null
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    fun searchPlanes(termino: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true, error = null)

            planesRepository.searchPlanes(termino)
                .onSuccess { planes ->
                    _uiState.value = _uiState.value.copy(
                        isSearching = false,
                        searchResults = planes,
                        searchResultsServicios = emptyList() // Limpiar resultados de servicios
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isSearching = false,
                        error = exception.message
                    )
                }
        }
    }

    fun searchServicios(termino: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true, error = null)

            serviciosRepository.searchServicios(termino)
                .onSuccess { servicios ->
                    _uiState.value = _uiState.value.copy(
                        isSearching = false,
                        searchResultsServicios = servicios,
                        searchResults = emptyList() // Limpiar resultados de planes
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isSearching = false,
                        error = exception.message
                    )
                }
        }
    }

    fun clearSearchResults() {
        _uiState.value = _uiState.value.copy(
            searchResults = emptyList(),
            searchResultsServicios = emptyList()
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val planesPopulares: List<Plan> = emptyList(),
    val servicios: List<Servicio> = emptyList(),
    val emprendedores: List<Emprendedor> = emptyList(),
    val categorias: List<Categoria> = emptyList(),
    val searchResults: List<Plan> = emptyList(),
    val searchResultsServicios: List<Servicio> = emptyList(),
    val error: String? = null
)