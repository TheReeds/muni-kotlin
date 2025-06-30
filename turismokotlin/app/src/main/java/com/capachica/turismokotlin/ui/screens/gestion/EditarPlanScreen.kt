package com.capachica.turismokotlin.ui.screens.gestion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.capachica.turismokotlin.data.model.*
import com.capachica.turismokotlin.ui.viewmodel.EditarPlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPlanScreen(
    planId: Long,
    onNavigateBack: () -> Unit,
    onPlanActualizado: () -> Unit,
    viewModel: EditarPlanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var duracionDias by remember { mutableStateOf("") }
    var capacidadMaxima by remember { mutableStateOf("") }
    var nivelDificultad by remember { mutableStateOf<NivelDificultad?>(null) }
    var municipalidadId by remember { mutableStateOf<Long?>(null) }
    var imagenPrincipalUrl by remember { mutableStateOf("") }
    var itinerario by remember { mutableStateOf("") }
    var incluye by remember { mutableStateOf("") }
    var noIncluye by remember { mutableStateOf("") }
    var recomendaciones by remember { mutableStateOf("") }
    var requisitos by remember { mutableStateOf("") }

    var showDificultadDialog by remember { mutableStateOf(false) }
    var showMunicipalidadDialog by remember { mutableStateOf(false) }
    var showServiciosDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDataLoaded by remember { mutableStateOf(false) }

    val serviciosSeleccionados = remember { mutableStateListOf<ServicioPlanRequest>() }

    // Cargar datos
    LaunchedEffect(planId) {
        viewModel.loadPlan(planId)
        viewModel.loadMunicipalidades()
        viewModel.loadServiciosDisponibles()
    }

    // Llenar formulario cuando se carguen los datos
    LaunchedEffect(uiState.plan) {
        uiState.plan?.let { plan ->
            if (!isDataLoaded) {
                nombre = plan.nombre
                descripcion = plan.descripcion
                duracionDias = plan.duracionDias.toString()
                capacidadMaxima = plan.capacidadMaxima.toString()
                nivelDificultad = plan.nivelDificultad
                municipalidadId = plan.municipalidad.id
                imagenPrincipalUrl = plan.imagenPrincipalUrl ?: ""
                itinerario = plan.itinerario ?: ""
                incluye = plan.incluye ?: ""
                noIncluye = plan.noIncluye ?: ""
                recomendaciones = plan.recomendaciones ?: ""
                requisitos = plan.requisitos ?: ""

                // Cargar servicios del plan
                serviciosSeleccionados.clear()
                serviciosSeleccionados.addAll(
                    plan.servicios.map { servicioPlan ->
                        ServicioPlanRequest(
                            servicioId = servicioPlan.servicio.id,
                            diaDelPlan = servicioPlan.diaDelPlan,
                            ordenEnElDia = servicioPlan.ordenEnElDia,
                            horaInicio = servicioPlan.horaInicio,
                            horaFin = servicioPlan.horaFin,
                            precioEspecial = servicioPlan.precioEspecial,
                            notas = servicioPlan.notas,
                            esOpcional = servicioPlan.esOpcional,
                            esPersonalizable = servicioPlan.esPersonalizable
                        )
                    }
                )
                isDataLoaded = true
            }
        }
    }

    LaunchedEffect(uiState.planActualizado, uiState.planEliminado) {
        if (uiState.planActualizado != null || uiState.planEliminado) {
            onPlanActualizado()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Editar Plan") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                // Botón eliminar
                IconButton(
                    onClick = { showDeleteDialog = true },
                    enabled = !uiState.isLoading
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar plan",
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                // Botón guardar
                Button(
                    onClick = {
                        val duracion = duracionDias.toIntOrNull() ?: 0
                        val capacidad = capacidadMaxima.toIntOrNull() ?: 0

                        if (nombre.isNotBlank() &&
                            descripcion.isNotBlank() &&
                            duracion > 0 &&
                            capacidad > 0 &&
                            nivelDificultad != null &&
                            municipalidadId != null) {

                            viewModel.actualizarPlan(
                                planId = planId,
                                nombre = nombre,
                                descripcion = descripcion,
                                duracionDias = duracion,
                                capacidadMaxima = capacidad,
                                nivelDificultad = nivelDificultad!!,
                                municipalidadId = municipalidadId!!,
                                imagenPrincipalUrl = imagenPrincipalUrl.takeIf { it.isNotBlank() },
                                itinerario = itinerario.takeIf { it.isNotBlank() },
                                incluye = incluye.takeIf { it.isNotBlank() },
                                noIncluye = noIncluye.takeIf { it.isNotBlank() },
                                recomendaciones = recomendaciones.takeIf { it.isNotBlank() },
                                requisitos = requisitos.takeIf { it.isNotBlank() },
                                servicios = serviciosSeleccionados.toList()
                            )
                        }
                    },
                    enabled = !uiState.isLoading &&
                            nombre.isNotBlank() &&
                            descripcion.isNotBlank() &&
                            duracionDias.toIntOrNull() != null &&
                            capacidadMaxima.toIntOrNull() != null &&
                            nivelDificultad != null &&
                            municipalidadId != null
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Guardar")
                }
            }
        )

        when {
            uiState.isLoadingPlan -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Error: ${uiState.error}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(onClick = { viewModel.loadPlan(planId) }) {
                        Text("Reintentar")
                    }
                }
            }

            uiState.plan != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Estado del plan
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = when (uiState.plan!!.estado) {
                                EstadoPlan.ACTIVO -> MaterialTheme.colorScheme.primaryContainer
                                EstadoPlan.BORRADOR -> MaterialTheme.colorScheme.secondaryContainer
                                EstadoPlan.INACTIVO -> MaterialTheme.colorScheme.surfaceVariant
                                EstadoPlan.AGOTADO -> MaterialTheme.colorScheme.errorContainer
                                EstadoPlan.SUSPENDIDO -> MaterialTheme.colorScheme.tertiaryContainer
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                when (uiState.plan!!.estado) {
                                    EstadoPlan.ACTIVO -> Icons.Default.CheckCircle
                                    EstadoPlan.BORRADOR -> Icons.Default.Edit
                                    EstadoPlan.INACTIVO -> Icons.Default.Pause
                                    EstadoPlan.AGOTADO -> Icons.Default.Warning
                                    EstadoPlan.SUSPENDIDO -> Icons.Default.Block
                                },
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Estado: ${uiState.plan!!.estado.name}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                if (uiState.plan!!.totalReservas > 0) {
                                    Text(
                                        text = "${uiState.plan!!.totalReservas} reservas totales",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                    // Formulario (igual que CrearPlanScreen pero con datos precargados)
                    // Información básica
                    Card {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Información Básica",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = nombre,
                                onValueChange = { nombre = it },
                                label = { Text("Nombre del plan *") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = descripcion,
                                onValueChange = { descripcion = it },
                                label = { Text("Descripción *") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 4
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = duracionDias,
                                    onValueChange = { duracionDias = it },
                                    label = { Text("Duración (días) *") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = capacidadMaxima,
                                    onValueChange = { capacidadMaxima = it },
                                    label = { Text("Capacidad máxima *") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Nivel de dificultad
                            OutlinedTextField(
                                value = nivelDificultad?.let { getNivelDificultadText(it) } ?: "",
                                onValueChange = { },
                                label = { Text("Nivel de dificultad *") },
                                modifier = Modifier.fillMaxWidth(),
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { showDificultadDialog = true }) {
                                        Icon(Icons.Default.ExpandMore, contentDescription = "Seleccionar")
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Municipalidad
                            OutlinedTextField(
                                value = uiState.municipalidades.find { it.id == municipalidadId }?.nombre ?: "",
                                onValueChange = { },
                                label = { Text("Municipalidad *") },
                                modifier = Modifier.fillMaxWidth(),
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { showMunicipalidadDialog = true }) {
                                        Icon(Icons.Default.ExpandMore, contentDescription = "Seleccionar")
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = imagenPrincipalUrl,
                                onValueChange = { imagenPrincipalUrl = it },
                                label = { Text("URL de imagen principal") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }

                    // Servicios incluidos (misma lógica que CrearPlanScreen)
                    Card {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Servicios del Plan",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                OutlinedButton(
                                    onClick = { showServiciosDialog = true }
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Agregar")
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            if (serviciosSeleccionados.isEmpty()) {
                                Text(
                                    text = "No hay servicios agregados.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                serviciosSeleccionados.forEachIndexed { index, servicioPlan ->
                                    val servicio = uiState.serviciosDisponibles.find { it.id == servicioPlan.servicioId }
                                    if (servicio != null) {
                                        ServicioPlanItem(
                                            servicio = servicio,
                                            servicioPlan = servicioPlan,
                                            onEdit = { nuevoServicioPlan ->
                                                serviciosSeleccionados[index] = nuevoServicioPlan
                                            },
                                            onRemove = {
                                                serviciosSeleccionados.removeAt(index)
                                            }
                                        )
                                        if (index < serviciosSeleccionados.size - 1) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Detalles adicionales (igual que CrearPlanScreen)
                    Card {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Detalles del Plan",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = itinerario,
                                onValueChange = { itinerario = it },
                                label = { Text("Itinerario detallado") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 5
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = incluye,
                                onValueChange = { incluye = it },
                                label = { Text("¿Qué incluye el plan?") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = noIncluye,
                                onValueChange = { noIncluye = it },
                                label = { Text("¿Qué NO incluye?") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = requisitos,
                                onValueChange = { requisitos = it },
                                label = { Text("Requisitos") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = recomendaciones,
                                onValueChange = { recomendaciones = it },
                                label = { Text("Recomendaciones") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialogs (iguales que CrearPlanScreen)
    if (showDificultadDialog) {
        NivelDificultadDialog(
            onDismiss = { showDificultadDialog = false },
            onSelected = { nivel ->
                nivelDificultad = nivel
                showDificultadDialog = false
            }
        )
    }

    if (showMunicipalidadDialog) {
        MunicipalidadDialog(
            municipalidades = uiState.municipalidades,
            onDismiss = { showMunicipalidadDialog = false },
            onSelected = { municipalidad ->
                municipalidadId = municipalidad.id
                showMunicipalidadDialog = false
            }
        )
    }

    if (showServiciosDialog) {
        AgregarServicioDialog(
            serviciosDisponibles = uiState.serviciosDisponibles,
            serviciosYaAgregados = serviciosSeleccionados.map { it.servicioId },
            onDismiss = { showServiciosDialog = false },
            onServicioAgregado = { servicioPlan ->
                serviciosSeleccionados.add(servicioPlan)
                showServiciosDialog = false
            }
        )
    }

    // Dialog de confirmación de eliminación
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Plan") },
            text = {
                Text("¿Estás seguro de que deseas eliminar este plan? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.eliminarPlan(planId)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}