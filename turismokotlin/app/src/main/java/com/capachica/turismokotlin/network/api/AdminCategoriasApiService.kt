package com.capachica.turismokotlin.network.api

import com.capachica.turismokotlin.data.model.Categoria
import com.capachica.turismokotlin.data.model.CreateCategoriaRequest
import com.capachica.turismokotlin.data.model.UpdateCategoriaRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AdminCategoriasApiService {
    @GET("categorias")
    suspend fun getAllCategorias(): Response<List<Categoria>>

    @GET("categorias/{id}")
    suspend fun getCategoriaById(@Path("id") categoriaId: Long): Response<Categoria>

    @POST("categorias")
    suspend fun createCategoria(@Body request: CreateCategoriaRequest): Response<Categoria>

    @PUT("categorias/{id}")
    suspend fun updateCategoria(
        @Path("id") categoriaId: Long,
        @Body request: UpdateCategoriaRequest
    ): Response<Categoria>

    @DELETE("categorias/{id}")
    suspend fun deleteCategoria(@Path("id") categoriaId: Long): Response<Unit>
}