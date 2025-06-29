package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.*
import com.capachica.turismokotlin.data.repository.UbicacionRepository
import com.capachica.turismokotlin.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UbicacionViewModel(private val repository: UbicacionRepository) : ViewModel() {

    private val _emprendedoresUbicacionState = MutableStateFlow<Result<List<EmprendedorUbicacion>>>(Result.Loading)
    val emprendedoresUbicacionState: StateFlow<Result<List<EmprendedorUbicacion>>> = _emprendedoresUbicacionState

    private val _serviciosUbicacionState = MutableStateFlow<Result<List<ServicioUbicacion>>>(Result.Loading)
    val serviciosUbicacionState: StateFlow<Result<List<ServicioUbicacion>>> = _serviciosUbicacionState

    private val _busquedaCercanosState = MutableStateFlow<Result<BusquedaCercanosResponse>?>(null)
    val busquedaCercanosState: StateFlow<Result<BusquedaCercanosResponse>?> = _busquedaCercanosState

    private val _distanciaState = MutableStateFlow<Result<DistanciaResponse>?>(null)
    val distanciaState: StateFlow<Result<DistanciaResponse>?> = _distanciaState

    private val _validacionState = MutableStateFlow<Result<ValidacionCoordenadaResponse>?>(null)
    val validacionState: StateFlow<Result<ValidacionCoordenadaResponse>?> = _validacionState

    private val _updateLocationState = MutableStateFlow<Result<UbicacionResponse>?>(null)
    val updateLocationState: StateFlow<Result<UbicacionResponse>?> = _updateLocationState

    // Coordenadas seleccionadas por el usuario en el mapa
    private val _coordenadasSeleccionadas = MutableStateFlow<Pair<Double, Double>?>(null)
    val coordenadasSeleccionadas: StateFlow<Pair<Double, Double>?> = _coordenadasSeleccionadas

    private val _direccionCompleta = MutableStateFlow<String>("")
    val direccionCompleta: StateFlow<String> = _direccionCompleta

    fun cargarEmprendedoresUbicacion() {
        viewModelScope.launch {
            repository.getUbicacionesEmprendedores().collect { result ->
                _emprendedoresUbicacionState.value = result
            }
        }
    }

    fun cargarServiciosUbicacion() {
        viewModelScope.launch {
            repository.getUbicacionesServicios().collect { result ->
                _serviciosUbicacionState.value = result
            }
        }
    }

    fun buscarCercanos(
        latitud: Double,
        longitud: Double,
        radio: Double = 10.0,
        tipo: String? = null
    ) {
        viewModelScope.launch {
            _busquedaCercanosState.value = Result.Loading
            repository.buscarCercanos(latitud, longitud, radio, tipo).collect { result ->
                _busquedaCercanosState.value = result
            }
        }
    }

    fun calcularDistancia(
        latitudOrigen: Double,
        longitudOrigen: Double,
        latitudDestino: Double,
        longitudDestino: Double
    ) {
        viewModelScope.launch {
            _distanciaState.value = Result.Loading
            repository.calcularDistancia(latitudOrigen, longitudOrigen, latitudDestino, longitudDestino)
                .collect { result ->
                    _distanciaState.value = result
                }
        }
    }

    fun validarCoordenadas(latitud: Double, longitud: Double) {
        viewModelScope.launch {
            _validacionState.value = Result.Loading
            repository.validarCoordenadas(latitud, longitud).collect { result ->
                _validacionState.value = result
            }
        }
    }

    fun actualizarUbicacionEmprendedor(emprendedorId: Long, request: UbicacionRequest) {
        viewModelScope.launch {
            _updateLocationState.value = Result.Loading
            repository.actualizarUbicacionEmprendedor(emprendedorId, request).collect { result ->
                _updateLocationState.value = result
            }
        }
    }

    fun actualizarUbicacionServicio(servicioId: Long, request: UbicacionRequest) {
        viewModelScope.launch {
            _updateLocationState.value = Result.Loading
            repository.actualizarUbicacionServicio(servicioId, request).collect { result ->
                _updateLocationState.value = result
            }
        }
    }

    // Funciones para manejar la selección de ubicación en el mapa
    fun seleccionarCoordenadas(latitud: Double, longitud: Double) {
        _coordenadasSeleccionadas.value = Pair(latitud, longitud)
        // Validar automáticamente las coordenadas seleccionadas
        validarCoordenadas(latitud, longitud)
    }

    fun actualizarDireccionCompleta(direccion: String) {
        _direccionCompleta.value = direccion
    }

    fun limpiarSeleccion() {
        _coordenadasSeleccionadas.value = null
        _direccionCompleta.value = ""
        _validacionState.value = null
    }

    fun crearUbicacionRequest(): UbicacionRequest? {
        val coordenadas = _coordenadasSeleccionadas.value
        return if (coordenadas != null) {
            UbicacionRequest(
                latitud = coordenadas.first,
                longitud = coordenadas.second,
                direccionCompleta = if (_direccionCompleta.value.isNotEmpty()) _direccionCompleta.value else null
            )
        } else null
    }

    fun clearStates() {
        _busquedaCercanosState.value = null
        _distanciaState.value = null
        _validacionState.value = null
        _updateLocationState.value = null
    }
}