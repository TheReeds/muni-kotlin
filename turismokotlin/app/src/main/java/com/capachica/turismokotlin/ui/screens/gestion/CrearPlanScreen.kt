// CrearPlanScreen.kt
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
import com.capachica.turismokotlin.ui.viewmodel.CrearPlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearPlanScreen(
    onNavigateBack: () -> Unit,
    onPlanCreado: () -> Unit,
    viewModel: CrearPlanViewModel = hiltViewModel()
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

    val serviciosSeleccionados = remember { mutableStateListOf<ServicioPlanRequest>() }

    LaunchedEffect(Unit) {
        viewModel.loadMunicipalidades()
        viewModel.loadServiciosDisponibles()
    }

    LaunchedEffect(uiState.planCreado) {
        if (uiState.planCreado != null) {
            onPlanCreado()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Crear Plan Turístico") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
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

                            viewModel.crearPlan(
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
                    Text("Crear Plan")
                }
            }
        )

        if (uiState.error != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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

            // Servicios incluidos
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
                            text = "No hay servicios agregados. Agrega servicios para crear el itinerario del plan.",
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

            // Detalles adicionales
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

    // Dialogs
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
                municipalidadId = municipalidad.id  // Esto ahora funcionará correctamente
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicioPlanItem(
    servicio: Servicio,
    servicioPlan: ServicioPlanRequest,
    onEdit: (ServicioPlanRequest) -> Unit,
    onRemove: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = servicio.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Día ${servicioPlan.diaDelPlan} • Orden ${servicioPlan.ordenEnElDia}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${servicioPlan.horaInicio} - ${servicioPlan.horaFin}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (servicioPlan.precioEspecial != servicio.precio) {
                    Text(
                        text = "Precio especial: S/ ${servicioPlan.precioEspecial}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row {
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }

    if (showEditDialog) {
        EditarServicioPlanDialog(
            servicio = servicio,
            servicioPlan = servicioPlan,
            onDismiss = { showEditDialog = false },
            onConfirm = { nuevoServicioPlan ->
                onEdit(nuevoServicioPlan)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun NivelDificultadDialog(
    onDismiss: () -> Unit,
    onSelected: (NivelDificultad) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Nivel de Dificultad") },
        text = {
            Column {
                NivelDificultad.values().forEach { nivel ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        onClick = { onSelected(nivel) }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                getNivelDificultadIcon(nivel),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = getNivelDificultadColor(nivel)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(getNivelDificultadText(nivel))
                        }
                    }
                }
            }
        },
        confirmButton = { },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun MunicipalidadDialog(
    municipalidades: List<MunicipalidadDetallada>,
    onDismiss: () -> Unit,
    onSelected: (MunicipalidadDetallada) -> Unit  // Cambiar de Municipalidad a MunicipalidadDetallada
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Municipalidad") },
        text = {
            LazyColumn(modifier = Modifier.height(300.dp)) {
                items(municipalidades) { municipalidad ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        onClick = { onSelected(municipalidad) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = municipalidad.nombre,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${municipalidad.distrito}, ${municipalidad.provincia}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = { },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}


@Composable
fun AgregarServicioDialog(
    serviciosDisponibles: List<Servicio>,
    serviciosYaAgregados: List<Long>,
    onDismiss: () -> Unit,
    onServicioAgregado: (ServicioPlanRequest) -> Unit
) {
    val serviciosLibres = serviciosDisponibles.filter { servicio ->
        servicio.id !in serviciosYaAgregados
    }

    var servicioSeleccionado by remember { mutableStateOf<Servicio?>(null) }
    var diaDelPlan by remember { mutableStateOf("1") }
    var ordenEnElDia by remember { mutableStateOf("1") }
    var horaInicio by remember { mutableStateOf("09:00") }
    var horaFin by remember { mutableStateOf("10:00") }
    var precioEspecial by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var esOpcional by remember { mutableStateOf(false) }
    var esPersonalizable by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Servicio al Plan") },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Seleccionar Servicio:")
                    LazyColumn(modifier = Modifier.height(120.dp)) {
                        items(serviciosLibres) { servicio ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                onClick = {
                                    servicioSeleccionado = servicio
                                    precioEspecial = servicio.precio.toString()
                                },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (servicioSeleccionado?.id == servicio.id)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = servicio.nombre,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = "S/ ${servicio.precio}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                if (servicioSeleccionado != null) {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = diaDelPlan,
                                onValueChange = { diaDelPlan = it },
                                label = { Text("Día") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            OutlinedTextField(
                                value = ordenEnElDia,
                                onValueChange = { ordenEnElDia = it },
                                label = { Text("Orden") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = horaInicio,
                                onValueChange = { horaInicio = it },
                                label = { Text("Hora inicio") },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("HH:MM") }
                            )
                            OutlinedTextField(
                                value = horaFin,
                                onValueChange = { horaFin = it },
                                label = { Text("Hora fin") },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("HH:MM") }
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = precioEspecial,
                            onValueChange = { precioEspecial = it },
                            label = { Text("Precio especial") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            supportingText = { Text("Precio original: S/ ${servicioSeleccionado!!.precio}") }
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = notas,
                            onValueChange = { notas = it },
                            label = { Text("Notas") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = esOpcional,
                                onCheckedChange = { esOpcional = it }
                            )
                            Text("Es opcional")
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = esPersonalizable,
                                onCheckedChange = { esPersonalizable = it }
                            )
                            Text("Es personalizable")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    servicioSeleccionado?.let { servicio ->
                        val servicioPlan = ServicioPlanRequest(
                            servicioId = servicio.id,
                            diaDelPlan = diaDelPlan.toIntOrNull() ?: 1,
                            ordenEnElDia = ordenEnElDia.toIntOrNull() ?: 1,
                            horaInicio = horaInicio,
                            horaFin = horaFin,
                            precioEspecial = precioEspecial.toDoubleOrNull() ?: servicio.precio,
                            notas = notas.takeIf { it.isNotBlank() },
                            esOpcional = esOpcional,
                            esPersonalizable = esPersonalizable
                        )
                        onServicioAgregado(servicioPlan)
                    }
                },
                enabled = servicioSeleccionado != null &&
                        diaDelPlan.toIntOrNull() != null &&
                        ordenEnElDia.toIntOrNull() != null &&
                        horaInicio.isNotBlank() &&
                        horaFin.isNotBlank() &&
                        precioEspecial.toDoubleOrNull() != null
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun EditarServicioPlanDialog(
    servicio: Servicio,
    servicioPlan: ServicioPlanRequest,
    onDismiss: () -> Unit,
    onConfirm: (ServicioPlanRequest) -> Unit
) {
    var diaDelPlan by remember { mutableStateOf(servicioPlan.diaDelPlan.toString()) }
    var ordenEnElDia by remember { mutableStateOf(servicioPlan.ordenEnElDia.toString()) }
    var horaInicio by remember { mutableStateOf(servicioPlan.horaInicio) }
    var horaFin by remember { mutableStateOf(servicioPlan.horaFin) }
    var precioEspecial by remember { mutableStateOf(servicioPlan.precioEspecial.toString()) }
    var notas by remember { mutableStateOf(servicioPlan.notas ?: "") }
    var esOpcional by remember { mutableStateOf(servicioPlan.esOpcional) }
    var esPersonalizable by remember { mutableStateOf(servicioPlan.esPersonalizable) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar ${servicio.nombre}") },
        text = {
            LazyColumn(
                modifier = Modifier.height(400.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = diaDelPlan,
                            onValueChange = { diaDelPlan = it },
                            label = { Text("Día") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = ordenEnElDia,
                            onValueChange = { ordenEnElDia = it },
                            label = { Text("Orden") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = horaInicio,
                            onValueChange = { horaInicio = it },
                            label = { Text("Hora inicio") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = horaFin,
                            onValueChange = { horaFin = it },
                            label = { Text("Hora fin") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = precioEspecial,
                        onValueChange = { precioEspecial = it },
                        label = { Text("Precio especial") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }

                item {
                    OutlinedTextField(
                        value = notas,
                        onValueChange = { notas = it },
                        label = { Text("Notas") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                }

                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = esOpcional,
                            onCheckedChange = { esOpcional = it }
                        )
                        Text("Es opcional")
                    }
                }

                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = esPersonalizable,
                            onCheckedChange = { esPersonalizable = it }
                        )
                        Text("Es personalizable")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val nuevoServicioPlan = ServicioPlanRequest(
                        servicioId = servicioPlan.servicioId,
                        diaDelPlan = diaDelPlan.toIntOrNull() ?: servicioPlan.diaDelPlan,
                        ordenEnElDia = ordenEnElDia.toIntOrNull() ?: servicioPlan.ordenEnElDia,
                        horaInicio = horaInicio,
                        horaFin = horaFin,
                        precioEspecial = precioEspecial.toDoubleOrNull() ?: servicioPlan.precioEspecial,
                        notas = notas.takeIf { it.isNotBlank() },
                        esOpcional = esOpcional,
                        esPersonalizable = esPersonalizable
                    )
                    onConfirm(nuevoServicioPlan)
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

fun getNivelDificultadText(nivel: NivelDificultad): String {
    return when (nivel) {
        NivelDificultad.FACIL -> "Fácil"
        NivelDificultad.MODERADO -> "Intermedio"
        NivelDificultad.DIFICIL -> "Avanzado"
        NivelDificultad.EXTREMO -> "Experto"
    }
}

private fun getNivelDificultadIcon(nivel: NivelDificultad): androidx.compose.ui.graphics.vector.ImageVector {
    return when (nivel) {
        NivelDificultad.FACIL -> Icons.Default.SentimentSatisfied
        NivelDificultad.MODERADO -> Icons.Default.SentimentNeutral
        NivelDificultad.DIFICIL -> Icons.Default.SentimentDissatisfied
        NivelDificultad.EXTREMO -> Icons.Default.Warning
    }
}

@Composable
private fun getNivelDificultadColor(nivel: NivelDificultad): androidx.compose.ui.graphics.Color {
    return when (nivel) {
        NivelDificultad.FACIL -> MaterialTheme.colorScheme.primary
        NivelDificultad.MODERADO -> MaterialTheme.colorScheme.secondary
        NivelDificultad.DIFICIL -> MaterialTheme.colorScheme.tertiary
        NivelDificultad.EXTREMO -> MaterialTheme.colorScheme.error
    }
}