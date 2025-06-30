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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.capachica.turismokotlin.data.model.Categoria
import com.capachica.turismokotlin.data.model.CreateEmprendedorRequest
import com.capachica.turismokotlin.data.model.Emprendedor
import com.capachica.turismokotlin.data.model.MunicipalidadDetallada
import com.capachica.turismokotlin.data.model.UpdateEmprendedorRequest
import com.capachica.turismokotlin.ui.viewmodel.AdminEmprendedoresViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEmprendedoresScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminEmprendedoresViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedEmprendedor by remember { mutableStateOf<Emprendedor?>(null) }

    // Manejar mensajes
    LaunchedEffect(uiState.successMessage, uiState.error) {
        if (uiState.successMessage != null || uiState.error != null) {
            delay(3000)
            viewModel.clearMessages()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Gestión de Emprendedores") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                IconButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar emprendedor")
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
                    items(uiState.emprendedores) { emprendedor ->
                        AdminEmprendedorItem(
                            emprendedor = emprendedor,
                            onEditClick = { selectedEmprendedor = it },
                            onDeleteClick = { viewModel.deleteEmprendedor(it.id) }
                        )
                    }
                }
            }
        }
    }

    // Dialog para crear emprendedor
    if (showCreateDialog) {
        CreateEmprendedorDialog(
            categorias = uiState.categorias,
            municipalidades = uiState.municipalidades,
            onDismiss = { showCreateDialog = false },
            onCreate = { request ->
                viewModel.createEmprendedor(request)
                showCreateDialog = false
            },
            isCreating = uiState.isCreating
        )
    }

    // Dialog para editar emprendedor
    selectedEmprendedor?.let { emprendedor ->
        EditEmprendedorDialog(
            emprendedor = emprendedor,
            categorias = uiState.categorias,
            municipalidades = uiState.municipalidades,
            onDismiss = { selectedEmprendedor = null },
            onUpdate = { request ->
                viewModel.updateEmprendedor(emprendedor.id, request)
                selectedEmprendedor = null
            },
            isUpdating = uiState.isUpdating
        )
    }
}

