package com.capachica.turismokotlin.ui.screens.admin

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
import com.capachica.turismokotlin.ui.viewmodel.PlanTuristicoViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPlanesScreen(
    onNavigateToDetail: (Long) -> Unit = {},
    onNavigateToCreate: () -> Unit = {},
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val viewModel: PlanTuristicoViewModel = viewModel(factory = factory)
    val planesState by viewModel.planesState.collectAsState()
    
    var selectedEstado by remember { mutableStateOf<EstadoPlan?>(null) }
    var showFilters by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        viewModel.getAllPlanes()
    }
    
    LaunchedEffect(selectedEstado) {
        if (selectedEstado != null) {
            viewModel.getPlanesByEstado(selectedEstado!!)
        } else {
            viewModel.getAllPlanes()
        }
    }
    
    Scaffold(
        topBar = {
            TurismoAppBar(
                title = "Administrar Planes",
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
                            contentDescription = "Crear plan"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear plan"
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
                AdminPlanesFilterSection(
                    selectedEstado = selectedEstado,
                    onEstadoChange = { selectedEstado = it },
                    onClearFilters = { selectedEstado = null }
                )
            }
            
            when (val state = planesState) {
                is Result.Loading -> LoadingScreen()
                is Result.Error -> ErrorScreen(
                    message = state.message,
                    onRetry = { viewModel.getAllPlanes() }
                )
                is Result.Success -> {
                    if (state.data.isEmpty()) {
                        EmptyListPlaceholder(
                            message = "No hay planes registrados",
                            buttonText = "Crear Plan",
                            onButtonClick = onNavigateToCreate
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.data) { plan ->
                                AdminPlanCard(
                                    plan = plan,
                                    onCardClick = { onNavigateToDetail(plan.id) },
                                    onEditClick = { onNavigateToDetail(plan.id) }
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
fun AdminPlanesFilterSection(
    selectedEstado: EstadoPlan?,
    onEstadoChange: (EstadoPlan?) -> Unit,
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
            
            Text("Estado del Plan:", style = MaterialTheme.typography.bodyMedium)
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EstadoPlan.values().forEach { estado ->
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
fun AdminPlanCard(
    plan: PlanTuristico,
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
                        containerColor = when (plan.estado) {
                            EstadoPlan.ACTIVO -> MaterialTheme.colorScheme.primaryContainer
                            EstadoPlan.BORRADOR -> MaterialTheme.colorScheme.tertiaryContainer
                            EstadoPlan.INACTIVO -> MaterialTheme.colorScheme.surfaceVariant
                            EstadoPlan.AGOTADO -> MaterialTheme.colorScheme.errorContainer
                            EstadoPlan.SUSPENDIDO -> MaterialTheme.colorScheme.errorContainer
                        }
                    )
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = when (plan.estado) {
                                EstadoPlan.ACTIVO -> Icons.Default.CheckCircle
                                EstadoPlan.BORRADOR -> Icons.Default.Edit
                                EstadoPlan.INACTIVO -> Icons.Default.Pause
                                EstadoPlan.AGOTADO -> Icons.Default.Block
                                EstadoPlan.SUSPENDIDO -> Icons.Default.Warning
                            },
                            contentDescription = null,
                            tint = when (plan.estado) {
                                EstadoPlan.ACTIVO -> MaterialTheme.colorScheme.onPrimaryContainer
                                EstadoPlan.BORRADOR -> MaterialTheme.colorScheme.onTertiaryContainer
                                EstadoPlan.INACTIVO -> MaterialTheme.colorScheme.onSurfaceVariant
                                EstadoPlan.AGOTADO -> MaterialTheme.colorScheme.onErrorContainer
                                EstadoPlan.SUSPENDIDO -> MaterialTheme.colorScheme.onErrorContainer
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = plan.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = plan.municipalidad.nombre,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = plan.estado.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = when (plan.estado) {
                                EstadoPlan.ACTIVO -> MaterialTheme.colorScheme.primary
                                EstadoPlan.BORRADOR -> MaterialTheme.colorScheme.tertiary
                                EstadoPlan.INACTIVO -> MaterialTheme.colorScheme.onSurfaceVariant
                                EstadoPlan.AGOTADO -> MaterialTheme.colorScheme.error
                                EstadoPlan.SUSPENDIDO -> MaterialTheme.colorScheme.error
                            }
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "• ${plan.totalReservas} reservas",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${plan.precioTotal}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${plan.duracionDias}d",
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