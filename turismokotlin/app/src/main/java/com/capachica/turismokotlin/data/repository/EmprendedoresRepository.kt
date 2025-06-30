package com.capachica.turismokotlin.data.repository

import com.capachica.turismokotlin.data.model.Emprendedor
import com.capachica.turismokotlin.network.api.EmprendedoresApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmprendedoresRepository @Inject constructor(
    private val emprendedoresApiService: EmprendedoresApiService
) {
    suspend fun getAllEmprendedores(): Result<List<Emprendedor>> {
        return try {
            val response = emprendedoresApiService.getAllEmprendedores()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get emprendedores: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEmprendedorById(emprendedorId: Long): Result<Emprendedor> {
        return try {
            val response = emprendedoresApiService.getEmprendedorById(emprendedorId)
            if (response.isSuccessful) {
                response.body()?.let { emprendedor ->
                    Result.success(emprendedor)
                } ?: Result.failure(Exception("Emprendedor not found"))
            } else {
                Result.failure(Exception("Failed to get emprendedor: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEmprendedoresCercanos(latitud: Double, longitud: Double, radio: Double = 5.0): Result<List<Emprendedor>> {
        return try {
            val response = emprendedoresApiService.getEmprendedoresCercanos(latitud, longitud, radio)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get nearby emprendedores: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEmprendedoresByCategoria(categoriaId: Long): Result<List<Emprendedor>> {
        return try {
            val response = emprendedoresApiService.getEmprendedoresByCategoria(categoriaId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get emprendedores by categoria: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}