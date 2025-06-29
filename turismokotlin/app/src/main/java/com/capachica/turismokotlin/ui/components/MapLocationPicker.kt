package com.capachica.turismokotlin.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capachica.turismokotlin.data.model.UbicacionRequest
import com.capachica.turismokotlin.data.model.ValidacionCoordenadaResponse
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.viewmodel.UbicacionViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory

// Nota: Este componente está preparado para Google Maps
// Necesitarás agregar las dependencias de Maps en build.gradle.kts:
// implementation "com.google.android.gms:play-services-maps:18.2.0"
// implementation "com.google.maps.android:maps-compose:4.3.0"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapLocationPicker(
    initialLatitud: Double? = null,
    initialLongitud: Double? = null,
    onLocationSelected: (UbicacionRequest) -> Unit,
    onDismiss: () -> Unit,
    factory: ViewModelFactory,
    modifier: Modifier = Modifier
) {
    val ubicacionViewModel: UbicacionViewModel = viewModel(factory = factory)
    val context = LocalContext.current
    
    var selectedLatitud by remember { mutableStateOf(initialLatitud ?: -15.8404) } // Default: Perú centro
    var selectedLongitud by remember { mutableStateOf(initialLongitud ?: -70.0219) }
    var direccionCompleta by remember { mutableStateOf("") }
    
    val validacionState by ubicacionViewModel.validacionState.collectAsState()
    
    // Validar coordenadas cuando cambien
    LaunchedEffect(selectedLatitud, selectedLongitud) {
        ubicacionViewModel.validarCoordenadas(selectedLatitud, selectedLongitud)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = {
            Text("Seleccionar Ubicación")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Placeholder para el mapa
                // TODO: Reemplazar con GoogleMap cuando se agregue la dependencia
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Mapa interactivo",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Configurar Google Maps SDK",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            // Mostrar coordenadas actuales
                            Text(
                                text = "Lat: ${String.format("%.6f", selectedLatitud)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Lng: ${String.format("%.6f", selectedLongitud)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
                
                // Controles manuales
                Text(
                    text = "Ajuste manual:",
                    style = MaterialTheme.typography.labelLarge
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = selectedLatitud.toString(),
                        onValueChange = { newValue ->
                            newValue.toDoubleOrNull()?.let { selectedLatitud = it }
                        },
                        label = { Text("Latitud") },
                        modifier = Modifier.weight(1f)
                    )
                    
                    OutlinedTextField(
                        value = selectedLongitud.toString(),
                        onValueChange = { newValue ->
                            newValue.toDoubleOrNull()?.let { selectedLongitud = it }
                        },
                        label = { Text("Longitud") },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                OutlinedTextField(
                    value = direccionCompleta,
                    onValueChange = { direccionCompleta = it },
                    label = { Text("Dirección (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Botones de ubicaciones predefinidas
                Text(
                    text = "Ubicaciones de referencia:",
                    style = MaterialTheme.typography.labelLarge
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = {
                            selectedLatitud = -12.0464
                            selectedLongitud = -77.0428
                            direccionCompleta = "Lima, Perú"
                        },
                        label = { Text("Lima") },
                        modifier = Modifier.weight(1f)
                    )
                    
                    AssistChip(
                        onClick = {
                            selectedLatitud = -13.5319
                            selectedLongitud = -71.9675
                            direccionCompleta = "Cusco, Perú"
                        },
                        label = { Text("Cusco") },
                        modifier = Modifier.weight(1f)
                    )
                    
                    AssistChip(
                        onClick = {
                            selectedLatitud = -15.8404
                            selectedLongitud = -70.0219
                            direccionCompleta = "Puno, Perú"
                        },
                        label = { Text("Puno") },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // Estado de validación
                when (validacionState) {
                    is Result.Loading -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp))
                            Text(
                                text = "Validando coordenadas...",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    is Result.Success -> {
                        if ((validacionState as Result.Success<ValidacionCoordenadaResponse>).data.esValida) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Ubicación válida",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = (validacionState as Result.Success<ValidacionCoordenadaResponse>).data.mensaje ?: "Coordenadas inválidas",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                    is Result.Error -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = (validacionState as Result.Error).message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    null -> {}
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val ubicacionRequest = UbicacionRequest(
                        latitud = selectedLatitud,
                        longitud = selectedLongitud,
                        direccionCompleta = if (direccionCompleta.isNotEmpty()) direccionCompleta else null
                    )
                    onLocationSelected(ubicacionRequest)
                },
                enabled = validacionState is Result.Success && (validacionState as Result.Success<ValidacionCoordenadaResponse>).data.esValida
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// TODO: Implementar con Google Maps
/*
@Composable
fun GoogleMapLocationPicker(
    initialLatitud: Double,
    initialLongitud: Double,
    onLocationSelected: (Double, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPosition by remember { 
        mutableStateOf(LatLng(initialLatitud, initialLongitud)) 
    }
    
    GoogleMap(
        modifier = modifier,
        cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(selectedPosition, 15f)
        },
        onMapClick = { latLng ->
            selectedPosition = latLng
            onLocationSelected(latLng.latitude, latLng.longitude)
        }
    ) {
        Marker(
            state = MarkerState(position = selectedPosition),
            title = "Ubicación seleccionada"
        )
    }
}
*/