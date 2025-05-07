package com.capachica.turismokotlin.data.repository

import android.util.Log
import com.capachica.turismokotlin.data.api.ApiService
import com.capachica.turismokotlin.data.model.Categoria
import com.capachica.turismokotlin.data.model.CategoriaRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException

class CategoriaRepository(
    private val apiService: ApiService
) {
    private val TAG = "CategoriaRepository"

    fun getAllCategorias(): Flow<Result<List<Categoria>>> = flow {
        emit(Result.Loading)

        try {
            val response = apiService.getAllCategorias()
            if (response.isSuccessful) {
                response.body()?.let { categorias ->
                    Log.d(TAG, "Obtenidas ${categorias.size} categorías del servidor")
                    emit(Result.Success(categorias))
                } ?: emit(Result.Error("Respuesta vacía del servidor"))
            } else {
                emit(Result.Error("Error: ${response.code()}"))
                Log.e(TAG, "Error al obtener datos del servidor: ${response.code()}")
            }
        } catch (e: IOException) {
            emit(Result.Error("Error de conexión: ${e.message}"))
            Log.e(TAG, "Error de conexión", e)
        } catch (e: Exception) {
            emit(Result.Error("Error en la solicitud: ${e.message}"))
            Log.e(TAG, "Error inesperado", e)
        }
    }.flowOn(Dispatchers.IO)

    fun getCategoriaById(id: Long): Flow<Result<Categoria>> = flow {
        emit(Result.Loading)

        try {
            val response = apiService.getCategoriaById(id)
            if (response.isSuccessful) {
                response.body()?.let { categoria ->
                    Log.d(TAG, "Obtenida categoría con ID $id del servidor")
                    emit(Result.Success(categoria))
                } ?: emit(Result.Error("Respuesta vacía del servidor"))
            } else {
                emit(Result.Error("Error: ${response.code()}"))
                Log.e(TAG, "Error al obtener datos del servidor: ${response.code()}")
            }
        } catch (e: IOException) {
            emit(Result.Error("Error de conexión: ${e.message}"))
            Log.e(TAG, "Error de conexión", e)
        } catch (e: Exception) {
            emit(Result.Error("Error en la solicitud: ${e.message}"))
            Log.e(TAG, "Error inesperado", e)
        }
    }.flowOn(Dispatchers.IO)

    fun createCategoria(request: CategoriaRequest): Flow<Result<Categoria>> = flow {
        emit(Result.Loading)

        try {
            val response = apiService.createCategoria(request)
            if (response.isSuccessful) {
                response.body()?.let { categoria ->
                    Log.d(TAG, "Categoría creada en el servidor con ID ${categoria.id}")
                    emit(Result.Success(categoria))
                } ?: emit(Result.Error("Respuesta vacía del servidor"))
            } else {
                emit(Result.Error("Error: ${response.code()}"))
                Log.e(TAG, "Error al crear categoría: ${response.code()}")
            }
        } catch (e: IOException) {
            emit(Result.Error("Error de conexión: ${e.message}"))
            Log.e(TAG, "Error de conexión", e)
        } catch (e: Exception) {
            emit(Result.Error("Error en la solicitud: ${e.message}"))
            Log.e(TAG, "Error inesperado", e)
        }
    }.flowOn(Dispatchers.IO)

    fun updateCategoria(id: Long, request: CategoriaRequest): Flow<Result<Categoria>> = flow {
        emit(Result.Loading)

        try {
            val response = apiService.updateCategoria(id, request)
            if (response.isSuccessful) {
                response.body()?.let { categoria ->
                    Log.d(TAG, "Categoría actualizada en el servidor con ID ${categoria.id}")
                    emit(Result.Success(categoria))
                } ?: emit(Result.Error("Respuesta vacía del servidor"))
            } else {
                emit(Result.Error("Error: ${response.code()}"))
                Log.e(TAG, "Error al actualizar categoría: ${response.code()}")
            }
        } catch (e: IOException) {
            emit(Result.Error("Error de conexión: ${e.message}"))
            Log.e(TAG, "Error de conexión", e)
        } catch (e: Exception) {
            emit(Result.Error("Error en la solicitud: ${e.message}"))
            Log.e(TAG, "Error inesperado", e)
        }
    }.flowOn(Dispatchers.IO)

    fun deleteCategoria(id: Long): Flow<Result<Boolean>> = flow {
        emit(Result.Loading)

        try {
            val response = apiService.deleteCategoria(id)
            if (response.isSuccessful) {
                Log.d(TAG, "Categoría eliminada del servidor con ID $id")
                emit(Result.Success(true))
            } else {
                emit(Result.Error("Error: ${response.code()}"))
                Log.e(TAG, "Error al eliminar categoría: ${response.code()}")
            }
        } catch (e: IOException) {
            emit(Result.Error("Error de conexión: ${e.message}"))
            Log.e(TAG, "Error de conexión", e)
        } catch (e: Exception) {
            emit(Result.Error("Error en la solicitud: ${e.message}"))
            Log.e(TAG, "Error inesperado", e)
        }
    }.flowOn(Dispatchers.IO)
}