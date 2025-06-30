package com.capachica.turismokotlin.data.repository

import com.capachica.turismokotlin.data.model.CreateEmprendedorRequest
import com.capachica.turismokotlin.data.model.Emprendedor
import com.capachica.turismokotlin.data.model.UpdateEmprendedorRequest
import com.capachica.turismokotlin.network.api.AdminEmprendedoresApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminEmprendedoresRepository @Inject constructor(
    private val adminEmprendedoresApiService: AdminEmprendedoresApiService
) {
    suspend fun getAllEmprendedores(): Result<List<Emprendedor>> {
        return try {
            val response = adminEmprendedoresApiService.getAllEmprendedores()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener emprendedores: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEmprendedorById(emprendedorId: Long): Result<Emprendedor> {
        return try {
            val response = adminEmprendedoresApiService.getEmprendedorById(emprendedorId)
            if (response.isSuccessful) {
                response.body()?.let { emprendedor ->
                    Result.success(emprendedor)
                } ?: Result.failure(Exception("Emprendedor no encontrado"))
            } else {
                Result.failure(Exception("Error al obtener emprendedor: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMiEmprendedor(): Result<Emprendedor> {
        return try {
            val response = adminEmprendedoresApiService.getMiEmprendedor()
            if (response.isSuccessful) {
                response.body()?.let { emprendedor ->
                    Result.success(emprendedor)
                } ?: Result.failure(Exception("No tienes emprendedor asignado"))
            } else {
                Result.failure(Exception("Error al obtener mi emprendedor: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createEmprendedor(request: CreateEmprendedorRequest): Result<Emprendedor> {
        return try {
            val response = adminEmprendedoresApiService.createEmprendedor(request)
            if (response.isSuccessful) {
                response.body()?.let { emprendedor ->
                    Result.success(emprendedor)
                } ?: Result.failure(Exception("Error al crear emprendedor"))
            } else {
                Result.failure(Exception("Error al crear emprendedor: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateEmprendedor(emprendedorId: Long, request: UpdateEmprendedorRequest): Result<Emprendedor> {
        return try {
            val response = adminEmprendedoresApiService.updateEmprendedor(emprendedorId, request)
            if (response.isSuccessful) {
                response.body()?.let { emprendedor ->
                    Result.success(emprendedor)
                } ?: Result.failure(Exception("Error al actualizar emprendedor"))
            } else {
                Result.failure(Exception("Error al actualizar emprendedor: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteEmprendedor(emprendedorId: Long): Result<Unit> {
        return try {
            val response = adminEmprendedoresApiService.deleteEmprendedor(emprendedorId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar emprendedor: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}