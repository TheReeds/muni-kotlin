package com.capachica.turismokotlin.ui.screens.reservas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.capachica.turismokotlin.ui.components.ErrorScreen
import com.capachica.turismokotlin.ui.components.LoadingScreen
import com.capachica.turismokotlin.ui.components.TurismoAppBar
import com.capachica.turismokotlin.ui.components.EstadoReservaChip
import com.capachica.turismokotlin.ui.viewmodel.ReservaViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaDetailScreen(
    reservaId: Long,
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val viewModel: ReservaViewModel = viewModel(factory = factory)
    val reservaState by viewModel.reservaState.collectAsState()
    val cancelarState by viewModel.cancelarState.collectAsState()
    
    val scope = rememberCoroutineScope()
    
    // Estados para diálogos
    var showCancelDialog by remember { mutableStateOf(false) }
    var motivoCancelacion by remember { mutableStateOf("") }
    
    // Cargar datos al inicio
    LaunchedEffect(reservaId) {
        viewModel.getReservaById(reservaId)
    }
    
    // Recargar después de cancelar
    LaunchedEffect(cancelarState) {
        if (cancelarState is Result.Success) {
            viewModel.getReservaById(reservaId)
            viewModel.clearStates()
        }
    }
    
    // Diálogo de cancelación
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { 
                showCancelDialog = false
                motivoCancelacion = ""
            },
            title = { Text("Cancelar Reserva") },
            text = {
                Column {
                    Text("¿Estás seguro que deseas cancelar esta reserva?")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = motivoCancelacion,
                        onValueChange = { motivoCancelacion = it },
                        label = { Text("Motivo de cancelación") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.cancelarReserva(reservaId, motivoCancelacion)
                            showCancelDialog = false
                            motivoCancelacion = ""
                        }
                    },
                    enabled = motivoCancelacion.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Cancelar Reserva")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showCancelDialog = false
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
                title = "Detalle de Reserva",
                onBackClick = onBack
            )
        }
    ) { paddingValues ->
        when (val state = reservaState) {
            is Result.Loading -> LoadingScreen()
            is Result.Error -> ErrorScreen(
                message = state.message,
                onRetry = { viewModel.getReservaById(reservaId) }
            )
            is Result.Success -> {
                val reserva = state.data
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header de la reserva
                    item {
                        ReservaHeaderCard(reserva = reserva)
                    }
                    
                    // Información del plan
                    item {
                        PlanInfoCard(plan = reserva.plan)
                    }
                    
                    // Detalles de la reserva
                    item {
                        ReservaDetailsCard(reserva = reserva)
                    }
                    
                    // Servicios incluidos
                    if (!reserva.serviciosPersonalizados.isNullOrEmpty()) {
                        item {
                            ServiciosIncludedCard(servicios = reserva.serviciosPersonalizados)
                        }
                    }
                    
                    // Información de pago
                    item {
                        PagoInfoCard(reserva = reserva)
                    }
                    
                    // Botones de acción
                    if (reserva.estado in listOf(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA)) {
                        item {
                            ActionButtonsSection(
                                onCancelClick = { showCancelDialog = true },
                                isLoading = cancelarState is Result.Loading
                            )
                        }
                    }
                }
            }

            null -> LoadingScreen()
        }
    }
}

@Composable
fun ReservaHeaderCard(reserva: Reserva) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = reserva.codigoReserva,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Reserva #${reserva.id}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
                
                EstadoReservaChip(estado = reserva.estado)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoColumn(
                    icon = Icons.Default.DateRange,
                    title = "Fecha",
                    value = reserva.fechaInicio
                )
                
                InfoColumn(
                    icon = Icons.Default.Group,
                    title = "Personas",
                    value = reserva.numeroPersonas.toString()
                )
                
                InfoColumn(
                    icon = Icons.Default.AttachMoney,
                    title = "Total",
                    value = "$${reserva.montoFinal}"
                )
            }
        }
    }
}

@Composable
fun InfoColumn(
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
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun PlanInfoCard(plan: PlanTuristicoBasic) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Plan Turístico",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = plan.nombre,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            plan.descripcion?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = plan.municipalidad.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Duración: ${plan.duracionDias} día${if (plan.duracionDias > 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun ReservaDetailsCard(reserva: Reserva) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Detalles de la Reserva",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (reserva.observaciones?.isNotEmpty() == true) {
                reserva.observaciones.let {
                    DetailRow(
                        label = "Observaciones:",
                        value = it
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (reserva.solicitudesEspeciales?.isNotEmpty() == true) {
                reserva.solicitudesEspeciales.let {
                    DetailRow(
                        label = "Solicitudes Especiales:",
                        value = it
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            DetailRow(
                label = "Contacto de Emergencia:",
                value = "${reserva.contactoEmergencia} - ${reserva.telefonoEmergencia}"
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            DetailRow(
                label = "Fecha de Reserva:",
                value = reserva.fechaReserva
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ServiciosIncludedCard(servicios: List<ReservaServicio>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Servicios Incluidos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            servicios.forEach { reservaServicio ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = reservaServicio.servicioPlan.servicio.nombre,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = reservaServicio.servicioPlan.servicio.emprendedor.nombreEmpresa,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Text(
                        text = "$${reservaServicio.precioPersonalizado ?: reservaServicio.servicioPlan.precioEspecial ?: reservaServicio.servicioPlan.servicio.precio}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun PagoInfoCard(reserva: Reserva) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Información de Pago",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Subtotal:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "$${reserva.montoTotal}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            if (reserva.montoDescuento != null && reserva.montoDescuento > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Descuentos:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "-$${reserva.montoDescuento}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Divider()
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$${reserva.montoFinal}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ActionButtonsSection(
    onCancelClick: () -> Unit,
    isLoading: Boolean
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Acciones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedButton(
                onClick = onCancelClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cancelar Reserva")
            }
        }
    }
}