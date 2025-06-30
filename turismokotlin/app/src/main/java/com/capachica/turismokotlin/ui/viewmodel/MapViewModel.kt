package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.Emprendedor
import com.capachica.turismokotlin.data.model.Servicio
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
class MapViewModel @Inject constructor(
    private val serviciosRepository: ServiciosRepository,
    private val emprendedoresRepository: EmprendedoresRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    fun loadNearbyServices(latitud: Double, longitud: Double, radio: Double = 5.0) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val serviciosDeferred = async { serviciosRepository.getServiciosCercanos(latitud, longitud, radio) }
            val emprendedoresDeferred = async { emprendedoresRepository.getEmprendedoresCercanos(latitud, longitud, radio) }

            val serviciosResult = serviciosDeferred.await()
            val emprendedoresResult = emprendedoresDeferred.await()

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                serviciosCercanos = serviciosResult.getOrElse { emptyList() },
                emprendedoresCercanos = emprendedoresResult.getOrElse { emptyList() },
                userLatitude = latitud,
                userLongitude = longitud,
                searchRadius = radio,
                error = when {
                    serviciosResult.isFailure -> serviciosResult.exceptionOrNull()?.message
                    emprendedoresResult.isFailure -> emprendedoresResult.exceptionOrNull()?.message
                    else -> null
                }
            )
        }
    }

    fun updateSearchRadius(radio: Double) {
        val currentState = _uiState.value
        if (currentState.userLatitude != null && currentState.userLongitude != null) {
            loadNearbyServices(currentState.userLatitude, currentState.userLongitude, radio)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class MapUiState(
    val isLoading: Boolean = false,
    val serviciosCercanos: List<Servicio> = emptyList(),
    val emprendedoresCercanos: List<Emprendedor> = emptyList(),
    val userLatitude: Double? = null,
    val userLongitude: Double? = null,
    val searchRadius: Double = 5.0,
    val error: String? = null
)