package com.capachica.turismokotlin.ui.screens.reservas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capachica.turismokotlin.data.model.CancelacionReservaRequest
import com.capachica.turismokotlin.data.model.EstadoReservaCarrito
import com.capachica.turismokotlin.data.model.ReservaCarritoResponse
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.viewmodel.CarritoViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaCarritoDetailScreen(
    reservaId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToChat: ((Long) -> Unit)? = null,
    viewModelFactory: ViewModelFactory
) {
    val carritoViewModel: CarritoViewModel = viewModel(factory = viewModelFactory)
    
    val reservaState by carritoViewModel.reservaCarritoDetailState.collectAsState()
    val confirmarReservaState by carritoViewModel.confirmarReservaState.collectAsState()
    val completarReservaState by carritoViewModel.completarReservaState.collectAsState()
    val cancelarReservaState by carritoViewModel.cancelarReservaState.collectAsState()
    
    var showCancelDialog by remember { mutableStateOf(false) }
    var motivoCancelacion by remember { mutableStateOf("") }

    // Cargar detalle de reserva al iniciar
    LaunchedEffect(reservaId) {
        carritoViewModel.cargarReservaCarritoById(reservaId)
    }
    
    // Limpiar estados al volver
    LaunchedEffect(Unit) {
        carritoViewModel.clearReservaStates()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top Bar
        TopAppBar(
            title = { Text("Detalle de Reserva") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            }
        )

        when (reservaState) {
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Error al cargar la reserva",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = (reservaState as Result.Error).message,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { carritoViewModel.cargarReservaCarritoById(reservaId) }
                    ) {
                        Text("Reintentar")
                    }
                }
            }
            
            is Result.Success -> {
                val reserva = (reservaState as Result.Success<ReservaCarritoResponse>).data
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Información general
                    item {
                        ReservaInfoCard(reserva = reserva)
                    }
                    
                    // Lista de servicios
                    item {
                        ServiciosCard(reserva = reserva)
                    }
                    
                    // Información de pago (si existe)
                    if (reserva.montoTotal > 0) {
                        item {
                            PagoInfoCard(reserva = reserva)
                        }
                    }
                    
                    // Observaciones (si existen)
                    if (reserva.observaciones?.isNotBlank() == true) {
                        item {
                            ObservacionesCard(observaciones = reserva.observaciones)
                        }
                    }
                    
                    // Botones de acción
                    item {
                        AccionesCard(
                            reserva = reserva,
                            onConfirmar = { carritoViewModel.confirmarReservaCarrito(reservaId) },
                            onCompletar = { carritoViewModel.completarReservaCarrito(reservaId) },
                            onCancelar = { showCancelDialog = true },
                            onChat = { onNavigateToChat?.invoke(reservaId) },
                            confirmarLoading = confirmarReservaState is Result.Loading,
                            completarLoading = completarReservaState is Result.Loading,
                            cancelarLoading = cancelarReservaState is Result.Loading
                        )
                    }
                    
                    // Mostrar errores de acciones
                    if (confirmarReservaState is Result.Error) {
                        item {
                            ErrorCard(message = "Error al confirmar: ${(confirmarReservaState as Result.Error).message}")
                        }
                    }
                    
                    if (completarReservaState is Result.Error) {
                        item {
                            ErrorCard(message = "Error al completar: ${(completarReservaState as Result.Error).message}")
                        }
                    }
                    
                    if (cancelarReservaState is Result.Error) {
                        item {
                            ErrorCard(message = "Error al cancelar: ${(cancelarReservaState as Result.Error).message}")
                        }
                    }
                }
            }
        }
    }
    
    // Dialog de cancelación
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancelar Reserva") },
            text = {
                Column {
                    Text("¿Estás seguro de que deseas cancelar esta reserva?")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = motivoCancelacion,
                        onValueChange = { motivoCancelacion = it },
                        label = { Text("Motivo de cancelación") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val request = CancelacionReservaRequest(motivoCancelacion)
                        carritoViewModel.cancelarReservaCarrito(reservaId, request)
                        showCancelDialog = false
                        motivoCancelacion = ""
                    },
                    enabled = motivoCancelacion.isNotBlank()
                ) {
                    Text("Cancelar Reserva")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
private fun ReservaInfoCard(reserva: ReservaCarritoResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reserva #${reserva.codigoReserva}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                EstadoChip(estado = reserva.estado)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            InfoRow(label = "Fecha de creación:", value = reserva.fechaCreacion)
            InfoRow(label = "Fecha inicio:", value = reserva.fechaInicio)
            InfoRow(label = "Fecha fin:", value = reserva.fechaFin)
            InfoRow(label = "Número de personas:", value = reserva.numeroPersonas.toString())
            InfoRow(label = "Total a pagar:", value = "S/ %.2f".format(reserva.montoTotal))
            
            if (reserva.metodoPago != null) {
                InfoRow(label = "Método de pago:", value = reserva.metodoPago.name)
            }
        }
    }
}

