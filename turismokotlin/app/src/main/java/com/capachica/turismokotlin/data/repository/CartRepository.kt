package com.capachica.turismokotlin.data.repository

import com.capachica.turismokotlin.data.model.AddToCartRequest
import com.capachica.turismokotlin.data.model.CartRemoto
import com.capachica.turismokotlin.network.api.CartApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val cartApiService: CartApiService
) {
    suspend fun getCarrito(): Result<CartRemoto> {
        return try {
            val response = cartApiService.getCarrito()
            if (response.isSuccessful) {
                Result.success(response.body() ?: CartRemoto(0, 0, "", "", 0.0, 0, emptyList()))
            } else {
                Result.failure(Exception("Error al obtener carrito: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addToCart(servicioId: Long, cantidad: Int, fechaServicio: String, notasEspeciales: String? = null): Result<CartRemoto> {
        return try {
            val request = AddToCartRequest(servicioId, cantidad, fechaServicio, notasEspeciales)
            val response = cartApiService.addToCart(request)
            if (response.isSuccessful) {
                response.body()?.let { cart ->
                    Result.success(cart)
                } ?: Result.failure(Exception("Error al agregar al carrito"))
            } else {
                Result.failure(Exception("Error al agregar al carrito: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCartItem(itemId: Long, cantidad: Int): Result<CartRemoto> {
        return try {
            val response = cartApiService.updateCartItem(itemId, cantidad)
            if (response.isSuccessful) {
                response.body()?.let { cart ->
                    Result.success(cart)
                } ?: Result.failure(Exception("Error al actualizar item"))
            } else {
                Result.failure(Exception("Error al actualizar item: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeCartItem(itemId: Long): Result<CartRemoto> {
        return try {
            val response = cartApiService.removeCartItem(itemId)
            if (response.isSuccessful) {
                response.body()?.let { cart ->
                    Result.success(cart)
                } ?: Result.failure(Exception("Error al eliminar item"))
            } else {
                Result.failure(Exception("Error al eliminar item: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearCart(): Result<Unit> {
        return try {
            val response = cartApiService.clearCart()
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al limpiar carrito: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCartCount(): Result<Int> {
        return try {
            val response = cartApiService.getCartCount()
            if (response.isSuccessful) {
                Result.success(response.body() ?: 0)
            } else {
                Result.failure(Exception("Error al obtener contador: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}