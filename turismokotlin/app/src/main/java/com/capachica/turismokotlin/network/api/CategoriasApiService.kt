package com.capachica.turismokotlin.network.api

import com.capachica.turismokotlin.data.model.Categoria
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CategoriasApiService {
    @GET("categorias")
    suspend fun getAllCategorias(): Response<List<Categoria>>

    @GET("categorias/{id}")
    suspend fun getCategoriaById(@Path("id") categoriaId: Long): Response<Categoria>
}