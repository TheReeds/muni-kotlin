package com.capachica.turismokotlin.data.repository

import com.capachica.turismokotlin.data.model.Categoria
import com.capachica.turismokotlin.data.model.CreateCategoriaRequest
import com.capachica.turismokotlin.data.model.UpdateCategoriaRequest
import com.capachica.turismokotlin.network.api.AdminCategoriasApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminCategoriasRepository @Inject constructor(
    private val adminCategoriasApiService: AdminCategoriasApiService
) {
    suspend fun getAllCategorias(): Result<List<Categoria>> {
        return try {
            val response = adminCategoriasApiService.getAllCategorias()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener categorías: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createCategoria(request: CreateCategoriaRequest): Result<Categoria> {
        return try {
            val response = adminCategoriasApiService.createCategoria(request)
            if (response.isSuccessful) {
                response.body()?.let { categoria ->
                    Result.success(categoria)
                } ?: Result.failure(Exception("Error al crear categoría"))
            } else {
                Result.failure(Exception("Error al crear categoría: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCategoria(categoriaId: Long, request: UpdateCategoriaRequest): Result<Categoria> {
        return try {
            val response = adminCategoriasApiService.updateCategoria(categoriaId, request)
            if (response.isSuccessful) {
                response.body()?.let { categoria ->
                    Result.success(categoria)
                } ?: Result.failure(Exception("Error al actualizar categoría"))
            } else {
                Result.failure(Exception("Error al actualizar categoría: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCategoria(categoriaId: Long): Result<Unit> {
        return try {
            val response = adminCategoriasApiService.deleteCategoria(categoriaId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar categoría: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}