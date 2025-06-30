package com.capachica.turismokotlin.data.repository

import com.capachica.turismokotlin.data.model.Categoria
import com.capachica.turismokotlin.network.api.CategoriasApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoriasRepository @Inject constructor(
    private val categoriasApiService: CategoriasApiService
) {
    suspend fun getAllCategorias(): Result<List<Categoria>> {
        return try {
            val response = categoriasApiService.getAllCategorias()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get categorias: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategoriaById(categoriaId: Long): Result<Categoria> {
        return try {
            val response = categoriasApiService.getCategoriaById(categoriaId)
            if (response.isSuccessful) {
                response.body()?.let { categoria ->
                    Result.success(categoria)
                } ?: Result.failure(Exception("Categoria not found"))
            } else {
                Result.failure(Exception("Failed to get categoria: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}