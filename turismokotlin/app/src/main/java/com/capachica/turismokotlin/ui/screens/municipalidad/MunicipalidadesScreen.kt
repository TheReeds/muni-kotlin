package com.capachica.turismokotlin.ui.screens.municipalidad

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capachica.turismokotlin.data.model.Municipalidad
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.components.EmptyListPlaceholder
import com.capachica.turismokotlin.ui.components.ErrorScreen
import com.capachica.turismokotlin.ui.components.LoadingScreen
import com.capachica.turismokotlin.ui.components.TurismoAppBar
import com.capachica.turismokotlin.ui.viewmodel.MunicipalidadViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MunicipalidadesScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToCreate: () -> Unit,
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val viewModel: MunicipalidadViewModel = viewModel(factory = factory)
    val municipalidadesState by viewModel.municipalidadesState.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()

    // Para mostrar diálogo de confirmación
    val showDeleteConfirmDialog = remember { mutableStateOf(false) }
    val municipalidadToDelete = remember { mutableStateOf<Municipalidad?>(null) }

    // Scope para lanzar corrutinas
    val scope = rememberCoroutineScope()

    // Cargar datos al inicio
    LaunchedEffect(Unit) {
        viewModel.getAllMunicipalidades()
    }

    // Recargar datos después de eliminar
    LaunchedEffect(deleteState) {
        if (deleteState is Result.Success && (deleteState as Result.Success).data) {
            viewModel.getAllMunicipalidades()
        }
    }

    // Diálogo de confirmación para eliminar
    if (showDeleteConfirmDialog.value && municipalidadToDelete.value != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmDialog.value = false
                municipalidadToDelete.value = null
            },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Está seguro que desea eliminar la municipalidad '${municipalidadToDelete.value?.nombre}'? Esta acción también eliminará todos los emprendedores asociados.") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            municipalidadToDelete.value?.id?.let { viewModel.deleteMunicipalidad(it) }
                            showDeleteConfirmDialog.value = false
                            municipalidadToDelete.value = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showDeleteConfirmDialog.value = false
                        municipalidadToDelete.value = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TurismoAppBar(
                title = "Municipalidades",
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = onNavigateToCreate) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Añadir municipalidad"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear municipalidad"
                )
            }
        }
    ) { paddingValues ->
        when (val state = municipalidadesState) {
            is Result.Loading -> LoadingScreen()
            is Result.Error -> ErrorScreen(
                message = state.message,
                onRetry = { viewModel.getAllMunicipalidades() }
            )
            is Result.Success -> {
                if (state.data.isEmpty()) {
                    EmptyListPlaceholder(
                        message = "No hay municipalidades registradas",
                        buttonText = "Crear Municipalidad",
                        onButtonClick = onNavigateToCreate
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp)
                    ) {
                        items(state.data) { municipalidad ->
                            MunicipalidadListItem(
                                municipalidad = municipalidad,
                                onClick = { onNavigateToDetail(municipalidad.id) },
                                onDelete = {
                                    municipalidadToDelete.value = municipalidad
                                    showDeleteConfirmDialog.value = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MunicipalidadListItem(
    municipalidad: Municipalidad,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationCity,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = municipalidad.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${municipalidad.provincia}, ${municipalidad.departamento}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Emprendedores: ${municipalidad.emprendedores.size}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Botón de eliminar
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}