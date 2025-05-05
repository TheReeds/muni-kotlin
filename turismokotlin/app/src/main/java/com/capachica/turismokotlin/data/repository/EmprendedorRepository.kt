package com.capachica.turismokotlin.data.repository

import android.util.Log
import com.capachica.turismokotlin.data.api.ApiService
import com.capachica.turismokotlin.data.local.dao.EmprendedorDao
import com.capachica.turismokotlin.data.local.dao.EmprendedorMunicipalidadDao
import com.capachica.turismokotlin.data.local.dao.MunicipalidadDao
import com.capachica.turismokotlin.data.local.entity.EmprendedorEntity
import com.capachica.turismokotlin.data.local.entity.EmprendedorMunicipalidadRef
import com.capachica.turismokotlin.data.local.entity.MunicipalidadEntity
import com.capachica.turismokotlin.data.model.Emprendedor
import com.capachica.turismokotlin.data.model.EmprendedorRequest
import com.capachica.turismokotlin.data.model.MunicipalidadBasic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.UnknownHostException

class EmprendedorRepository(
    private val apiService: ApiService,
    private val emprendedorDao: EmprendedorDao,
    private val municipalidadDao: MunicipalidadDao,
    private val emprendedorMunicipalidadDao: EmprendedorMunicipalidadDao
) {
    private val TAG = "EmprendedorRepository"

    fun getAllEmprendedores(): Flow<Result<List<Emprendedor>>> = flow {
        emit(Result.Loading)

        // Primero emitimos datos locales si existen
        val localData = emprendedorDao.getAllEmprendedores().first()
        if (localData.isNotEmpty()) {
            Log.d(TAG, "Emitiendo ${localData.size} emprendedores desde la base de datos local")

            // Convertimos a modelos incluyendo las municipalidades asociadas
            val emprendedores = localData.map { entity ->
                // Para cada emprendedor, buscamos su municipalidad básica
                val municipalidadEntity = municipalidadDao.getMunicipalidadById(entity.municipalidadId).firstOrNull()
                val municipalidadBasic = municipalidadEntity?.let {
                    MunicipalidadBasic(it.id, it.nombre, it.distrito)
                }
                entity.toModel(municipalidadBasic)
            }

            emit(Result.Success(emprendedores))
        }

        try {
            // Intentamos obtener datos frescos de la API
            val response = apiService.getAllEmprendedores()
            if (response.isSuccessful) {
                val emprendedores = response.body() ?: emptyList()
                Log.d(TAG, "Obtenidos ${emprendedores.size} emprendedores del servidor")

                // Guardamos en la base de datos local
                withContext(Dispatchers.IO) {
                    // Guardamos los emprendedores
                    val entities = emprendedores.map { EmprendedorEntity.fromModel(it) }
                    emprendedorDao.insertEmprendedores(entities)

                    // Guardamos las relaciones
                    val relations = emprendedores.filter { it.municipalidad != null }
                        .map {
                            EmprendedorMunicipalidadRef(
                                emprendedorId = it.id,
                                municipalidadId = it.municipalidad!!.id
                            )
                        }
                    emprendedorMunicipalidadDao.insertAll(relations)

                    // Guardamos las municipalidades básicas
                    val municipalidades = emprendedores
                        .mapNotNull { it.municipalidad }
                        .distinctBy { it.id }
                        .map {
                            MunicipalidadEntity(
                                id = it.id,
                                nombre = it.nombre,
                                departamento = "",  // Podría completarse luego
                                provincia = "",     // Podría completarse luego
                                distrito = it.distrito,
                                direccion = null,
                                telefono = null,
                                sitioWeb = null,
                                descripcion = null,
                                usuarioId = 0       // No tenemos esta info
                            )
                        }
                    if (municipalidades.isNotEmpty()) {
                        municipalidadDao.insertMunicipalidades(municipalidades)
                    }
                }

                emit(Result.Success(emprendedores))
            } else {
                // Si falla la API pero tenemos datos locales, no emitimos error
                if (localData.isEmpty()) {
                    emit(Result.Error("Error: ${response.code()} - ${response.message()}"))
                }
                Log.e(TAG, "Error al obtener datos del servidor: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo emprendedores", e)

            // En caso de error de red, usamos datos locales
            if (localData.isEmpty()) {
                emit(Result.Error(e.message ?: "Error desconocido"))
            }
        }
    }.flowOn(Dispatchers.IO)

    fun getEmprendedorById(id: Long): Flow<Result<Emprendedor>> = flow {
        emit(Result.Loading)

        // Primero intentamos obtener datos locales
        val localEntity = emprendedorDao.getEmprendedorById(id).firstOrNull()
        if (localEntity != null) {
            Log.d(TAG, "Emitiendo emprendedor con ID $id desde la base de datos local")

            // Buscamos su municipalidad asociada
            val municipalidadEntity = municipalidadDao.getMunicipalidadById(localEntity.municipalidadId).firstOrNull()
            val municipalidadBasic = municipalidadEntity?.let {
                MunicipalidadBasic(it.id, it.nombre, it.distrito)
            }

            val emprendedor = localEntity.toModel(municipalidadBasic)
            emit(Result.Success(emprendedor))
        }

        try {
            // Luego intentamos obtener datos actualizados de la API
            val response = apiService.getEmprendedorById(id)
            if (response.isSuccessful) {
                response.body()?.let { emprendedor ->
                    Log.d(TAG, "Obtenido emprendedor con ID $id del servidor")

                    // Actualizamos la base de datos local
                    withContext(Dispatchers.IO) {
                        // Guardamos el emprendedor
                        val entity = EmprendedorEntity.fromModel(emprendedor)
                        emprendedorDao.insertEmprendedor(entity)

                        // Guardamos la relación si existe municipalidad
                        emprendedor.municipalidad?.let { municipalidad ->
                            // Guardamos la relación
                            emprendedorMunicipalidadDao.insert(
                                EmprendedorMunicipalidadRef(
                                    emprendedorId = emprendedor.id,
                                    municipalidadId = municipalidad.id
                                )
                            )

                            // También guardamos la municipalidad básica
                            municipalidadDao.insertMunicipalidad(
                                MunicipalidadEntity(
                                    id = municipalidad.id,
                                    nombre = municipalidad.nombre,
                                    departamento = "",  // No tenemos esta info
                                    provincia = "",     // No tenemos esta info
                                    distrito = municipalidad.distrito,
                                    direccion = null,
                                    telefono = null,
                                    sitioWeb = null,
                                    descripcion = null,
                                    usuarioId = 0       // No tenemos esta info
                                )
                            )
                        }
                    }

                    emit(Result.Success(emprendedor))
                } ?: run {
                    if (localEntity == null) {
                        emit(Result.Error("Respuesta vacía del servidor"))
                    }
                }
            } else {
                if (localEntity == null) {
                    emit(Result.Error("Error: ${response.code()}"))
                }
                Log.e(TAG, "Error al obtener datos del servidor: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo emprendedor por ID", e)
            if (localEntity == null) {
                emit(Result.Error(e.message ?: "Error desconocido"))
            }
        }
    }.flowOn(Dispatchers.IO)

    fun getEmprendedoresByMunicipalidad(municipalidadId: Long): Flow<Result<List<Emprendedor>>> = flow {
        emit(Result.Loading)

        // Primero emitimos datos de la base de datos local
        val localData = emprendedorDao.getEmprendedoresByMunicipalidad(municipalidadId).first()
        if (localData.isNotEmpty()) {
            Log.d(TAG, "Emitiendo ${localData.size} emprendedores para municipalidad $municipalidadId desde la base de datos local")

            // Obtenemos información de la municipalidad
            val municipalidadEntity = municipalidadDao.getMunicipalidadById(municipalidadId).firstOrNull()
            val municipalidadBasic = municipalidadEntity?.let {
                MunicipalidadBasic(it.id, it.nombre, it.distrito)
            }

            val emprendedores = localData.map { it.toModel(municipalidadBasic) }
            emit(Result.Success(emprendedores))
        }

        // Luego intentamos obtener datos del servidor
        try {
            val response = apiService.getEmprendedoresByMunicipalidad(municipalidadId)
            if (response.isSuccessful) {
                response.body()?.let { emprendedores ->
                    Log.d(TAG, "Obtenidos ${emprendedores.size} emprendedores para municipalidad $municipalidadId del servidor")

                    // Guardamos en la base de datos local
                    withContext(Dispatchers.IO) {
                        // Guardamos los emprendedores
                        val entities = emprendedores.map { EmprendedorEntity.fromModel(it) }
                        emprendedorDao.insertEmprendedores(entities)

                        // Guardamos las relaciones
                        val relations = emprendedores.filter { it.municipalidad != null }
                            .map {
                                EmprendedorMunicipalidadRef(
                                    emprendedorId = it.id,
                                    municipalidadId = it.municipalidad!!.id
                                )
                            }
                        emprendedorMunicipalidadDao.insertAll(relations)
                    }

                    // Emitimos los datos actualizados
                    emit(Result.Success(emprendedores))
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

    fun getEmprendedoresByRubro(rubro: String): Flow<Result<List<Emprendedor>>> = flow {
        emit(Result.Loading)

        // Primero emitimos datos de la base de datos local
        val localData = emprendedorDao.getEmprendedoresByRubro(rubro).first()
        if (localData.isNotEmpty()) {
            Log.d(TAG, "Emitiendo ${localData.size} emprendedores para rubro $rubro desde la base de datos local")

            // Convertimos a modelos
            val emprendedores = localData.map { entity ->
                // Para cada emprendedor, buscamos su municipalidad básica
                val municipalidadEntity = municipalidadDao.getMunicipalidadById(entity.municipalidadId).firstOrNull()
                val municipalidadBasic = municipalidadEntity?.let {
                    MunicipalidadBasic(it.id, it.nombre, it.distrito)
                }
                entity.toModel(municipalidadBasic)
            }

            emit(Result.Success(emprendedores))
        }

        // Luego intentamos obtener datos del servidor
        try {
            val response = apiService.getEmprendedoresByRubro(rubro)
            if (response.isSuccessful) {
                response.body()?.let { emprendedores ->
                    Log.d(TAG, "Obtenidos ${emprendedores.size} emprendedores para rubro $rubro del servidor")

                    // Guardamos en la base de datos local
                    withContext(Dispatchers.IO) {
                        // Guardamos los emprendedores
                        val entities = emprendedores.map { EmprendedorEntity.fromModel(it) }
                        emprendedorDao.insertEmprendedores(entities)

                        // Guardamos las relaciones
                        val relations = emprendedores.filter { it.municipalidad != null }
                            .map {
                                EmprendedorMunicipalidadRef(
                                    emprendedorId = it.id,
                                    municipalidadId = it.municipalidad!!.id
                                )
                            }
                        emprendedorMunicipalidadDao.insertAll(relations)

                        // Y también guardamos las municipalidades básicas
                        val municipalidades = emprendedores
                            .mapNotNull { it.municipalidad }
                            .distinctBy { it.id }
                            .map {
                                MunicipalidadEntity(
                                    id = it.id,
                                    nombre = it.nombre,
                                    departamento = "",  // Podría completarse luego
                                    provincia = "",     // Podría completarse luego
                                    distrito = it.distrito,
                                    direccion = null,
                                    telefono = null,
                                    sitioWeb = null,
                                    descripcion = null,
                                    usuarioId = 0       // No tenemos esta info
                                )
                            }
                        if (municipalidades.isNotEmpty()) {
                            municipalidadDao.insertMunicipalidades(municipalidades)
                        }
                    }

                    // Emitimos los datos actualizados
                    emit(Result.Success(emprendedores))
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

    fun getMiEmprendedor(): Flow<Result<Emprendedor>> = flow {
        emit(Result.Loading)

        // Para este caso, no tenemos un ID conocido para buscar en local
        // Directamente intentamos obtener del servidor
        try {
            val response = apiService.getMiEmprendedor()
            if (response.isSuccessful) {
                response.body()?.let { emprendedor ->
                    Log.d(TAG, "Obtenido mi emprendedor del servidor con ID ${emprendedor.id}")

                    // Guardamos en la base de datos local
                    withContext(Dispatchers.IO) {
                        // Guardamos el emprendedor
                        val entity = EmprendedorEntity.fromModel(emprendedor)
                        emprendedorDao.insertEmprendedor(entity)

                        // Guardamos la relación si existe municipalidad
                        emprendedor.municipalidad?.let { municipalidad ->
                            // Guardamos la relación
                            emprendedorMunicipalidadDao.insert(
                                EmprendedorMunicipalidadRef(
                                    emprendedorId = emprendedor.id,
                                    municipalidadId = municipalidad.id
                                )
                            )

                            // También guardamos la municipalidad básica
                            municipalidadDao.insertMunicipalidad(
                                MunicipalidadEntity(
                                    id = municipalidad.id,
                                    nombre = municipalidad.nombre,
                                    departamento = "",  // No tenemos esta info
                                    provincia = "",     // No tenemos esta info
                                    distrito = municipalidad.distrito,
                                    direccion = null,
                                    telefono = null,
                                    sitioWeb = null,
                                    descripcion = null,
                                    usuarioId = 0       // No tenemos esta info
                                )
                            )
                        }
                    }

                    // Emitimos los datos
                    emit(Result.Success(emprendedor))
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

    fun createEmprendedor(request: EmprendedorRequest): Flow<Result<Emprendedor>> = flow {
        emit(Result.Loading)

        try {
            val response = apiService.createEmprendedor(request)
            if (response.isSuccessful) {
                val emprendedor = response.body()
                if (emprendedor != null) {
                    Log.d(TAG, "Emprendedor creado en el servidor con ID ${emprendedor.id}")

                    // Guardar en la base de datos local
                    withContext(Dispatchers.IO) {
                        // Guardamos el emprendedor
                        emprendedorDao.insertEmprendedor(EmprendedorEntity.fromModel(emprendedor))

                        // Guardamos la relación si existe municipalidad
                        emprendedor.municipalidad?.let { municipalidad ->
                            // Guardamos la relación
                            emprendedorMunicipalidadDao.insert(
                                EmprendedorMunicipalidadRef(
                                    emprendedorId = emprendedor.id,
                                    municipalidadId = municipalidad.id
                                )
                            )

                            // También guardamos la municipalidad básica
                            municipalidadDao.insertMunicipalidad(
                                MunicipalidadEntity(
                                    id = municipalidad.id,
                                    nombre = municipalidad.nombre,
                                    departamento = "",  // No tenemos esta info
                                    provincia = "",     // No tenemos esta info
                                    distrito = municipalidad.distrito,
                                    direccion = null,
                                    telefono = null,
                                    sitioWeb = null,
                                    descripcion = null,
                                    usuarioId = 0       // No tenemos esta info
                                )
                            )
                        }
                    }

                    emit(Result.Success(emprendedor))
                } else {
                    emit(Result.Error("Respuesta vacía del servidor"))
                }
            } else {
                emit(Result.Error("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creando emprendedor", e)
            emit(Result.Error(e.message ?: "Error desconocido"))
        }
    }.flowOn(Dispatchers.IO)

    fun updateEmprendedor(id: Long, request: EmprendedorRequest): Flow<Result<Emprendedor>> = flow {
        emit(Result.Loading)

        try {
            val response = apiService.updateEmprendedor(id, request)
            if (response.isSuccessful) {
                response.body()?.let { emprendedor ->
                    Log.d(TAG, "Emprendedor actualizado en el servidor con ID ${emprendedor.id}")

                    // Actualizamos en la base de datos local
                    withContext(Dispatchers.IO) {
                        // Actualizamos el emprendedor
                        val entity = EmprendedorEntity.fromModel(emprendedor)
                        emprendedorDao.updateEmprendedor(entity)

                        // Actualizamos la relación si existe municipalidad
                        emprendedor.municipalidad?.let { municipalidad ->
                            // Borramos relaciones anteriores
                            emprendedorMunicipalidadDao.deleteByEmprendedorId(emprendedor.id)

                            // Creamos la nueva relación
                            emprendedorMunicipalidadDao.insert(
                                EmprendedorMunicipalidadRef(
                                    emprendedorId = emprendedor.id,
                                    municipalidadId = municipalidad.id
                                )
                            )

                            // También guardamos la municipalidad básica
                            municipalidadDao.insertMunicipalidad(
                                MunicipalidadEntity(
                                    id = municipalidad.id,
                                    nombre = municipalidad.nombre,
                                    departamento = "",  // No tenemos esta info
                                    provincia = "",     // No tenemos esta info
                                    distrito = municipalidad.distrito,
                                    direccion = null,
                                    telefono = null,
                                    sitioWeb = null,
                                    descripcion = null,
                                    usuarioId = 0       // No tenemos esta info
                                )
                            )
                        }
                    }

                    // Emitimos los datos
                    emit(Result.Success(emprendedor))
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

    fun deleteEmprendedor(id: Long): Flow<Result<Boolean>> = flow {
        emit(Result.Loading)

        try {
            val response = apiService.deleteEmprendedor(id)
            if (response.isSuccessful) {
                Log.d(TAG, "Emprendedor eliminado del servidor con ID $id")

                // Si la eliminación es exitosa, eliminamos de la base de datos local
                withContext(Dispatchers.IO) {
                    // Primero eliminamos las relaciones
                    emprendedorMunicipalidadDao.deleteByEmprendedorId(id)
                    // Luego eliminamos el emprendedor
                    emprendedorDao.deleteEmprendedorById(id)
                }

                emit(Result.Success(true))
            } else {
                emit(Result.Error("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error eliminando emprendedor", e)
            emit(Result.Error(e.message ?: "Error desconocido"))
        }
    }.flowOn(Dispatchers.IO)
}