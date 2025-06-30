package com.capachica.turismokotlin.ui.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.capachica.turismokotlin.data.model.Emprendedor
import com.capachica.turismokotlin.data.model.Servicio
import com.capachica.turismokotlin.ui.components.ServiceCard
import com.capachica.turismokotlin.ui.viewmodel.EmprendedorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmprendedorDetailScreen(
    emprendedorId: Long,
    onNavigateBack: () -> Unit,
    viewModel: EmprendedorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(emprendedorId) {
        viewModel.loadEmprendedorDetails(emprendedorId)
    }

    when {
        uiState.isLoading -> {
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
                Text("Error: ${uiState.error}")
                Button(onClick = { viewModel.loadEmprendedorDetails(emprendedorId) }) {
                    Text("Reintentar")
                }
            }
        }

        uiState.emprendedor != null -> {
            EmprendedorDetailContent(
                emprendedor = uiState.emprendedor!!,
                servicios = uiState.servicios,
                onNavigateBack = onNavigateBack
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmprendedorDetailContent(
    emprendedor: Emprendedor,
    servicios: List<Servicio>,
    onNavigateBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Emprendedor") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                IconButton(onClick = { /* Compartir */ }) {
                    Icon(Icons.Default.Share, contentDescription = "Compartir")
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Header del emprendedor
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Business,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = emprendedor.nombreEmpresa,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = emprendedor.rubro,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = emprendedor.municipalidad.nombre,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                }
                            }
                        }

                        if (!emprendedor.descripcion.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = emprendedor.descripcion,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            item {
                // Información de contacto
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Información de Contacto",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ContactInfoRow(
                            icon = Icons.Default.Phone,
                            label = "Teléfono",
                            value = emprendedor.telefono
                        )

                        ContactInfoRow(
                            icon = Icons.Default.Email,
                            label = "Email",
                            value = emprendedor.email
                        )

                        emprendedor.sitioWeb?.let { sitio ->
                            ContactInfoRow(
                                icon = Icons.Default.Language,
                                label = "Sitio Web",
                                value = sitio
                            )
                        }

                        emprendedor.direccionCompleta?.let { direccion ->
                            ContactInfoRow(
                                icon = Icons.Default.LocationOn,
                                label = "Dirección",
                                value = direccion
                            )
                        }
                    }
                }
            }

            // Botones de acción
            item {
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

                    OutlinedButton(
                        onClick = { /* Email */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Email, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Email")
                    }

                    if (emprendedor.latitud != null && emprendedor.longitud != null) {
                        OutlinedButton(
                            onClick = { /* Ver en mapa */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Map, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ubicación")
                        }
                    }
                }
            }

            // Servicios y productos
            if (!emprendedor.productos.isNullOrEmpty() || !emprendedor.servicios.isNullOrEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Productos y Servicios",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            emprendedor.productos?.let { productos ->
                                if (productos.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Productos:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = productos,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }

                            emprendedor.servicios?.let { serviciosTexto ->
                                if (serviciosTexto.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Servicios:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = serviciosTexto,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Lista de servicios turísticos
            if (servicios.isNotEmpty()) {
                item {
                    Text(
                        text = "Servicios Turísticos (${servicios.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(servicios) { servicio ->
                    ServiceCard(
                        servicio = servicio,
                        onAddToCart = { /* Agregar al carrito */ },
                        onNavigateToEmprendedor = { /* Ya estamos aquí */ }
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}