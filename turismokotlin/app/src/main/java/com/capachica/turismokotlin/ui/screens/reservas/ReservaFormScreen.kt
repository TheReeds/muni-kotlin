package com.capachica.turismokotlin.ui.screens.reservas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capachica.turismokotlin.data.model.*
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.components.ErrorScreen
import com.capachica.turismokotlin.ui.components.LoadingScreen
import com.capachica.turismokotlin.ui.components.TurismoAppBar
import com.capachica.turismokotlin.ui.viewmodel.PlanTuristicoViewModel
import com.capachica.turismokotlin.ui.viewmodel.ReservaViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.rememberDatePickerState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaFormScreen(
    planId: Long,
    onSuccess: () -> Unit,
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val planViewModel: PlanTuristicoViewModel = viewModel(factory = factory)
    val reservaViewModel: ReservaViewModel = viewModel(factory = factory)
    
    val planState by planViewModel.planState.collectAsState()
    val createState by reservaViewModel.createState.collectAsState()
    
    val scope = rememberCoroutineScope()
    
    // Estados del formulario
    var fechaInicio by remember { mutableStateOf("") }
    var numeroPersonas by remember { mutableStateOf("1") }
    var observaciones by remember { mutableStateOf("") }
    var solicitudesEspeciales by remember { mutableStateOf("") }
    var contactoEmergencia by remember { mutableStateOf("") }
    var telefonoEmergencia by remember { mutableStateOf("") }
    var metodoPago by remember { mutableStateOf<MetodoPago?>(null) }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showMetodoPagoDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val datePickerState = rememberDatePickerState()
    // Cargar datos del plan y limpiar estados
    LaunchedEffect(planId) {
        planViewModel.getPlanById(planId)
        reservaViewModel.clearStates()
    }
    
    // Manejar resultado de la reserva
    LaunchedEffect(createState) {
        when (val state = createState) {
            is Result.Success -> {
                onSuccess()
            }
            is Result.Error -> {
                errorMessage = state.message
                showErrorDialog = true
            }
            else -> {}
        }
    }
    
    // Diálogos
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(onClick = { showErrorDialog = false }) {
                    Text("Entendido")
                }
            }
        )
    }
    
    if (showMetodoPagoDialog) {
        MetodoPagoDialog(
            onMethodSelected = { metodo ->
                metodoPago = metodo
                showMetodoPagoDialog = false
            },
            onDismiss = { showMetodoPagoDialog = false }
        )
    }
    
    Scaffold(
        topBar = {
            TurismoAppBar(
                title = "Nueva Reserva",
                onBackClick = onBack
            )
        }
    ) { paddingValues ->
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    Button(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            fechaInicio = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                        }
                        showDatePicker = false
                    }) {
                        Text("Seleccionar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
        when (val state = planState) {
            is Result.Loading -> LoadingScreen()
            is Result.Error -> ErrorScreen(
                message = state.message,
                onRetry = { planViewModel.getPlanById(planId) }
            )
            is Result.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Información del plan
                    item {
                        PlanSummaryCard(plan = state.data)
                    }
                    
                    // Formulario de reserva
                    item {
                        ReservaFormCard(
                            plan = state.data,
                            fechaInicio = fechaInicio,
                            onFechaInicioChange = { fechaInicio = it },
                            numeroPersonas = numeroPersonas,
                            onNumeroPersonasChange = { numeroPersonas = it },
                            observaciones = observaciones,
                            onObservacionesChange = { observaciones = it },
                            solicitudesEspeciales = solicitudesEspeciales,
                            onSolicitudesEspecialesChange = { solicitudesEspeciales = it },
                            contactoEmergencia = contactoEmergencia,
                            onContactoEmergenciaChange = { contactoEmergencia = it },
                            telefonoEmergencia = telefonoEmergencia,
                            onTelefonoEmergenciaChange = { telefonoEmergencia = it },
                            metodoPago = metodoPago,
                            onShowMetodoPagoDialog = { showMetodoPagoDialog = true },
                            onShowDatePicker = { showDatePicker = true }
                        )
                    }
                    
                    // Resumen de precio
                    item {
                        PriceCard(
                            plan = state.data,
                            numeroPersonas = numeroPersonas.toIntOrNull() ?: 1
                        )
                    }
                    
                    // Botón confirmar
                    item {
                        Button(
                            onClick = {
                                scope.launch {
                                    val request = ReservaRequest(
                                        planId = planId,
                                        fechaInicio = fechaInicio,
                                        numeroPersonas = numeroPersonas.toIntOrNull() ?: 1,
                                        observaciones = observaciones.takeIf { it.isNotEmpty() },
                                        solicitudesEspeciales = solicitudesEspeciales.takeIf { it.isNotEmpty() },
                                        contactoEmergencia = contactoEmergencia.takeIf { it.isNotEmpty() },
                                        telefonoEmergencia = telefonoEmergencia.takeIf { it.isNotEmpty() },
                                        metodoPago = metodoPago
                                    )
                                    reservaViewModel.createReserva(request)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = fechaInicio.isNotEmpty() && 
                                    numeroPersonas.isNotEmpty() && 
                                    (numeroPersonas.toIntOrNull() ?: 0) > 0 &&
                                    createState !is Result.Loading
                        ) {
                            if (createState is Result.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Confirmar Reserva")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlanSummaryCard(plan: PlanTuristico) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = plan.nombre,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${plan.duracionDias} día${if (plan.duracionDias > 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Máx. ${plan.capacidadMaxima}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun ReservaFormCard(
    plan: PlanTuristico,
    fechaInicio: String,
    onFechaInicioChange: (String) -> Unit,
    numeroPersonas: String,
    onNumeroPersonasChange: (String) -> Unit,
    observaciones: String,
    onObservacionesChange: (String) -> Unit,
    solicitudesEspeciales: String,
    onSolicitudesEspecialesChange: (String) -> Unit,
    contactoEmergencia: String,
    onContactoEmergenciaChange: (String) -> Unit,
    telefonoEmergencia: String,
    onTelefonoEmergenciaChange: (String) -> Unit,
    metodoPago: MetodoPago?,
    onShowMetodoPagoDialog: () -> Unit,
    onShowDatePicker: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Datos de la Reserva",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Fecha de inicio
            OutlinedTextField(
                value = fechaInicio,
                onValueChange = onFechaInicioChange,
                label = { Text("Fecha de Inicio") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = onShowDatePicker) {
                        Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Número de personas
            OutlinedTextField(
                value = numeroPersonas,
                onValueChange = onNumeroPersonasChange,
                label = { Text("Número de Personas") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            
            // Método de pago
            OutlinedTextField(
                value = metodoPago?.name ?: "",
                onValueChange = { },
                label = { Text("Método de Pago (Opcional)") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = onShowMetodoPagoDialog) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Seleccionar método")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Contacto de emergencia
            OutlinedTextField(
                value = contactoEmergencia,
                onValueChange = onContactoEmergenciaChange,
                label = { Text("Contacto de Emergencia (Opcional)") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = telefonoEmergencia,
                onValueChange = onTelefonoEmergenciaChange,
                label = { Text("Teléfono de Emergencia (Opcional)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            
            // Observaciones
            OutlinedTextField(
                value = observaciones,
                onValueChange = onObservacionesChange,
                label = { Text("Observaciones (Opcional)") },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Solicitudes especiales
            OutlinedTextField(
                value = solicitudesEspeciales,
                onValueChange = onSolicitudesEspecialesChange,
                label = { Text("Solicitudes Especiales (Opcional)") },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PriceCard(
    plan: PlanTuristico,
    numeroPersonas: Int
) {
    val total = plan.precioTotal * numeroPersonas
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Resumen de Precio",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Precio por persona:")
                Text("$${plan.precioTotal}")
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Número de personas:")
                Text("$numeroPersonas")
            }
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
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
                    text = "$$total",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun MetodoPagoDialog(
    onMethodSelected: (MetodoPago) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Método de Pago") },
        text = {
            Column {
                MetodoPago.values().forEach { metodo ->
                    TextButton(
                        onClick = { onMethodSelected(metodo) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = metodo.name,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}