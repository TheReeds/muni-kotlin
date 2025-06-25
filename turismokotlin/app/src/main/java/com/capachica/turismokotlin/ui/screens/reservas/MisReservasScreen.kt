package com.capachica.turismokotlin.ui.screens.reservas

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
fun MisReservasScreen(
    onNavigateToDetail: (Long) -> Unit,
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val viewModel: ReservaViewModel = viewModel(factory = factory)
    val reservasState by viewModel.reservasState.collectAsState()
    val cancelarState by viewModel.cancelarState.collectAsState()
    
    val scope = rememberCoroutineScope()
    
    // Para mostrar diálogo de cancelación
    val showCancelDialog = remember { mutableStateOf(false) }
    val reservaToCancel = remember { mutableStateOf<Reserva?>(null) }
    val motivoCancelacion = remember { mutableStateOf("") }
    
    // Cargar datos al inicio
    LaunchedEffect(Unit) {
        viewModel.getMisReservas()
    }
    
    // Recargar datos después de cancelar
    LaunchedEffect(cancelarState) {
        if (cancelarState is Result.Success) {
            viewModel.getMisReservas()
            viewModel.clearStates()
        }
    }
    
    // Diálogo de cancelación
    if (showCancelDialog.value && reservaToCancel.value != null) {
        CancelReservaDialog(
            reserva = reservaToCancel.value!!,
            motivo = motivoCancelacion.value,
            onMotivoChange = { motivoCancelacion.value = it },
            onConfirm = {
                scope.launch {
                    reservaToCancel.value?.let { reserva ->
                        viewModel.cancelarReserva(reserva.id, motivoCancelacion.value)
                        showCancelDialog.value = false
                        reservaToCancel.value = null
                        motivoCancelacion.value = ""
                    }
                }
            },
            onDismiss = {
                showCancelDialog.value = false
                reservaToCancel.value = null
                motivoCancelacion.value = ""
            },
            isLoading = cancelarState is Result.Loading
        )
    }
    
    Scaffold(
        topBar = {
            TurismoAppBar(
                title = "Mis Reservas",
                onBackClick = onBack
            )
        }
    ) { paddingValues ->
        when (val state = reservasState) {
            is Result.Loading -> LoadingScreen()
            is Result.Error -> ErrorScreen(
                message = state.message,
                onRetry = { viewModel.getMisReservas() }
            )
            is Result.Success -> {
                if (state.data.isEmpty()) {
                    EmptyListPlaceholder(
                        message = "No tienes reservas aún",
                        buttonText = "Explorar Planes",
                        onButtonClick = onBack
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.data) { reserva ->
                            ReservaCard(
                                reserva = reserva,
                                onClick = { onNavigateToDetail(reserva.id) },
                                onCancelClick = {
                                    reservaToCancel.value = reserva
                                    showCancelDialog.value = true
                                }
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
fun ReservaCard(
    reserva: Reserva,
    onClick: () -> Unit,
    onCancelClick: () -> Unit
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
            // Header con estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reserva.codigoReserva,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                
                EstadoReservaChip(estado = reserva.estado)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Información del plan
            Text(
                text = reserva.plan.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
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
            
            // Detalles de la reserva
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
            if (reserva.estado in listOf(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA)) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancelClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cancelar")
                    }
                    
                    Button(
                        onClick = onClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ver Detalle")
                    }
                }
            }
        }
    }
}


@Composable
fun CancelReservaDialog(
    reserva: Reserva,
    motivo: String,
    onMotivoChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cancelar Reserva") },
        text = {
            Column {
                Text("¿Estás seguro que deseas cancelar la reserva ${reserva.codigoReserva}?")
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = motivo,
                    onValueChange = onMotivoChange,
                    label = { Text("Motivo de cancelación") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = motivo.isNotEmpty() && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onError
                    )
                } else {
                    Text("Cancelar Reserva")
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Volver")
            }
        }
    )
}