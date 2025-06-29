package com.turismo.turismobackend.service;

import com.turismo.turismobackend.dto.request.CarritoItemRequest;
import com.turismo.turismobackend.dto.response.*;
import com.turismo.turismobackend.exception.ResourceNotFoundException;
import com.turismo.turismobackend.model.*;
import com.turismo.turismobackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CarritoService {
    
    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final ServicioTuristicoRepository servicioRepository;
    
    public CarritoResponse obtenerCarrito() {
        Usuario usuario = getCurrentUser();
        Carrito carrito = obtenerOCrearCarrito(usuario);
        return convertToCarritoResponse(carrito);
    }
    
    public CarritoResponse agregarItem(CarritoItemRequest request) {
        Usuario usuario = getCurrentUser();
        Carrito carrito = obtenerOCrearCarrito(usuario);
        
        ServicioTuristico servicio = servicioRepository.findById(request.getServicioId())
                .orElseThrow(() -> new ResourceNotFoundException("Servicio", "id", request.getServicioId()));
        
        // Validar disponibilidad
        if (servicio.getEstado() != ServicioTuristico.EstadoServicio.ACTIVO) {
            throw new RuntimeException("El servicio no está disponible");
        }
        
        // Verificar si ya existe el item con la misma fecha
        Optional<CarritoItem> itemExistente = carritoItemRepository
                .findByCarritoIdAndServicioIdAndFechaServicio(
                        carrito.getId(), 
                        request.getServicioId(), 
                        request.getFechaServicio()
                );
        
        if (itemExistente.isPresent()) {
            // Actualizar cantidad
            CarritoItem item = itemExistente.get();
            item.setCantidad(item.getCantidad() + request.getCantidad());
            item.setNotasEspeciales(request.getNotasEspeciales());
            carritoItemRepository.save(item);
        } else {
            // Crear nuevo item
            CarritoItem nuevoItem = CarritoItem.builder()
                    .carrito(carrito)
                    .servicio(servicio)
                    .cantidad(request.getCantidad())
                    .precioUnitario(servicio.getPrecio())
                    .fechaServicio(request.getFechaServicio())
                    .notasEspeciales(request.getNotasEspeciales())
                    .build();
            carritoItemRepository.save(nuevoItem);
        }
        
        return convertToCarritoResponse(carrito);
    }
    
    public CarritoResponse actualizarCantidad(Long itemId, Integer nuevaCantidad) {
        Usuario usuario = getCurrentUser();
        
        CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item del carrito", "id", itemId));
        
        // Verificar que el item pertenece al usuario
        if (!item.getCarrito().getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tiene permisos para modificar este item");
        }
        
        if (nuevaCantidad <= 0) {
            carritoItemRepository.delete(item);
        } else {
            item.setCantidad(nuevaCantidad);
            carritoItemRepository.save(item);
        }
        
        return convertToCarritoResponse(item.getCarrito());
    }
    
    public CarritoResponse eliminarItem(Long itemId) {
        Usuario usuario = getCurrentUser();
        
        CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item del carrito", "id", itemId));
        
        // Verificar que el item pertenece al usuario
        if (!item.getCarrito().getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tiene permisos para eliminar este item");
        }
        
        Carrito carrito = item.getCarrito();
        carritoItemRepository.delete(item);
        
        return convertToCarritoResponse(carrito);
    }
    
    public void limpiarCarrito() {
        Usuario usuario = getCurrentUser();
        
        Optional<Carrito> carritoOpt = carritoRepository.findByUsuario(usuario);
        if (carritoOpt.isPresent()) {
            carritoItemRepository.deleteByCarritoId(carritoOpt.get().getId());
        }
    }
    
    public Long contarItems() {
        Usuario usuario = getCurrentUser();
        return carritoItemRepository.countByUsuarioId(usuario.getId());
    }
    
    private Carrito obtenerOCrearCarrito(Usuario usuario) {
        return carritoRepository.findByUsuario(usuario)
                .orElseGet(() -> {
                    Carrito nuevoCarrito = Carrito.builder()
                            .usuario(usuario)
                            .build();
                    return carritoRepository.save(nuevoCarrito);
                });
    }
    
    private CarritoResponse convertToCarritoResponse(Carrito carrito) {
        List<CarritoItemResponse> items = carrito.getItems().stream()
                .map(this::convertToCarritoItemResponse)
                .collect(Collectors.toList());
        
        return CarritoResponse.builder()
                .id(carrito.getId())
                .usuarioId(carrito.getUsuario().getId())
                .fechaCreacion(carrito.getFechaCreacion())
                .fechaActualizacion(carrito.getFechaActualizacion())
                .totalCarrito(carrito.getTotalCarrito())
                .totalItems(carrito.getTotalItems())
                .items(items)
                .build();
    }
    
    private CarritoItemResponse convertToCarritoItemResponse(CarritoItem item) {
        return CarritoItemResponse.builder()
                .id(item.getId())
                .cantidad(item.getCantidad())
                .precioUnitario(item.getPrecioUnitario())
                .subtotal(item.getSubtotal())
                .fechaServicio(item.getFechaServicio())
                .notasEspeciales(item.getNotasEspeciales())
                .fechaAgregado(item.getFechaAgregado())
                .servicio(convertToServicioBasicResponse(item.getServicio()))
                .build();
    }
    
    private ServicioTuristicoBasicResponse convertToServicioBasicResponse(ServicioTuristico servicio) {
        return ServicioTuristicoBasicResponse.builder()
                .id(servicio.getId())
                .nombre(servicio.getNombre())
                .descripcion(servicio.getDescripcion())
                .precio(servicio.getPrecio())
                .duracionHoras(servicio.getDuracionHoras())
                .tipo(servicio.getTipo())
                .imagenUrl(servicio.getImagenUrl())
                .ubicacion(UbicacionResponse.builder()
                        .latitud(servicio.getLatitud())
                        .longitud(servicio.getLongitud())
                        .tieneUbicacionValida(servicio.tieneUbicacionValida())
                        .build())
                .emprendedor(EmprendedorBasicResponse.builder()
                        .id(servicio.getEmprendedor().getId())
                        .nombreEmpresa(servicio.getEmprendedor().getNombreEmpresa())
                        .rubro(servicio.getEmprendedor().getRubro())
                        .telefono(servicio.getEmprendedor().getTelefono())
                        .email(servicio.getEmprendedor().getEmail())
                        .build())
                .build();
    }
    
    private Usuario getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Usuario)) {
            throw new RuntimeException("Usuario no autenticado correctamente");
        }
        return (Usuario) principal;
    }
}