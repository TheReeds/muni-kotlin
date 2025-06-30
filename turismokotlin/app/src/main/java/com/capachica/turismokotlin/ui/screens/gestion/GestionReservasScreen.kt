// GestionReservasScreen.kt
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
import com.capachica.turismokotlin.ui.viewmodel.GestionServiciosViewModel
import com.capachica.turismokotlin.ui.viewmodel.GestionPlanesViewModel
import com.capachica.turismokotlin.ui.components.ReservaServicioCard
import com.capachica.turismokotlin.ui.components.ReservaPlanCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionReservasScreen(
    onNavigateBack: () -> Unit,
    onNavigateToReservaDetail: (Long) -> Unit,
    serviciosViewModel: GestionServiciosViewModel = hiltViewModel(),
    planesViewModel: GestionPlanesViewModel = hiltViewModel()
) {
    val serviciosState by serviciosViewModel.uiState.collectAsState()
    val planesState by planesViewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        serviciosViewModel.loadReservasEmprendedor()
        planesViewModel.loadReservasPlanes()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Gestión de Reservas") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                IconButton(onClick = {
                    serviciosViewModel.loadReservasEmprendedor()
                    planesViewModel.loadReservasPlanes()
                }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                }
            }
        )

        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Servicios") },
                icon = {
                    BadgedBox(
                        badge = {
                            if (serviciosState.reservasEmprendedor.count { it.estado == EstadoReserva.PENDIENTE } > 0) {
                                Badge {
                                    Text("${serviciosState.reservasEmprendedor.count { it.estado == EstadoReserva.PENDIENTE }}")
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
                onClick = { selectedTab = 1 },
                text = { Text("Planes") },
                icon = {
                    BadgedBox(
                        badge = {
                            if (planesState.reservasPlanes.count { it.estado == EstadoServicio.ACTIVO } > 0) {
                                Badge {
                                    Text("${planesState.reservasPlanes.count { it.estado == EstadoServicio.ACTIVO }}")
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.Map, contentDescription = null)
                    }
                }
            )
        }

        when (selectedTab) {
            0 -> ReservasServiciosTab(
                reservas = serviciosState.reservasEmprendedor,
                isLoading = serviciosState.isLoadingReservas,
                isOperating = serviciosState.isOperating,
                error = serviciosState.error,
                onConfirmarReserva = { serviciosViewModel.confirmarReserva(it) },
                onCompletarReserva = { serviciosViewModel.completarReserva(it) },
                onRefresh = { serviciosViewModel.loadReservasEmprendedor() }
            )
            1 -> ReservasPlanesTab(
                reservas = planesState.reservasPlanes,
                isLoading = planesState.isLoadingReservas,
                isOperating = planesState.isOperating,
                error = planesState.error,
                onConfirmarReserva = { planesViewModel.confirmarReservaPlan(it) },
                onRefresh = { planesViewModel.loadReservasPlanes() }
            )
        }
    }

    // Manejar mensajes de éxito
    LaunchedEffect(serviciosState.successMessage) {
        serviciosState.successMessage?.let {
            kotlinx.coroutines.delay(2000)
            serviciosViewModel.clearMessages()
        }
    }

    LaunchedEffect(planesState.successMessage) {
        planesState.successMessage?.let {
            kotlinx.coroutines.delay(2000)
            planesViewModel.clearMessages()
        }
    }
}

@Composable
private fun ReservasServiciosTab(
    reservas: List<ReservaCarrito>,
    isLoading: Boolean,
    isOperating: Boolean,
    error: String?,
    onConfirmarReserva: (Long) -> Unit,
    onCompletarReserva: (Long) -> Unit,
    onRefresh: () -> Unit
) {
    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        error != null -> {
            ErrorStateCard(
                error = error,
                onRetry = onRefresh
            )
        }

        reservas.isEmpty() -> {
            EmptyStateCard(
                title = "No hay reservas de servicios",
                description = "Las reservas de tus servicios aparecerán aquí",
                icon = Icons.Default.RoomService
            )
        }

        else -> {
            val reservasPendientes = reservas.filter { it.estado == EstadoReserva.PENDIENTE }
            val reservasConfirmadas = reservas.filter { it.estado == EstadoReserva.CONFIRMADA }
            val reservasCompletadas = reservas.filter { it.estado == EstadoReserva.COMPLETADA }
            val reservasCanceladas = reservas.filter { it.estado == EstadoReserva.CANCELADA }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Resumen de estadísticas
                item {
                    ReservasStatsCard(
                        pendientes = reservasPendientes.size,
                        confirmadas = reservasConfirmadas.size,
                        completadas = reservasCompletadas.size,
                        canceladas = reservasCanceladas.size
                    )
                }

                // Reservas pendientes
                if (reservasPendientes.isNotEmpty()) {
                    item {
                        Text(
                            text = "Pendientes de Confirmación (${reservasPendientes.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    items(reservasPendientes) { reserva ->
                        ReservaServicioCard(
                            reserva = reserva,
                            onConfirmarClick = { onConfirmarReserva(reserva.id) },
                            onCompletarClick = { onCompletarReserva(reserva.id) },
                            isOperating = isOperating
                        )
                    }
                }

                // Reservas confirmadas
                if (reservasConfirmadas.isNotEmpty()) {
                    item {
                        Text(
                            text = "Confirmadas (${reservasConfirmadas.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    items(reservasConfirmadas) { reserva ->
                        ReservaServicioCard(
                            reserva = reserva,
                            onConfirmarClick = { onConfirmarReserva(reserva.id) },
                            onCompletarClick = { onCompletarReserva(reserva.id) },
                            isOperating = isOperating
                        )
                    }
                }

                // Reservas completadas
                if (reservasCompletadas.isNotEmpty()) {
                    item {
                        Text(
                            text = "Completadas (${reservasCompletadas.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }

                    items(reservasCompletadas) { reserva ->
                        ReservaServicioCard(
                            reserva = reserva,
                            onConfirmarClick = { },
                            onCompletarClick = { },
                            isOperating = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReservasPlanesTab(
    reservas: List<ReservaPlan>,
    isLoading: Boolean,
    isOperating: Boolean,
    error: String?,
    onConfirmarReserva: (Long) -> Unit,
    onRefresh: () -> Unit
) {
    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        error != null -> {
            ErrorStateCard(
                error = error,
                onRetry = onRefresh
            )
        }

        reservas.isEmpty() -> {
            EmptyStateCard(
                title = "No hay reservas de planes",
                description = "Las reservas de planes aparecerán aquí",
                icon = Icons.Default.Map
            )
        }

        else -> {
            val reservasPendientes = reservas.filter { it.estado == EstadoServicio.ACTIVO }
            val reservasConfirmadas = reservas.filter { it.estado == EstadoServicio.ACTIVO }
            val reservasCompletadas = reservas.filter { it.estado == EstadoServicio.ACTIVO }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Resumen
                item {
                    ReservasStatsCard(
                        pendientes = reservasPendientes.size,
                        confirmadas = reservasConfirmadas.size,
                        completadas = reservasCompletadas.size,
                        canceladas = 0 // Los planes no manejan canceladas igual
                    )
                }

                // Todas las reservas agrupadas por estado
                if (reservasPendientes.isNotEmpty()) {
                    item {
                        Text(
                            text = "Pendientes (${reservasPendientes.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    items(reservasPendientes) { reserva ->
                        ReservaPlanCard(
                            reserva = reserva,
                            onConfirmarClick = { onConfirmarReserva(reserva.id) },
                            onCompletarClick = { /* TODO */ },
                            isOperating = isOperating
                        )
                    }
                }

                if (reservasConfirmadas.isNotEmpty()) {
                    item {
                        Text(
                            text = "Confirmadas (${reservasConfirmadas.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    items(reservasConfirmadas) { reserva ->
                        ReservaPlanCard(
                            reserva = reserva,
                            onConfirmarClick = { },
                            onCompletarClick = { /* TODO */ },
                            isOperating = false
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReservasStatsCard(
    pendientes: Int,
    confirmadas: Int,
    completadas: Int,
    canceladas: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Resumen de Reservas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    count = pendientes,
                    label = "Pendientes",
                    color = MaterialTheme.colorScheme.error
                )
                StatItem(
                    count = confirmadas,
                    label = "Confirmadas",
                    color = MaterialTheme.colorScheme.primary
                )
                StatItem(
                    count = completadas,
                    label = "Completadas",
                    color = MaterialTheme.colorScheme.tertiary
                )
                if (canceladas > 0) {
                    StatItem(
                        count = canceladas,
                        label = "Canceladas",
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    count: Int,
    label: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$count",
            style = MaterialTheme.typography.headlineMedium,
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
private fun ErrorStateCard(
    error: String,
    onRetry: () -> Unit
) {
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
            text = "Error: $error",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}