@Composable
private fun AdminEmprendedorItem(
    emprendedor: Emprendedor,
    onEditClick: (Emprendedor) -> Unit,
    onDeleteClick: (Emprendedor) -> Unit
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
                        text = emprendedor.nombreEmpresa,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = emprendedor.rubro,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = emprendedor.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row {
                    IconButton(onClick = { onEditClick(emprendedor) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = { onDeleteClick(emprendedor) }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "📍 ${emprendedor.municipalidad.nombre}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "🏷️ ${emprendedor.categoria.nombre}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateEmprendedorDialog(
    categorias: List<Categoria>,
    municipalidades: List<MunicipalidadDetallada>,
    onDismiss: () -> Unit,
    onCreate: (CreateEmprendedorRequest) -> Unit,
    isCreating: Boolean
) {
    var nombreEmpresa by remember { mutableStateOf("") }
    var rubro by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var selectedCategoria by remember { mutableStateOf<Categoria?>(null) }
    var selectedMunicipalidad by remember { mutableStateOf<MunicipalidadDetallada?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Crear Emprendedor") },
        text = {
            LazyColumn(
                modifier = Modifier.height(400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = nombreEmpresa,
                        onValueChange = { nombreEmpresa = it },
                        label = { Text("Nombre de la Empresa") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = rubro,
                        onValueChange = { rubro = it },
                        label = { Text("Rubro") },
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
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
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

                // Selector de categoría
                item {
                    Text("Categoría:", fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(categorias) { categoria ->
                            FilterChip(
                                onClick = { selectedCategoria = categoria },
                                label = { Text(categoria.nombre) },
                                selected = selectedCategoria?.id == categoria.id
                            )
                        }
                    }
                }

                // Selector de municipalidad
                item {
                    Text("Municipalidad:", fontWeight = FontWeight.Bold)
                    LazyColumn(modifier = Modifier.height(100.dp)) {
                        items(municipalidades) { municipalidad ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .clickable { selectedMunicipalidad = municipalidad },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedMunicipalidad?.id == municipalidad.id)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Text(
                                    text = "${municipalidad.nombre} - ${municipalidad.distrito}",
                                    modifier = Modifier.padding(8.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombreEmpresa.isNotBlank() && rubro.isNotBlank() && telefono.isNotBlank() &&
                        email.isNotBlank() && selectedCategoria != null && selectedMunicipalidad != null) {
                        onCreate(
                            CreateEmprendedorRequest(
                                nombreEmpresa = nombreEmpresa,
                                rubro = rubro,
                                telefono = telefono,
                                email = email,
                                descripcion = descripcion.takeIf { it.isNotBlank() },
                                categoriaId = selectedCategoria!!.id,
                                municipalidadId = selectedMunicipalidad!!.id
                            )
                        )
                    }
                },
                enabled = !isCreating && nombreEmpresa.isNotBlank() && rubro.isNotBlank() &&
                        telefono.isNotBlank() && email.isNotBlank() && selectedCategoria != null &&
                        selectedMunicipalidad != null
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
private fun EditEmprendedorDialog(
    emprendedor: Emprendedor,
    categorias: List<Categoria>,
    municipalidades: List<MunicipalidadDetallada>,
    onDismiss: () -> Unit,
    onUpdate: (UpdateEmprendedorRequest) -> Unit,
    isUpdating: Boolean
) {
    var nombreEmpresa by remember { mutableStateOf(emprendedor.nombreEmpresa) }
    var rubro by remember { mutableStateOf(emprendedor.rubro) }
    var telefono by remember { mutableStateOf(emprendedor.telefono) }
    var email by remember { mutableStateOf(emprendedor.email) }
    var descripcion by remember { mutableStateOf(emprendedor.descripcion ?: "") }
    var selectedCategoria by remember { mutableStateOf(emprendedor.categoria) }
    var selectedMunicipalidad by remember {
        mutableStateOf(
            municipalidades.find { it.id == emprendedor.municipalidad.id }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Emprendedor") },
        text = {
            LazyColumn(
                modifier = Modifier.height(400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = nombreEmpresa,
                        onValueChange = { nombreEmpresa = it },
                        label = { Text("Nombre de la Empresa") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = rubro,
                        onValueChange = { rubro = it },
                        label = { Text("Rubro") },
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
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
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

                // Selector de categoría
                item {
                    Text("Categoría:", fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(categorias) { categoria ->
                            FilterChip(
                                onClick = { selectedCategoria = categoria },
                                label = { Text(categoria.nombre) },
                                selected = selectedCategoria.id == categoria.id
                            )
                        }
                    }
                }

                // Selector de municipalidad
                item {
                    Text("Municipalidad:", fontWeight = FontWeight.Bold)
                    LazyColumn(modifier = Modifier.height(100.dp)) {
                        items(municipalidades) { municipalidad ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .clickable { selectedMunicipalidad = municipalidad },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedMunicipalidad?.id == municipalidad.id)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Text(
                                    text = "${municipalidad.nombre} - ${municipalidad.distrito}",
                                    modifier = Modifier.padding(8.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombreEmpresa.isNotBlank() && rubro.isNotBlank() && telefono.isNotBlank() &&
                        email.isNotBlank() && selectedMunicipalidad != null) {
                        onUpdate(
                            UpdateEmprendedorRequest(
                                nombreEmpresa = nombreEmpresa,
                                rubro = rubro,
                                telefono = telefono,
                                email = email,
                                descripcion = descripcion.takeIf { it.isNotBlank() },
                                categoriaId = selectedCategoria.id,
                                municipalidadId = selectedMunicipalidad!!.id
                            )
                        )
                    }
                },
                enabled = !isUpdating && nombreEmpresa.isNotBlank() && rubro.isNotBlank() &&
                        telefono.isNotBlank() && email.isNotBlank() && selectedMunicipalidad != null
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