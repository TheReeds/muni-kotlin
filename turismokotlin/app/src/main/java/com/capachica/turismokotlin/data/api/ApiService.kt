package com.capachica.turismokotlin.data.api

import com.capachica.turismokotlin.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Autenticación
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("auth/init")
    suspend fun initRoles(): Response<String>

    // Municipalidades
    @GET("municipalidades")
    suspend fun getAllMunicipalidades(): Response<List<Municipalidad>>

    @GET("municipalidades/{id}")
    suspend fun getMunicipalidadById(@Path("id") id: Long): Response<Municipalidad>

    @GET("municipalidades/departamento/{departamento}")
    suspend fun getMunicipalidadesByDepartamento(@Path("departamento") departamento: String): Response<List<Municipalidad>>

    @GET("municipalidades/provincia/{provincia}")
    suspend fun getMunicipalidadesByProvincia(@Path("provincia") provincia: String): Response<List<Municipalidad>>

    @GET("municipalidades/distrito/{distrito}")
    suspend fun getMunicipalidadesByDistrito(@Path("distrito") distrito: String): Response<List<Municipalidad>>

    @GET("municipalidades/mi-municipalidad")
    suspend fun getMiMunicipalidad(): Response<Municipalidad>

    @POST("municipalidades")
    suspend fun createMunicipalidad(@Body request: MunicipalidadRequest): Response<Municipalidad>

    @PUT("municipalidades/{id}")
    suspend fun updateMunicipalidad(@Path("id") id: Long, @Body request: MunicipalidadRequest): Response<Municipalidad>

    @DELETE("municipalidades/{id}")
    suspend fun deleteMunicipalidad(@Path("id") id: Long): Response<Void>

    // Emprendedores
    @GET("emprendedores")
    suspend fun getAllEmprendedores(): Response<List<Emprendedor>>

    @GET("emprendedores/{id}")
    suspend fun getEmprendedorById(@Path("id") id: Long): Response<Emprendedor>

    @GET("emprendedores/municipalidad/{municipalidadId}")
    suspend fun getEmprendedoresByMunicipalidad(@Path("municipalidadId") municipalidadId: Long): Response<List<Emprendedor>>

    @GET("emprendedores/rubro/{rubro}")
    suspend fun getEmprendedoresByRubro(@Path("rubro") rubro: String): Response<List<Emprendedor>>

    @GET("emprendedores/mi-emprendedor")
    suspend fun getMiEmprendedor(): Response<Emprendedor>

    @POST("emprendedores")
    suspend fun createEmprendedor(@Body request: EmprendedorRequest): Response<Emprendedor>

    @PUT("emprendedores/{id}")
    suspend fun updateEmprendedor(@Path("id") id: Long, @Body request: EmprendedorRequest): Response<Emprendedor>

    @DELETE("emprendedores/{id}")
    suspend fun deleteEmprendedor(@Path("id") id: Long): Response<Void>
    // Categorías
    @GET("categorias")
    suspend fun getAllCategorias(): Response<List<Categoria>>

    @GET("categorias/{id}")
    suspend fun getCategoriaById(@Path("id") id: Long): Response<Categoria>

    @POST("categorias")
    suspend fun createCategoria(@Body request: CategoriaRequest): Response<Categoria>

    @PUT("categorias/{id}")
    suspend fun updateCategoria(@Path("id") id: Long, @Body request: CategoriaRequest): Response<Categoria>

    @DELETE("categorias/{id}")
    suspend fun deleteCategoria(@Path("id") id: Long): Response<Void>

    // Update emprendedor endpoints to get by category
    @GET("emprendedores/categoria/{categoriaId}")
    suspend fun getEmprendedoresByCategoria(@Path("categoriaId") categoriaId: Long): Response<List<Emprendedor>>
}