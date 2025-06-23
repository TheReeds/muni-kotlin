package com.capachica.turismokotlin.ui.screens.admin

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
import com.capachica.turismokotlin.ui.components.EmptyListPlaceholder
import com.capachica.turismokotlin.ui.components.ErrorScreen
import com.capachica.turismokotlin.ui.components.LoadingScreen
import com.capachica.turismokotlin.ui.components.TurismoAppBar
import com.capachica.turismokotlin.ui.components.EstadoReservaChip
import com.capachica.turismokotlin.ui.components.InfoRowSmall
import com.capachica.turismokotlin.ui.viewmodel.ReservaViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReservasScreen(
    onNavigateToDetail: (Long) -> Unit,
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val viewModel: ReservaViewModel = viewModel(factory = factory)
    val reservasState by viewModel.reservasState.collectAsState()
    val confirmarState by viewModel.confirmarState.collectAsState()
    val completarState by viewModel.completarState.collectAsState()
    val cancelarState by viewModel.cancelarState.collectAsState()
    
    val scope = rememberCoroutineScope()
    
    // Filtros
    var selectedEstado by remember { mutableStateOf<EstadoReserva?>(null) }
    var showFilters by remember { mutableStateOf(false) }
    
    // Diálogos
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showCompleteDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var selectedReserva by remember { mutableStateOf<Reserva?>(null) }
    var motivoCancelacion by remember { mutableStateOf("") }
    
    // Cargar datos al inicio
    LaunchedEffect(Unit) {
        viewModel.getAllReservas()
    }
    
    // Recargar después de acciones
    LaunchedEffect(confirmarState, completarState, cancelarState) {
        if (confirmarState is Result.Success || 
            completarState is Result.Success || 
            cancelarState is Result.Success) {
            viewModel.getAllReservas()
            viewModel.clearStates()
        }
    }
    
    // Diálogos de confirmación
    if (showConfirmDialog && selectedReserva != null) {
        AlertDialog(
            onDismissRequest = { 
                showConfirmDialog = false
                selectedReserva = null
            },
            title = { Text("Confirmar Reserva") },
            text = { Text("¿Confirmar la reserva ${selectedReserva!!.codigoReserva}?") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            selectedReserva?.let { viewModel.confirmarReserva(it.id) }
                            showConfirmDialog = false
                            selectedReserva = null
                        }
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showConfirmDialog = false
                        selectedReserva = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    if (showCompleteDialog && selectedReserva != null) {
        AlertDialog(
            onDismissRequest = { 
                showCompleteDialog = false
                selectedReserva = null
            },
            title = { Text("Completar Reserva") },
            text = { Text("¿Marcar como completada la reserva ${selectedReserva!!.codigoReserva}?") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            selectedReserva?.let { viewModel.completarReserva(it.id) }
                            showCompleteDialog = false
                            selectedReserva = null
                        }
                    }
                ) {
                    Text("Completar")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showCompleteDialog = false
                        selectedReserva = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    if (showCancelDialog && selectedReserva != null) {
        AlertDialog(
            onDismissRequest = { 
                showCancelDialog = false
                selectedReserva = null
                motivoCancelacion = ""
            },
            title = { Text("Cancelar Reserva") },
            text = {
                Column {
                    Text("¿Cancelar la reserva ${selectedReserva!!.codigoReserva}?")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = motivoCancelacion,
                        onValueChange = { motivoCancelacion = it },
                        label = { Text("Motivo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            selectedReserva?.let { 
                                viewModel.cancelarReserva(it.id, motivoCancelacion) 
                            }
                            showCancelDialog = false
                            selectedReserva = null
                            motivoCancelacion = ""
                        }
                    },
                    enabled = motivoCancelacion.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Cancelar")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showCancelDialog = false
                        selectedReserva = null
                        motivoCancelacion = ""
                    }
                ) {
                    Text("Volver")
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TurismoAppBar(
                title = "Gestión de Reservas",
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
                FilterSection(
                    selectedEstado = selectedEstado,
                    onEstadoChange = { selectedEstado = it }
                )
            }
            
            // Lista de reservas
            when (val state = reservasState) {
                is Result.Loading -> LoadingScreen()
                is Result.Error -> ErrorScreen(
                    message = state.message,
                    onRetry = { viewModel.getAllReservas() }
                )
                is Result.Success -> {
                    val filteredReservas = if (selectedEstado != null) {
                        state.data.filter { it.estado == selectedEstado }
                    } else {
                        state.data
                    }
                    
                    if (filteredReservas.isEmpty()) {
                        EmptyListPlaceholder(
                            message = "No hay reservas disponibles",
                            buttonText = "Recargar",
                            onButtonClick = { viewModel.getAllReservas() }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredReservas) { reserva ->
                                AdminReservaCard(
                                    reserva = reserva,
                                    onClick = { onNavigateToDetail(reserva.id) },
                                    onConfirmar = {
                                        selectedReserva = reserva
                                        showConfirmDialog = true
                                    },
                                    onCompletar = {
                                        selectedReserva = reserva
                                        showCompleteDialog = true
                                    },
                                    onCancelar = {
                                        selectedReserva = reserva
                                        showCancelDialog = true
                                    }
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
fun FilterSection(
    selectedEstado: EstadoReserva?,
    onEstadoChange: (EstadoReserva?) -> Unit
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
                text = "Filtrar por Estado",
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
                
                FilterChip(
                    onClick = { 
                        onEstadoChange(
                            if (selectedEstado == EstadoReserva.PENDIENTE) null 
                            else EstadoReserva.PENDIENTE
                        ) 
                    },
                    label = { Text("Pendientes") },
                    selected = selectedEstado == EstadoReserva.PENDIENTE
                )
                
                FilterChip(
                    onClick = { 
                        onEstadoChange(
                            if (selectedEstado == EstadoReserva.CONFIRMADA) null 
                            else EstadoReserva.CONFIRMADA
                        ) 
                    },
                    label = { Text("Confirmadas") },
                    selected = selectedEstado == EstadoReserva.CONFIRMADA
                )
            }
        }
    }
}

@Composable
fun AdminReservaCard(
    reserva: Reserva,
    onClick: () -> Unit,
    onConfirmar: () -> Unit,
    onCompletar: () -> Unit,
    onCancelar: () -> Unit
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
                        text = reserva.codigoReserva,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Usuario: ${reserva.usuario.username}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                EstadoReservaChip(estado = reserva.estado)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Información del plan
            Text(
                text = reserva.plan.nombre,
                style = MaterialTheme.typography.bodyLarge,
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
                    text = reserva.plan.municipalidad.nombre,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Detalles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    InfoRowSmall(
                        icon = Icons.Default.DateRange,
                        text = "Inicio: ${reserva.fechaInicio}"
                    )
                    InfoRowSmall(
                        icon = Icons.Default.Group,
                        text = "${reserva.numeroPersonas} persona${if (reserva.numeroPersonas > 1) "s" else ""}"
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${reserva.montoFinal}",
                        style = MaterialTheme.typography.titleMedium,
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
            
            // Botones de acción
            when (reserva.estado) {
                EstadoReserva.PENDIENTE -> {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onCancelar,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Cancelar")
                        }
                        
                        Button(
                            onClick = onConfirmar,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Confirmar")
                        }
                    }
                }
                EstadoReserva.CONFIRMADA, EstadoReserva.PAGADA, EstadoReserva.EN_PROCESO -> {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onCancelar,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Cancelar")
                        }
                        
                        Button(
                            onClick = onCompletar,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Completar")
                        }
                    }
                }
                else -> {
                    // No mostrar botones para reservas completadas o canceladas
                }
            }
        }
    }
}

