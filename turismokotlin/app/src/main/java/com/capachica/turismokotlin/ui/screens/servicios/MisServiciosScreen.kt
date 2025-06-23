package com.capachica.turismokotlin.ui.screens.servicios

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capachica.turismokotlin.data.model.*
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.components.*
import com.capachica.turismokotlin.ui.viewmodel.ServicioTuristicoViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisServiciosScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToCreate: () -> Unit,
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val viewModel: ServicioTuristicoViewModel = viewModel(factory = factory)
    val serviciosState by viewModel.serviciosState.collectAsState()
    
    // Estados de filtro para mis servicios
    var selectedEstado by remember { mutableStateOf<EstadoServicio?>(null) }
    var showFilters by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        viewModel.getMisServicios()
    }
    
    LaunchedEffect(selectedEstado) {
        if (selectedEstado != null) {
            // Para filtrar los servicios del emprendedor por estado
            // Necesitaríamos una función específica o filtrar en el cliente
            viewModel.getMisServicios()
        } else {
            viewModel.getMisServicios()
        }
    }
    
    Scaffold(
        topBar = {
            TurismoAppBar(
                title = "Mis Servicios",
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filtros"
                        )
                    }
                    IconButton(onClick = onNavigateToCreate) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Crear servicio"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear servicio"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Estadísticas rápidas
            MisServiciosStats(serviciosState = serviciosState)
            
            // Filtros
            if (showFilters) {
                MisServiciosFilterSection(
                    selectedEstado = selectedEstado,
                    onEstadoChange = { selectedEstado = it },
                    onClearFilters = { selectedEstado = null }
                )
            }
            
            // Lista de servicios
            when (val state = serviciosState) {
                is Result.Loading -> LoadingScreen()
                is Result.Error -> ErrorScreen(
                    message = state.message,
                    onRetry = { viewModel.getMisServicios() }
                )
                is Result.Success -> {
                    val filteredServicios = if (selectedEstado != null) {
                        state.data.filter { it.estado == selectedEstado }
                    } else {
                        state.data
                    }
                    
                    if (filteredServicios.isEmpty()) {
                        EmptyListPlaceholder(
                            message = if (state.data.isEmpty()) "No tienes servicios registrados" else "No hay servicios con este filtro",
                            buttonText = "Crear Servicio",
                            onButtonClick = onNavigateToCreate
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredServicios) { servicio ->
                                MiServicioCard(
                                    servicio = servicio,
                                    onCardClick = { onNavigateToDetail(servicio.id) }
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
fun MisServiciosStats(serviciosState: Result<List<ServicioTuristico>>) {
    if (serviciosState is Result.Success) {
        val servicios = serviciosState.data
        val activos = servicios.count { it.estado == EstadoServicio.ACTIVO }
        val inactivos = servicios.count { it.estado == EstadoServicio.INACTIVO }
        val agotados = servicios.count { it.estado == EstadoServicio.AGOTADO }
        val promedioPrecios = servicios.map { it.precio }.average()
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Resumen de mis servicios",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatsItem(
                        label = "Total",
                        value = servicios.size.toString(),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    StatsItem(
                        label = "Activos",
                        value = activos.toString(),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    StatsItem(
                        label = "Inactivos",
                        value = inactivos.toString(),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    if (servicios.isNotEmpty()) {
                        StatsItem(
                            label = "Precio Prom.",
                            value = "$${String.format("%.0f", promedioPrecios)}",
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatsItem(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
    }
}

@Composable
fun MisServiciosFilterSection(
    selectedEstado: EstadoServicio?,
    onEstadoChange: (EstadoServicio?) -> Unit,
    onClearFilters: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Filtrar por estado",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    onClick = { onEstadoChange(null) },
                    label = { Text("Todos") },
                    selected = selectedEstado == null
                )
                
                EstadoServicio.values().forEach { estado ->
                    FilterChip(
                        onClick = {
                            onEstadoChange(if (selectedEstado == estado) null else estado)
                        },
                        label = { Text(estado.name) },
                        selected = selectedEstado == estado
                    )
                }
            }
            
            if (selectedEstado != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onClearFilters) {
                        Text("Limpiar Filtros")
                    }
                }
            }
        }
    }
}

@Composable
fun MiServicioCard(
    servicio: ServicioTuristico,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onCardClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Icono del estado
                Card(
                    modifier = Modifier.size(50.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when (servicio.estado) {
                            EstadoServicio.ACTIVO -> MaterialTheme.colorScheme.primaryContainer
                            EstadoServicio.INACTIVO -> MaterialTheme.colorScheme.surfaceVariant
                            EstadoServicio.AGOTADO -> MaterialTheme.colorScheme.errorContainer
                            EstadoServicio.MANTENIMIENTO -> MaterialTheme.colorScheme.tertiaryContainer
                        }
                    )
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = when (servicio.estado) {
                                EstadoServicio.ACTIVO -> Icons.Default.CheckCircle
                                EstadoServicio.INACTIVO -> Icons.Default.Pause
                                EstadoServicio.AGOTADO -> Icons.Default.Block
                                EstadoServicio.MANTENIMIENTO -> Icons.Default.Build
                            },
                            contentDescription = null,
                            tint = when (servicio.estado) {
                                EstadoServicio.ACTIVO -> MaterialTheme.colorScheme.onPrimaryContainer
                                EstadoServicio.INACTIVO -> MaterialTheme.colorScheme.onSurfaceVariant
                                EstadoServicio.AGOTADO -> MaterialTheme.colorScheme.onErrorContainer
                                EstadoServicio.MANTENIMIENTO -> MaterialTheme.colorScheme.onTertiaryContainer
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = servicio.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = servicio.tipo.name.replace("_", " "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = servicio.estado.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = when (servicio.estado) {
                                EstadoServicio.ACTIVO -> MaterialTheme.colorScheme.primary
                                EstadoServicio.INACTIVO -> MaterialTheme.colorScheme.onSurfaceVariant
                                EstadoServicio.AGOTADO -> MaterialTheme.colorScheme.error
                                EstadoServicio.MANTENIMIENTO -> MaterialTheme.colorScheme.tertiary
                            }
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "• ${servicio.duracionHoras}h • Max ${servicio.capacidadMaxima}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${servicio.precio}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "por persona",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (!servicio.descripcion.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = servicio.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ver detalles",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}