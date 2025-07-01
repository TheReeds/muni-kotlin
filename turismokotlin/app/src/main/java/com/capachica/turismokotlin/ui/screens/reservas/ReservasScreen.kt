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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.capachica.turismokotlin.data.model.EstadoReserva
import com.capachica.turismokotlin.data.model.ReservaCarrito
import com.capachica.turismokotlin.ui.viewmodel.ReservasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservasScreen(
    onNavigateBack: () -> Unit,
    onNavigateToReservaDetail: (Long) -> Unit,
    onNavigateToMisReservasPlanes: () -> Unit, // NUEVO parámetro
    viewModel: ReservasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.loadMisReservas()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Mis Reservas") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                IconButton(onClick = { viewModel.loadMisReservas() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                }
            }
        )

        // Tabs para alternar entre servicios y planes
        TabRow(
            selectedTabIndex = selectedTab
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Servicios") },
                icon = {
                    BadgedBox(
                        badge = {
                            val pendientes = uiState.reservas.count { it.estado == EstadoReserva.PENDIENTE }
                            if (pendientes > 0) {
                                Badge {
                                    Text("$pendientes")
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.RoomService, contentDescription = null)
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = {
                    selectedTab = 1
                    // Navegar directamente a la pantalla de reservas de planes
                    onNavigateToMisReservasPlanes()
                },
                text = { Text("Planes") },
                icon = {
                    Icon(Icons.Default.Map, contentDescription = null)
                }
            )
        }

        // Contenido (solo mostramos servicios cuando selectedTab == 0)
        when (selectedTab) {
            0 -> {
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CircularProgressIndicator()
                                Text("Cargando tus reservas de servicios...")
                            }
                        }
                    }

                    uiState.error != null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "Error: ${uiState.error}",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(16.dp)
                            )
                            Button(onClick = { viewModel.loadMisReservas() }) {
                                Text("Reintentar")
                            }
                        }
                    }

                    uiState.reservas.isEmpty() -> {
                        EmptyReservasContent(
                            title = "No tienes reservas de servicios",
                            description = "Explora nuestros servicios y realiza tu primera reserva",
                            onNavigateBack = onNavigateBack
                        )
                    }

                    else -> {
                        ReservasServiciosContent(
                            reservas = uiState.reservas,
                            onNavigateToReservaDetail = onNavigateToReservaDetail,
                            onCancelarReserva = { reservaId, motivo ->
                                viewModel.cancelarReserva(reservaId, motivo)
                            }
                        )
                    }
                }
            }
            // El tab 1 navega directamente, no necesitamos contenido aquí
        }
    }

    // Resetear tab cuando se regresa de planes
    LaunchedEffect(selectedTab) {
        if (selectedTab == 1) {
            selectedTab = 0 // Resetear a servicios
        }
    }
}

@Composable
private fun EmptyReservasContent(
    title: String,
    description: String,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.RoomService,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )

        Button(
            onClick = onNavigateBack,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Explorar Servicios")
        }
    }
}

