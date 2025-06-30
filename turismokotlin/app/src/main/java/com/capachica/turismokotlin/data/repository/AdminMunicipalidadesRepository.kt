package com.capachica.turismokotlin.data.repository

import com.capachica.turismokotlin.data.model.CreateMunicipalidadRequest
import com.capachica.turismokotlin.data.model.MunicipalidadDetallada
import com.capachica.turismokotlin.data.model.UpdateMunicipalidadRequest
import com.capachica.turismokotlin.network.api.AdminMunicipalidadesApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminMunicipalidadesRepository @Inject constructor(
    private val adminMunicipalidadesApiService: AdminMunicipalidadesApiService
) {
    suspend fun getAllMunicipalidades(): Result<List<MunicipalidadDetallada>> {
        return try {
            val response = adminMunicipalidadesApiService.getAllMunicipalidades()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener municipalidades: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createMunicipalidad(request: CreateMunicipalidadRequest): Result<MunicipalidadDetallada> {
        return try {
            val response = adminMunicipalidadesApiService.createMunicipalidad(request)
            if (response.isSuccessful) {
                response.body()?.let { municipalidad ->
                    Result.success(municipalidad)
                } ?: Result.failure(Exception("Error al crear municipalidad"))
            } else {
                Result.failure(Exception("Error al crear municipalidad: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateMunicipalidad(municipalidadId: Long, request: UpdateMunicipalidadRequest): Result<MunicipalidadDetallada> {
        return try {
            val response = adminMunicipalidadesApiService.updateMunicipalidad(municipalidadId, request)
            if (response.isSuccessful) {
                response.body()?.let { municipalidad ->
                    Result.success(municipalidad)
                } ?: Result.failure(Exception("Error al actualizar municipalidad"))
            } else {
                Result.failure(Exception("Error al actualizar municipalidad: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteMunicipalidad(municipalidadId: Long): Result<Unit> {
        return try {
            val response = adminMunicipalidadesApiService.deleteMunicipalidad(municipalidadId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar municipalidad: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}