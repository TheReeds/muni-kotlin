package com.capachica.turismokotlin.ui.screens.gestion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.capachica.turismokotlin.data.model.EstadoServicio
import com.capachica.turismokotlin.data.model.Servicio
import com.capachica.turismokotlin.ui.components.ReservaServicioCard
import com.capachica.turismokotlin.ui.viewmodel.GestionServiciosUiState
import com.capachica.turismokotlin.ui.viewmodel.GestionServiciosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionServiciosScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCrearServicio: () -> Unit,
    onNavigateToEditarServicio: (Long) -> Unit,
    viewModel: GestionServiciosViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Gestión de Servicios") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                IconButton(onClick = onNavigateToCrearServicio) {
                    Icon(Icons.Default.Add, contentDescription = "Crear servicio")
                }
            }
        )

        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Mis Servicios") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Reservas") }
            )
        }

        when (selectedTab) {
            0 -> MisServiciosTab(
                uiState = uiState,
                onEditarServicio = onNavigateToEditarServicio,
                onRefresh = { viewModel.loadMisServicios() }
            )
            1 -> ReservasServiciosTab(
                uiState = uiState,
                onConfirmarReserva = { viewModel.confirmarReserva(it) },
                onCompletarReserva = { viewModel.completarReserva(it) },
                onRefresh = { viewModel.loadReservasEmprendedor() }
            )
        }
    }

    // Manejar mensajes
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            kotlinx.coroutines.delay(2000)
            viewModel.clearMessages()
        }
    }
}

@Composable
private fun MisServiciosTab(
    uiState: GestionServiciosUiState,
    onEditarServicio: (Long) -> Unit,
    onRefresh: () -> Unit
) {
    when {
        uiState.isLoadingServicios -> {
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
                Button(onClick = onRefresh) {
                    Text("Reintentar")
                }
            }
        }

        uiState.misServicios.isEmpty() -> {
            EmptyStateCard(
                title = "No tienes servicios",
                description = "Comienza creando tu primer servicio turístico",
                icon = Icons.Default.RoomService
            )
        }

        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.misServicios) { servicio ->
                    ServicioGestionCard(
                        servicio = servicio,
                        onEditarClick = { onEditarServicio(servicio.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ReservasServiciosTab(
    uiState: GestionServiciosUiState,
    onConfirmarReserva: (Long) -> Unit,
    onCompletarReserva: (Long) -> Unit,
    onRefresh: () -> Unit
) {
    when {
        uiState.isLoadingReservas -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.reservasEmprendedor.isEmpty() -> {
            EmptyStateCard(
                title = "No hay reservas",
                description = "Las reservas de tus servicios aparecerán aquí",
                icon = Icons.Default.BookmarkBorder
            )
        }

        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.reservasEmprendedor) { reserva ->
                    ReservaServicioCard(
                        reserva = reserva,
                        onConfirmarClick = { onConfirmarReserva(reserva.id) },
                        onCompletarClick = { onCompletarReserva(reserva.id) },
                        isOperating = uiState.isOperating
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServicioGestionCard(
    servicio: Servicio,
    onEditarClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = servicio.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "S/ ${servicio.precio}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Cap. ${servicio.capacidadMaxima} personas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    AssistChip(
                        onClick = { },
                        label = { Text(servicio.estado.name) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = when (servicio.estado) {
                                EstadoServicio.ACTIVO -> MaterialTheme.colorScheme.primaryContainer
                                EstadoServicio.INACTIVO -> MaterialTheme.colorScheme.errorContainer
                                EstadoServicio.MANTENIMIENTO -> MaterialTheme.colorScheme.secondaryContainer
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onEditarClick,
                        modifier = Modifier.size(36.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}