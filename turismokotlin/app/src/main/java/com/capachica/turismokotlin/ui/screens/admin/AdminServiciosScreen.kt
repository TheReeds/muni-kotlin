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
import com.capachica.turismokotlin.ui.components.*
import com.capachica.turismokotlin.ui.viewmodel.ServicioTuristicoViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminServiciosScreen(
    onNavigateToDetail: (Long) -> Unit = {},
    onNavigateToCreate: () -> Unit = {},
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val viewModel: ServicioTuristicoViewModel = viewModel(factory = factory)
    val serviciosState by viewModel.serviciosState.collectAsState()
    
    var selectedTipo by remember { mutableStateOf<TipoServicio?>(null) }
    var selectedEstado by remember { mutableStateOf<EstadoServicio?>(null) }
    var showFilters by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        viewModel.getAllServicios()
    }
    
    LaunchedEffect(selectedTipo, selectedEstado) {
        when {
            selectedTipo != null -> viewModel.getServiciosByTipo(selectedTipo!!)
            selectedEstado != null -> viewModel.getServiciosByEstado(selectedEstado!!)
            else -> viewModel.getAllServicios()
        }
    }
    
    Scaffold(
        topBar = {
            TurismoAppBar(
                title = "Administrar Servicios",
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
            if (showFilters) {
                AdminServiciosFilterSection(
                    selectedTipo = selectedTipo,
                    selectedEstado = selectedEstado,
                    onTipoChange = { selectedTipo = it },
                    onEstadoChange = { selectedEstado = it },
                    onClearFilters = { 
                        selectedTipo = null
                        selectedEstado = null
                    }
                )
            }
            
            when (val state = serviciosState) {
                is Result.Loading -> LoadingScreen()
                is Result.Error -> ErrorScreen(
                    message = state.message,
                    onRetry = { viewModel.getAllServicios() }
                )
                is Result.Success -> {
                    if (state.data.isEmpty()) {
                        EmptyListPlaceholder(
                            message = "No hay servicios registrados",
                            buttonText = "Crear Servicio",
                            onButtonClick = onNavigateToCreate
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.data) { servicio ->
                                AdminServicioCard(
                                    servicio = servicio,
                                    onCardClick = { onNavigateToDetail(servicio.id) },
                                    onEditClick = { onNavigateToDetail(servicio.id) }
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
fun AdminServiciosFilterSection(
    selectedTipo: TipoServicio?,
    selectedEstado: EstadoServicio?,
    onTipoChange: (TipoServicio?) -> Unit,
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
                text = "Filtros",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Filtro por tipo
            Text("Tipo de Servicio:", style = MaterialTheme.typography.bodyMedium)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items(TipoServicio.values()) { tipo ->
                    FilterChip(
                        onClick = {
                            onTipoChange(if (selectedTipo == tipo) null else tipo)
                        },
                        label = { Text(tipo.name.replace("_", " ")) },
                        selected = selectedTipo == tipo
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Filtro por estado
            Text("Estado:", style = MaterialTheme.typography.bodyMedium)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items(EstadoServicio.values()) { estado ->
                    FilterChip(
                        onClick = {
                            onEstadoChange(if (selectedEstado == estado) null else estado)
                        },
                        label = { Text(estado.name) },
                        selected = selectedEstado == estado
                    )
                }
            }
            
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

@Composable
fun AdminServicioCard(
    servicio: ServicioTuristico,
    onCardClick: () -> Unit,
    onEditClick: () -> Unit
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
                Card(
                    modifier = Modifier.size(60.dp),
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
                            imageVector = when (servicio.tipo) {
                                TipoServicio.ALOJAMIENTO -> Icons.Default.Hotel
                                TipoServicio.TRANSPORTE -> Icons.Default.DirectionsBus
                                TipoServicio.ALIMENTACION -> Icons.Default.Restaurant
                                TipoServicio.GUIA_TURISTICO -> Icons.Default.Person
                                TipoServicio.TOUR -> Icons.Default.Tour
                                TipoServicio.AVENTURA -> Icons.Default.Hiking
                                TipoServicio.CULTURAL -> Icons.Default.Museum
                                TipoServicio.GASTRONOMICO -> Icons.Default.Restaurant
                                TipoServicio.WELLNESS -> Icons.Default.Spa
                                else -> Icons.Default.Star
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
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = servicio.emprendedor.nombreEmpresa,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = servicio.tipo.name.replace("_", " "),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "• ${servicio.estado.name}",
                            style = MaterialTheme.typography.bodySmall,
                            color = when (servicio.estado) {
                                EstadoServicio.ACTIVO -> MaterialTheme.colorScheme.primary
                                EstadoServicio.INACTIVO -> MaterialTheme.colorScheme.onSurfaceVariant
                                EstadoServicio.AGOTADO -> MaterialTheme.colorScheme.error
                                EstadoServicio.MANTENIMIENTO -> MaterialTheme.colorScheme.tertiary
                            }
                        )
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${servicio.precio}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${servicio.duracionHoras}h",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Max ${servicio.capacidadMaxima}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Editar")
                }
            }
        }
    }
}