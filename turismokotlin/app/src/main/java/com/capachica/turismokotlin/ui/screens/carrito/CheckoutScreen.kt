package com.capachica.turismokotlin.ui.screens.carrito

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Payment
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capachica.turismokotlin.data.model.CarritoResponse
import com.capachica.turismokotlin.data.model.ReservaCarritoRequest
import com.capachica.turismokotlin.data.model.MetodoPago
import com.capachica.turismokotlin.data.model.ReservaCarritoResponse
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.viewmodel.CarritoViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onNavigateBack: () -> Unit,
    onCheckoutSuccess: (Long) -> Unit,
    viewModelFactory: ViewModelFactory
) {
    val carritoViewModel: CarritoViewModel = viewModel(factory = viewModelFactory)
    
    val carritoState by carritoViewModel.carritoState.collectAsState()
    val crearReservaState by carritoViewModel.crearReservaState.collectAsState()
    
    var observaciones by remember { mutableStateOf("") }
    var fechaInicio by remember { mutableStateOf("") }
    var fechaFin by remember { mutableStateOf("") }
    var numeroPersonas by remember { mutableIntStateOf(1) }
    var metodoPago by remember { mutableStateOf<MetodoPago?>(null) }
    var showMetodoPagoMenu by remember { mutableStateOf(false) }
    
    // Cargar carrito al iniciar
    LaunchedEffect(Unit) {
        carritoViewModel.cargarCarrito()
    }
    
    // Manejar éxito en creación de reserva
    LaunchedEffect(crearReservaState) {
        if (crearReservaState is Result.Success) {
            onCheckoutSuccess((crearReservaState as Result.Success<ReservaCarritoResponse>).data.id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver"
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Finalizar Reserva",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        when (carritoState) {
            is Result.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            is Result.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Error al cargar el carrito",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = (carritoState as Result.Error).message,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { carritoViewModel.cargarCarrito() }) {
                        Text("Reintentar")
                    }
                }
            }
            
            is Result.Success -> {
                val carrito = (carritoState as Result.Success<CarritoResponse>).data
                
                if (carrito.items.isEmpty()) {
                    // Carrito vacío
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "El carrito está vacío",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Agrega servicios antes de finalizar la reserva",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onNavigateBack) {
                            Text("Volver a Servicios")
                        }
                    }
                } else {
                    // Contenido del checkout
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Resumen del carrito
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Resumen de la Reserva",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    carrito.items.forEach { item ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = item.servicio.nombre,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Text(
                                                    text = "Cantidad: ${item.cantidad}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.outline
                                                )
                                                if (item.notasEspeciales?.isNotBlank() == true) {
                                                    Text(
                                                        text = "Obs: ${item.notasEspeciales}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.outline
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "S/ %.2f".format(item.subtotal),
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        if (item != carrito.items.last()) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }
                                    
                                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Total:",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "S/ %.2f".format(carrito.totalCarrito),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Formulario de datos adicionales
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Información de la Reserva",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    // Fechas
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = fechaInicio,
                                            onValueChange = { fechaInicio = it },
                                            label = { Text("Fecha Inicio") },
                                            placeholder = { Text("2024-03-15") },
                                            modifier = Modifier.weight(1f)
                                        )
                                        
                                        OutlinedTextField(
                                            value = fechaFin,
                                            onValueChange = { fechaFin = it },
                                            label = { Text("Fecha Fin") },
                                            placeholder = { Text("2024-03-16") },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    // Número de personas
                                    OutlinedTextField(
                                        value = numeroPersonas.toString(),
                                        onValueChange = { 
                                            val num = it.toIntOrNull()
                                            if (num != null && num > 0) {
                                                numeroPersonas = num
                                            }
                                        },
                                        label = { Text("Número de Personas") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    // Método de pago
                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        OutlinedTextField(
                                            value = metodoPago?.name ?: "",
                                            onValueChange = { },
                                            label = { Text("Método de Pago (Opcional)") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { showMetodoPagoMenu = true },
                                            enabled = false,
                                            trailingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.ArrowDropDown,
                                                    contentDescription = null
                                                )
                                            }
                                        )
                                        
                                        DropdownMenu(
                                            expanded = showMetodoPagoMenu,
                                            onDismissRequest = { showMetodoPagoMenu = false }
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("Sin especificar") },
                                                onClick = {
                                                    metodoPago = null
                                                    showMetodoPagoMenu = false
                                                }
                                            )
                                            MetodoPago.values().forEach { metodo ->
                                                DropdownMenuItem(
                                                    text = { Text(metodo.name) },
                                                    onClick = {
                                                        metodoPago = metodo
                                                        showMetodoPagoMenu = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    // Observaciones
                                    OutlinedTextField(
                                        value = observaciones,
                                        onValueChange = { observaciones = it },
                                        label = { Text("Observaciones (Opcional)") },
                                        placeholder = { Text("Comentarios adicionales...") },
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 3
                                    )
                                }
                            }
                        }
                    }
                    
                    // Error en creación de reserva
                    if (crearReservaState is Result.Error) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = "Error: ${(crearReservaState as Result.Error).message}",
                                modifier = Modifier.padding(12.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    // Botón de finalizar reserva
                    Button(
                        onClick = {
                            if (fechaInicio.isNotBlank() && fechaFin.isNotBlank()) {
                                val request = ReservaCarritoRequest(
                                    fechaInicio = fechaInicio,
                                    fechaFin = fechaFin,
                                    numeroPersonas = numeroPersonas,
                                    metodoPago = metodoPago,
                                    observaciones = observaciones.takeIf { it.isNotBlank() }
                                )
                                carritoViewModel.crearReservaDesdeCarrito(request)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = crearReservaState !is Result.Loading && fechaInicio.isNotBlank() && fechaFin.isNotBlank()
                    ) {
                        if (crearReservaState is Result.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        } else {
                            Icon(
                                imageVector = Icons.Default.Payment,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("Finalizar Reserva")
                    }
                }
            }
        }
    }
}