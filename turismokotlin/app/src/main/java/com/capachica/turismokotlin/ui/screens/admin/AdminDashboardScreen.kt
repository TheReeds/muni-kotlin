package com.capachica.turismokotlin.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capachica.turismokotlin.data.model.*
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.components.EstadoReservaChip
import com.capachica.turismokotlin.ui.components.LoadingScreen
import com.capachica.turismokotlin.ui.components.TurismoAppBar
import com.capachica.turismokotlin.ui.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateToReservas: () -> Unit,
    onNavigateToPlanes: () -> Unit,
    onNavigateToServicios: () -> Unit,
    onNavigateToPagos: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val reservaViewModel: ReservaViewModel = viewModel(factory = factory)
    val planViewModel: PlanTuristicoViewModel = viewModel(factory = factory)
    val servicioViewModel: ServicioTuristicoViewModel = viewModel(factory = factory)
    
    val reservasState by reservaViewModel.reservasState.collectAsState()
    val planesState by planViewModel.planesState.collectAsState()
    val serviciosState by servicioViewModel.serviciosState.collectAsState()
    
    // Cargar datos al inicio
    LaunchedEffect(Unit) {
        reservaViewModel.getAllReservas()
        planViewModel.getAllPlanes()
        servicioViewModel.getAllServicios()
    }
    
    Scaffold(
        topBar = {
            TurismoAppBar(
                title = "Panel de Administración",
                onBackClick = onBack
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Estadísticas generales
            item {
                StatsSection(
                    reservasState = reservasState,
                    planesState = planesState,
                    serviciosState = serviciosState
                )
            }
            
            // Accesos rápidos
            item {
                QuickAccessSection(
                    onNavigateToReservas = onNavigateToReservas,
                    onNavigateToPlanes = onNavigateToPlanes,
                    onNavigateToServicios = onNavigateToServicios,
                    onNavigateToPagos = onNavigateToPagos,
                    onNavigateToUsers = onNavigateToUsers
                )
            }
            
            // Reservas recientes
            item {
                RecentReservasSection(
                    reservasState = reservasState,
                    onSeeAll = onNavigateToReservas
                )
            }
            
            // Planes populares
            item {
                PopularPlanesSection(
                    planesState = planesState,
                    onSeeAll = onNavigateToPlanes
                )
            }
        }
    }
}

@Composable
fun StatsSection(
    reservasState: Result<List<Reserva>>,
    planesState: Result<List<PlanTuristico>>,
    serviciosState: Result<List<ServicioTuristico>>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Estadísticas Generales",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(
                    icon = Icons.Default.BookOnline,
                    title = "Reservas",
                    value = when (reservasState) {
                        is Result.Success -> reservasState.data.size.toString()
                        else -> "..."
                    },
                    subtitle = "Total"
                )
                
                StatCard(
                    icon = Icons.Default.Map,
                    title = "Planes",
                    value = when (planesState) {
                        is Result.Success -> planesState.data.size.toString()
                        else -> "..."
                    },
                    subtitle = "Activos"
                )
                
                StatCard(
                    icon = Icons.Default.RoomService,
                    title = "Servicios",
                    value = when (serviciosState) {
                        is Result.Success -> serviciosState.data.size.toString()
                        else -> "..."
                    },
                    subtitle = "Disponibles"
                )
            }
        }
    }
}

@Composable
fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    subtitle: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun QuickAccessSection(
    onNavigateToReservas: () -> Unit,
    onNavigateToPlanes: () -> Unit,
    onNavigateToServicios: () -> Unit,
    onNavigateToPagos: () -> Unit,
    onNavigateToUsers: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Accesos Rápidos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickAccessButton(
                    icon = Icons.Default.BookOnline,
                    text = "Reservas",
                    onClick = onNavigateToReservas,
                    modifier = Modifier.weight(1f)
                )
                
                QuickAccessButton(
                    icon = Icons.Default.Map,
                    text = "Planes",
                    onClick = onNavigateToPlanes,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickAccessButton(
                    icon = Icons.Default.RoomService,
                    text = "Servicios",
                    onClick = onNavigateToServicios,
                    modifier = Modifier.weight(1f)
                )
                
                QuickAccessButton(
                    icon = Icons.Default.Payment,
                    text = "Pagos",
                    onClick = onNavigateToPagos,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickAccessButton(
                    icon = Icons.Default.People,
                    text = "Usuarios",
                    onClick = onNavigateToUsers,
                    modifier = Modifier.weight(1f)
                )
                
                // Placeholder para mantener la alineación
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun QuickAccessButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        contentPadding = PaddingValues(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
fun RecentReservasSection(
    reservasState: Result<List<Reserva>>,
    onSeeAll: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reservas Recientes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(onClick = onSeeAll) {
                    Text("Ver todas")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            when (reservasState) {
                is Result.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    }
                }
                is Result.Error -> {
                    Text(
                        text = "Error al cargar reservas",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is Result.Success -> {
                    val recentReservas = reservasState.data.take(3)
                    if (recentReservas.isEmpty()) {
                        Text(
                            text = "No hay reservas recientes",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        recentReservas.forEach { reserva ->
                            RecentReservaItem(reserva = reserva)
                            if (reserva != recentReservas.last()) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecentReservaItem(reserva: Reserva) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.BookOnline,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = reserva.codigoReserva,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = reserva.plan.nombre,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Column(horizontalAlignment = Alignment.End) {
            EstadoReservaChip(estado = reserva.estado)
            Text(
                text = "$${reserva.montoFinal}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun PopularPlanesSection(
    planesState: Result<List<PlanTuristico>>,
    onSeeAll: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Planes Populares",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(onClick = onSeeAll) {
                    Text("Ver todos")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            when (planesState) {
                is Result.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    }
                }
                is Result.Error -> {
                    Text(
                        text = "Error al cargar planes",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is Result.Success -> {
                    val popularPlanes = planesState.data
                        .sortedByDescending { it.totalReservas }
                        .take(3)
                    
                    if (popularPlanes.isEmpty()) {
                        Text(
                            text = "No hay planes disponibles",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(popularPlanes) { plan ->
                                PopularPlanCard(plan = plan)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PopularPlanCard(plan: PlanTuristico) {
    Card(
        modifier = Modifier.width(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = plan.nombre,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = plan.municipalidad.nombre,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$${plan.precioTotal}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "${plan.totalReservas} reservas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}