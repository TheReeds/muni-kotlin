package com.capachica.turismokotlin.ui.screens.servicios

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.capachica.turismokotlin.data.model.*
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.components.*
import com.capachica.turismokotlin.ui.viewmodel.ServicioTuristicoViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicioStoreScreen(
    onNavigateToDetail: (Long) -> Unit,
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val viewModel: ServicioTuristicoViewModel = viewModel(factory = factory)
    val serviciosState by viewModel.serviciosState.collectAsState()
    
    // Filtros
    var selectedTipo by remember { mutableStateOf<TipoServicio?>(null) }
    var selectedMunicipalidad by remember { mutableStateOf<Long?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    var sortBy by remember { mutableStateOf("nombre") }
    
    // Cargar datos al inicio
    LaunchedEffect(Unit, selectedTipo, selectedMunicipalidad, searchQuery) {
        when {
            searchQuery.isNotEmpty() -> {
                // Si hay término de búsqueda, usar searchServicios
                viewModel.searchServicios(searchQuery)
            }
            selectedTipo != null -> {
                // Si hay filtro por tipo
                viewModel.getServiciosByTipo(selectedTipo!!)
            }
            selectedMunicipalidad != null -> {
                // Si hay filtro por municipalidad
                viewModel.getServiciosByMunicipalidad(selectedMunicipalidad!!)
            }
            else -> {
                // Por defecto cargar todos los servicios activos
                viewModel.getServiciosByEstado(EstadoServicio.ACTIVO)
            }
        }
    }
    
    Scaffold(
        topBar = {
            TurismoAppBar(
                title = "Tienda de Servicios",
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
            // Barra de búsqueda
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Buscar servicios...",
                modifier = Modifier.padding(16.dp)
            )
            
            // Filtros
            if (showFilters) {
                ServicioFiltersSection(
                    selectedTipo = selectedTipo,
                    onTipoChange = { selectedTipo = it },
                    sortBy = sortBy,
                    onSortChange = { sortBy = it }
                )
            }
            
            // Lista de servicios
            when (val state = serviciosState) {
                is Result.Loading -> LoadingScreen()
                is Result.Error -> ErrorScreen(
                    message = state.message,
                    onRetry = { 
                        when {
                            searchQuery.isNotEmpty() -> viewModel.searchServicios(searchQuery)
                            selectedTipo != null -> viewModel.getServiciosByTipo(selectedTipo!!)
                            selectedMunicipalidad != null -> viewModel.getServiciosByMunicipalidad(
                                selectedMunicipalidad!!
                            )
                            else -> viewModel.getServiciosByEstado(EstadoServicio.ACTIVO)
                        }
                    }
                )
                is Result.Success -> {
                    val sortedServicios = when (sortBy) {
                        "precio_asc" -> state.data.sortedBy { it.precio }
                        "precio_desc" -> state.data.sortedByDescending { it.precio }
                        "capacidad" -> state.data.sortedByDescending { it.capacidadMaxima }
                        else -> state.data.sortedBy { it.nombre }
                    }
                    
                    if (sortedServicios.isEmpty()) {
                        EmptyListPlaceholder(
                            message = "No se encontraron servicios",
                            buttonText = "Limpiar filtros",
                            onButtonClick = {
                                selectedTipo = null
                                selectedMunicipalidad = null
                                searchQuery = ""
                            }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Servicios destacados
                            if (searchQuery.isEmpty() && selectedTipo == null) {
                                item {
                                    FeaturedServicesSection(
                                        servicios = sortedServicios.take(5),
                                        onServiceClick = onNavigateToDetail
                                    )
                                }
                            }
                            
                            // Lista principal de servicios
                            items(sortedServicios) { servicio ->
                                ServicioStoreCard(
                                    servicio = servicio,
                                    onClick = { onNavigateToDetail(servicio.id) }
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
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar"
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Limpiar"
                    )
                }
            }
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun ServicioFiltersSection(
    selectedTipo: TipoServicio?,
    onTipoChange: (TipoServicio?) -> Unit,
    sortBy: String,
    onSortChange: (String) -> Unit
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
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Filtro por tipo
            Text(
                text = "Tipo de Servicio",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        onClick = { onTipoChange(null) },
                        label = { Text("Todos") },
                        selected = selectedTipo == null
                    )
                }
                
                items(TipoServicio.values()) { tipo ->
                    FilterChip(
                        onClick = { 
                            onTipoChange(if (selectedTipo == tipo) null else tipo) 
                        },
                        label = { Text(tipo.name) },
                        selected = selectedTipo == tipo
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Ordenamiento
            Text(
                text = "Ordenar por",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val sortOptions = listOf(
                    "nombre" to "Nombre",
                    "precio_asc" to "Precio ↑",
                    "precio_desc" to "Precio ↓",
                    "capacidad" to "Mayor capacidad"
                )
                
                items(sortOptions) { (value, label) ->
                    FilterChip(
                        onClick = { onSortChange(value) },
                        label = { Text(label) },
                        selected = sortBy == value
                    )
                }
            }
        }
    }
}

@Composable
fun FeaturedServicesSection(
    servicios: List<ServicioTuristico>,
    onServiceClick: (Long) -> Unit
) {
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
                text = "Servicios Destacados",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(servicios) { servicio ->
                    FeaturedServiceCard(
                        servicio = servicio,
                        onClick = { onServiceClick(servicio.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun FeaturedServiceCard(
    servicio: ServicioTuristico,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            AsyncImage(
                model = servicio.imagenUrl ?: "https://via.placeholder.com/180x120",
                contentDescription = servicio.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentScale = ContentScale.Crop
            )
            
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = servicio.nombre,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "4.5",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "$${servicio.precio}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ServicioStoreCard(
    servicio: ServicioTuristico,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            // Imagen del servicio
            AsyncImage(
                model = servicio.imagenUrl ?: "https://via.placeholder.com/120x90",
                contentDescription = servicio.nombre,
                modifier = Modifier
                    .size(100.dp)
                    .padding(end = 12.dp),
                contentScale = ContentScale.Crop
            )
            
            // Información del servicio
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = servicio.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))

                servicio.descripcion?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Información adicional
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
                        text = servicio.emprendedor.municipalidad.nombre,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "4.5 (12 reseñas)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Precio y capacidad
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
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
                    
                    if (servicio.capacidadMaxima > 0) {
                        AssistChip(
                            onClick = { },
                            label = {
                                Text(
                                    text = "Max ${servicio.capacidadMaxima}",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Group,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}