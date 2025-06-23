package com.capachica.turismokotlin.data.repository

import com.capachica.turismokotlin.data.api.ApiService
import com.capachica.turismokotlin.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PlanTuristicoRepository @Inject constructor(
    private val apiService: ApiService
) {
    
    fun getAllPlanes(): Flow<Result<List<PlanTuristico>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getAllPlanes()
            if (response.isSuccessful) {
                emit(Result.Success(response.body() ?: emptyList()))
            } else {
                emit(Result.Error("Error al obtener planes: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun getPlanById(id: Long): Flow<Result<PlanTuristico>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getPlanById(id)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Error("Plan no encontrado"))
            } else {
                emit(Result.Error("Error al obtener plan: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun getPlanesByMunicipalidad(municipalidadId: Long): Flow<Result<List<PlanTuristico>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getPlanesByMunicipalidad(municipalidadId)
            if (response.isSuccessful) {
                emit(Result.Success(response.body() ?: emptyList()))
            } else {
                emit(Result.Error("Error al obtener planes: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun getPlanesByEstado(estado: EstadoPlan): Flow<Result<List<PlanTuristico>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getPlanesByEstado(estado.name)
            if (response.isSuccessful) {
                emit(Result.Success(response.body() ?: emptyList()))
            } else {
                emit(Result.Error("Error al obtener planes: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun getPlanesByNivelDificultad(nivel: NivelDificultad): Flow<Result<List<PlanTuristico>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getPlanesByNivelDificultad(nivel.name)
            if (response.isSuccessful) {
                emit(Result.Success(response.body() ?: emptyList()))
            } else {
                emit(Result.Error("Error al obtener planes: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun getPlanesByDuracion(duracionMin: Int, duracionMax: Int): Flow<Result<List<PlanTuristico>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getPlanesByDuracion(duracionMin, duracionMax)
            if (response.isSuccessful) {
                emit(Result.Success(response.body() ?: emptyList()))
            } else {
                emit(Result.Error("Error al obtener planes: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun getPlanesByPrecio(precioMin: Double, precioMax: Double): Flow<Result<List<PlanTuristico>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getPlanesByPrecio(precioMin, precioMax)
            if (response.isSuccessful) {
                emit(Result.Success(response.body() ?: emptyList()))
            } else {
                emit(Result.Error("Error al obtener planes: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun searchPlanes(termino: String): Flow<Result<List<PlanTuristico>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.searchPlanes(termino)
            if (response.isSuccessful) {
                emit(Result.Success(response.body() ?: emptyList()))
            } else {
                emit(Result.Error("Error al buscar planes: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun getMisPlanes(): Flow<Result<List<PlanTuristico>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getMisPlanes()
            if (response.isSuccessful) {
                emit(Result.Success(response.body() ?: emptyList()))
            } else {
                emit(Result.Error("Error al obtener mis planes: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun getPlanesMasPopulares(): Flow<Result<List<PlanTuristico>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getPlanesMasPopulares()
            if (response.isSuccessful) {
                emit(Result.Success(response.body() ?: emptyList()))
            } else {
                emit(Result.Error("Error al obtener planes populares: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun createPlan(request: PlanTuristicoRequest): Flow<Result<PlanTuristico>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.createPlan(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Error("Error al crear plan"))
            } else {
                emit(Result.Error("Error al crear plan: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun updatePlan(id: Long, request: PlanTuristicoRequest): Flow<Result<PlanTuristico>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.updatePlan(id, request)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Error("Error al actualizar plan"))
            } else {
                emit(Result.Error("Error al actualizar plan: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun deletePlan(id: Long): Flow<Result<Boolean>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.deletePlan(id)
            if (response.isSuccessful) {
                emit(Result.Success(true))
            } else {
                emit(Result.Error("Error al eliminar plan: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de red: ${e.message}"))
        }
    }
    
    fun cambiarEstadoPlan(id: Long, estado: EstadoPlan): Flow<Result<PlanTuristico>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.cambiarEstadoPlan(id, estado.name)
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