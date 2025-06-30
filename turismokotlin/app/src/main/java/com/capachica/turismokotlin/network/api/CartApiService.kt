package com.capachica.turismokotlin.network.api

import com.capachica.turismokotlin.data.model.AddToCartRequest
import com.capachica.turismokotlin.data.model.CartRemoto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CartApiService {
    @GET("carrito")
    suspend fun getCarrito(): Response<CartRemoto>

    @POST("carrito/agregar")
    suspend fun addToCart(@Body request: AddToCartRequest): Response<CartRemoto>

    @PUT("carrito/item/{itemId}")
    suspend fun updateCartItem(
        @Path("itemId") itemId: Long,
        @Query("cantidad") cantidad: Int
    ): Response<CartRemoto>

    @DELETE("carrito/item/{itemId}")
    suspend fun removeCartItem(@Path("itemId") itemId: Long): Response<CartRemoto>

    @DELETE("carrito/limpiar")
    suspend fun clearCart(): Response<Unit>

    @GET("carrito/contar")
    suspend fun getCartCount(): Response<Int>

    @GET("carrito/total")
    suspend fun getCartTotal(): Response<CartRemoto>
}
