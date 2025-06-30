package com.capachica.turismokotlin.network.api

import com.capachica.turismokotlin.data.model.ActualizarPlanRequest
import com.capachica.turismokotlin.data.model.CrearPlanRequest
import com.capachica.turismokotlin.data.model.Plan
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PlanesApiService {
    @GET("planes")
    suspend fun getAllPlanes(): Response<List<Plan>>

    @GET("planes/{id}")
    suspend fun getPlanById(@Path("id") planId: Long): Response<Plan>

    @GET("planes/search")
    suspend fun searchPlanes(@Query("termino") termino: String): Response<List<Plan>>

    @GET("planes/populares")
    suspend fun getPlanesPopulares(): Response<List<Plan>>

    @GET("planes/mis-planes")
    suspend fun getMisPlanes(): Response<List<Plan>>

    @GET("planes/municipalidad/{municipalidadId}")
    suspend fun getPlanesByMunicipalidad(@Path("municipalidadId") municipalidadId: Long): Response<List<Plan>>

    @GET("planes/estado/{estado}")
    suspend fun getPlanesByEstado(@Path("estado") estado: String): Response<List<Plan>>

    @GET("planes/precio")
    suspend fun getPlanesByPrecio(
        @Query("precioMin") precioMin: Double,
        @Query("precioMax") precioMax: Double
    ): Response<List<Plan>>

    @POST("planes")
    suspend fun crearPlan(@Body request: CrearPlanRequest): Response<Plan>

    @PUT("planes/{id}")
    suspend fun actualizarPlan(
        @Path("id") planId: Long,
        @Body request: ActualizarPlanRequest
    ): Response<Plan>

    @PATCH("planes/{id}/estado")
    suspend fun cambiarEstadoPlan(
        @Path("id") planId: Long,
        @Query("estado") estado: String
    ): Response<Plan>

    @DELETE("planes/{id}")
    suspend fun eliminarPlan(@Path("id") planId: Long): Response<Unit>
}