package com.capachica.turismokotlin.ui.screens.planes

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
import com.capachica.turismokotlin.ui.components.TurismoAppBar
import com.capachica.turismokotlin.ui.viewmodel.PlanTuristicoViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisPlanesScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val viewModel: PlanTuristicoViewModel = viewModel(factory = factory)
    val planesState by viewModel.planesState.collectAsState()
    val cambiarEstadoState by viewModel.cambiarEstadoState.collectAsState()
    
    // Cargar mis planes al inicio
    LaunchedEffect(Unit) {
        viewModel.getMisPlanes()
    }
    
    // Recargar cuando se cambie el estado de un plan
    LaunchedEffect(cambiarEstadoState) {
        if (cambiarEstadoState is Result.Success) {
            viewModel.getMisPlanes()
        }
    }
    
    Scaffold(
        topBar = {
            TurismoAppBar(
                title = "Mis Planes Turísticos",
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = { viewModel.getMisPlanes() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
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
        when (val state = planesState) {
            is Result.Loading -> {
                LoadingScreen()
            }
            is Result.Success -> {
                if (state.data.isEmpty()) {
                    EmptyMyPlansSection(
                        onCreatePlan = onNavigateToCreate
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Estadísticas rápidas
                        item {
                            MyPlansStatsCard(planes = state.data)
                        }
                        
                        // Lista de planes
                        items(state.data) { plan ->
                            MyPlanCard(
                                plan = plan,
                                onViewDetails = { onNavigateToDetail(plan.id) },
                                onEdit = { onNavigateToEdit(plan.id) },
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
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.getMisPlanes() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onErrorContainer,
                                contentColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyPlansStatsCard(planes: List<PlanTuristico>) {
    val totalPlanes = planes.size
    val planesActivos = planes.count { it.estado == EstadoPlan.ACTIVO }
    val totalReservas = planes.sumOf { it.totalReservas }
    val ingresosPotenciales = planes.filter { it.estado == EstadoPlan.ACTIVO }.sumOf { it.precioTotal }
    
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
                text = "Resumen",
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
                    icon = Icons.Default.Map,
                    label = "Planes",
                    value = totalPlanes.toString(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                StatItem(
                    icon = Icons.Default.CheckCircle,
                    label = "Activos",
                    value = planesActivos.toString(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                StatItem(
                    icon = Icons.Default.BookOnline,
                    label = "Reservas",
                    value = totalReservas.toString(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                StatItem(
                    icon = Icons.Default.AttachMoney,
                    label = "Precio desde",
                    value = "S/. ${ingresosPotenciales.toInt()}",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = color.copy(alpha = 0.8f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPlanCard(
    plan: PlanTuristico,
    onViewDetails: () -> Unit,
    onEdit: () -> Unit,
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
                            icon = Icons.Default.BookOnline,
                            text = "${plan.totalReservas} reservas"
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
                        
                        // Opciones de cambio de estado
                        if (plan.estado != EstadoPlan.ACTIVO) {
                            DropdownMenuItem(
                                text = { Text("Activar") },
                                onClick = {
                                    showStatusMenu = false
                                    onChangeStatus(EstadoPlan.ACTIVO)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                                }
                            )
                        }
                        
                        if (plan.estado == EstadoPlan.ACTIVO) {
                            DropdownMenuItem(
                                text = { Text("Pausar") },
                                onClick = {
                                    showStatusMenu = false
                                    onChangeStatus(EstadoPlan.INACTIVO)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Pause, contentDescription = null)
                                }
                            )
                        }
                        
                        if (plan.estado != EstadoPlan.BORRADOR) {
                            DropdownMenuItem(
                                text = { Text("Marcar como borrador") },
                                onClick = {
                                    showStatusMenu = false
                                    onChangeStatus(EstadoPlan.BORRADOR)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Edit, contentDescription = null)
                                }
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Estado y estadísticas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
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
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = onViewDetails) {
                        Text("Ver")
                    }
                    
                    if (plan.estado == EstadoPlan.BORRADOR || plan.estado == EstadoPlan.INACTIVO) {
                        Button(onClick = onEdit) {
                            Text("Editar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyMyPlansSection(
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
                text = "No tienes planes turísticos",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Crea tu primer plan turístico para comenzar a recibir reservas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onCreatePlan) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crear Mi Primer Plan")
            }
        }
    }
}