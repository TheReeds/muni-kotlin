package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.Plan
import com.capachica.turismokotlin.data.repository.PlanesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanesViewModel @Inject constructor(
    private val planesRepository: PlanesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlanesUiState())
    val uiState: StateFlow<PlanesUiState> = _uiState.asStateFlow()

    init {
        loadPlanes()
    }

    fun loadPlanes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val planesDeferred = async { planesRepository.getAllPlanes() }
            val planesPopularesDeferred = async { planesRepository.getPlanesPopulares() }

            val planesResult = planesDeferred.await()
            val planesPopularesResult = planesPopularesDeferred.await()

            val planes = planesResult.getOrElse { emptyList() }
            val planesPopulares = planesPopularesResult.getOrElse { emptyList() }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                planes = planes,
                planesPopulares = planesPopulares,
                error = when {
                    planesResult.isFailure -> planesResult.exceptionOrNull()?.message
                    planesPopularesResult.isFailure -> planesPopularesResult.exceptionOrNull()?.message
                    else -> null
                }
            )
        }
    }

    fun searchPlanes(termino: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true, error = null)

            planesRepository.searchPlanes(termino)
                .onSuccess { planes ->
                    _uiState.value = _uiState.value.copy(
                        isSearching = false,
                        searchResults = planes
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

    fun getPlanesByPrecio(precioMin: Double, precioMax: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            planesRepository.getPlanesByPrecio(precioMin, precioMax)
                .onSuccess { planes ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        filteredPlanes = planes
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

    fun clearSearchResults() {
        _uiState.value = _uiState.value.copy(searchResults = emptyList(), filteredPlanes = emptyList())
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class PlanesUiState(
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val planes: List<Plan> = emptyList(),
    val planesPopulares: List<Plan> = emptyList(),
    val searchResults: List<Plan> = emptyList(),
    val filteredPlanes: List<Plan> = emptyList(),
    val error: String? = null
)