package com.capachica.turismokotlin.data.repository

import android.util.Log
import com.capachica.turismokotlin.data.api.ApiService
import com.capachica.turismokotlin.data.model.UsuarioResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import java.net.UnknownHostException

class UserRepository(
    private val apiService: ApiService
) {
    private val TAG = "UserRepository"

    fun getAllUsuarios(): Flow<Result<List<UsuarioResponse>>> = flow {
        emit(Result.Loading)

        try {
            val response = apiService.getAllUsuarios()
            if (response.isSuccessful) {
                val usuarios = response.body() ?: emptyList()
                Log.d(TAG, "Obtenidos ${usuarios.size} usuarios del servidor")
                emit(Result.Success(usuarios))
            } else {
                emit(Result.Error("Error: ${response.code()} - ${response.message()}"))
                Log.e(TAG, "Error al obtener usuarios del servidor: ${response.code()}")
            }
        } catch (e: IOException) {
            emit(Result.Error("Error de conexión: ${e.message}"))
            Log.e(TAG, "Error de conexión", e)
        } catch (e: Exception) {
            emit(Result.Error("Error en la solicitud: ${e.message}"))
            Log.e(TAG, "Error inesperado", e)
        }
    }.flowOn(Dispatchers.IO)

    fun getUsuarioById(id: Long): Flow<Result<UsuarioResponse>> = flow {
        emit(Result.Loading)

        try {
            val response = apiService.getUsuarioById(id)
            if (response.isSuccessful) {
                response.body()?.let { usuario ->
                    Log.d(TAG, "Obtenido usuario con ID $id del servidor")
                    emit(Result.Success(usuario))
                } ?: emit(Result.Error("Usuario no encontrado"))
            } else {
                emit(Result.Error("Error: ${response.code()} - ${response.message()}"))
                Log.e(TAG, "Error al obtener usuario por ID: ${response.code()}")
            }
        } catch (e: IOException) {
            emit(Result.Error("Error de conexión: ${e.message}"))
            Log.e(TAG, "Error de conexión", e)
        } catch (e: Exception) {
            emit(Result.Error("Error en la solicitud: ${e.message}"))
            Log.e(TAG, "Error inesperado", e)
        }
    }.flowOn(Dispatchers.IO)

    fun getUsuariosSinEmprendedor(): Flow<Result<List<UsuarioResponse>>> = flow {
        emit(Result.Loading)

        try {
            val response = apiService.getUsuariosSinEmprendedor()
            if (response.isSuccessful) {
                val usuarios = response.body() ?: emptyList()
                Log.d(TAG, "Obtenidos ${usuarios.size} usuarios sin emprendedor del servidor")
                emit(Result.Success(usuarios))
            } else {
                emit(Result.Error("Error: ${response.code()} - ${response.message()}"))
                Log.e(TAG, "Error al obtener usuarios sin emprendedor: ${response.code()}")
            }
        } catch (e: IOException) {
            emit(Result.Error("Error de conexión: ${e.message}"))
            Log.e(TAG, "Error de conexión", e)
        } catch (e: Exception) {
            emit(Result.Error("Error en la solicitud: ${e.message}"))
            Log.e(TAG, "Error inesperado", e)
        }
    }.flowOn(Dispatchers.IO)

    fun getUsuariosPorRol(rol: String): Flow<Result<List<UsuarioResponse>>> = flow {
        emit(Result.Loading)

        try {
            val response = apiService.getUsuariosPorRol(rol)
            if (response.isSuccessful) {
                val usuarios = response.body() ?: emptyList()
                Log.d(TAG, "Obtenidos ${usuarios.size} usuarios con rol $rol del servidor")
                emit(Result.Success(usuarios))
            } else {
                emit(Result.Error("Error: ${response.code()} - ${response.message()}"))
                Log.e(TAG, "Error al obtener usuarios por rol: ${response.code()}")
            }
        } catch (e: IOException) {
            emit(Result.Error("Error de conexión: ${e.message}"))
            Log.e(TAG, "Error de conexión", e)
        } catch (e: Exception) {
            emit(Result.Error("Error en la solicitud: ${e.message}"))
            Log.e(TAG, "Error inesperado", e)
        }
    }.flowOn(Dispatchers.IO)

    fun asignarRolAUsuario(usuarioId: Long, rol: String): Flow<Result<String>> = flow {
        emit(Result.Loading)

        try {
            val response = apiService.asignarRolAUsuario(usuarioId, rol)
            if (response.isSuccessful) {
                val mensaje = response.body() ?: "Rol asignado exitosamente"
                Log.d(TAG, "Rol $rol asignado al usuario $usuarioId")
                emit(Result.Success(mensaje))
            } else {
                emit(Result.Error("Error: ${response.code()} - ${response.message()}"))
                Log.e(TAG, "Error al asignar rol: ${response.code()}")
            }
        } catch (e: IOException) {
            emit(Result.Error("Error de conexión: ${e.message}"))
            Log.e(TAG, "Error de conexión", e)
        } catch (e: Exception) {
            emit(Result.Error("Error en la solicitud: ${e.message}"))
            Log.e(TAG, "Error inesperado", e)
        }
    }.flowOn(Dispatchers.IO)

    fun quitarRolAUsuario(usuarioId: Long, rol: String): Flow<Result<String>> = flow {
        emit(Result.Loading)

        try {
            val response = apiService.quitarRolAUsuario(usuarioId, rol)
            if (response.isSuccessful) {
                val mensaje = response.body() ?: "Rol removido exitosamente"
                Log.d(TAG, "Rol $rol removido del usuario $usuarioId")
                emit(Result.Success(mensaje))
            } else {
                emit(Result.Error("Error: ${response.code()} - ${response.message()}"))
                Log.e(TAG, "Error al remover rol: ${response.code()}")
            }
        } catch (e: IOException) {
            emit(Result.Error("Error de conexión: ${e.message}"))
            Log.e(TAG, "Error de conexión", e)
        } catch (e: Exception) {
            emit(Result.Error("Error en la solicitud: ${e.message}"))
            Log.e(TAG, "Error inesperado", e)
        }
    }.flowOn(Dispatchers.IO)

    fun resetearRolesUsuario(usuarioId: Long): Flow<Result<String>> = flow {
        emit(Result.Loading)

        try {
            val response = apiService.resetearRolesUsuario(usuarioId)
            if (response.isSuccessful) {
                val mensaje = response.body() ?: "Roles reseteados exitosamente"
                Log.d(TAG, "Roles del usuario $usuarioId reseteados")
                emit(Result.Success(mensaje))
            } else {
                emit(Result.Error("Error: ${response.code()} - ${response.message()}"))
                Log.e(TAG, "Error al resetear roles: ${response.code()}")
            }
        } catch (e: IOException) {
            emit(Result.Error("Error de conexión: ${e.message}"))
            Log.e(TAG, "Error de conexión", e)
        } catch (e: Exception) {
            emit(Result.Error("Error en la solicitud: ${e.message}"))
            Log.e(TAG, "Error inesperado", e)
        }
    }.flowOn(Dispatchers.IO)

    fun asignarUsuarioAEmprendedor(usuarioId: Long, emprendedorId: Long): Flow<Result<String>> = flow {
        emit(Result.Loading)

        try {
            val response = apiService.asignarUsuarioAEmprendedor(usuarioId, emprendedorId)
            if (response.isSuccessful) {
                val mensaje = response.body() ?: "Usuario asignado al emprendedor exitosamente"
                Log.d(TAG, "Usuario $usuarioId asignado al emprendedor $emprendedorId")
                emit(Result.Success(mensaje))
            } else {
                emit(Result.Error("Error: ${response.code()} - ${response.message()}"))
                Log.e(TAG, "Error al asignar usuario a emprendedor: ${response.code()}")
            }
        } catch (e: IOException) {
            emit(Result.Error("Error de conexión: ${e.message}"))
            Log.e(TAG, "Error de conexión", e)
        } catch (e: Exception) {
            emit(Result.Error("Error en la solicitud: ${e.message}"))
            Log.e(TAG, "Error inesperado", e)
        }
    }.flowOn(Dispatchers.IO)

    fun cambiarUsuarioDeEmprendedor(usuarioId: Long, emprendedorId: Long): Flow<Result<String>> = flow {
        emit(Result.Loading)

        try {
            val response = apiService.cambiarUsuarioDeEmprendedor(usuarioId, emprendedorId)
            if (response.isSuccessful) {
                val mensaje = response.body() ?: "Usuario cambiado de emprendedor exitosamente"
                Log.d(TAG, "Usuario $usuarioId cambiado al emprendedor $emprendedorId")
                emit(Result.Success(mensaje))
            } else {
                emit(Result.Error("Error: ${response.code()} - ${response.message()}"))
                Log.e(TAG, "Error al cambiar usuario de emprendedor: ${response.code()}")
            }
        } catch (e: IOException) {
            emit(Result.Error("Error de conexión: ${e.message}"))
            Log.e(TAG, "Error de conexión", e)
        } catch (e: Exception) {
            emit(Result.Error("Error en la solicitud: ${e.message}"))
            Log.e(TAG, "Error inesperado", e)
        }
    }.flowOn(Dispatchers.IO)

    fun desasignarUsuarioDeEmprendedor(usuarioId: Long): Flow<Result<String>> = flow {
        emit(Result.Loading)

        try {
            val response = apiService.desasignarUsuarioDeEmprendedor(usuarioId)
            if (response.isSuccessful) {
                val mensaje = response.body() ?: "Usuario desasignado del emprendedor exitosamente"
                Log.d(TAG, "Usuario $usuarioId desasignado de emprendedor")
                emit(Result.Success(mensaje))
            } else {
                emit(Result.Error("Error: ${response.code()} - ${response.message()}"))
                Log.e(TAG, "Error al desasignar usuario de emprendedor: ${response.code()}")
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