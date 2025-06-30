package com.capachica.turismokotlin.ui.screens.map

import androidx.compose.foundation.clickable
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.capachica.turismokotlin.data.model.Servicio
import com.capachica.turismokotlin.ui.viewmodel.MapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEmprendedor: (Long) -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Ubicación por defecto (Centro de Perú)
    val defaultLocation = LatLng(-9.19, -75.0152)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 6f)
    }

    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedServicio by remember { mutableStateOf<Servicio?>(null) }

    // Obtener ubicación del usuario y servicios cercanos
    LaunchedEffect(Unit) {
        // Aquí puedes obtener la ubicación real del usuario
        viewModel.loadNearbyServices(-12.0464, -77.0428) // Lima como ejemplo
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Servicios Cercanos") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                IconButton(onClick = { /* Centrar en mi ubicación */ }) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Mi ubicación")
                }
            }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = true,
                    mapType = MapType.NORMAL
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false
                )
            ) {
                // Marcadores para servicios
                uiState.serviciosCercanos.forEach { servicio ->
                    if (servicio.latitud != null && servicio.longitud != null) {
                        Marker(
                            state = MarkerState(
                                position = LatLng(servicio.latitud, servicio.longitud)
                            ),
                            title = servicio.nombre,
                            snippet = servicio.emprendedor.nombreEmpresa,
                            onInfoWindowClick = {
                                selectedServicio = servicio
                                showBottomSheet = true
                            }
                        )
                    }
                }

                // Marcadores para emprendedores
                uiState.emprendedoresCercanos.forEach { emprendedor ->
                    if (emprendedor.latitud != null && emprendedor.longitud != null) {
                        Marker(
                            state = MarkerState(
                                position = LatLng(emprendedor.latitud, emprendedor.longitud)
                            ),
                            title = emprendedor.nombreEmpresa,
                            snippet = emprendedor.rubro,
                            onInfoWindowClick = {
                                onNavigateToEmprendedor(emprendedor.id)
                            }
                        )
                    }
                }
            }

            // Controles flotantes
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FloatingActionButton(
                    onClick = { /* Centrar en mi ubicación */ },
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Mi ubicación")
                }

                FloatingActionButton(
                    onClick = { /* Filtros */ },
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filtros")
                }
            }

            // Loading indicator
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Card {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }

    // Bottom Sheet para detalles del servicio
    if (showBottomSheet && selectedServicio != null) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                selectedServicio = null
            }
        ) {
            ServiceBottomSheetContent(
                servicio = selectedServicio!!,
                onNavigateToEmprendedor = { onNavigateToEmprendedor(selectedServicio!!.emprendedor.id) },
                onDismiss = {
                    showBottomSheet = false
                    selectedServicio = null
                }
            )
        }
    }
}

@Composable
private fun ServiceBottomSheetContent(
    servicio: Servicio,
    onNavigateToEmprendedor: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = servicio.nombre,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar")
            }
        }

        Text(
            text = servicio.descripcion,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Precio",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "S/ ${servicio.precio}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column {
                Text(
                    text = "Duración",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${servicio.duracionHoras}h",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Column {
                Text(
                    text = "Capacidad",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${servicio.capacidadMaxima} pax",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // Información del emprendedor
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToEmprendedor() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Business,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = servicio.emprendedor.nombreEmpresa,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = servicio.emprendedor.rubro,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botones de acción
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { /* Llamar */ },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Phone, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Llamar")
            }

            Button(
                onClick = { /* Agregar al carrito */ },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.AddShoppingCart, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Agregar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}