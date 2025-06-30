package com.capachica.turismokotlin.network.api

import com.capachica.turismokotlin.data.model.ActualizarServicioRequest
import com.capachica.turismokotlin.data.model.CrearServicioRequest
import com.capachica.turismokotlin.data.model.Servicio
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ServiciosApiService {
    @GET("servicios")
    suspend fun getAllServicios(): Response<List<Servicio>>

    @GET("servicios/{id}")
    suspend fun getServicioById(@Path("id") servicioId: Long): Response<Servicio>

    @GET("servicios/search")
    suspend fun searchServicios(@Query("termino") termino: String): Response<List<Servicio>>

    @GET("servicios/emprendedor/{emprendedorId}")
    suspend fun getServiciosByEmprendedor(@Path("emprendedorId") emprendedorId: Long): Response<List<Servicio>>

    @GET("servicios/cercanos")
    suspend fun getServiciosCercanos(
        @Query("latitud") latitud: Double,
        @Query("longitud") longitud: Double,
        @Query("radio") radio: Double = 5.0
    ): Response<List<Servicio>>
    @GET("servicios/mis-servicios")
    suspend fun getMisServicios(): Response<List<Servicio>>

    @POST("servicios")
    suspend fun crearServicio(@Body request: CrearServicioRequest): Response<Servicio>

    @PUT("servicios/{id}")
    suspend fun actualizarServicio(
        @Path("id") servicioId: Long,
        @Body request: ActualizarServicioRequest
    ): Response<Servicio>

    @DELETE("servicios/{id}")
    suspend fun eliminarServicio(@Path("id") servicioId: Long): Response<Unit>
}