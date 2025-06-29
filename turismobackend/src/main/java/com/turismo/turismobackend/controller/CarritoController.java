package com.turismo.turismobackend.controller;

import com.turismo.turismobackend.dto.request.CarritoItemRequest;
import com.turismo.turismobackend.dto.response.CarritoResponse;
import com.turismo.turismobackend.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
@Tag(name = "Carrito", description = "API para gestión del carrito de compras")
@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_EMPRENDEDOR', 'ROLE_MUNICIPALIDAD', 'ROLE_ADMIN')")
public class CarritoController {
    
    private final CarritoService carritoService;
    
    @GetMapping
    @Operation(summary = "Obtener carrito del usuario autenticado")
    public ResponseEntity<CarritoResponse> obtenerCarrito() {
        CarritoResponse carrito = carritoService.obtenerCarrito();
        return ResponseEntity.ok(carrito);
    }
    
    @PostMapping("/agregar")
    @Operation(summary = "Agregar item al carrito")
    public ResponseEntity<CarritoResponse> agregarItem(
            @Valid @RequestBody CarritoItemRequest request) {
        CarritoResponse carrito = carritoService.agregarItem(request);
        return ResponseEntity.ok(carrito);
    }
    
    @PutMapping("/item/{itemId}")
    @Operation(summary = "Actualizar cantidad de un item")
    public ResponseEntity<CarritoResponse> actualizarCantidad(
            @Parameter(description = "ID del item") @PathVariable Long itemId,
            @Parameter(description = "Nueva cantidad") @RequestParam Integer cantidad) {
        CarritoResponse carrito = carritoService.actualizarCantidad(itemId, cantidad);
        return ResponseEntity.ok(carrito);
    }
    
    @DeleteMapping("/item/{itemId}")
    @Operation(summary = "Eliminar item del carrito")
    public ResponseEntity<CarritoResponse> eliminarItem(
            @Parameter(description = "ID del item") @PathVariable Long itemId) {
        CarritoResponse carrito = carritoService.eliminarItem(itemId);
        return ResponseEntity.ok(carrito);
    }
    
    @DeleteMapping("/limpiar")
    @Operation(summary = "Limpiar carrito completo")
    public ResponseEntity<Void> limpiarCarrito() {
        carritoService.limpiarCarrito();
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/contar")
    @Operation(summary = "Contar items en el carrito")
    public ResponseEntity<Long> contarItems() {
        Long cantidad = carritoService.contarItems();
        return ResponseEntity.ok(cantidad);
    }
    
    @GetMapping("/total")
    @Operation(summary = "Obtener total del carrito")
    public ResponseEntity<CarritoResponse> obtenerTotalCarrito() {
        CarritoResponse carrito = carritoService.obtenerCarrito();
        return ResponseEntity.ok(carrito);
    }
}