package com.capachica.turismokotlin.network.api

import com.capachica.turismokotlin.data.model.Emprendedor
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EmprendedoresApiService {
    @GET("emprendedores")
    suspend fun getAllEmprendedores(): Response<List<Emprendedor>>

    @GET("emprendedores/{id}")
    suspend fun getEmprendedorById(@Path("id") emprendedorId: Long): Response<Emprendedor>

    @GET("emprendedores/cercanos")
    suspend fun getEmprendedoresCercanos(
        @Query("latitud") latitud: Double,
        @Query("longitud") longitud: Double,
        @Query("radio") radio: Double = 5.0
    ): Response<List<Emprendedor>>

    @GET("emprendedores/categoria/{categoriaId}")
    suspend fun getEmprendedoresByCategoria(@Path("categoriaId") categoriaId: Long): Response<List<Emprendedor>>
}