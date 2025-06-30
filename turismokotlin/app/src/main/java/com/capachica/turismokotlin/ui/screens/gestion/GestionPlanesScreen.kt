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
import com.capachica.turismokotlin.data.model.*
import com.capachica.turismokotlin.ui.viewmodel.GestionPlanesViewModel
import com.capachica.turismokotlin.ui.components.ReservaPlanCard
import com.capachica.turismokotlin.ui.viewmodel.GestionPlanesUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionPlanesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCrearPlan: () -> Unit,
    onNavigateToEditarPlan: (Long) -> Unit,
    viewModel: GestionPlanesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Gestión de Planes") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                IconButton(onClick = onNavigateToCrearPlan) {
                    Icon(Icons.Default.Add, contentDescription = "Crear plan")
                }
            }
        )

        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Mis Planes") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Reservas de Planes") }
            )
        }

        when (selectedTab) {
            0 -> MisPlanesTab(
                uiState = uiState,
                onEditarPlan = onNavigateToEditarPlan,
                onCambiarEstado = { planId, estado ->
                    viewModel.cambiarEstadoPlan(planId, estado)
                },
                onRefresh = { viewModel.loadMisPlanes() }
            )
            1 -> ReservasPlanesTab(
                uiState = uiState,
                onConfirmarReserva = { viewModel.confirmarReservaPlan(it) },
                onRefresh = { viewModel.loadReservasPlanes() }
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
private fun MisPlanesTab(
    uiState: GestionPlanesUiState,
    onEditarPlan: (Long) -> Unit,
    onCambiarEstado: (Long, EstadoPlan) -> Unit,
    onRefresh: () -> Unit
) {
    when {
        uiState.isLoadingPlanes -> {
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

        uiState.misPlanes.isEmpty() -> {
            EmptyStateCard(
                title = "No tienes planes",
                description = "Comienza creando tu primer plan turístico",
                icon = Icons.Default.Map
            )
        }

        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.misPlanes) { plan ->
                    PlanGestionCard(
                        plan = plan,
                        onEditarClick = { onEditarPlan(plan.id) },
                        onCambiarEstado = { estado -> onCambiarEstado(plan.id, estado) },
                        isOperating = uiState.isOperating
                    )
                }
            }
        }
    }
}

@Composable
private fun ReservasPlanesTab(
    uiState: GestionPlanesUiState,
    onConfirmarReserva: (Long) -> Unit,
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

        uiState.reservasPlanes.isEmpty() -> {
            EmptyStateCard(
                title = "No hay reservas de planes",
                description = "Las reservas de tus planes aparecerán aquí",
                icon = Icons.Default.BookmarkBorder
            )
        }

        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.reservasPlanes) { reserva ->
                    ReservaPlanCard(
                        reserva = reserva,
                        onConfirmarClick = { onConfirmarReserva(reserva.id) },
                        onCompletarClick = { /* TODO: Implementar completar reserva plan */ },
                        isOperating = uiState.isOperating
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanGestionCard(
    plan: Plan,
    onEditarClick: () -> Unit,
    onCambiarEstado: (EstadoPlan) -> Unit,
    isOperating: Boolean
) {
    var showEstadoDialog by remember { mutableStateOf(false) }

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
                        text = plan.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "S/ ${plan.precioTotal}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${plan.duracionDias} días • Cap. ${plan.capacidadMaxima}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Dificultad: ${plan.nivelDificultad.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (plan.totalReservas > 0) {
                        Text(
                            text = "${plan.totalReservas} reservas",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    AssistChip(
                        onClick = { showEstadoDialog = true },
                        label = { Text(getEstadoPlanText(plan.estado)) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = getEstadoPlanColor(plan.estado)
                        ),
                        enabled = !isOperating
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
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

            // Servicios incluidos
            if (plan.servicios.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Servicios incluidos: ${plan.servicios.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // Dialog para cambiar estado
    if (showEstadoDialog) {
        EstadoPlanDialog(
            estadoActual = plan.estado,
            onDismiss = { showEstadoDialog = false },
            onConfirm = { nuevoEstado ->
                onCambiarEstado(nuevoEstado)
                showEstadoDialog = false
            }
        )
    }
}

@Composable
private fun EstadoPlanDialog(
    estadoActual: EstadoPlan,
    onDismiss: () -> Unit,
    onConfirm: (EstadoPlan) -> Unit
) {
    var selectedEstado by remember { mutableStateOf(estadoActual) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cambiar Estado del Plan") },
        text = {
            Column {
                EstadoPlan.values().forEach { estado ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = selectedEstado == estado,
                            onClick = { selectedEstado = estado }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = getEstadoPlanText(estado),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedEstado) },
                enabled = selectedEstado != estadoActual
            ) {
                Text("Cambiar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private fun getEstadoPlanText(estado: EstadoPlan): String {
    return when (estado) {
        EstadoPlan.BORRADOR -> "Borrador"
        EstadoPlan.ACTIVO -> "Activo"
        EstadoPlan.INACTIVO -> "Inactivo"
        EstadoPlan.AGOTADO -> "Agotado"
        EstadoPlan.SUSPENDIDO -> "Suspendido"
    }
}

@Composable
private fun getEstadoPlanColor(estado: EstadoPlan): androidx.compose.ui.graphics.Color {
    return when (estado) {
        EstadoPlan.BORRADOR -> MaterialTheme.colorScheme.secondaryContainer
        EstadoPlan.ACTIVO -> MaterialTheme.colorScheme.primaryContainer
        EstadoPlan.INACTIVO -> MaterialTheme.colorScheme.surfaceVariant
        EstadoPlan.AGOTADO -> MaterialTheme.colorScheme.errorContainer
        EstadoPlan.SUSPENDIDO -> MaterialTheme.colorScheme.tertiaryContainer
    }
}
