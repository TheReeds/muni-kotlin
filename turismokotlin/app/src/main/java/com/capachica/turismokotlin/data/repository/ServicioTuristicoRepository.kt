package com.capachica.turismokotlin.data.repository

import com.capachica.turismokotlin.data.api.ApiService
import com.capachica.turismokotlin.data.model.*
import com.capachica.turismokotlin.utils.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ServicioTuristicoRepository @Inject constructor(
    private val apiService: ApiService
) {
    
    fun getAllServicios(): Flow<Result<List<ServicioTuristico>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getAllServicios()
            if (response.isSuccessful) {
                emit(Result.Success(response.body() ?: emptyList()))
            } else {
                emit(Result.Error("Error al obtener servicios: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun getServicioById(id: Long): Flow<Result<ServicioTuristico>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getServicioById(id)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Error("Servicio no encontrado"))
            } else {
                emit(Result.Error("Error al obtener servicio: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun getServiciosByEmprendedor(emprendedorId: Long): Flow<Result<List<ServicioTuristico>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getServiciosByEmprendedor(emprendedorId)
            if (response.isSuccessful) {
                emit(Result.Success(response.body() ?: emptyList()))
            } else {
                emit(Result.Error("Error al obtener servicios: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun getServiciosByMunicipalidad(municipalidadId: Long): Flow<Result<List<ServicioTuristico>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getServiciosByMunicipalidad(municipalidadId)
            if (response.isSuccessful) {
                emit(Result.Success(response.body() ?: emptyList()))
            } else {
                emit(Result.Error("Error al obtener servicios: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun getServiciosByTipo(tipo: TipoServicio): Flow<Result<List<ServicioTuristico>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getServiciosByTipo(tipo.name)
            if (response.isSuccessful) {
                emit(Result.Success(response.body() ?: emptyList()))
            } else {
                emit(Result.Error("Error al obtener servicios: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun getServiciosByEstado(estado: EstadoServicio): Flow<Result<List<ServicioTuristico>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getServiciosByEstado(estado.name)
            if (response.isSuccessful) {
                emit(Result.Success(response.body() ?: emptyList()))
            } else {
                emit(Result.Error("Error al obtener servicios: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun searchServicios(termino: String): Flow<Result<List<ServicioTuristico>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.searchServicios(termino)
            if (response.isSuccessful) {
                emit(Result.Success(response.body() ?: emptyList()))
            } else {
                emit(Result.Error("Error al buscar servicios: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun getMisServicios(): Flow<Result<List<ServicioTuristico>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getMisServicios()
            if (response.isSuccessful) {
                emit(Result.Success(response.body() ?: emptyList()))
            } else {
                emit(Result.Error("Error al obtener mis servicios: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun createServicio(request: ServicioTuristicoRequest): Flow<Result<ServicioTuristico>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.createServicio(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Error("Error al crear servicio"))
            } else {
                emit(Result.Error("Error al crear servicio: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun updateServicio(id: Long, request: ServicioTuristicoRequest): Flow<Result<ServicioTuristico>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.updateServicio(id, request)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Error("Error al actualizar servicio"))
            } else {
                emit(Result.Error("Error al actualizar servicio: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun deleteServicio(id: Long): Flow<Result<Boolean>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.deleteServicio(id)
            if (response.isSuccessful) {
                emit(Result.Success(true))
            } else {
                emit(Result.Error("Error al eliminar servicio: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun cambiarEstadoServicio(id: Long, estado: EstadoServicio): Flow<Result<ServicioTuristico>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.cambiarEstadoServicio(id, estado.name)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Error("Error al cambiar estado"))
            } else {
                emit(Result.Error("Error al cambiar estado: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
}