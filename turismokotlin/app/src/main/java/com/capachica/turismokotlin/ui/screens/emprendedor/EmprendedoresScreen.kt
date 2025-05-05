package com.capachica.turismokotlin.ui.screens.emprendedor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capachica.turismokotlin.data.model.Emprendedor
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.components.EmptyListPlaceholder
import com.capachica.turismokotlin.ui.components.ErrorScreen
import com.capachica.turismokotlin.ui.components.LoadingScreen
import com.capachica.turismokotlin.ui.components.TurismoAppBar
import com.capachica.turismokotlin.ui.viewmodel.EmprendedorViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmprendedoresScreen(
    municipalidadId: Long = 0,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToCreate: () -> Unit,
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val viewModel: EmprendedorViewModel = viewModel(factory = factory)
    val emprendedoresState by viewModel.emprendedoresState.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()

    // Para mostrar diálogo de confirmación
    val showDeleteConfirmDialog = remember { mutableStateOf(false) }
    val emprendedorToDelete = remember { mutableStateOf<Emprendedor?>(null) }

    // Scope para lanzar corrutinas
    val scope = rememberCoroutineScope()

    // Cargar datos al inicio o cuando cambie municipalidadId
    LaunchedEffect(municipalidadId) {
        if (municipalidadId > 0) {
            viewModel.getEmprendedoresByMunicipalidad(municipalidadId)
        } else {
            viewModel.getAllEmprendedores()
        }
    }

    // Recargar datos después de eliminar
    LaunchedEffect(deleteState) {
        if (deleteState is Result.Success && (deleteState as Result.Success).data) {
            if (municipalidadId > 0) {
                viewModel.getEmprendedoresByMunicipalidad(municipalidadId)
            } else {
                viewModel.getAllEmprendedores()
            }
        }
    }

    // Diálogo de confirmación para eliminar
    if (showDeleteConfirmDialog.value && emprendedorToDelete.value != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmDialog.value = false
                emprendedorToDelete.value = null
            },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Está seguro que desea eliminar el emprendedor '${emprendedorToDelete.value?.nombreEmpresa}'? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            emprendedorToDelete.value?.id?.let { viewModel.deleteEmprendedor(it) }
                            showDeleteConfirmDialog.value = false
                            emprendedorToDelete.value = null
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
                        emprendedorToDelete.value = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    val title = if (municipalidadId > 0) {
        "Emprendedores de Municipalidad"
    } else {
        "Emprendedores"
    }

    Scaffold(
        topBar = {
            TurismoAppBar(
                title = title,
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = onNavigateToCreate) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Añadir emprendedor"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear emprendedor"
                )
            }
        }
    ) { paddingValues ->
        when (val state = emprendedoresState) {
            is Result.Loading -> LoadingScreen()
            is Result.Error -> ErrorScreen(
                message = state.message,
                onRetry = {
                    if (municipalidadId > 0) {
                        viewModel.getEmprendedoresByMunicipalidad(municipalidadId)
                    } else {
                        viewModel.getAllEmprendedores()
                    }
                }
            )
            is Result.Success -> {
                if (state.data.isEmpty()) {
                    EmptyListPlaceholder(
                        message = "No hay emprendedores registrados",
                        buttonText = "Crear Emprendedor",
                        onButtonClick = onNavigateToCreate
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp)
                    ) {
                        items(state.data) { emprendedor ->
                            EmprendedorListItem(
                                emprendedor = emprendedor,
                                onClick = { onNavigateToDetail(emprendedor.id) },
                                onDelete = {
                                    emprendedorToDelete.value = emprendedor
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
fun EmprendedorListItem(
    emprendedor: Emprendedor,
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
                imageVector = Icons.Default.Business,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = emprendedor.nombreEmpresa,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Rubro: ${emprendedor.rubro}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                emprendedor.municipalidad?.let { municipalidad ->
                    Text(
                        text = "Municipalidad: ${municipalidad.nombre}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
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