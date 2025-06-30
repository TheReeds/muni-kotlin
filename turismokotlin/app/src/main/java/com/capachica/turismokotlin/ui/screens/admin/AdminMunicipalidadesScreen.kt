package com.capachica.turismokotlin.ui.screens.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.capachica.turismokotlin.data.model.Categoria
import com.capachica.turismokotlin.data.model.CreateEmprendedorRequest
import com.capachica.turismokotlin.data.model.CreateMunicipalidadRequest
import com.capachica.turismokotlin.data.model.Emprendedor
import com.capachica.turismokotlin.data.model.MunicipalidadDetallada
import com.capachica.turismokotlin.data.model.UpdateEmprendedorRequest
import com.capachica.turismokotlin.data.model.UpdateMunicipalidadRequest
import com.capachica.turismokotlin.ui.viewmodel.AdminEmprendedoresViewModel
import com.capachica.turismokotlin.ui.viewmodel.AdminMunicipalidadesViewModel
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMunicipalidadesScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminMunicipalidadesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedMunicipalidad by remember { mutableStateOf<MunicipalidadDetallada?>(null) }

    // Manejar mensajes
    LaunchedEffect(uiState.successMessage, uiState.error) {
        if (uiState.successMessage != null || uiState.error != null) {
            delay(3000)
            viewModel.clearMessages()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Gestión de Municipalidades") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                IconButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar municipalidad")
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
                    items(uiState.municipalidades) { municipalidad ->
                        AdminMunicipalidadItem(
                            municipalidad = municipalidad,
                            onEditClick = { selectedMunicipalidad = it },
                            onDeleteClick = { viewModel.deleteMunicipalidad(it.id) }
                        )
                    }
                }
            }
        }
    }

    // Dialog para crear municipalidad
    if (showCreateDialog) {
        CreateMunicipalidadDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { request ->
                viewModel.createMunicipalidad(request)
                showCreateDialog = false
            },
            isCreating = uiState.isCreating
        )
    }

    // Dialog para editar municipalidad
    selectedMunicipalidad?.let { municipalidad ->
        EditMunicipalidadDialog(
            municipalidad = municipalidad,
            onDismiss = { selectedMunicipalidad = null },
            onUpdate = { request ->
                viewModel.updateMunicipalidad(municipalidad.id, request)
                selectedMunicipalidad = null
            },
            isUpdating = uiState.isUpdating
        )
    }
}

@Composable
private fun AdminMunicipalidadItem(
    municipalidad: MunicipalidadDetallada,
    onEditClick: (MunicipalidadDetallada) -> Unit,
    onDeleteClick: (MunicipalidadDetallada) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = municipalidad.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${municipalidad.distrito}, ${municipalidad.provincia}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = municipalidad.departamento,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row {
                    IconButton(onClick = { onEditClick(municipalidad) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = { onDeleteClick(municipalidad) }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Información adicional
            municipalidad.descripcion?.let { descripcion ->
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (municipalidad.telefono != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Phone,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = municipalidad.telefono,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                Text(
                    text = "${municipalidad.emprendedores.size} emprendedores",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            if (municipalidad.sitioWeb != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Language,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = municipalidad.sitioWeb,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateMunicipalidadDialog(
    onDismiss: () -> Unit,
    onCreate: (CreateMunicipalidadRequest) -> Unit,
    isCreating: Boolean
) {
    var nombre by remember { mutableStateOf("") }
    var departamento by remember { mutableStateOf("") }
    var provincia by remember { mutableStateOf("") }
    var distrito by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var sitioWeb by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Crear Municipalidad") },
        text = {
            LazyColumn(
                modifier = Modifier.height(500.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = departamento,
                        onValueChange = { departamento = it },
                        label = { Text("Departamento *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = provincia,
                        onValueChange = { provincia = it },
                        label = { Text("Provincia *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = distrito,
                        onValueChange = { distrito = it },
                        label = { Text("Distrito *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = direccion,
                        onValueChange = { direccion = it },
                        label = { Text("Dirección") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text("Teléfono") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = sitioWeb,
                        onValueChange = { sitioWeb = it },
                        label = { Text("Sitio Web") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombre.isNotBlank() && departamento.isNotBlank() &&
                        provincia.isNotBlank() && distrito.isNotBlank()) {
                        onCreate(
                            CreateMunicipalidadRequest(
                                nombre = nombre,
                                departamento = departamento,
                                provincia = provincia,
                                distrito = distrito,
                                direccion = direccion.takeIf { it.isNotBlank() },
                                telefono = telefono.takeIf { it.isNotBlank() },
                                sitioWeb = sitioWeb.takeIf { it.isNotBlank() },
                                descripcion = descripcion.takeIf { it.isNotBlank() }
                            )
                        )
                    }
                },
                enabled = !isCreating && nombre.isNotBlank() && departamento.isNotBlank() &&
                        provincia.isNotBlank() && distrito.isNotBlank()
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
private fun EditMunicipalidadDialog(
    municipalidad: MunicipalidadDetallada,
    onDismiss: () -> Unit,
    onUpdate: (UpdateMunicipalidadRequest) -> Unit,
    isUpdating: Boolean
) {
    var nombre by remember { mutableStateOf(municipalidad.nombre) }
    var departamento by remember { mutableStateOf(municipalidad.departamento) }
    var provincia by remember { mutableStateOf(municipalidad.provincia) }
    var distrito by remember { mutableStateOf(municipalidad.distrito) }
    var direccion by remember { mutableStateOf(municipalidad.direccion ?: "") }
    var telefono by remember { mutableStateOf(municipalidad.telefono ?: "") }
    var sitioWeb by remember { mutableStateOf(municipalidad.sitioWeb ?: "") }
    var descripcion by remember { mutableStateOf(municipalidad.descripcion ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Municipalidad") },
        text = {
            LazyColumn(
                modifier = Modifier.height(500.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = departamento,
                        onValueChange = { departamento = it },
                        label = { Text("Departamento *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = provincia,
                        onValueChange = { provincia = it },
                        label = { Text("Provincia *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = distrito,
                        onValueChange = { distrito = it },
                        label = { Text("Distrito *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = direccion,
                        onValueChange = { direccion = it },
                        label = { Text("Dirección") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text("Teléfono") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = sitioWeb,
                        onValueChange = { sitioWeb = it },
                        label = { Text("Sitio Web") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombre.isNotBlank() && departamento.isNotBlank() &&
                        provincia.isNotBlank() && distrito.isNotBlank()) {
                        onUpdate(
                            UpdateMunicipalidadRequest(
                                nombre = nombre,
                                departamento = departamento,
                                provincia = provincia,
                                distrito = distrito,
                                direccion = direccion.takeIf { it.isNotBlank() },
                                telefono = telefono.takeIf { it.isNotBlank() },
                                sitioWeb = sitioWeb.takeIf { it.isNotBlank() },
                                descripcion = descripcion.takeIf { it.isNotBlank() }
                            )
                        )
                    }
                },
                enabled = !isUpdating && nombre.isNotBlank() && departamento.isNotBlank() &&
                        provincia.isNotBlank() && distrito.isNotBlank()
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