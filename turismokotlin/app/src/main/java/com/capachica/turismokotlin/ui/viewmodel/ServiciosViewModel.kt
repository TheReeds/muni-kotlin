package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.Servicio
import com.capachica.turismokotlin.data.repository.ServiciosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServiciosViewModel @Inject constructor(
    private val serviciosRepository: ServiciosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServiciosUiState())
    val uiState: StateFlow<ServiciosUiState> = _uiState.asStateFlow()

    init {
        loadServicios()
    }

    fun loadServicios() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            serviciosRepository.getAllServicios()
                .onSuccess { servicios ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        servicios = servicios
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

    fun searchServicios(termino: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true, error = null)

            serviciosRepository.searchServicios(termino)
                .onSuccess { servicios ->
                    _uiState.value = _uiState.value.copy(
                        isSearching = false,
                        searchResults = servicios
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
        _uiState.value = _uiState.value.copy(searchResults = emptyList())
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class ServiciosUiState(
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val servicios: List<Servicio> = emptyList(),
    val searchResults: List<Servicio> = emptyList(),
    val error: String? = null
)