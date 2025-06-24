package com.capachica.turismokotlin.ui.screens.planes

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capachica.turismokotlin.data.model.*
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.components.LoadingScreen
import com.capachica.turismokotlin.ui.components.TurismoAppBar
import com.capachica.turismokotlin.ui.viewmodel.PlanTuristicoViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanManagementScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val viewModel: PlanTuristicoViewModel = viewModel(factory = factory)
    val planesState by viewModel.planesState.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()
    
    var showFilters by remember { mutableStateOf(false) }
    var selectedEstado by remember { mutableStateOf<EstadoPlan?>(null) }
    var selectedNivel by remember { mutableStateOf<NivelDificultad?>(null) }
    var searchText by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var planToDelete by remember { mutableStateOf<PlanTuristico?>(null) }
    
    // Cargar datos al inicio
    LaunchedEffect(Unit) {
        viewModel.getAllPlanes()
    }
    
    // Aplicar filtros cuando cambien
    LaunchedEffect(selectedEstado, selectedNivel, searchText) {
        when {
            searchText.isNotBlank() -> viewModel.searchPlanes(searchText)
            selectedEstado != null -> viewModel.getPlanesByEstado(selectedEstado!!)
            selectedNivel != null -> viewModel.getPlanesByNivelDificultad(selectedNivel!!)
            else -> viewModel.getAllPlanes()
        }
    }
    
    // Manejar resultado de eliminación
    LaunchedEffect(deleteState) {
        if (deleteState is Result.Success) {
            viewModel.resetDeleteState()
            viewModel.getAllPlanes() // Recargar lista
        }
    }
    
    Scaffold(
        topBar = {
            TurismoAppBar(
                title = "Gestión de Planes",
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(
                            imageVector = if (showFilters) Icons.Default.FilterListOff else Icons.Default.FilterList,
                            contentDescription = "Filtros"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear Plan")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filtros
            if (showFilters) {
                FilterSection(
                    selectedEstado = selectedEstado,
                    onEstadoChange = { selectedEstado = it },
                    selectedNivel = selectedNivel,
                    onNivelChange = { selectedNivel = it },
                    searchText = searchText,
                    onSearchChange = { searchText = it },
                    onClearFilters = {
                        selectedEstado = null
                        selectedNivel = null
                        searchText = ""
                    }
                )
            }
            
            // Lista de planes
            when (val state = planesState) {
                is Result.Loading -> {
                    LoadingScreen()
                }
                is Result.Success -> {
                    if (state.data.isEmpty()) {
                        EmptyPlansSection(
                            onCreatePlan = onNavigateToCreate
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.data) { plan ->
                                PlanManagementCard(
                                    plan = plan,
                                    onViewDetails = { onNavigateToDetail(plan.id) },
                                    onEdit = { onNavigateToEdit(plan.id) },
                                    onDelete = { 
                                        planToDelete = plan
                                        showDeleteDialog = true
                                    },
                                    onChangeStatus = { newStatus ->
                                        viewModel.cambiarEstadoPlan(plan.id, newStatus)
                                    }
                                )
                            }
                        }
                    }
                }
                is Result.Error -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Dialog de confirmación de eliminación
    if (showDeleteDialog && planToDelete != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteDialog = false
                planToDelete = null
            },
            title = { Text("Confirmar eliminación") },
            text = { 
                Text("¿Estás seguro de que quieres eliminar el plan '${planToDelete?.nombre}'? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        planToDelete?.let { viewModel.deletePlan(it.id) }
                        showDeleteDialog = false
                        planToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showDeleteDialog = false
                        planToDelete = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun FilterSection(
    selectedEstado: EstadoPlan?,
    onEstadoChange: (EstadoPlan?) -> Unit,
    selectedNivel: NivelDificultad?,
    onNivelChange: (NivelDificultad?) -> Unit,
    searchText: String,
    onSearchChange: (String) -> Unit,
    onClearFilters: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtros",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(onClick = onClearFilters) {
                    Text("Limpiar")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Búsqueda
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchChange,
                label = { Text("Buscar planes") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Filtros por estado
            Text(
                text = "Estado",
                style = MaterialTheme.typography.labelMedium
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedEstado == null,
                        onClick = { onEstadoChange(null) },
                        label = { Text("Todos") }
                    )
                }
                items(EstadoPlan.values()) { estado ->
                    FilterChip(
                        selected = selectedEstado == estado,
                        onClick = { 
                            onEstadoChange(if (selectedEstado == estado) null else estado)
                        },
                        label = { Text(estado.name) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Filtros por nivel de dificultad
            Text(
                text = "Nivel de Dificultad",
                style = MaterialTheme.typography.labelMedium
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedNivel == null,
                        onClick = { onNivelChange(null) },
                        label = { Text("Todos") }
                    )
                }
                items(NivelDificultad.values()) { nivel ->
                    FilterChip(
                        selected = selectedNivel == nivel,
                        onClick = { 
                            onNivelChange(if (selectedNivel == nivel) null else nivel)
                        },
                        label = { Text(nivel.name) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanManagementCard(
    plan: PlanTuristico,
    onViewDetails: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onChangeStatus: (EstadoPlan) -> Unit
) {
    var showStatusMenu by remember { mutableStateOf(false) }
    
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
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = plan.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    plan.descripcion?.let { descripcion ->
                        Text(
                            text = descripcion,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        InfoChip(
                            icon = Icons.Default.AttachMoney,
                            text = "S/. ${plan.precioTotal}"
                        )
                        InfoChip(
                            icon = Icons.Default.Schedule,
                            text = "${plan.duracionDias} días"
                        )
                        InfoChip(
                            icon = Icons.Default.People,
                            text = "${plan.capacidadMaxima} pax"
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        AssistChip(
                            onClick = { },
                            label = { Text(plan.estado.name) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = when (plan.estado) {
                                    EstadoPlan.ACTIVO -> MaterialTheme.colorScheme.primaryContainer
                                    EstadoPlan.BORRADOR -> MaterialTheme.colorScheme.surfaceVariant
                                    EstadoPlan.INACTIVO -> MaterialTheme.colorScheme.errorContainer
                                    EstadoPlan.AGOTADO -> MaterialTheme.colorScheme.tertiaryContainer
                                    EstadoPlan.SUSPENDIDO -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                }
                            )
                        )
                        
                        AssistChip(
                            onClick = { },
                            label = { Text(plan.nivelDificultad.name) }
                        )
                    }
                }
                
                // Menú de acciones
                Box {
                    IconButton(onClick = { showStatusMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                    }
                    
                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Ver detalles") },
                            onClick = {
                                showStatusMenu = false
                                onViewDetails()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Visibility, contentDescription = null)
                            }
                        )
                        
                        DropdownMenuItem(
                            text = { Text("Editar") },
                            onClick = {
                                showStatusMenu = false
                                onEdit()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null)
                            }
                        )
                        
                        Divider()
                        
                        // Cambio de estado
                        EstadoPlan.values().filter { it != plan.estado }.forEach { estado ->
                            DropdownMenuItem(
                                text = { Text("Marcar como ${estado.name}") },
                                onClick = {
                                    showStatusMenu = false
                                    onChangeStatus(estado)
                                },
                                leadingIcon = {
                                    Icon(
                                        when (estado) {
                                            EstadoPlan.ACTIVO -> Icons.Default.PlayArrow
                                            EstadoPlan.INACTIVO -> Icons.Default.Pause
                                            EstadoPlan.BORRADOR -> Icons.Default.Edit
                                            EstadoPlan.AGOTADO -> Icons.Default.EventBusy
                                            EstadoPlan.SUSPENDIDO -> Icons.Default.Block
                                        },
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                        
                        Divider()
                        
                        DropdownMenuItem(
                            text = { Text("Eliminar") },
                            onClick = {
                                showStatusMenu = false
                                onDelete()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete, 
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Información adicional
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Municipalidad: ${plan.municipalidad.nombre}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "${plan.totalReservas} reservas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EmptyPlansSection(
    onCreatePlan: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Map,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No hay planes turísticos",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Crea el primer plan turístico para comenzar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onCreatePlan) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crear Plan")
            }
        }
    }
}