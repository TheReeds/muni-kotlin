package com.capachica.turismokotlin.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay

data class LocationSuggestion(
    val id: String,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPicker(
    currentLocation: Pair<Double, Double>? = null,
    onLocationSelected: (Double, Double, String) -> Unit,
    onDismiss: () -> Unit,
    onUseCurrentLocation: (() -> Unit)? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf(currentLocation) }
    var locationName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var suggestions by remember { mutableStateOf<List<LocationSuggestion>>(emptyList()) }
    
    // Coordenadas manuales
    var manualLatitude by remember { mutableStateOf(currentLocation?.first?.toString() ?: "") }
    var manualLongitude by remember { mutableStateOf(currentLocation?.second?.toString() ?: "") }
    var showManualEntry by remember { mutableStateOf(false) }

    // Simular búsqueda de ubicaciones (en un caso real sería una API como Google Places)
    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 3) {
            isLoading = true
            delay(500) // Simular delay de API
            
            // Sugerencias de ejemplo para demostración
            suggestions = listOf(
                LocationSuggestion("1", "Cusco, Peru", "Ciudad histórica", -13.5319, -71.9675),
                LocationSuggestion("2", "Lima, Peru", "Capital del Perú", -12.0464, -77.0428),
                LocationSuggestion("3", "Arequipa, Peru", "Ciudad Blanca", -16.4090, -71.5375),
                LocationSuggestion("4", "Trujillo, Peru", "Ciudad de la eterna primavera", -8.1116, -79.0290),
                LocationSuggestion("5", "Iquitos, Peru", "Puerta de entrada a la Amazonía", -3.7437, -73.2516)
            ).filter { 
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
            }
            isLoading = false
        } else {
            suggestions = emptyList()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Seleccionar Ubicación",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para usar ubicación actual con GeolocationHelper
                if (onUseCurrentLocation != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MyLocation,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Usar mi ubicación actual",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            
                            GeolocationHelper(
                                onLocationObtained = { lat, lng ->
                                    selectedLocation = Pair(lat, lng)
                                    locationName = "Mi ubicación actual"
                                },
                                onError = { error ->
                                    // Podrías mostrar un snackbar o mensaje de error aquí
                                },
                                onPermissionDenied = {
                                    // Podrías mostrar un mensaje explicando por qué necesitas permisos
                                }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Búsqueda de ubicación
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Buscar ubicación") },
                    placeholder = { Text("Ej: Cusco, Lima, Arequipa...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de sugerencias
                if (suggestions.isNotEmpty()) {
                    Text(
                        text = "Sugerencias:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(suggestions) { suggestion ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedLocation = Pair(suggestion.latitude, suggestion.longitude)
                                        locationName = suggestion.name
                                        searchQuery = suggestion.name
                                        suggestions = emptyList()
                                    }
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Text(
                                        text = suggestion.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = suggestion.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                    Text(
                                        text = "${suggestion.latitude}, ${suggestion.longitude}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Entrada manual de coordenadas
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Coordenadas manuales",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                TextButton(
                                    onClick = { showManualEntry = !showManualEntry }
                                ) {
                                    Text(if (showManualEntry) "Ocultar" else "Mostrar")
                                }
                            }
                            
                            if (showManualEntry) {
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = manualLatitude,
                                        onValueChange = { manualLatitude = it },
                                        modifier = Modifier.weight(1f),
                                        label = { Text("Latitud") },
                                        placeholder = { Text("-12.0464") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                    )
                                    
                                    OutlinedTextField(
                                        value = manualLongitude,
                                        onValueChange = { manualLongitude = it },
                                        modifier = Modifier.weight(1f),
                                        label = { Text("Longitud") },
                                        placeholder = { Text("-77.0428") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Button(
                                    onClick = {
                                        val lat = manualLatitude.toDoubleOrNull()
                                        val lng = manualLongitude.toDoubleOrNull()
                                        if (lat != null && lng != null) {
                                            selectedLocation = Pair(lat, lng)
                                            locationName = "Coordenadas: $lat, $lng"
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = manualLatitude.toDoubleOrNull() != null && 
                                             manualLongitude.toDoubleOrNull() != null
                                ) {
                                    Text("Usar coordenadas")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                }

                // Ubicación seleccionada
                selectedLocation?.let { (lat, lng) ->
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Ubicación seleccionada:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = locationName.takeIf { it.isNotBlank() } 
                                    ?: "Coordenadas: $lat, $lng",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Lat: ${String.format("%.6f", lat)}, Lng: ${String.format("%.6f", lng)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                    
                    Button(
                        onClick = {
                            selectedLocation?.let { (lat, lng) ->
                                val name = locationName.takeIf { it.isNotBlank() }
                                    ?: "Ubicación seleccionada"
                                onLocationSelected(lat, lng, name)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = selectedLocation != null
                    ) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }
}