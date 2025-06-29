package com.capachica.turismokotlin.ui.screens.emprendedor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capachica.turismokotlin.data.model.Emprendedor
import com.capachica.turismokotlin.data.model.BusquedaCercanosResponse
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.components.EmptyListPlaceholder
import com.capachica.turismokotlin.ui.components.ErrorScreen
import com.capachica.turismokotlin.ui.components.LoadingScreen
import com.capachica.turismokotlin.ui.components.TurismoAppBar
import com.capachica.turismokotlin.ui.components.GeolocationHelper
import com.capachica.turismokotlin.ui.components.LocationPermissionDialog
import com.capachica.turismokotlin.ui.components.LocationServicesDialog
import com.capachica.turismokotlin.ui.viewmodel.AuthViewModel
import com.capachica.turismokotlin.ui.viewmodel.EmprendedorViewModel
import com.capachica.turismokotlin.ui.viewmodel.UbicacionViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory
import com.capachica.turismokotlin.utils.DistanceUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmprendedoresScreen(
    municipalidadId: Long = 0,
    categoriaId: Long? = null,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToMapa: () -> Unit,
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val emprendedorViewModel: EmprendedorViewModel = viewModel(factory = factory)
    val ubicacionViewModel: UbicacionViewModel = viewModel(factory = factory)
    val authViewModel: AuthViewModel = viewModel(factory = factory)
    
    val emprendedoresState by emprendedorViewModel.emprendedoresState.collectAsState()
    val deleteState by emprendedorViewModel.deleteState.collectAsState()
    val busquedaCercanosState by ubicacionViewModel.busquedaCercanosState.collectAsState()
    val userRoles by authViewModel.userRoles.collectAsState()
    
    // Control de acceso
    val isAdmin = userRoles.contains("ROLE_ADMIN")
    val canManage = isAdmin || userRoles.contains("ROLE_MUNICIPALIDAD")

    // Para mostrar diálogo de confirmación
    val showDeleteConfirmDialog = remember { mutableStateOf(false) }
    val emprendedorToDelete = remember { mutableStateOf<Emprendedor?>(null) }

    // Scope para lanzar corrutinas
    val scope = rememberCoroutineScope()

    // Estados de búsqueda
    var searchQuery by remember { mutableStateOf("") }
    var showLocationSearch by remember { mutableStateOf(false) }
    var showFilterOptions by remember { mutableStateOf(false) }
    var currentFilter by remember { mutableStateOf("todos") } // todos, cercanos, con_ubicacion
    var userLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    
    // Cargar datos al inicio o cuando cambien los parámetros
    LaunchedEffect(municipalidadId, categoriaId, currentFilter) {
        when {
            municipalidadId > 0 -> emprendedorViewModel.getEmprendedoresByMunicipalidad(municipalidadId)
            categoriaId != null -> emprendedorViewModel.getEmprendedoresByCategoria(categoriaId)
            else -> emprendedorViewModel.getAllEmprendedores()
        }
    }

    // Recargar datos después de eliminar
    LaunchedEffect(deleteState) {
        if (deleteState is Result.Success && (deleteState as Result.Success).data) {
            when {
                municipalidadId > 0 -> emprendedorViewModel.getEmprendedoresByMunicipalidad(municipalidadId)
                categoriaId != null -> emprendedorViewModel.getEmprendedoresByCategoria(categoriaId)
                else -> emprendedorViewModel.getAllEmprendedores()
            }
        }
    }

    // Diálogo de confirmación para eliminar
    if (showDeleteConfirmDialog.value && emprendedorToDelete.value != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmDialog.value = false
                emprendedorToDelete.value = null
            },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Está seguro que desea eliminar el emprendedor '${emprendedorToDelete.value?.nombreEmpresa}'? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            emprendedorToDelete.value?.id?.let { emprendedorViewModel.deleteEmprendedor(it) }
                            showDeleteConfirmDialog.value = false
                            emprendedorToDelete.value = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showDeleteConfirmDialog.value = false
                        emprendedorToDelete.value = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    val title = when {
        municipalidadId > 0 -> "Emprendedores de Municipalidad"
        categoriaId != null -> "Emprendedores por Categoría"
        else -> "Emprendedores"
    }

    Scaffold(
        topBar = {
            TurismoAppBar(
                title = title,
                onBackClick = onBack,
                actions = {
                    // Botón de mapa
                    IconButton(onClick = onNavigateToMapa) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Ver en mapa"
                        )
                    }
                    
                    // Botón de filtros
                    IconButton(onClick = { showFilterOptions = !showFilterOptions }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filtros"
                        )
                    }
                    
                    // Botón de búsqueda por ubicación
                    IconButton(onClick = { showLocationSearch = !showLocationSearch }) {
                        Icon(
                            imageVector = Icons.Default.LocationSearching,
                            contentDescription = "Búsqueda por ubicación"
                        )
                    }
                    
                    // Botón de crear (solo si tiene permisos)
                    if (canManage) {
                        IconButton(onClick = onNavigateToCreate) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Añadir emprendedor"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (canManage) {
                FloatingActionButton(onClick = onNavigateToCreate) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Crear emprendedor"
                    )
                }
            }
        }
    ) { paddingValues ->
        when (val state = emprendedoresState) {
            is Result.Loading -> LoadingScreen()
            is Result.Error -> ErrorScreen(
                message = state.message,
                onRetry = {
                    when {
                        municipalidadId > 0 -> emprendedorViewModel.getEmprendedoresByMunicipalidad(municipalidadId)
                        categoriaId != null -> emprendedorViewModel.getEmprendedoresByCategoria(categoriaId)
                        else -> emprendedorViewModel.getAllEmprendedores()
                    }
                }
            )
            is Result.Success -> {
                if (state.data.isEmpty()) {
                    EmptyListPlaceholder(
                        message = "No hay emprendedores registrados",
                        buttonText = "Crear Emprendedor",
                        onButtonClick = onNavigateToCreate
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        // Barra de búsqueda y filtros
                        SearchAndFiltersSection(
                            searchQuery = searchQuery,
                            onSearchQueryChange = { searchQuery = it },
                            showLocationSearch = showLocationSearch,
                            showFilterOptions = showFilterOptions,
                            currentFilter = currentFilter,
                            onFilterChange = { currentFilter = it },
                            onLocationSearch = { lat, lng, radius ->
                                ubicacionViewModel.buscarCercanos(lat, lng, radius, "emprendedor")
                            },
                            onUserLocationObtained = { lat, lng ->
                                userLocation = Pair(lat, lng)
                            },
                            factory = factory
                        )
                        
                        // Lista de emprendedores
                        val filteredEmprendedores = filterEmprendedores(state.data, searchQuery, currentFilter, busquedaCercanosState)
                        
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredEmprendedores) { emprendedor ->
                                EmprendedorListItem(
                                    emprendedor = emprendedor,
                                    onClick = { onNavigateToDetail(emprendedor.id) },
                                    onDelete = if (canManage) {
                                        {
                                            emprendedorToDelete.value = emprendedor
                                            showDeleteConfirmDialog.value = true
                                        }
                                    } else null,
                                    showLocationInfo = true,
                                    userLocation = userLocation
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Función para filtrar emprendedores
fun filterEmprendedores(
    emprendedores: List<Emprendedor>,
    searchQuery: String,
    currentFilter: String,
    busquedaCercanosState: Result<BusquedaCercanosResponse>? // <-- Change this parameter to be nullable
): List<Emprendedor> {
    var filteredList = emprendedores

    // Filtrar por búsqueda de texto
    if (searchQuery.isNotEmpty()) {
        filteredList = filteredList.filter { emprendedor ->
            emprendedor.nombreEmpresa.contains(searchQuery, ignoreCase = true) ||
                    emprendedor.rubro.contains(searchQuery, ignoreCase = true) ||
                    emprendedor.direccionCompleta?.contains(searchQuery, ignoreCase = true) == true ||
                    emprendedor.municipalidad?.nombre?.contains(searchQuery, ignoreCase = true) == true
        }
    }

    // Filtrar por tipo
    when (currentFilter) {
        "cercanos" -> {
            // This check now correctly handles a null busquedaCercanosState
            if (busquedaCercanosState is Result.Success) {
                val cercanosIds = busquedaCercanosState.data.emprendedores.map { it.id }.toSet()
                filteredList = filteredList.filter { it.id in cercanosIds }
            }
        }
        "con_ubicacion" -> {
            filteredList = filteredList.filter {
                it.latitud != null && it.longitud != null
            }
        }
        // "todos" no aplica filtro adicional
    }

    return filteredList
}

@Composable
fun SearchAndFiltersSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    showLocationSearch: Boolean,
    showFilterOptions: Boolean,
    currentFilter: String,
    onFilterChange: (String) -> Unit,
    onLocationSearch: (Double, Double, Double) -> Unit,
    onUserLocationObtained: (Double, Double) -> Unit = { _, _ -> },
    factory: ViewModelFactory
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Barra de búsqueda
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("Buscar emprendedores...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar"
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpiar búsqueda"
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        // Filtros
        if (showFilterOptions) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    onClick = { onFilterChange("todos") },
                    label = { Text("Todos") },
                    selected = currentFilter == "todos"
                )
                FilterChip(
                    onClick = { onFilterChange("con_ubicacion") },
                    label = { Text("Con ubicación") },
                    selected = currentFilter == "con_ubicacion"
                )
                FilterChip(
                    onClick = { onFilterChange("cercanos") },
                    label = { Text("Cercanos") },
                    selected = currentFilter == "cercanos"
                )
            }
        }
        
        // Búsqueda por ubicación
        if (showLocationSearch) {
            LocationSearchSection(
                onLocationSearch = onLocationSearch,
                onUserLocationObtained = onUserLocationObtained,
                factory = factory
            )
        }
    }
}

@Composable
fun LocationSearchSection(
    onLocationSearch: (Double, Double, Double) -> Unit,
    onUserLocationObtained: (Double, Double) -> Unit = { _, _ -> },
    factory: ViewModelFactory
) {
    val ubicacionViewModel: UbicacionViewModel = viewModel(factory = factory)
    
    var latitudText by remember { mutableStateOf("") }
    var longitudText by remember { mutableStateOf("") }
    var radiusText by remember { mutableStateOf("5.0") }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showLocationServicesDialog by remember { mutableStateOf(false) }
    var locationError by remember { mutableStateOf<String?>(null) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Búsqueda por proximidad",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = latitudText,
                    onValueChange = { latitudText = it },
                    label = { Text("Latitud") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                
                OutlinedTextField(
                    value = longitudText,
                    onValueChange = { longitudText = it },
                    label = { Text("Longitud") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                
                OutlinedTextField(
                    value = radiusText,
                    onValueChange = { radiusText = it },
                    label = { Text("Radio (km)") },
                    modifier = Modifier.weight(0.8f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón para usar ubicación actual con geolocalización
                GeolocationHelper(
                    onLocationObtained = { lat, lng ->
                        latitudText = lat.toString()
                        longitudText = lng.toString()
                        locationError = null
                        onUserLocationObtained(lat, lng)
                    },
                    onError = { error ->
                        locationError = error
                    },
                    onPermissionDenied = {
                        showPermissionDialog = true
                    }
                )
                
                // Botón de búsqueda
                Button(
                    onClick = {
                        val lat = latitudText.toDoubleOrNull()
                        val lng = longitudText.toDoubleOrNull()
                        val radius = radiusText.toDoubleOrNull()
                        
                        if (lat != null && lng != null && radius != null) {
                            onLocationSearch(lat, lng, radius)
                            locationError = null
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = latitudText.toDoubleOrNull() != null && 
                             longitudText.toDoubleOrNull() != null && 
                             radiusText.toDoubleOrNull() != null
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Buscar")
                }
            }
            
            // Mostrar errores de ubicación
            locationError?.let { error ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            // Ubicaciones rápidas
            Text(
                text = "Ubicaciones rápidas:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = {
                        latitudText = "-12.0464"
                        longitudText = "-77.0428"
                    },
                    label = { Text("Lima") }
                )
                AssistChip(
                    onClick = {
                        latitudText = "-13.5319"
                        longitudText = "-71.9675"
                    },
                    label = { Text("Cusco") }
                )
                AssistChip(
                    onClick = {
                        latitudText = "-15.8404"
                        longitudText = "-70.0219"
                    },
                    label = { Text("Puno") }
                )
            }
        }
    }
    
    // Diálogos para permisos y servicios de ubicación
    LocationPermissionDialog(
        showDialog = showPermissionDialog,
        onDismiss = { showPermissionDialog = false },
        onRequestPermission = { 
            showPermissionDialog = false
            // El usuario deberá intentar obtener la ubicación nuevamente
        }
    )
    
    LocationServicesDialog(
        showDialog = showLocationServicesDialog,
        onDismiss = { showLocationServicesDialog = false },
        onOpenSettings = {
            showLocationServicesDialog = false
            // TODO: Abrir configuración de ubicación del dispositivo
        }
    )
}

@Composable
fun EmprendedorListItem(
    emprendedor: Emprendedor,
    onClick: () -> Unit,
    onDelete: (() -> Unit)? = null,
    showLocationInfo: Boolean = false,
    userLocation: Pair<Double, Double>? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Business,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = emprendedor.nombreEmpresa,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Rubro: ${emprendedor.rubro}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                emprendedor.municipalidad?.let { municipalidad ->
                    Text(
                        text = "Municipalidad: ${municipalidad.nombre}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                // Mostrar información de ubicación si está disponible y se solicita
                if (showLocationInfo && emprendedor.latitud != null && emprendedor.longitud != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Dirección o coordenadas
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (!emprendedor.direccionCompleta.isNullOrEmpty()) {
                                emprendedor.direccionCompleta
                            } else {
                                "${String.format("%.4f", emprendedor.latitud)}, ${String.format("%.4f", emprendedor.longitud)}"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    
                    // Distancia si se proporciona ubicación del usuario
                    if (userLocation != null) {
                        val distance = DistanceUtils.calculateDistance(
                            userLocation.first, userLocation.second,
                            emprendedor.latitud, emprendedor.longitud
                        )
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Straighten,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = DistanceUtils.formatDistance(distance),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Botón de eliminar (solo si se proporciona la función)
            onDelete?.let { deleteAction ->
                IconButton(onClick = deleteAction) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}