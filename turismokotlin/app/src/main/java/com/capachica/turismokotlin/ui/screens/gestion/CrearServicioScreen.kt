// CrearServicioScreen.kt
package com.capachica.turismokotlin.ui.screens.gestion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.capachica.turismokotlin.data.model.*
import com.capachica.turismokotlin.ui.viewmodel.CrearServicioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearServicioScreen(
    onNavigateBack: () -> Unit,
    onServicioCreado: () -> Unit,
    viewModel: CrearServicioViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var duracionHoras by remember { mutableStateOf("") }
    var capacidadMaxima by remember { mutableStateOf("") }
    var tipoSeleccionado by remember { mutableStateOf<TipoServicio?>(null) }
    var ubicacion by remember { mutableStateOf("") }
    var latitud by remember { mutableStateOf("") }
    var longitud by remember { mutableStateOf("") }
    var requisitos by remember { mutableStateOf("") }
    var incluye by remember { mutableStateOf("") }
    var noIncluye by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf("") }

    var showTipoDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.servicioCreado) {
        if (uiState.servicioCreado != null) {
            onServicioCreado()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Crear Servicio") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                Button(
                    onClick = {
                        val precioDouble = precio.toDoubleOrNull() ?: 0.0
                        val duracion = duracionHoras.toIntOrNull() ?: 0
                        val capacidad = capacidadMaxima.toIntOrNull() ?: 0
                        val lat = latitud.toDoubleOrNull()
                        val lng = longitud.toDoubleOrNull()

                        if (nombre.isNotBlank() &&
                            descripcion.isNotBlank() &&
                            precioDouble > 0 &&
                            duracion > 0 &&
                            capacidad > 0 &&
                            tipoSeleccionado != null &&
                            ubicacion.isNotBlank()) {

                            viewModel.crearServicio(
                                nombre = nombre,
                                descripcion = descripcion,
                                precio = precioDouble,
                                duracionHoras = duracion,
                                capacidadMaxima = capacidad,
                                tipo = tipoSeleccionado!!,
                                ubicacion = ubicacion,
                                latitud = lat,
                                longitud = lng,
                                requisitos = requisitos.takeIf { it.isNotBlank() },
                                incluye = incluye.takeIf { it.isNotBlank() },
                                noIncluye = noIncluye.takeIf { it.isNotBlank() },
                                imagenUrl = imagenUrl.takeIf { it.isNotBlank() }
                            )
                        }
                    },
                    enabled = !uiState.isLoading &&
                            nombre.isNotBlank() &&
                            descripcion.isNotBlank() &&
                            precio.toDoubleOrNull() != null &&
                            duracionHoras.toIntOrNull() != null &&
                            capacidadMaxima.toIntOrNull() != null &&
                            tipoSeleccionado != null &&
                            ubicacion.isNotBlank()
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Crear")
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
                        label = { Text("Nombre del servicio *") },
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

                    // Tipo de servicio
                    OutlinedTextField(
                        value = tipoSeleccionado?.let { getTipoServicioText(it) } ?: "",
                        onValueChange = { },
                        label = { Text("Tipo de servicio *") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showTipoDialog = true }) {
                                Icon(Icons.Default.ExpandMore, contentDescription = "Seleccionar")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = precio,
                            onValueChange = { precio = it },
                            label = { Text("Precio (S/) *") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = duracionHoras,
                            onValueChange = { duracionHoras = it },
                            label = { Text("Duración (horas) *") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = capacidadMaxima,
                        onValueChange = { capacidadMaxima = it },
                        label = { Text("Capacidad máxima *") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            }

            // Ubicación
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ubicación",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = ubicacion,
                        onValueChange = { ubicacion = it },
                        label = { Text("Ubicación *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = latitud,
                            onValueChange = { latitud = it },
                            label = { Text("Latitud (opcional)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = longitud,
                            onValueChange = { longitud = it },
                            label = { Text("Longitud (opcional)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true
                        )
                    }
                }
            }

            // Detalles adicionales
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Detalles Adicionales",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = incluye,
                        onValueChange = { incluye = it },
                        label = { Text("¿Qué incluye?") },
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
                        value = imagenUrl,
                        onValueChange = { imagenUrl = it },
                        label = { Text("URL de imagen") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        }
    }

    // Dialog de tipo de servicio
    if (showTipoDialog) {
        TipoServicioDialog(
            onDismiss = { showTipoDialog = false },
            onTipoSelected = { tipo ->
                tipoSeleccionado = tipo
                showTipoDialog = false
            }
        )
    }
}

@Composable
fun TipoServicioDialog(
    onDismiss: () -> Unit,
    onTipoSelected: (TipoServicio) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Tipo de Servicio") },
        text = {
            Column {
                TipoServicio.values().forEach { tipo ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        onClick = { onTipoSelected(tipo) }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                getTipoServicioIcon(tipo),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(getTipoServicioText(tipo))
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

fun getTipoServicioText(tipo: TipoServicio): String {
    return when (tipo) {
        TipoServicio.ALOJAMIENTO -> "Alojamiento"
        TipoServicio.TRANSPORTE -> "Transporte"
        TipoServicio.ALIMENTACION -> "Alimentación"
        TipoServicio.GUIA_TURISTICO -> "Guía turística"
        TipoServicio.ACTIVIDAD_RECREATIVA -> "Recreación"
        TipoServicio.CULTURAL -> "Cultural"
        TipoServicio.AVENTURA -> "Aventura"
        TipoServicio.WELLNESS -> "Bienestar"
        TipoServicio.TOUR -> "Tour"
        TipoServicio.GASTRONOMICO -> "Gastronómico"
        TipoServicio.OTRO -> "Otro"
    }
}

private fun getTipoServicioIcon(tipo: TipoServicio): androidx.compose.ui.graphics.vector.ImageVector {
    return when (tipo) {
        TipoServicio.ALOJAMIENTO -> Icons.Default.Hotel
        TipoServicio.TRANSPORTE -> Icons.Default.DirectionsBus
        TipoServicio.ALIMENTACION -> Icons.Default.Restaurant
        TipoServicio.GUIA_TURISTICO -> Icons.Default.Person
        TipoServicio.ACTIVIDAD_RECREATIVA -> Icons.Default.SportsEsports
        TipoServicio.CULTURAL -> Icons.Default.Museum
        TipoServicio.AVENTURA -> Icons.Default.Hiking
        TipoServicio.WELLNESS -> Icons.Default.Favorite
        TipoServicio.TOUR -> Icons.Default.Map
        TipoServicio.GASTRONOMICO -> Icons.Default.LocalDining
        TipoServicio.OTRO -> Icons.Default.Category
    }
}
