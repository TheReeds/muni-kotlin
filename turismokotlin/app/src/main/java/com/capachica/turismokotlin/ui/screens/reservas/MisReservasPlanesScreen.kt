package com.capachica.turismokotlin.ui.screens.reservas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.capachica.turismokotlin.data.model.EstadoReserva
import com.capachica.turismokotlin.data.model.ReservaPlan
import com.capachica.turismokotlin.ui.components.ReservaPlanCard
import com.capachica.turismokotlin.ui.screens.gestion.EmptyStateCard
import com.capachica.turismokotlin.ui.viewmodel.MisReservasPlanesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisReservasPlanesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPlanDetail: (Long) -> Unit,
    viewModel: MisReservasPlanesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedFilter by remember { mutableStateOf<EstadoReserva?>(null) } // null = todos
    var showCancelDialog by remember { mutableStateOf<ReservaPlan?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadMisReservasPlanes()
    }

    // Manejar mensajes de éxito
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            kotlinx.coroutines.delay(3000)
            viewModel.clearMessages()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Mis Reservas de Planes") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                IconButton(onClick = { viewModel.loadMisReservasPlanes() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                }
            }
        )

        // Mensaje de éxito
        uiState.successMessage?.let { message ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }

        // Filtros por estado
        if (uiState.reservasPlanes.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Filtro "Todos"
                item {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { selectedFilter = null },
                        label = {
                            Text("Todos (${uiState.reservasPlanes.size})")
                        },
                        leadingIcon = if (selectedFilter == null) {
                            { Icon(Icons.Default.Check, contentDescription = null) }
                        } else null
                    )
                }

                // Filtros por estado
                items(EstadoReserva.values()) { estado ->
                    val reservasCount = uiState.reservasPlanes.count { it.estado == estado }
                    if (reservasCount > 0) {
                        FilterChip(
                            selected = selectedFilter == estado,
                            onClick = { selectedFilter = estado },
                            label = {
                                Text("${getEstadoReservaText(estado)} ($reservasCount)")
                            },
                            leadingIcon = if (selectedFilter == estado) {
                                { Icon(Icons.Default.Check, contentDescription = null) }
                            } else null
                        )
                    }
                }
            }
        }

        // Contenido principal
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
                        Text("Cargando tus reservas...")
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
                    Button(onClick = { viewModel.loadMisReservasPlanes() }) {
                        Text("Reintentar")
                    }
                }
            }

            uiState.reservasPlanes.isEmpty() -> {
                EmptyStateCard(
                    title = "No tienes reservas de planes",
                    description = "Explora nuestros planes turísticos y realiza tu primera reserva",
                    icon = Icons.Default.Animation
                )
            }

            else -> {
                val reservasFiltradas = if (selectedFilter != null) {
                    uiState.reservasPlanes.filter { it.estado == selectedFilter }
                } else {
                    uiState.reservasPlanes
                }

                if (reservasFiltradas.isEmpty()) {
                    EmptyStateCard(
                        title = "No hay reservas ${selectedFilter?.let { getEstadoReservaText(it).lowercase() } ?: ""}",
                        description = getEmptyStateDescription(selectedFilter),
                        icon = getEmptyStateIcon(selectedFilter)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Resumen de reservas
                        item {
                            ReservasUserStatsCard(reservas = uiState.reservasPlanes)
                        }

                        item {
                            Text(
                                text = "${reservasFiltradas.size} reserva${if (reservasFiltradas.size != 1) "s" else ""}${selectedFilter?.let { " ${getEstadoReservaText(it).lowercase()}" } ?: ""}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        items(reservasFiltradas.sortedByDescending { it.fechaReserva }) { reserva ->
                            ReservaPlanCard(
                                reserva = reserva,
                                onConfirmarClick = {
                                    // Usuario no puede confirmar, solo ver
                                },
                                onCompletarClick = {
                                    // Usuario no puede completar, solo ver
                                },
                                onCancelarClick = {
                                    if (reserva.estado == EstadoReserva.PENDIENTE || reserva.estado == EstadoReserva.CONFIRMADA) {
                                        showCancelDialog = reserva
                                    }
                                },
                                isOperating = uiState.isOperating,
                                showActions = puedeUsuarioCancelar(reserva)
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog de cancelación
    showCancelDialog?.let { reserva ->
        CancelarReservaDialog(
            reserva = reserva,
            isLoading = uiState.isOperating,
            onDismiss = { showCancelDialog = null },
            onConfirm = { motivo ->
                viewModel.cancelarReservaPlan(reserva.id, motivo)
                showCancelDialog = null
            }
        )
    }
}

@Composable
private fun ReservasUserStatsCard(
    reservas: List<ReservaPlan>
) {
    val pendientes = reservas.count { it.estado == EstadoReserva.PENDIENTE }
    val confirmadas = reservas.count { it.estado == EstadoReserva.CONFIRMADA }
    val completadas = reservas.count { it.estado == EstadoReserva.COMPLETADA }
    val canceladas = reservas.count { it.estado == EstadoReserva.CANCELADA }
    val montoTotal = reservas.filter { it.estado != EstadoReserva.CANCELADA }.sumOf { it.montoFinal }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Resumen de tus Reservas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                UserStatItem(
                    count = pendientes,
                    label = "Pendientes",
                    color = MaterialTheme.colorScheme.secondary,
                    icon = Icons.Default.Schedule
                )
                UserStatItem(
                    count = confirmadas,
                    label = "Confirmadas",
                    color = MaterialTheme.colorScheme.primary,
                    icon = Icons.Default.CheckCircle
                )
                UserStatItem(
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
                        text = "Total invertido:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "S/ $montoTotal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun UserStatItem(
    count: Int,
    label: String,
    color: androidx.compose.ui.graphics.Color,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = "$count",
            style = MaterialTheme.typography.titleLarge,
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

@Composable
private fun CancelarReservaDialog(
    reserva: ReservaPlan,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var motivo by remember { mutableStateOf("") }
    val motivosPredefinidos = listOf(
        "Cambio de planes personales",
        "Emergencia familiar",
        "Problema de salud",
        "Condiciones climáticas adversas",
        "Dificultades económicas",
        "Otro motivo"
    )
    var selectedMotivo by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {
            if (!isLoading) onDismiss()
        },
        title = {
            Text(
                text = "Cancelar Reserva",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "¿Estás seguro de que deseas cancelar esta reserva?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Código: ${reserva.codigoReserva}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Plan: ${reserva.plan.nombre}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Fecha: ${reserva.fechaInicio}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Personas: ${reserva.numeroPersonas}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Monto: S/ ${reserva.montoFinal}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Motivo de cancelación:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(motivosPredefinidos) { motivoPredefinido ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedMotivo == motivoPredefinido,
                                onClick = {
                                    selectedMotivo = motivoPredefinido
                                    motivo = if (motivoPredefinido == "Otro motivo") "" else motivoPredefinido
                                }
                            )
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedMotivo == motivoPredefinido,
                            onClick = {
                                selectedMotivo = motivoPredefinido
                                motivo = if (motivoPredefinido == "Otro motivo") "" else motivoPredefinido
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = motivoPredefinido,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                if (selectedMotivo == "Otro motivo") {
                    item {
                        OutlinedTextField(
                            value = motivo,
                            onValueChange = { motivo = it },
                            label = { Text("Especifica el motivo") },
                            placeholder = { Text("Describe el motivo de cancelación...") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3,
                            enabled = !isLoading
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val motivoFinal = if (selectedMotivo == "Otro motivo") motivo else selectedMotivo
                    onConfirm(motivoFinal)
                },
                enabled = !isLoading &&
                        selectedMotivo.isNotEmpty() &&
                        (selectedMotivo != "Otro motivo" || motivo.isNotBlank()),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onError
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Cancelar Reserva")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cerrar")
            }
        }
    )
}

private fun getEstadoReservaText(estado: EstadoReserva): String {
    return when (estado) {
        EstadoReserva.PENDIENTE -> "Pendientes"
        EstadoReserva.CONFIRMADA -> "Confirmadas"
        EstadoReserva.COMPLETADA -> "Completadas"
        EstadoReserva.CANCELADA -> "Canceladas"
    }
}

private fun getEmptyStateDescription(estado: EstadoReserva?): String {
    return when (estado) {
        EstadoReserva.PENDIENTE -> "No tienes reservas pendientes de confirmación"
        EstadoReserva.CONFIRMADA -> "No tienes reservas confirmadas en este momento"
        EstadoReserva.COMPLETADA -> "Aún no has completado ningún plan"
        EstadoReserva.CANCELADA -> "No has cancelado ninguna reserva"
        null -> "Explora nuestros planes y realiza tu primera reserva"
    }
}

private fun getEmptyStateIcon(estado: EstadoReserva?): ImageVector {
    return when (estado) {
        EstadoReserva.PENDIENTE -> Icons.Default.Schedule
        EstadoReserva.CONFIRMADA -> Icons.Default.CheckCircle
        EstadoReserva.COMPLETADA -> Icons.Default.TaskAlt
        EstadoReserva.CANCELADA -> Icons.Default.Cancel
        null -> Icons.Default.Emergency
    }
}

private fun puedeUsuarioCancelar(reserva: ReservaPlan): Boolean {
    // El usuario solo puede cancelar si está pendiente o confirmada
    return reserva.estado == EstadoReserva.PENDIENTE || reserva.estado == EstadoReserva.CONFIRMADA
}