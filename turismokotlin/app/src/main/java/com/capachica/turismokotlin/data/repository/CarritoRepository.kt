package com.capachica.turismokotlin.data.repository

import com.capachica.turismokotlin.data.api.ApiService
import com.capachica.turismokotlin.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CarritoRepository(private val apiService: ApiService) {

    fun getCarrito(): Flow<Result<CarritoResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getCarrito()
            if (response.isSuccessful) {
                response.body()?.let { carrito ->
                    emit(Result.Success(carrito))
                } ?: emit(Result.Error("Carrito no encontrado"))
            } else {
                emit(Result.Error("Error al obtener carrito: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun agregarItemAlCarrito(request: CarritoItemRequest): Flow<Result<CarritoResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.agregarItemAlCarrito(request)
            if (response.isSuccessful) {
                response.body()?.let { carrito ->
                    emit(Result.Success(carrito))
                } ?: emit(Result.Error("Error al agregar item"))
            } else {
                emit(Result.Error("Error al agregar item: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun actualizarCantidadItem(itemId: Long, cantidad: Int): Flow<Result<CarritoResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.actualizarCantidadItem(itemId, cantidad)
            if (response.isSuccessful) {
                response.body()?.let { carrito ->
                    emit(Result.Success(carrito))
                } ?: emit(Result.Error("Error al actualizar item"))
            } else {
                emit(Result.Error("Error al actualizar item: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun eliminarItemDelCarrito(itemId: Long): Flow<Result<CarritoResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.eliminarItemDelCarrito(itemId)
            if (response.isSuccessful) {
                response.body()?.let { carrito ->
                    emit(Result.Success(carrito))
                } ?: emit(Result.Error("Error al eliminar item"))
            } else {
                emit(Result.Error("Error al eliminar item: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun limpiarCarrito(): Flow<Result<CarritoResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.limpiarCarrito()
            if (response.isSuccessful) {
                response.body()?.let { carrito ->
                    emit(Result.Success(carrito))
                } ?: emit(Result.Error("Error al limpiar carrito"))
            } else {
                emit(Result.Error("Error al limpiar carrito: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun getTotalCarrito(): Flow<Result<CarritoTotalResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getTotalCarrito()
            if (response.isSuccessful) {
                response.body()?.let { total ->
                    emit(Result.Success(total))
                } ?: emit(Result.Error("Error al obtener total"))
            } else {
                emit(Result.Error("Error al obtener total: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun contarItemsCarrito(): Flow<Result<CarritoContarResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.contarItemsCarrito()
            if (response.isSuccessful) {
                response.body()?.let { count ->
                    emit(Result.Success(count))
                } ?: emit(Result.Error("Error al contar items"))
            } else {
                emit(Result.Error("Error al contar items: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    // ========== MÉTODOS PARA RESERVAS DESDE CARRITO ==========

    fun crearReservaDesdeCarrito(request: ReservaCarritoRequest): Flow<Result<ReservaCarritoResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.crearReservaDesdeCarrito(request)
            if (response.isSuccessful) {
                response.body()?.let { reserva ->
                    emit(Result.Success(reserva))
                } ?: emit(Result.Error("Error al crear reserva"))
            } else {
                emit(Result.Error("Error al crear reserva: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun getMisReservasCarrito(): Flow<Result<List<ReservaCarritoResponse>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getMisReservasCarrito()
            if (response.isSuccessful) {
                response.body()?.let { reservas ->
                    emit(Result.Success(reservas))
                } ?: emit(Result.Error("No se encontraron reservas"))
            } else {
                emit(Result.Error("Error al obtener reservas: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun getReservaCarritoById(id: Long): Flow<Result<ReservaCarritoResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getReservaCarritoById(id)
            if (response.isSuccessful) {
                response.body()?.let { reserva ->
                    emit(Result.Success(reserva))
                } ?: emit(Result.Error("Reserva no encontrada"))
            } else {
                emit(Result.Error("Error al obtener reserva: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun getReservasCarritoPorEstado(estado: EstadoReservaCarrito): Flow<Result<List<ReservaCarritoResponse>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getReservasCarritoPorEstado(estado)
            if (response.isSuccessful) {
                response.body()?.let { reservas ->
                    emit(Result.Success(reservas))
                } ?: emit(Result.Error("No se encontraron reservas"))
            } else {
                emit(Result.Error("Error al obtener reservas: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun confirmarReservaCarrito(id: Long): Flow<Result<ReservaCarritoResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.confirmarReservaCarrito(id)
            if (response.isSuccessful) {
                response.body()?.let { reserva ->
                    emit(Result.Success(reserva))
                } ?: emit(Result.Error("Error al confirmar reserva"))
            } else {
                emit(Result.Error("Error al confirmar reserva: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun completarReservaCarrito(id: Long): Flow<Result<ReservaCarritoResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.completarReservaCarrito(id)
            if (response.isSuccessful) {
                response.body()?.let { reserva ->
                    emit(Result.Success(reserva))
                } ?: emit(Result.Error("Error al completar reserva"))
            } else {
                emit(Result.Error("Error al completar reserva: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }

    fun cancelarReservaCarrito(id: Long, request: CancelacionReservaRequest): Flow<Result<ReservaCarritoResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.cancelarReservaCarrito(id, request)
            if (response.isSuccessful) {
                response.body()?.let { reserva ->
                    emit(Result.Success(reserva))
                } ?: emit(Result.Error("Error al cancelar reserva"))
            } else {
                emit(Result.Error("Error al cancelar reserva: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error de conexión: ${e.message}"))
        }
    }
}