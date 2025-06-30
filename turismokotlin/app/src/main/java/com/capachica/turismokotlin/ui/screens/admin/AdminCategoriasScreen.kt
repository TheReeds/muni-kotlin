package com.capachica.turismokotlin.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.capachica.turismokotlin.data.model.Categoria
import com.capachica.turismokotlin.data.model.CreateCategoriaRequest
import com.capachica.turismokotlin.data.model.UpdateCategoriaRequest
import com.capachica.turismokotlin.ui.viewmodel.AdminCategoriasViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCategoriasScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminCategoriasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedCategoria by remember { mutableStateOf<Categoria?>(null) }

    // Manejar mensajes
    LaunchedEffect(uiState.successMessage, uiState.error) {
        if (uiState.successMessage != null || uiState.error != null) {
            delay(3000)
            viewModel.clearMessages()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Gestión de Categorías") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                IconButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar categoría")
                }
            }
        )

        // Mensajes de estado
        uiState.successMessage?.let { message ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        uiState.error?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
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

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.categorias) { categoria ->
                        AdminCategoriaItem(
                            categoria = categoria,
                            onEditClick = { selectedCategoria = it },
                            onDeleteClick = { viewModel.deleteCategoria(it.id) }
                        )
                    }
                }
            }
        }
    }

    // Dialog para crear categoría
    if (showCreateDialog) {
        CreateCategoriaDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { request ->
                viewModel.createCategoria(request)
                showCreateDialog = false
            },
            isCreating = uiState.isCreating
        )
    }

    // Dialog para editar categoría
    selectedCategoria?.let { categoria ->
        EditCategoriaDialog(
            categoria = categoria,
            onDismiss = { selectedCategoria = null },
            onUpdate = { request ->
                viewModel.updateCategoria(categoria.id, request)
                selectedCategoria = null
            },
            isUpdating = uiState.isUpdating
        )
    }
}

@Composable
private fun AdminCategoriaItem(
    categoria: Categoria,
    onEditClick: (Categoria) -> Unit,
    onDeleteClick: (Categoria) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = categoria.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                categoria.descripcion?.let { descripcion ->
                    Text(
                        text = descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${categoria.cantidadEmprendedores} emprendedores",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row {
                IconButton(onClick = { onEditClick(categoria) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = { onDeleteClick(categoria) }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateCategoriaDialog(
    onDismiss: () -> Unit,
    onCreate: (CreateCategoriaRequest) -> Unit,
    isCreating: Boolean
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Crear Categoría") },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombre.isNotBlank()) {
                        onCreate(
                            CreateCategoriaRequest(
                                nombre = nombre,
                                descripcion = descripcion.takeIf { it.isNotBlank() }
                            )
                        )
                    }
                },
                enabled = !isCreating && nombre.isNotBlank()
            ) {
                if (isCreating) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                } else {
                    Text("Crear")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditCategoriaDialog(
    categoria: Categoria,
    onDismiss: () -> Unit,
    onUpdate: (UpdateCategoriaRequest) -> Unit,
    isUpdating: Boolean
) {
    var nombre by remember { mutableStateOf(categoria.nombre) }
    var descripcion by remember { mutableStateOf(categoria.descripcion ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Categoría") },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombre.isNotBlank()) {
                        onUpdate(
                            UpdateCategoriaRequest(
                                nombre = nombre,
                                descripcion = descripcion.takeIf { it.isNotBlank() }
                            )
                        )
                    }
                },
                enabled = !isUpdating && nombre.isNotBlank()
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                } else {
                    Text("Actualizar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}