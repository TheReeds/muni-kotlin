package com.capachica.turismokotlin.ui.screens.pagos

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capachica.turismokotlin.data.model.*
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.components.*
import com.capachica.turismokotlin.ui.components.InfoRowSmall
import com.capachica.turismokotlin.ui.viewmodel.PagoViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagosScreen(
    onNavigateToPagoDetail: (Long) -> Unit,
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val viewModel: PagoViewModel = viewModel(factory = factory)
    val pagosState by viewModel.pagosState.collectAsState()
    
    // Filtros
    var selectedMetodo by remember { mutableStateOf<MetodoPago?>(null) }
    var selectedEstado by remember { mutableStateOf<EstadoPago?>(null) }
    var showFilters by remember { mutableStateOf(false) }
    
    // Cargar datos al inicio
    LaunchedEffect(Unit) {
        viewModel.getAllPagos()
    }
    
    Scaffold(
        topBar = {
            TurismoAppBar(
                title = "Gestión de Pagos",
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filtros"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filtros
            if (showFilters) {
                PagosFiltersSection(
                    selectedMetodo = selectedMetodo,
                    onMetodoChange = { selectedMetodo = it },
                    selectedEstado = selectedEstado,
                    onEstadoChange = { selectedEstado = it }
                )
            }
            
            // Lista de pagos
            when (val state = pagosState) {
                is Result.Loading -> LoadingScreen()
                is Result.Error -> ErrorScreen(
                    message = state.message,
                    onRetry = { viewModel.getAllPagos() }
                )
                is Result.Success -> {
                    val filteredPagos = state.data.filter { pago ->
                        (selectedMetodo == null || pago.metodoPago == selectedMetodo) &&
                        (selectedEstado == null || pago.estado == selectedEstado)
                    }
                    
                    if (filteredPagos.isEmpty()) {
                        EmptyListPlaceholder(
                            message = "No hay pagos disponibles",
                            buttonText = "Recargar",
                            onButtonClick = { viewModel.getAllPagos() }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Resumen de pagos
                            item {
                                PagosStatsCard(pagos = filteredPagos)
                            }
                            
                            // Lista de pagos
                            items(filteredPagos) { pago ->
                                PagoCard(
                                    pago = pago,
                                    onClick = { onNavigateToPagoDetail(pago.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PagosFiltersSection(
    selectedMetodo: MetodoPago?,
    onMetodoChange: (MetodoPago?) -> Unit,
    selectedEstado: EstadoPago?,
    onEstadoChange: (EstadoPago?) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Filtros de Pagos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Filtro por método de pago
            Text(
                text = "Método de Pago",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    onClick = { onMetodoChange(null) },
                    label = { Text("Todos") },
                    selected = selectedMetodo == null
                )
                
                MetodoPago.values().forEach { metodo ->
                    FilterChip(
                        onClick = { 
                            onMetodoChange(if (selectedMetodo == metodo) null else metodo) 
                        },
                        label = { Text(metodo.name) },
                        selected = selectedMetodo == metodo
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Filtro por estado
            Text(
                text = "Estado del Pago",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    onClick = { onEstadoChange(null) },
                    label = { Text("Todos") },
                    selected = selectedEstado == null
                )
                
                EstadoPago.values().forEach { estado ->
                    FilterChip(
                        onClick = { 
                            onEstadoChange(if (selectedEstado == estado) null else estado) 
                        },
                        label = { Text(estado.name) },
                        selected = selectedEstado == estado
                    )
                }
            }
        }
    }
}

@Composable
fun PagosStatsCard(pagos: List<Pago>) {
    val totalPagos = pagos.size
    val totalMonto = pagos.sumOf { it.monto }
    val pagosCompletados = pagos.count { it.estado == EstadoPago.CONFIRMADO }
    val pagosPendientes = pagos.count { it.estado == EstadoPago.PENDIENTE }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Resumen de Pagos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatColumn(
                    icon = Icons.Default.Payment,
                    title = "Total Pagos",
                    value = totalPagos.toString()
                )
                
                StatColumn(
                    icon = Icons.Default.AttachMoney,
                    title = "Monto Total",
                    value = "$${totalMonto}"
                )
                
                StatColumn(
                    icon = Icons.Default.CheckCircle,
                    title = "Completados",
                    value = pagosCompletados.toString()
                )
                
                StatColumn(
                    icon = Icons.Default.HourglassEmpty,
                    title = "Pendientes",
                    value = pagosPendientes.toString()
                )
            }
        }
    }
}

@Composable
fun StatColumn(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun PagoCard(
    pago: Pago,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Pago #${pago.id}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Reserva: ${pago.reserva.codigoReserva}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                EstadoPagoChip(estado = pago.estado)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Información del pago
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    InfoRowSmall(
                        icon = Icons.Default.Payment,
                        text = "Método: ${pago.metodoPago.name}"
                    )
                    InfoRowSmall(
                        icon = Icons.Default.DateRange,
                        text = "Fecha: ${pago.fechaPago}"
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${pago.monto}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Plan turístico
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = pago.reserva.plan.nombre,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = pago.reserva.plan.municipalidad.nombre,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EstadoPagoChip(estado: EstadoPago) {
    val (color, icon) = when (estado) {
        EstadoPago.PENDIENTE -> MaterialTheme.colorScheme.tertiary to Icons.Default.HourglassEmpty
        EstadoPago.PROCESANDO -> MaterialTheme.colorScheme.primary to Icons.Default.Sync
        EstadoPago.CONFIRMADO -> MaterialTheme.colorScheme.secondary to Icons.Default.CheckCircle
        EstadoPago.FALLIDO -> MaterialTheme.colorScheme.error to Icons.Default.Error
        EstadoPago.REEMBOLSADO -> MaterialTheme.colorScheme.tertiary to Icons.Default.Undo
        EstadoPago.CANCELADO -> MaterialTheme.colorScheme.error to Icons.Default.Cancel
    }
    
    AssistChip(
        onClick = { },
        label = {
            Text(
                text = estado.name,
                style = MaterialTheme.typography.labelSmall
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.1f),
            labelColor = color,
            leadingIconContentColor = color
        )
    )
}

