package com.capachica.turismokotlin.ui.screens.mapa

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
import com.capachica.turismokotlin.ui.components.LoadingScreen
import com.capachica.turismokotlin.ui.viewmodel.UbicacionViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaEmprendedoresScreen(
    onNavigateToEmprendedor: (Long) -> Unit,
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val ubicacionViewModel: UbicacionViewModel = viewModel(factory = factory)
    
    val emprendedoresUbicacionState by ubicacionViewModel.emprendedoresUbicacionState.collectAsState()
    val serviciosUbicacionState by ubicacionViewModel.serviciosUbicacionState.collectAsState()
    
    var showMapView by remember { mutableStateOf(true) }
    var selectedFilter by remember { mutableStateOf("todos") } // todos, emprendedores, servicios
    
    LaunchedEffect(Unit) {
        ubicacionViewModel.cargarEmprendedoresUbicacion()
        ubicacionViewModel.cargarServiciosUbicacion()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa de Ubicaciones") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showMapView = !showMapView }) {
                        Icon(
                            imageVector = if (showMapView) Icons.Default.List else Icons.Default.Map,
                            contentDescription = if (showMapView) "Ver lista" else "Ver mapa"
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    onClick = { selectedFilter = "todos" },
                    label = { Text("Todos") },
                    selected = selectedFilter == "todos"
                )
                FilterChip(
                    onClick = { selectedFilter = "emprendedores" },
                    label = { Text("Emprendedores") },
                    selected = selectedFilter == "emprendedores"
                )
                FilterChip(
                    onClick = { selectedFilter = "servicios" },
                    label = { Text("Servicios") },
                    selected = selectedFilter == "servicios"
                )
            }
            
            if (showMapView) {
                // Vista de mapa
                MapView(
                    emprendedoresState = emprendedoresUbicacionState,
                    serviciosState = serviciosUbicacionState,
                    selectedFilter = selectedFilter,
                    onEmprendedorClick = onNavigateToEmprendedor,
                    modifier = Modifier.weight(1f)
                )
            } else {
                // Vista de lista
                ListView(
                    emprendedoresState = emprendedoresUbicacionState,
                    serviciosState = serviciosUbicacionState,
                    selectedFilter = selectedFilter,
                    onEmprendedorClick = onNavigateToEmprendedor,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun MapView(
    emprendedoresState: Result<List<EmprendedorUbicacion>>,
    serviciosState: Result<List<ServicioUbicacion>>,
    selectedFilter: String,
    onEmprendedorClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        emprendedoresState is Result.Success && serviciosState is Result.Success -> {
            val emprendedores = emprendedoresState.data
            val servicios = serviciosState.data
            
            // Filtrar datos según el filtro seleccionado
            val filteredEmprendedores = when (selectedFilter) {
                "emprendedores", "todos" -> emprendedores
                else -> emptyList()
            }
            val filteredServicios = when (selectedFilter) {
                "servicios", "todos" -> servicios
                else -> emptyList()
            }
            
            InteractiveMapPlaceholder(
                emprendedores = filteredEmprendedores,
                servicios = filteredServicios,
                onEmprendedorClick = onEmprendedorClick,
                modifier = modifier
            )
        }
        emprendedoresState is Result.Loading || serviciosState is Result.Loading -> {
            LoadingMapView(modifier = modifier)
        }
        emprendedoresState is Result.Error -> {
            ErrorMapView(
                message = emprendedoresState.message,
                modifier = modifier
            )
        }
        serviciosState is Result.Error -> {
            ErrorMapView(
                message = serviciosState.message,
                modifier = modifier
            )
        }
        else -> {
            ErrorMapView(
                message = "Error desconocido al cargar ubicaciones",
                modifier = modifier
            )
        }
    }
}

@Composable
fun InteractiveMapPlaceholder(
    emprendedores: List<EmprendedorUbicacion>,
    servicios: List<ServicioUbicacion>,
    onEmprendedorClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header con información del mapa
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Mapa Interactivo",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Para implementar: Google Maps SDK",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // Estadísticas
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        StatisticItem(
                            count = emprendedores.size,
                            label = "Emprendedores",
                            color = MaterialTheme.colorScheme.primary
                        )
                        StatisticItem(
                            count = servicios.size,
                            label = "Servicios",
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
            
            // Área principal del mapa simulado
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Simulated map background
                Card(
                    modifier = Modifier.fillMaxSize(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Terrain,
                                contentDescription = null,
                                modifier = Modifier.size(72.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "Vista de Mapa",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Aquí se mostrarán las ubicaciones de emprendedores y servicios",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
                
                // Overlay con marcadores simulados
                if (emprendedores.isNotEmpty() || servicios.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Text(
                                text = "Ubicaciones disponibles:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        
                        items(emprendedores.take(3)) { emprendedor ->
                            MapMarkerItem(
                                title = emprendedor.nombreEmpresa,
                                subtitle = emprendedor.rubro,
                                icon = Icons.Default.Business,
                                color = MaterialTheme.colorScheme.primary,
                                coordinates = "Lat: ${String.format("%.4f", emprendedor.latitud)}, Lng: ${String.format("%.4f", emprendedor.longitud)}",
                                onClick = { onEmprendedorClick(emprendedor.id) }
                            )
                        }
                        
                        items(servicios.take(3)) { servicio ->
                            MapMarkerItem(
                                title = servicio.nombre,
                                subtitle = servicio.tipo.name.replace("_", " "),
                                icon = Icons.Default.Place,
                                color = MaterialTheme.colorScheme.secondary,
                                coordinates = "Lat: ${String.format("%.4f", servicio.latitud)}, Lng: ${String.format("%.4f", servicio.longitud)}",
                                onClick = { /* TODO: Navigate to service */ }
                            )
                        }
                        
                        if (emprendedores.size + servicios.size > 6) {
                            item {
                                Text(
                                    text = "... y ${emprendedores.size + servicios.size - 6} ubicaciones más",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            // Footer con instrucciones
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Cambia a vista de lista para interactuar con las ubicaciones",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun StatisticItem(
    count: Int,
    label: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun MapMarkerItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
    coordinates: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = color
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = coordinates,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun LoadingMapView(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "Cargando ubicaciones...",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun ErrorMapView(
    message: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    text = "Error al cargar el mapa",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ListView(
    emprendedoresState: Result<List<EmprendedorUbicacion>>,
    serviciosState: Result<List<ServicioUbicacion>>,
    selectedFilter: String,
    onEmprendedorClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when {
            emprendedoresState is Result.Loading || serviciosState is Result.Loading -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            emprendedoresState is Result.Error -> {
                item {
                    Text(
                        text = "Error: ${emprendedoresState.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            emprendedoresState is Result.Success && serviciosState is Result.Success -> {
                val emprendedores = emprendedoresState.data
                val servicios = serviciosState.data
                
                // Mostrar emprendedores
                if (selectedFilter == "todos" || selectedFilter == "emprendedores") {
                    if (emprendedores.isNotEmpty()) {
                        item {
                            Text(
                                text = "Emprendedores (${emprendedores.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        
                        items(emprendedores) { emprendedor ->
                            EmprendedorUbicacionCard(
                                emprendedor = emprendedor,
                                onClick = { onEmprendedorClick(emprendedor.id) }
                            )
                        }
                    }
                }
                
                // Mostrar servicios
                if (selectedFilter == "todos" || selectedFilter == "servicios") {
                    if (servicios.isNotEmpty()) {
                        item {
                            Text(
                                text = "Servicios (${servicios.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        
                        items(servicios) { servicio ->
                            ServicioUbicacionCard(
                                servicio = servicio,
                                onClick = { /* TODO: Navigate to service detail */ }
                            )
                        }
                    }
                }
                
                if (emprendedores.isEmpty() && servicios.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No hay ubicaciones disponibles")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmprendedorUbicacionCard(
    emprendedor: EmprendedorUbicacion,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = emprendedor.nombreEmpresa,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = emprendedor.rubro,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = emprendedor.municipalidad.nombre,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (!emprendedor.direccionCompleta.isNullOrEmpty()) {
                        Text(
                            text = emprendedor.direccionCompleta,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${String.format("%.4f", emprendedor.latitud)}, ${String.format("%.4f", emprendedor.longitud)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicioUbicacionCard(
    servicio: ServicioUbicacion,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = servicio.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = servicio.tipo.name.replace("_", " "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "Por ${servicio.emprendedor.nombreEmpresa}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (!servicio.direccionCompleta.isNullOrEmpty()) {
                        Text(
                            text = servicio.direccionCompleta,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
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
                    
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${String.format("%.4f", servicio.latitud)}, ${String.format("%.4f", servicio.longitud)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}