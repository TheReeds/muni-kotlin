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

    @GET("emprendedores/cercanos")
    suspend fun getEmprendedoresCercanos(
        @Query("latitud") latitud: Double,
        @Query("longitud") longitud: Double,
        @Query("radio") radio: Double = 10.0
    ): Response<List<Emprendedor>>

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

    @GET("emprendedores/categoria/{categoriaId}")
    suspend fun getEmprendedoresByCategoria(@Path("categoriaId") categoriaId: Long): Response<List<Emprendedor>>

    // ========== SERVICIOS TURÍSTICOS ==========
    @GET("servicios")
    suspend fun getAllServicios(): Response<List<ServicioTuristico>>

    @GET("servicios/{id}")
    suspend fun getServicioById(@Path("id") id: Long): Response<ServicioTuristico>

    @GET("servicios/emprendedor/{emprendedorId}")
    suspend fun getServiciosByEmprendedor(@Path("emprendedorId") emprendedorId: Long): Response<List<ServicioTuristico>>

    @GET("servicios/municipalidad/{municipalidadId}")
    suspend fun getServiciosByMunicipalidad(@Path("municipalidadId") municipalidadId: Long): Response<List<ServicioTuristico>>

    @GET("servicios/tipo/{tipo}")
    suspend fun getServiciosByTipo(@Path("tipo") tipo: String): Response<List<ServicioTuristico>>

    @GET("servicios/estado/{estado}")
    suspend fun getServiciosByEstado(@Path("estado") estado: String): Response<List<ServicioTuristico>>

    @GET("servicios/precio")
    suspend fun getServiciosByPrecio(@Query("precioMin") precioMin: Double, @Query("precioMax") precioMax: Double): Response<List<ServicioTuristico>>

    @GET("servicios/search")
    suspend fun searchServicios(@Query("termino") termino: String): Response<List<ServicioTuristico>>

    @GET("servicios/categoria/{categoriaId}")
    suspend fun getServiciosByCategoria(@Path("categoriaId") categoriaId: Long): Response<List<ServicioTuristico>>

    @GET("servicios/mis-servicios")
    suspend fun getMisServicios(): Response<List<ServicioTuristico>>

    @POST("servicios")
    suspend fun createServicio(@Body request: ServicioTuristicoRequest): Response<ServicioTuristico>

    @PUT("servicios/{id}")
    suspend fun updateServicio(@Path("id") id: Long, @Body request: ServicioTuristicoRequest): Response<ServicioTuristico>

    @DELETE("servicios/{id}")
    suspend fun deleteServicio(@Path("id") id: Long): Response<Void>

    @PATCH("servicios/{id}/estado")
    suspend fun cambiarEstadoServicio(@Path("id") id: Long, @Query("estado") estado: String): Response<ServicioTuristico>

    // ========== PLANES TURÍSTICOS ==========
    @GET("planes")
    suspend fun getAllPlanes(): Response<List<PlanTuristico>>

    @GET("planes/{id}")
    suspend fun getPlanById(@Path("id") id: Long): Response<PlanTuristico>

    @GET("planes/municipalidad/{municipalidadId}")
    suspend fun getPlanesByMunicipalidad(@Path("municipalidadId") municipalidadId: Long): Response<List<PlanTuristico>>

    @GET("planes/estado/{estado}")
    suspend fun getPlanesByEstado(@Path("estado") estado: String): Response<List<PlanTuristico>>

    @GET("planes/dificultad/{nivel}")
    suspend fun getPlanesByNivelDificultad(@Path("nivel") nivel: String): Response<List<PlanTuristico>>

    @GET("planes/duracion")
    suspend fun getPlanesByDuracion(@Query("duracionMin") duracionMin: Int, @Query("duracionMax") duracionMax: Int): Response<List<PlanTuristico>>

    @GET("planes/precio")
    suspend fun getPlanesByPrecio(@Query("precioMin") precioMin: Double, @Query("precioMax") precioMax: Double): Response<List<PlanTuristico>>

    @GET("planes/search")
    suspend fun searchPlanes(@Query("termino") termino: String): Response<List<PlanTuristico>>

    @GET("planes/mis-planes")
    suspend fun getMisPlanes(): Response<List<PlanTuristico>>

    @GET("planes/populares")
    suspend fun getPlanesMasPopulares(): Response<List<PlanTuristico>>

    @GET("planes/categoria/{categoriaId}")
    suspend fun getPlanesByCategoria(@Path("categoriaId") categoriaId: Long): Response<List<PlanTuristico>>

    @POST("planes")
    suspend fun createPlan(@Body request: PlanTuristicoRequest): Response<PlanTuristico>

    @PUT("planes/{id}")
    suspend fun updatePlan(@Path("id") id: Long, @Body request: PlanTuristicoRequest): Response<PlanTuristico>

    @DELETE("planes/{id}")
    suspend fun deletePlan(@Path("id") id: Long): Response<Void>

    @PATCH("planes/{id}/estado")
    suspend fun cambiarEstadoPlan(@Path("id") id: Long, @Query("estado") estado: String): Response<PlanTuristico>

    // ========== RESERVAS ==========
    @GET("reservas")
    suspend fun getAllReservas(): Response<List<Reserva>>

    @GET("reservas/{id}")
    suspend fun getReservaById(@Path("id") id: Long): Response<Reserva>

    @GET("reservas/codigo/{codigo}")
    suspend fun getReservaByCodigo(@Path("codigo") codigo: String): Response<Reserva>

    @GET("reservas/mis-reservas")
    suspend fun getMisReservas(): Response<List<Reserva>>

    @GET("reservas/plan/{planId}")
    suspend fun getReservasByPlan(@Path("planId") planId: Long): Response<List<Reserva>>

    @GET("reservas/municipalidad/{municipalidadId}")
    suspend fun getReservasByMunicipalidad(@Path("municipalidadId") municipalidadId: Long): Response<List<Reserva>>

    @POST("reservas")
    suspend fun createReserva(@Body request: ReservaRequest): Response<Reserva>

    @PATCH("reservas/{id}/confirmar")
    suspend fun confirmarReserva(@Path("id") id: Long): Response<Reserva>

    @PATCH("reservas/{id}/cancelar")
    suspend fun cancelarReserva(@Path("id") id: Long, @Query("motivo") motivo: String): Response<Reserva>

    @PATCH("reservas/{id}/completar")
    suspend fun completarReserva(@Path("id") id: Long): Response<Reserva>

    // ========== PAGOS ==========
    @GET("pagos")
    suspend fun getAllPagos(): Response<List<Pago>>

    @GET("pagos/{id}")
    suspend fun getPagoById(@Path("id") id: Long): Response<Pago>

    @GET("pagos/codigo/{codigo}")
    suspend fun getPagoByCodigo(@Path("codigo") codigo: String): Response<Pago>

    @GET("pagos/reserva/{reservaId}")
    suspend fun getPagosByReserva(@Path("reservaId") reservaId: Long): Response<List<Pago>>

    @GET("pagos/mis-pagos")
    suspend fun getMisPagos(): Response<List<Pago>>

    @GET("pagos/municipalidad/{municipalidadId}")
    suspend fun getPagosByMunicipalidad(@Path("municipalidadId") municipalidadId: Long): Response<List<Pago>>

    @POST("pagos")
    suspend fun registrarPago(@Body request: PagoRequest): Response<Pago>

    @PATCH("pagos/{id}/confirmar")
    suspend fun confirmarPago(@Path("id") id: Long): Response<Pago>

    @PATCH("pagos/{id}/rechazar")
    suspend fun rechazarPago(@Path("id") id: Long, @Query("motivo") motivo: String): Response<Pago>

    // ========== USUARIOS ==========
    @GET("usuarios")
    suspend fun getAllUsuarios(): Response<List<UsuarioResponse>>

    @GET("usuarios/{id}")
    suspend fun getUsuarioById(@Path("id") id: Long): Response<UsuarioResponse>

    @GET("usuarios/sin-emprendedor")
    suspend fun getUsuariosSinEmprendedor(): Response<List<UsuarioResponse>>

    @GET("usuarios/con-rol/{rol}")
    suspend fun getUsuariosPorRol(@Path("rol") rol: String): Response<List<UsuarioResponse>>

    @PUT("usuarios/{usuarioId}/asignar-rol/{rol}")
    suspend fun asignarRolAUsuario(@Path("usuarioId") usuarioId: Long, @Path("rol") rol: String): Response<String>

    @PUT("usuarios/{usuarioId}/quitar-rol/{rol}")
    suspend fun quitarRolAUsuario(@Path("usuarioId") usuarioId: Long, @Path("rol") rol: String): Response<String>

    @PUT("usuarios/{usuarioId}/resetear-roles")
    suspend fun resetearRolesUsuario(@Path("usuarioId") usuarioId: Long): Response<String>

    @PUT("usuarios/{usuarioId}/asignar-emprendedor/{emprendedorId}")
    suspend fun asignarUsuarioAEmprendedor(@Path("usuarioId") usuarioId: Long, @Path("emprendedorId") emprendedorId: Long): Response<String>

    @PUT("usuarios/{usuarioId}/cambiar-emprendedor/{emprendedorId}")
    suspend fun cambiarUsuarioDeEmprendedor(@Path("usuarioId") usuarioId: Long, @Path("emprendedorId") emprendedorId: Long): Response<String>

    @DELETE("usuarios/{usuarioId}/desasignar-emprendedor")
    suspend fun desasignarUsuarioDeEmprendedor(@Path("usuarioId") usuarioId: Long): Response<String>

    // ========== CARRITO ==========
    @GET("carrito")
    suspend fun getCarrito(): Response<CarritoResponse>

    @POST("carrito/agregar")
    suspend fun agregarItemAlCarrito(@Body request: CarritoItemRequest): Response<CarritoResponse>

    @PUT("carrito/item/{itemId}")
    suspend fun actualizarCantidadItem(
        @Path("itemId") itemId: Long,
        @Query("cantidad") cantidad: Int
    ): Response<CarritoResponse>

    @DELETE("carrito/item/{itemId}")
    suspend fun eliminarItemDelCarrito(@Path("itemId") itemId: Long): Response<CarritoResponse>

    @DELETE("carrito/limpiar")
    suspend fun limpiarCarrito(): Response<CarritoResponse>

    @GET("carrito/total")
    suspend fun getTotalCarrito(): Response<CarritoTotalResponse>

    @GET("carrito/contar")
    suspend fun contarItemsCarrito(): Response<CarritoContarResponse>

    // ========== RESERVAS DESDE CARRITO ==========
    @POST("reservas-carrito/crear")
    suspend fun crearReservaDesdeCarrito(@Body request: ReservaCarritoRequest): Response<ReservaCarritoResponse>

    @GET("reservas-carrito/mis-reservas")
    suspend fun getMisReservasCarrito(): Response<List<ReservaCarritoResponse>>

    @GET("reservas-carrito/{id}")
    suspend fun getReservaCarritoById(@Path("id") id: Long): Response<ReservaCarritoResponse>

    @GET("reservas-carrito/codigo/{codigo}")
    suspend fun getReservaCarritoByCodigo(@Path("codigo") codigo: String): Response<ReservaCarritoResponse>

    @GET("reservas-carrito/estado/{estado}")
    suspend fun getReservasCarritoPorEstado(@Path("estado") estado: EstadoReservaCarrito): Response<List<ReservaCarritoResponse>>

    @GET("reservas-carrito/estadisticas")
    suspend fun getEstadisticasReservasCarrito(): Response<EstadisticasReservaResponse>

    @GET("reservas-carrito/emprendedor/reservas")
    suspend fun getReservasParaEmprendedor(): Response<List<ReservaCarritoResponse>>

    @PATCH("reservas-carrito/{id}/confirmar")
    suspend fun confirmarReservaCarrito(@Path("id") id: Long): Response<ReservaCarritoResponse>

    @PATCH("reservas-carrito/{id}/completar")
    suspend fun completarReservaCarrito(@Path("id") id: Long): Response<ReservaCarritoResponse>

    @PATCH("reservas-carrito/{id}/cancelar")
    suspend fun cancelarReservaCarrito(
        @Path("id") id: Long,
        @Body request: CancelacionReservaRequest
    ): Response<ReservaCarritoResponse>

    // ========== UBICACIONES ==========
    @GET("ubicaciones/emprendedores")
    suspend fun getUbicacionesEmprendedores(): Response<List<EmprendedorUbicacion>>

    @GET("ubicaciones/servicios")
    suspend fun getUbicacionesServicios(): Response<List<ServicioUbicacion>>

    @GET("ubicaciones/cercanos")
    suspend fun buscarCercanos(
        @Query("latitud") latitud: Double,
        @Query("longitud") longitud: Double,
        @Query("radio") radio: Double = 10.0,
        @Query("tipo") tipo: String? = null
    ): Response<BusquedaCercanosResponse>

    @GET("ubicaciones/distancia")
    suspend fun calcularDistancia(
        @Query("latitudOrigen") latitudOrigen: Double,
        @Query("longitudOrigen") longitudOrigen: Double,
        @Query("latitudDestino") latitudDestino: Double,
        @Query("longitudDestino") longitudDestino: Double
    ): Response<DistanciaResponse>

    @GET("ubicaciones/validar-coordenadas")
    suspend fun validarCoordenadas(
        @Query("latitud") latitud: Double,
        @Query("longitud") longitud: Double
    ): Response<ValidacionCoordenadaResponse>

    @PUT("ubicaciones/emprendedor/{emprendedorId}")
    suspend fun actualizarUbicacionEmprendedor(
        @Path("emprendedorId") emprendedorId: Long,
        @Body request: UbicacionRequest
    ): Response<UbicacionResponse>

    @PUT("ubicaciones/servicio/{servicioId}")
    suspend fun actualizarUbicacionServicio(
        @Path("servicioId") servicioId: Long,
        @Body request: UbicacionRequest
    ): Response<UbicacionResponse>

    // ========== CHAT ==========
    @POST("chat/mensaje")
    suspend fun enviarMensaje(@Body request: MensajeRequest): Response<MensajeResponse>

    @POST("chat/conversacion/iniciar-carrito")
    suspend fun iniciarConversacionCarrito(@Body request: IniciarConversacionCarritoRequest): Response<ConversacionResponse>

    @POST("chat/reserva-carrito/{reservaCarritoId}/mensaje-rapido")
    suspend fun enviarMensajeRapido(
        @Path("reservaCarritoId") reservaCarritoId: Long,
        @Body request: MensajeRapidoRequest
    ): Response<List<MensajeResponse>>

    @GET("chat/conversaciones")
    suspend fun getConversaciones(): Response<List<ConversacionResponse>>

    @GET("chat/mensajes-no-leidos")
    suspend fun getMensajesNoLeidos(): Response<MensajesNoLeidosResponse>

    @GET("chat/reserva-carrito/{reservaCarritoId}/conversaciones")
    suspend fun getConversacionesPorReserva(@Path("reservaCarritoId") reservaCarritoId: Long): Response<List<ConversacionResponse>>
}