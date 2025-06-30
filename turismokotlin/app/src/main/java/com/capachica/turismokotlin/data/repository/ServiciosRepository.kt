package com.capachica.turismokotlin.data.repository

import com.capachica.turismokotlin.data.model.ActualizarServicioRequest
import com.capachica.turismokotlin.data.model.CrearServicioRequest
import com.capachica.turismokotlin.data.model.Servicio
import com.capachica.turismokotlin.network.api.ServiciosApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiciosRepository @Inject constructor(
    private val serviciosApiService: ServiciosApiService
) {
    suspend fun getAllServicios(): Result<List<Servicio>> {
        return try {
            val response = serviciosApiService.getAllServicios()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get servicios: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getServicioById(servicioId: Long): Result<Servicio> {
        return try {
            val response = serviciosApiService.getServicioById(servicioId)
            if (response.isSuccessful) {
                response.body()?.let { servicio ->
                    Result.success(servicio)
                } ?: Result.failure(Exception("Servicio not found"))
            } else {
                Result.failure(Exception("Failed to get servicio: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchServicios(termino: String): Result<List<Servicio>> {
        return try {
            val response = serviciosApiService.searchServicios(termino)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Search failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getServiciosByEmprendedor(emprendedorId: Long): Result<List<Servicio>> {
        return try {
            val response = serviciosApiService.getServiciosByEmprendedor(emprendedorId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get servicios by emprendedor: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getServiciosCercanos(latitud: Double, longitud: Double, radio: Double = 5.0): Result<List<Servicio>> {
        return try {
            val response = serviciosApiService.getServiciosCercanos(latitud, longitud, radio)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get nearby servicios: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getMisServicios(): Result<List<Servicio>> {
        return try {
            val response = serviciosApiService.getMisServicios()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener mis servicios: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun crearServicio(request: CrearServicioRequest): Result<Servicio> {
        return try {
            val response = serviciosApiService.crearServicio(request)
            if (response.isSuccessful) {
                response.body()?.let { servicio ->
                    Result.success(servicio)
                } ?: Result.failure(Exception("Error al crear servicio"))
            } else {
                Result.failure(Exception("Error al crear servicio: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarServicio(servicioId: Long, request: ActualizarServicioRequest): Result<Servicio> {
        return try {
            val response = serviciosApiService.actualizarServicio(servicioId, request)
            if (response.isSuccessful) {
                response.body()?.let { servicio ->
                    Result.success(servicio)
                } ?: Result.failure(Exception("Error al actualizar servicio"))
            } else {
                Result.failure(Exception("Error al actualizar servicio: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarServicio(servicioId: Long): Result<Unit> {
        return try {
            val response = serviciosApiService.eliminarServicio(servicioId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar servicio: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}