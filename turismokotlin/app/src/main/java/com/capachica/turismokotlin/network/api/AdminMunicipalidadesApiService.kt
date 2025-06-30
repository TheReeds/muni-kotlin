package com.capachica.turismokotlin.network.api

import com.capachica.turismokotlin.data.model.CreateMunicipalidadRequest
import com.capachica.turismokotlin.data.model.MunicipalidadDetallada
import com.capachica.turismokotlin.data.model.UpdateMunicipalidadRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AdminMunicipalidadesApiService {
    @GET("municipalidades")
    suspend fun getAllMunicipalidades(): Response<List<MunicipalidadDetallada>>

    @GET("municipalidades/{id}")
    suspend fun getMunicipalidadById(@Path("id") municipalidadId: Long): Response<MunicipalidadDetallada>

    @POST("municipalidades")
    suspend fun createMunicipalidad(@Body request: CreateMunicipalidadRequest): Response<MunicipalidadDetallada>

    @PUT("municipalidades/{id}")
    suspend fun updateMunicipalidad(
        @Path("id") municipalidadId: Long,
        @Body request: UpdateMunicipalidadRequest
    ): Response<MunicipalidadDetallada>

    @DELETE("municipalidades/{id}")
    suspend fun deleteMunicipalidad(@Path("id") municipalidadId: Long): Response<Unit>
}