package com.capachica.turismokotlin.network.api

import com.capachica.turismokotlin.data.model.AdminResponse
import com.capachica.turismokotlin.data.model.UsuarioDetallado
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface AdminUsuariosApiService {
    @GET("usuarios")
    suspend fun getAllUsuarios(): Response<List<UsuarioDetallado>>

    @GET("usuarios/{id}")
    suspend fun getUsuarioById(@Path("id") usuarioId: Long): Response<UsuarioDetallado>

    @GET("usuarios/con-rol/{rol}")
    suspend fun getUsuariosPorRol(@Path("rol") rol: String): Response<List<UsuarioDetallado>>

    @GET("usuarios/sin-emprendedor")
    suspend fun getUsuariosSinEmprendedor(): Response<List<UsuarioDetallado>>

    @PUT("usuarios/{usuarioId}/asignar-rol/{rol}")
    suspend fun asignarRol(
        @Path("usuarioId") usuarioId: Long,
        @Path("rol") rol: String
    ): Response<AdminResponse>

    @PUT("usuarios/{usuarioId}/quitar-rol/{rol}")
    suspend fun quitarRol(
        @Path("usuarioId") usuarioId: Long,
        @Path("rol") rol: String
    ): Response<AdminResponse>

    @PUT("usuarios/{usuarioId}/resetear-roles")
    suspend fun resetearRoles(@Path("usuarioId") usuarioId: Long): Response<AdminResponse>

    @PUT("usuarios/{usuarioId}/asignar-emprendedor/{emprendedorId}")
    suspend fun asignarEmprendedor(
        @Path("usuarioId") usuarioId: Long,
        @Path("emprendedorId") emprendedorId: Long
    ): Response<AdminResponse>

    @DELETE("usuarios/{usuarioId}/desasignar-emprendedor")
    suspend fun desasignarEmprendedor(@Path("usuarioId") usuarioId: Long): Response<AdminResponse>

    @PUT("usuarios/{usuarioId}/cambiar-emprendedor/{emprendedorId}")
    suspend fun cambiarEmprendedor(
        @Path("usuarioId") usuarioId: Long,
        @Path("emprendedorId") emprendedorId: Long
    ): Response<AdminResponse>
}
