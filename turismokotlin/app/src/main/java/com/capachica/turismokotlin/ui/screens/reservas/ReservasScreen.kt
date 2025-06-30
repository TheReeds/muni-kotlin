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
    viewModel: ReservasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
            }
        )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
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
                EmptyReservasContent(onNavigateBack = onNavigateBack)
            }

            else -> {
                ReservasContent(
                    reservas = uiState.reservas,
                    onNavigateToReservaDetail = onNavigateToReservaDetail,
                    onCancelarReserva = { reservaId, motivo ->
                        viewModel.cancelarReserva(reservaId, motivo)
                    }
                )
            }
        }
    }
}

@Composable
private fun EmptyReservasContent(
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
            Icons.Default.BookmarkBorder,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "No tienes reservas",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = "Explora nuestros planes y servicios para hacer tu primera reserva",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )

        Button(
            onClick = onNavigateBack,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Explorar Planes")
        }
    }
}

@Composable
private fun ReservasContent(
    reservas: List<ReservaCarrito>,
    onNavigateToReservaDetail: (Long) -> Unit,
    onCancelarReserva: (Long, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(reservas) { reserva ->
            ReservaCard(
                reserva = reserva,
                onClick = { onNavigateToReservaDetail(reserva.id) },
                onCancelar = { motivo -> onCancelarReserva(reserva.id, motivo) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReservaCard(
    reserva: ReservaCarrito,
    onClick: () -> Unit,
    onCancelar: (String) -> Unit,
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

            // Acciones
            if (reserva.estado == EstadoReserva.PENDIENTE) {
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