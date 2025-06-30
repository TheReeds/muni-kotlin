package com.capachica.turismokotlin.data.repository

import com.capachica.turismokotlin.data.model.UsuarioDetallado
import com.capachica.turismokotlin.network.api.AdminUsuariosApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminUsuariosRepository @Inject constructor(
    private val adminUsuariosApiService: AdminUsuariosApiService
) {
    suspend fun getAllUsuarios(): Result<List<UsuarioDetallado>> {
        return try {
            val response = adminUsuariosApiService.getAllUsuarios()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener usuarios: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUsuarioById(usuarioId: Long): Result<UsuarioDetallado> {
        return try {
            val response = adminUsuariosApiService.getUsuarioById(usuarioId)
            if (response.isSuccessful) {
                response.body()?.let { usuario ->
                    Result.success(usuario)
                } ?: Result.failure(Exception("Usuario no encontrado"))
            } else {
                Result.failure(Exception("Error al obtener usuario: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUsuariosPorRol(rol: String): Result<List<UsuarioDetallado>> {
        return try {
            val response = adminUsuariosApiService.getUsuariosPorRol(rol)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener usuarios por rol: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUsuariosSinEmprendedor(): Result<List<UsuarioDetallado>> {
        return try {
            val response = adminUsuariosApiService.getUsuariosSinEmprendedor()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener usuarios sin emprendedor: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun asignarRol(usuarioId: Long, rol: String): Result<String> {
        return try {
            val response = adminUsuariosApiService.asignarRol(usuarioId, rol)
            if (response.isSuccessful) {
                val message = response.body()?.message ?: "Rol asignado exitosamente"
                Result.success(message)
            } else {
                Result.failure(Exception("Error al asignar rol: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun quitarRol(usuarioId: Long, rol: String): Result<String> {
        return try {
            val response = adminUsuariosApiService.quitarRol(usuarioId, rol)
            if (response.isSuccessful) {
                val message = response.body()?.message ?: "Rol removido exitosamente"
                Result.success(message)
            } else {
                Result.failure(Exception("Error al quitar rol: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetearRoles(usuarioId: Long): Result<String> {
        return try {
            val response = adminUsuariosApiService.resetearRoles(usuarioId)
            if (response.isSuccessful) {
                val message = response.body()?.message ?: "Roles reseteados exitosamente"
                Result.success(message)
            } else {
                Result.failure(Exception("Error al resetear roles: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun asignarEmprendedor(usuarioId: Long, emprendedorId: Long): Result<String> {
        return try {
            val response = adminUsuariosApiService.asignarEmprendedor(usuarioId, emprendedorId)
            if (response.isSuccessful) {
                val message = response.body()?.message ?: "Emprendedor asignado exitosamente"
                Result.success(message)
            } else {
                Result.failure(Exception("Error al asignar emprendedor: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun desasignarEmprendedor(usuarioId: Long): Result<String> {
        return try {
            val response = adminUsuariosApiService.desasignarEmprendedor(usuarioId)
            if (response.isSuccessful) {
                val message = response.body()?.message ?: "Emprendedor desasignado exitosamente"
                Result.success(message)
            } else {
                Result.failure(Exception("Error al desasignar emprendedor: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cambiarEmprendedor(usuarioId: Long, emprendedorId: Long): Result<String> {
        return try {
            val response = adminUsuariosApiService.cambiarEmprendedor(usuarioId, emprendedorId)
            if (response.isSuccessful) {
                val message = response.body()?.message ?: "Emprendedor cambiado exitosamente"
                Result.success(message)
            } else {
                Result.failure(Exception("Error al cambiar emprendedor: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}