@Composable
private fun ServiciosCard(reserva: ReservaCarritoResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Servicios Incluidos (${reserva.items.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            reserva.items.forEach { item ->
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
                            text = "Cantidad: ${item.cantidad} | Precio: S/ %.2f".format(item.precioUnitario),
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
                
                if (item != reserva.items.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun PagoInfoCard(reserva: ReservaCarritoResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Información de Pago",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            InfoRow(label = "Subtotal:", value = "S/ %.2f".format(reserva.montoTotal))
            InfoRow(label = "Estado de pago:", value = "Pendiente") // Esto se podría obtener de la reserva
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "El pago se coordina directamente con cada emprendedor",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun ObservacionesCard(observaciones: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Observaciones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = observaciones,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun AccionesCard(
    reserva: ReservaCarritoResponse,
    onConfirmar: () -> Unit,
    onCompletar: () -> Unit,
    onCancelar: () -> Unit,
    onChat: () -> Unit,
    confirmarLoading: Boolean,
    completarLoading: Boolean,
    cancelarLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Acciones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Botón de chat (siempre disponible)
            OutlinedButton(
                onClick = onChat,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Chat, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Contactar Emprendedores")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Acciones según el estado
            when (reserva.estado) {
                EstadoReservaCarrito.PENDIENTE -> {
                    Button(
                        onClick = onConfirmar,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !confirmarLoading
                    ) {
                        if (confirmarLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("Confirmar Reserva")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedButton(
                        onClick = onCancelar,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !cancelarLoading
                    ) {
                        if (cancelarLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("Cancelar Reserva")
                    }
                }
                
                EstadoReservaCarrito.CONFIRMADA -> {
                    Button(
                        onClick = onCompletar,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !completarLoading
                    ) {
                        if (completarLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("Marcar como Completada")
                    }
                }
                
                EstadoReservaCarrito.PAGADA -> {
                    Text(
                        text = "Reserva pagada - En espera de inicio",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                EstadoReservaCarrito.EN_PROCESO -> {
                    Button(
                        onClick = onCompletar,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !completarLoading
                    ) {
                        if (completarLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("Marcar como Completada")
                    }
                }
                
                EstadoReservaCarrito.COMPLETADA -> {
                    Text(
                        text = "Reserva completada exitosamente",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                EstadoReservaCarrito.CANCELADA -> {
                    Text(
                        text = "Esta reserva ha sido cancelada",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun EstadoChip(estado: EstadoReservaCarrito) {
    val (backgroundColor, contentColor) = when (estado) {
        EstadoReservaCarrito.PENDIENTE -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        EstadoReservaCarrito.CONFIRMADA -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        EstadoReservaCarrito.PAGADA -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        EstadoReservaCarrito.EN_PROCESO -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
        EstadoReservaCarrito.COMPLETADA -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        EstadoReservaCarrito.CANCELADA -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }
    
    Surface(
        modifier = Modifier.clip(RoundedCornerShape(16.dp)),
        color = backgroundColor
    ) {
        Text(
            text = estado.name,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(12.dp),
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodySmall
        )
    }
}