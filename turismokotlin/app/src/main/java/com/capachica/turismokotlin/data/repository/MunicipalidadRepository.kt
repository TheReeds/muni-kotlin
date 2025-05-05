package com.capachica.turismokotlin.data.repository

import android.util.Log
import com.capachica.turismokotlin.data.api.ApiService
import com.capachica.turismokotlin.data.local.dao.EmprendedorMunicipalidadDao
import com.capachica.turismokotlin.data.local.dao.MunicipalidadDao
import com.capachica.turismokotlin.data.local.entity.MunicipalidadEntity
import com.capachica.turismokotlin.data.model.Municipalidad
import com.capachica.turismokotlin.data.model.MunicipalidadRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.io.IOException

class MunicipalidadRepository(
    private val apiService: ApiService,
    private val municipalidadDao: MunicipalidadDao,
    private val emprendedorMunicipalidadDao: EmprendedorMunicipalidadDao
) {
    private val TAG = "MunicipalidadRepository"

    fun getAllMunicipalidades(): Flow<Result<List<Municipalidad>>> = flow {
        emit(Result.Loading)

        // Primero emitimos datos de la base de datos local
        val localData = municipalidadDao.getAllMunicipalidades().first()
        if (localData.isNotEmpty()) {
            Log.d(TAG, "Emitiendo ${localData.size} municipalidades desde la base de datos local")

            // Para cada municipalidad, buscamos sus emprendedores
            val municipalidades = withContext(Dispatchers.IO) {
                localData.map { entity ->
                    val emprendedores = emprendedorMunicipalidadDao.getEmprendedoresByMunicipalidadId(entity.id).firstOrNull() ?: emptyList()
                    entity.toModel(emprendedores)
                }
            }

            emit(Result.Success(municipalidades))
        }

        // Luego intentamos obtener datos del servidor
        try {
            val response = apiService.getAllMunicipalidades()
            if (response.isSuccessful) {
                response.body()?.let { municipalidades ->
                    Log.d(TAG, "Obtenidas ${municipalidades.size} municipalidades del servidor")

                    // Guardamos en la base de datos local
                    withContext(Dispatchers.IO) {
                        val entities = municipalidades.map { MunicipalidadEntity.fromModel(it) }
                        municipalidadDao.insertMunicipalidades(entities)
                    }

                    // Emitimos los datos actualizados
                    emit(Result.Success(municipalidades))
                } ?: run {
                    if (localData.isEmpty()) {
                        emit(Result.Error("Respuesta vacía del servidor"))
                    }
                }
            } else {
                if (localData.isEmpty()) {
                    emit(Result.Error("Error: ${response.code()}"))
                }
                // Si ya emitimos datos locales, no emitimos error
                Log.e(TAG, "Error al obtener datos del servidor: ${response.code()}")
            }
        } catch (e: IOException) {
            // Error de red, pero ya hemos mostrado datos locales si estaban disponibles
            if (localData.isEmpty()) {
                emit(Result.Error("Error de conexión: ${e.message}"))
            }
            Log.e(TAG, "Error de conexión", e)
        } catch (e: Exception) {
            if (localData.isEmpty()) {
                emit(Result.Error("Error en la solicitud: ${e.message}"))
            }
            Log.e(TAG, "Error inesperado", e)
        }
    }.flowOn(Dispatchers.IO)

    fun getMunicipalidadById(id: Long): Flow<Result<Municipalidad>> = flow {
        emit(Result.Loading)

        // Primero emitimos datos de la base de datos local
        val localData = municipalidadDao.getMunicipalidadById(id).firstOrNull()
        if (localData != null) {
            Log.d(TAG, "Emitiendo municipalidad con ID $id desde la base de datos local")

            // Buscamos los emprendedores asociados
            val emprendedores = emprendedorMunicipalidadDao.getEmprendedoresByMunicipalidadId(id).first()
            val municipalidad = localData.toModel(emprendedores)

            emit(Result.Success(municipalidad))
        }

        // Luego intentamos obtener datos actualizados del servidor
        try {
            val response = apiService.getMunicipalidadById(id)
            if (response.isSuccessful) {
                response.body()?.let { municipalidad ->
                    Log.d(TAG, "Obtenida municipalidad con ID $id del servidor")

                    // Guardamos en la base de datos local
                    withContext(Dispatchers.IO) {
                        val entity = MunicipalidadEntity.fromModel(municipalidad)
                        municipalidadDao.insertMunicipalidad(entity)
                    }

                    // Emitimos los datos actualizados
                    emit(Result.Success(municipalidad))
                } ?: run {
                    if (localData == null) {
                        emit(Result.Error("Respuesta vacía del servidor"))
                    }
                }
            } else {
                if (localData == null) {
                    emit(Result.Error("Error: ${response.code()}"))
                }
                // Si ya emitimos datos locales, no emitimos error
                Log.e(TAG, "Error al obtener datos del servidor: ${response.code()}")
            }
        } catch (e: IOException) {
            // Error de red, pero ya hemos mostrado datos locales si estaban disponibles
            if (localData == null) {
                emit(Result.Error("Error de conexión: ${e.message}"))
            }
            Log.e(TAG, "Error de conexión", e)
        } catch (e: Exception) {
            if (localData == null) {
                emit(Result.Error("Error en la solicitud: ${e.message}"))
            }
            Log.e(TAG, "Error inesperado", e)
        }
    }.flowOn(Dispatchers.IO)

    fun getMunicipalidadesByDepartamento(departamento: String): Flow<Result<List<Municipalidad>>> = flow {
        emit(Result.Loading)

        // Primero emitimos datos de la base de datos local
        val localData = municipalidadDao.getMunicipalidadesByDepartamento(departamento).first()
        if (localData.isNotEmpty()) {
            Log.d(TAG, "Emitiendo ${localData.size} municipalidades para departamento $departamento desde la base de datos local")

            // Para cada municipalidad, buscamos sus emprendedores
            val municipalidades = withContext(Dispatchers.IO) {
                localData.map { entity ->
                    val emprendedores = emprendedorMunicipalidadDao.getEmprendedoresByMunicipalidadId(entity.id).firstOrNull() ?: emptyList()
                    entity.toModel(emprendedores)
                }
            }

            emit(Result.Success(municipalidades))
        }

        // Luego intentamos obtener datos del servidor
        try {
            val response = apiService.getMunicipalidadesByDepartamento(departamento)
            if (response.isSuccessful) {
                response.body()?.let { municipalidades ->
                    Log.d(TAG, "Obtenidas ${municipalidades.size} municipalidades para departamento $departamento del servidor")

                    // Guardamos en la base de datos local
                    withContext(Dispatchers.IO) {
                        val entities = municipalidades.map { MunicipalidadEntity.fromModel(it) }
                        municipalidadDao.insertMunicipalidades(entities)
                    }

                    // Emitimos los datos actualizados
                    emit(Result.Success(municipalidades))
                } ?: run {
                    if (localData.isEmpty()) {
                        emit(Result.Error("Respuesta vacía del servidor"))
                    }
                }
            } else {
                if (localData.isEmpty()) {
                    emit(Result.Error("Error: ${response.code()}"))
                }
                // Si ya emitimos datos locales, no emitimos error
                Log.e(TAG, "Error al obtener datos del servidor: ${response.code()}")
            }
        } catch (e: IOException) {
            // Error de red, pero ya hemos mostrado datos locales si estaban disponibles
            if (localData.isEmpty()) {
                emit(Result.Error("Error de conexión: ${e.message}"))
            }
            Log.e(TAG, "Error de conexión", e)
        } catch (e: Exception) {
            if (localData.isEmpty()) {
                emit(Result.Error("Error en la solicitud: ${e.message}"))
            }
            Log.e(TAG, "Error inesperado", e)
        }
    }.flowOn(Dispatchers.IO)

    fun getMiMunicipalidad(): Flow<Result<Municipalidad>> = flow {
        emit(Result.Loading)

        // Para este caso, no tenemos un ID conocido para buscar en local
        // Directamente intentamos obtener del servidor
        try {
            val response = apiService.getMiMunicipalidad()
            if (response.isSuccessful) {
                response.body()?.let { municipalidad ->
                    Log.d(TAG, "Obtenida mi municipalidad del servidor con ID ${municipalidad.id}")

                    // Guardamos en la base de datos local
                    withContext(Dispatchers.IO) {
                        val entity = MunicipalidadEntity.fromModel(municipalidad)
                        municipalidadDao.insertMunicipalidad(entity)
                    }

                    // Emitimos los datos
                    emit(Result.Success(municipalidad))
                } ?: emit(Result.Error("Respuesta vacía del servidor"))
            } else {
                emit(Result.Error("Error: ${response.code()}"))
            }
        } catch (e: IOException) {
            emit(Result.Error("Error de conexión: ${e.message}"))
            Log.e(TAG, "Error de conexión", e)
        } catch (e: Exception) {
            emit(Result.Error("Error en la solicitud: ${e.message}"))
            Log.e(TAG, "Error inesperado", e)
        }
    }.flowOn(Dispatchers.IO)

    fun createMunicipalidad(request: MunicipalidadRequest): Flow<Result<Municipalidad>> = flow {
        emit(Result.Loading)

        try {
            val response = apiService.createMunicipalidad(request)
            if (response.isSuccessful) {
                response.body()?.let { municipalidad ->
                    Log.d(TAG, "Municipalidad creada en el servidor con ID ${municipalidad.id}")

                    // Guardamos en la base de datos local
                    withContext(Dispatchers.IO) {
                        val entity = MunicipalidadEntity.fromModel(municipalidad)
                        municipalidadDao.insertMunicipalidad(entity)
                    }

                    // Emitimos los datos
                    emit(Result.Success(municipalidad))
                } ?: emit(Result.Error("Respuesta vacía del servidor"))
            } else {
                emit(Result.Error("Error: ${response.code()}"))
            }
        } catch (e: IOException) {
            emit(Result.Error("Error de conexión: ${e.message}"))
            Log.e(TAG, "Error de conexión", e)
        } catch (e: Exception) {
            emit(Result.Error("Error en la solicitud: ${e.message}"))
            Log.e(TAG, "Error inesperado", e)
        }
    }.flowOn(Dispatchers.IO)

    fun updateMunicipalidad(id: Long, request: MunicipalidadRequest): Flow<Result<Municipalidad>> = flow {
        emit(Result.Loading)

        try {
            val response = apiService.updateMunicipalidad(id, request)
            if (response.isSuccessful) {
                response.body()?.let { municipalidad ->
                    Log.d(TAG, "Municipalidad actualizada en el servidor con ID ${municipalidad.id}")

                    // Actualizamos en la base de datos local
                    withContext(Dispatchers.IO) {
                        val entity = MunicipalidadEntity.fromModel(municipalidad)
                        municipalidadDao.updateMunicipalidad(entity)
                    }

                    // Emitimos los datos
                    emit(Result.Success(municipalidad))
                } ?: emit(Result.Error("Respuesta vacía del servidor"))
            } else {
                emit(Result.Error("Error: ${response.code()}"))
            }
        } catch (e: IOException) {
            emit(Result.Error("Error de conexión: ${e.message}"))
            Log.e(TAG, "Error de conexión", e)
        } catch (e: Exception) {
            emit(Result.Error("Error en la solicitud: ${e.message}"))
            Log.e(TAG, "Error inesperado", e)
        }
    }.flowOn(Dispatchers.IO)

    fun deleteMunicipalidad(id: Long): Flow<Result<Boolean>> = flow {
        emit(Result.Loading)

        try {
            val response = apiService.deleteMunicipalidad(id)
            if (response.isSuccessful) {
                Log.d(TAG, "Municipalidad eliminada del servidor con ID $id")

                // Eliminamos de la base de datos local
                withContext(Dispatchers.IO) {
                    // Primero eliminamos las relaciones
                    emprendedorMunicipalidadDao.deleteByMunicipalidadId(id)
                    // Luego eliminamos la municipalidad
                    municipalidadDao.deleteMunicipalidadById(id)
                }

                // Emitimos éxito
                emit(Result.Success(true))
            } else {
                emit(Result.Error("Error: ${response.code()}"))
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