package com.capachica.turismokotlin.network.api

import com.capachica.turismokotlin.data.model.CreateEmprendedorRequest
import com.capachica.turismokotlin.data.model.Emprendedor
import com.capachica.turismokotlin.data.model.UpdateEmprendedorRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AdminEmprendedoresApiService {
    @GET("emprendedores")
    suspend fun getAllEmprendedores(): Response<List<Emprendedor>>

    @GET("emprendedores/{id}")
    suspend fun getEmprendedorById(@Path("id") emprendedorId: Long): Response<Emprendedor>

    @GET("emprendedores/rubro/{rubro}")
    suspend fun getEmprendedoresPorRubro(@Path("rubro") rubro: String): Response<List<Emprendedor>>

    @GET("emprendedores/municipalidad/{municipalidadId}")
    suspend fun getEmprendedoresPorMunicipalidad(
        @Path("municipalidadId") municipalidadId: Long
    ): Response<List<Emprendedor>>

    @GET("emprendedores/mi-emprendedor")
    suspend fun getMiEmprendedor(): Response<Emprendedor>

    @POST("emprendedores")
    suspend fun createEmprendedor(@Body request: CreateEmprendedorRequest): Response<Emprendedor>

    @PUT("emprendedores/{id}")
    suspend fun updateEmprendedor(
        @Path("id") emprendedorId: Long,
        @Body request: UpdateEmprendedorRequest
    ): Response<Emprendedor>

    @DELETE("emprendedores/{id}")
    suspend fun deleteEmprendedor(@Path("id") emprendedorId: Long): Response<Unit>
}