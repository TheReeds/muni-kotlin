package com.capachica.turismokotlin.data.repository

import com.capachica.turismokotlin.data.model.ActualizarPlanRequest
import com.capachica.turismokotlin.data.model.CrearPlanRequest
import com.capachica.turismokotlin.data.model.EstadoPlan
import com.capachica.turismokotlin.data.model.Plan
import com.capachica.turismokotlin.network.api.PlanesApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlanesRepository @Inject constructor(
    private val planesApiService: PlanesApiService
) {
    suspend fun getAllPlanes(): Result<List<Plan>> {
        return try {
            val response = planesApiService.getAllPlanes()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get planes: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPlanById(planId: Long): Result<Plan> {
        return try {
            val response = planesApiService.getPlanById(planId)
            if (response.isSuccessful) {
                response.body()?.let { plan ->
                    Result.success(plan)
                } ?: Result.failure(Exception("Plan not found"))
            } else {
                Result.failure(Exception("Failed to get plan: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchPlanes(termino: String): Result<List<Plan>> {
        return try {
            val response = planesApiService.searchPlanes(termino)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Search failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPlanesPopulares(): Result<List<Plan>> {
        return try {
            val response = planesApiService.getPlanesPopulares()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get popular planes: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPlanesByMunicipalidad(municipalidadId: Long): Result<List<Plan>> {
        return try {
            val response = planesApiService.getPlanesByMunicipalidad(municipalidadId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get planes by municipalidad: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPlanesByEstado(estado: EstadoPlan): Result<List<Plan>> {
        return try {
            val response = planesApiService.getPlanesByEstado(estado.name)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get planes by estado: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getPlanesByPrecio(precioMin: Double, precioMax: Double): Result<List<Plan>> {
        return try {
            val response = planesApiService.getPlanesByPrecio(precioMin, precioMax)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener planes por precio: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getMisPlanes(): Result<List<Plan>> {
        return try {
            val response = planesApiService.getMisPlanes()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener mis planes: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun crearPlan(request: CrearPlanRequest): Result<Plan> {
        return try {
            val response = planesApiService.crearPlan(request)
            if (response.isSuccessful) {
                response.body()?.let { plan ->
                    Result.success(plan)
                } ?: Result.failure(Exception("Error al crear plan"))
            } else {
                Result.failure(Exception("Error al crear plan: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarPlan(planId: Long, request: ActualizarPlanRequest): Result<Plan> {
        return try {
            val response = planesApiService.actualizarPlan(planId, request)
            if (response.isSuccessful) {
                response.body()?.let { plan ->
                    Result.success(plan)
                } ?: Result.failure(Exception("Error al actualizar plan"))
            } else {
                Result.failure(Exception("Error al actualizar plan: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cambiarEstadoPlan(planId: Long, nuevoEstado: EstadoPlan): Result<Plan> {
        return try {
            val response = planesApiService.cambiarEstadoPlan(planId, nuevoEstado.name)
            if (response.isSuccessful) {
                response.body()?.let { plan ->
                    Result.success(plan)
                } ?: Result.failure(Exception("Error al cambiar estado del plan"))
            } else {
                Result.failure(Exception("Error al cambiar estado del plan: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarPlan(planId: Long): Result<Unit> {
        return try {
            val response = planesApiService.eliminarPlan(planId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar plan: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}