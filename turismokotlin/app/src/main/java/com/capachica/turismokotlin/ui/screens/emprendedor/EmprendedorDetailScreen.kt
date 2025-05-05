package com.capachica.turismokotlin.ui.screens.emprendedor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.components.ErrorScreen
import com.capachica.turismokotlin.ui.components.InfoRow
import com.capachica.turismokotlin.ui.components.LoadingScreen
import com.capachica.turismokotlin.ui.components.TurismoAppBar
import com.capachica.turismokotlin.ui.viewmodel.EmprendedorViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory

@Composable
fun EmprendedorDetailScreen(
    emprendedorId: Long,
    onNavigateToEdit: () -> Unit,
    onNavigateToMunicipalidad: (Long) -> Unit,
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val viewModel: EmprendedorViewModel = viewModel(factory = factory)
    val emprendedorState by viewModel.emprendedorState.collectAsState()

    // Variable para controlar el tiempo máximo de carga
    var loadingTimeout by remember { mutableStateOf(false) }

    // Establecer un timeout para evitar carga infinita
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(5000) // 5 segundos máximo de espera
        loadingTimeout = true
    }

    LaunchedEffect(key1 = emprendedorId) {
        viewModel.getEmprendedorById(emprendedorId)
    }

    Scaffold(
        topBar = {
            TurismoAppBar(
                title = "Detalle de Emprendedor",
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = onNavigateToEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar emprendedor"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            // Si estamos cargando pero no ha pasado el timeout
            emprendedorState is Result.Loading && !loadingTimeout -> LoadingScreen()

            // Si hay un error o pasó el timeout con una carga continua
            emprendedorState is Result.Error || (emprendedorState is Result.Loading && loadingTimeout) -> {
                val message = if (emprendedorState is Result.Error) {
                    (emprendedorState as Result.Error).message
                } else {
                    "Tiempo de espera excedido. Por favor, inténtelo nuevamente."
                }

                ErrorScreen(
                    message = message,
                    onRetry = {
                        loadingTimeout = false
                        viewModel.getEmprendedorById(emprendedorId)
                    }
                )
            }

            // Si tenemos datos exitosos
            emprendedorState is Result.Success -> {
                val emprendedor = (emprendedorState as Result.Success).data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Resto del contenido (sin cambios)

                    // Cabecera
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = emprendedor.nombreEmpresa,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = {
                                        emprendedor.municipalidad?.let {
                                            onNavigateToMunicipalidad(it.id)
                                        }
                                    },
                                    enabled = emprendedor.municipalidad != null
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationCity,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = emprendedor.municipalidad?.nombre ?: "Sin municipalidad asignada"
                                    )
                                }
                            }

                            Text(
                                text = "Rubro: ${emprendedor.rubro}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            if (!emprendedor.direccion.isNullOrBlank()) {
                                InfoRow(
                                    icon = Icons.Default.Home,
                                    label = "Dirección",
                                    value = emprendedor.direccion
                                )
                            }

                            if (!emprendedor.telefono.isNullOrBlank()) {
                                InfoRow(
                                    icon = Icons.Default.Phone,
                                    label = "Teléfono",
                                    value = emprendedor.telefono
                                )
                            }

                            if (!emprendedor.email.isNullOrBlank()) {
                                InfoRow(
                                    icon = Icons.Default.Email,
                                    label = "Email",
                                    value = emprendedor.email
                                )
                            }

                            if (!emprendedor.sitioWeb.isNullOrBlank()) {
                                InfoRow(
                                    icon = Icons.Default.Web,
                                    label = "Sitio Web",
                                    value = emprendedor.sitioWeb
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Descripción
                    if (!emprendedor.descripcion.isNullOrBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Acerca de",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = emprendedor.descripcion,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Productos y Servicios
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Productos
                            if (!emprendedor.productos.isNullOrBlank()) {
                                Text(
                                    text = "Productos",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = emprendedor.productos,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // Servicios
                            if (!emprendedor.servicios.isNullOrBlank()) {
                                Text(
                                    text = "Servicios",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = emprendedor.servicios,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}