@Composable
private fun ReservasServiciosContent(
    reservas: List<ReservaCarrito>,
    onNavigateToReservaDetail: (Long) -> Unit,
    onCancelarReserva: (Long, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Resumen estadístico
        item {
            ReservasServiciosStatsCard(reservas = reservas)
        }

        // Agrupar por estado
        val reservasPendientes = reservas.filter { it.estado == EstadoReserva.PENDIENTE }
        val reservasConfirmadas = reservas.filter { it.estado == EstadoReserva.CONFIRMADA }
        val reservasCompletadas = reservas.filter { it.estado == EstadoReserva.COMPLETADA }

        // Pendientes
        if (reservasPendientes.isNotEmpty()) {
            item {
                Text(
                    text = "Pendientes (${reservasPendientes.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(reservasPendientes.sortedByDescending { it.fechaReserva }) { reserva ->
                ReservaCard(
                    reserva = reserva,
                    onClick = { onNavigateToReservaDetail(reserva.id) },
                    onCancelar = { motivo -> onCancelarReserva(reserva.id, motivo) }
                )
            }
        }

        // Confirmadas
        if (reservasConfirmadas.isNotEmpty()) {
            item {
                Text(
                    text = "Confirmadas (${reservasConfirmadas.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(reservasConfirmadas.sortedByDescending { it.fechaReserva }) { reserva ->
                ReservaCard(
                    reserva = reserva,
                    onClick = { onNavigateToReservaDetail(reserva.id) },
                    onCancelar = { motivo -> onCancelarReserva(reserva.id, motivo) }
                )
            }
        }

        // Completadas (últimas 5)
        if (reservasCompletadas.isNotEmpty()) {
            item {
                Text(
                    text = "Completadas (${reservasCompletadas.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(reservasCompletadas.take(5).sortedByDescending { it.fechaReserva }) { reserva ->
                ReservaCard(
                    reserva = reserva,
                    onClick = { onNavigateToReservaDetail(reserva.id) },
                    onCancelar = { motivo -> onCancelarReserva(reserva.id, motivo) },
                    showCancelButton = false // No se pueden cancelar las completadas
                )
            }

            if (reservasCompletadas.size > 5) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Y ${reservasCompletadas.size - 5} reservas completadas más...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            TextButton(
                                onClick = { /* Navegar a historial completo */ }
                            ) {
                                Text("Ver todas")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReservasServiciosStatsCard(
    reservas: List<ReservaCarrito>
) {
    val pendientes = reservas.count { it.estado == EstadoReserva.PENDIENTE }
    val confirmadas = reservas.count { it.estado == EstadoReserva.CONFIRMADA }
    val completadas = reservas.count { it.estado == EstadoReserva.COMPLETADA }
    val canceladas = reservas.count { it.estado == EstadoReserva.CANCELADA }
    val montoTotal = reservas.filter { it.estado != EstadoReserva.CANCELADA }.sumOf { it.montoFinal }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reservas de Servicios",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                Icon(
                    Icons.Default.RoomService,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ServicioStatItem(
                    count = pendientes,
                    label = "Pendientes",
                    color = MaterialTheme.colorScheme.secondary,
                    icon = Icons.Default.Schedule
                )
                ServicioStatItem(
                    count = confirmadas,
                    label = "Confirmadas",
                    color = MaterialTheme.colorScheme.primary,
                    icon = Icons.Default.CheckCircle
                )
                ServicioStatItem(
                    count = completadas,
                    label = "Completadas",
                    color = MaterialTheme.colorScheme.tertiary,
                    icon = Icons.Default.TaskAlt
                )
            }

            if (montoTotal > 0) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total en servicios:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "S/ $montoTotal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun ServicioStatItem(
    count: Int,
    label: String,
    color: androidx.compose.ui.graphics.Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = "$count",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReservaCard(
    reserva: ReservaCarrito,
    onClick: () -> Unit,
    onCancelar: (String) -> Unit,
    showCancelButton: Boolean = true,
    modifier: Modifier = Modifier
) {
    var showCancelDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header con código y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reserva #${reserva.codigoReserva}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                EstadoChip(estado = reserva.estado)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Información básica
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Fecha de reserva",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = reserva.fechaReserva,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "S/ ${reserva.montoFinal}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Items de la reserva (mostrar solo el primero si hay varios)
            if (reserva.items.isNotEmpty()) {
                val primerItem = reserva.items.first()
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = primerItem.servicio.imagenUrl ?: "https://via.placeholder.com/60x60",
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = primerItem.servicio.nombre,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Fecha: ${primerItem.fechaServicio}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (reserva.items.size > 1) {
                            Text(
                                text = "+ ${reserva.items.size - 1} más",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Acciones (solo cancelar para pendientes)
            if (showCancelButton && reserva.estado == EstadoReserva.PENDIENTE) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { showCancelDialog = true }) {
                        Text("Cancelar", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }

    // Dialog de cancelación
    if (showCancelDialog) {
        CancelReservaDialog(
            onDismiss = { showCancelDialog = false },
            onConfirm = { motivo ->
                onCancelar(motivo)
                showCancelDialog = false
            }
        )
    }
}

@Composable
private fun EstadoChip(estado: EstadoReserva) {
    val (color, text) = when (estado) {
        EstadoReserva.PENDIENTE -> MaterialTheme.colorScheme.tertiary to "Pendiente"
        EstadoReserva.CONFIRMADA -> MaterialTheme.colorScheme.primary to "Confirmado"
        EstadoReserva.COMPLETADA -> MaterialTheme.colorScheme.secondary to "Completado"
        EstadoReserva.CANCELADA -> MaterialTheme.colorScheme.error to "Cancelado"
    }

    AssistChip(
        onClick = { },
        label = { Text(text) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.1f),
            labelColor = color
        )
    )
}

@Composable
private fun CancelReservaDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var motivo by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cancelar Reserva") },
        text = {
            Column {
                Text("¿Estás seguro de que deseas cancelar esta reserva?")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = motivo,
                    onValueChange = { motivo = it },
                    label = { Text("Motivo de cancelación") },
                    placeholder = { Text("Describe el motivo...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(motivo) },
                enabled = motivo.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Cancelar Reserva")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Mantener")
            }
        }
    )
}