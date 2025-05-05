package com.capachica.turismokotlin.ui.screens.municipalidad

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
import com.capachica.turismokotlin.ui.viewmodel.MunicipalidadViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory

@Composable
fun MunicipalidadDetailScreen(
    municipalidadId: Long,
    onNavigateToEdit: () -> Unit,
    onNavigateToEmprendedores: () -> Unit,
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val viewModel: MunicipalidadViewModel = viewModel(factory = factory)
    val municipalidadState by viewModel.municipalidadState.collectAsState()

    LaunchedEffect(key1 = municipalidadId) {
        viewModel.getMunicipalidadById(municipalidadId)
    }

    Scaffold(
        topBar = {
            TurismoAppBar(
                title = "Detalle de Municipalidad",
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = onNavigateToEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar municipalidad"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = municipalidadState) {
            is Result.Loading -> LoadingScreen()
            is Result.Error -> ErrorScreen(
                message = state.message,
                onRetry = { viewModel.getMunicipalidadById(municipalidadId) }
            )
            is Result.Success -> {
                val municipalidad = state.data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
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
                                text = municipalidad.nombre,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            InfoRow(
                                icon = Icons.Default.LocationOn,
                                label = "Ubicación",
                                value = "${municipalidad.distrito}, ${municipalidad.provincia}, ${municipalidad.departamento}"
                            )

                            if (!municipalidad.direccion.isNullOrBlank()) {
                                InfoRow(
                                    icon = Icons.Default.Home,
                                    label = "Dirección",
                                    value = municipalidad.direccion
                                )
                            }

                            if (!municipalidad.telefono.isNullOrBlank()) {
                                InfoRow(
                                    icon = Icons.Default.Phone,
                                    label = "Teléfono",
                                    value = municipalidad.telefono
                                )
                            }

                            if (!municipalidad.sitioWeb.isNullOrBlank()) {
                                InfoRow(
                                    icon = Icons.Default.Web,
                                    label = "Sitio Web",
                                    value = municipalidad.sitioWeb
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Descripción
                    if (!municipalidad.descripcion.isNullOrBlank()) {
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
                                    text = municipalidad.descripcion,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Emprendedores
                    Card(
                        modifier = Modifier.fillMaxWidth()
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
                                    text = "Emprendedores (${municipalidad.emprendedores.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                TextButton(onClick = onNavigateToEmprendedores) {
                                    Text("Ver todos")
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (municipalidad.emprendedores.isEmpty()) {
                                Text(
                                    text = "No hay emprendedores registrados",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            } else {
                                municipalidad.emprendedores.take(3).forEach { emprendedor ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Business,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Column {
                                            Text(
                                                text = emprendedor.nombreEmpresa,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )

                                            Text(
                                                text = "Rubro: ${emprendedor.rubro}",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }

                                    if (municipalidad.emprendedores.indexOf(emprendedor) < municipalidad.emprendedores.size - 1) {
                                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                                    }
                                }

                                if (municipalidad.emprendedores.size > 3) {
                                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        TextButton(onClick = onNavigateToEmprendedores) {
                                            Text("Ver ${municipalidad.emprendedores.size - 3} más")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}