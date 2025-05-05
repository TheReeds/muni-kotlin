package com.capachica.turismokotlin.ui.screens.emprendedor

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capachica.turismokotlin.data.model.EmprendedorRequest
import com.capachica.turismokotlin.data.model.Municipalidad
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.components.LoadingScreen
import com.capachica.turismokotlin.ui.components.TurismoAppBar
import com.capachica.turismokotlin.ui.components.TurismoTextField
import com.capachica.turismokotlin.ui.viewmodel.EmprendedorViewModel
import com.capachica.turismokotlin.ui.viewmodel.MunicipalidadViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmprendedorFormScreen(
    emprendedorId: Long,
    preselectedMunicipalidadId: Long = 0L,
    onSuccess: () -> Unit,
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val emprendedorViewModel: EmprendedorViewModel = viewModel(factory = factory)
    val municipalidadViewModel: MunicipalidadViewModel = viewModel(factory = factory)

    val emprendedorState by emprendedorViewModel.emprendedorState.collectAsState()
    val createUpdateState by emprendedorViewModel.createUpdateState.collectAsState()
    val deleteState by emprendedorViewModel.deleteState.collectAsState()
    val municipalidadesState by municipalidadViewModel.municipalidadesState.collectAsState()

    val isEditing = emprendedorId > 0
    val scope = rememberCoroutineScope()

    // Para manejar la visibilidad del formulario
    var formReady by remember { mutableStateOf(!isEditing) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // Campos del formulario
    var nombreEmpresa by remember { mutableStateOf("") }
    var rubro by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var sitioWeb by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var productos by remember { mutableStateOf("") }
    var servicios by remember { mutableStateOf("") }
    var selectedMunicipalidadId by remember { mutableStateOf(preselectedMunicipalidadId) }

    // Validación
    var nombreEmpresaError by remember { mutableStateOf(false) }
    var rubroError by remember { mutableStateOf(false) }
    var municipalidadError by remember { mutableStateOf(false) }

    var municipalidades by remember { mutableStateOf<List<Municipalidad>>(emptyList()) }
    var expandedMunicipalidad by remember { mutableStateOf(false) }

    // Inicialización para limpiar estados
    LaunchedEffect(Unit) {
        emprendedorViewModel.resetStates()
        municipalidadViewModel.getAllMunicipalidades()

        // Solo cargamos datos del emprendedor si estamos editando
        if (isEditing) {
            emprendedorViewModel.getEmprendedorById(emprendedorId)
        }
    }

    // Manejar cambios en municipalidades
    LaunchedEffect(municipalidadesState) {
        if (municipalidadesState is Result.Success) {
            municipalidades = (municipalidadesState as Result.Success).data

            // Si no hay preseleccionada y hay municipalidades, seleccionar la primera
            if (selectedMunicipalidadId <= 0 && municipalidades.isNotEmpty()) {
                selectedMunicipalidadId = municipalidades.first().id
            }
        }
    }

    // Manejar carga del emprendedor (para edición)
    LaunchedEffect(emprendedorState) {
        if (isEditing && emprendedorState is Result.Success) {
            val emprendedor = (emprendedorState as Result.Success).data

            // Asignar valores a los campos
            nombreEmpresa = emprendedor.nombreEmpresa
            rubro = emprendedor.rubro
            direccion = emprendedor.direccion ?: ""
            telefono = emprendedor.telefono ?: ""
            email = emprendedor.email ?: ""
            sitioWeb = emprendedor.sitioWeb ?: ""
            descripcion = emprendedor.descripcion ?: ""
            productos = emprendedor.productos ?: ""
            servicios = emprendedor.servicios ?: ""

            // Asignar la municipalidad
            emprendedor.municipalidad?.let {
                selectedMunicipalidadId = it.id
            }

            formReady = true
        } else if (isEditing && emprendedorState is Result.Error) {
            formReady = true
        }
    }

    // Manejar resultado de crear/actualizar
    LaunchedEffect(createUpdateState) {
        if (createUpdateState is Result.Success) {
            onSuccess()
        }
    }

    // Manejar resultado de eliminar
    LaunchedEffect(deleteState) {
        if (deleteState is Result.Success && (deleteState as Result.Success).data) {
            onSuccess()
        }
    }

    // BackHandler
    BackHandler { onBack() }

    // Diálogo de confirmación para eliminar
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Está seguro que desea eliminar este emprendedor? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            emprendedorViewModel.deleteEmprendedor(emprendedorId)
                            showDeleteConfirmDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteConfirmDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TurismoAppBar(
                title = if (isEditing) "Editar Emprendedor" else "Crear Emprendedor",
                onBackClick = onBack
            )
        }
    ) { paddingValues ->
        // Simplificamos las condiciones de carga
        if ((isEditing && !formReady) ||
            createUpdateState is Result.Loading ||
            deleteState is Result.Loading) {
            LoadingScreen()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TurismoTextField(
                    value = nombreEmpresa,
                    onValueChange = {
                        nombreEmpresa = it
                        nombreEmpresaError = false
                    },
                    label = "Nombre de la empresa",
                    isError = nombreEmpresaError,
                    errorMessage = "Ingrese el nombre de la empresa",
                    leadingIcon = Icons.Default.Business
                )

                Spacer(modifier = Modifier.height(8.dp))

                TurismoTextField(
                    value = rubro,
                    onValueChange = {
                        rubro = it
                        rubroError = false
                    },
                    label = "Rubro",
                    isError = rubroError,
                    errorMessage = "Ingrese el rubro",
                    leadingIcon = Icons.Default.Category
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Selección de municipalidad
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Municipalidad",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = expandedMunicipalidad,
                        onExpandedChange = { expandedMunicipalidad = it }
                    ) {
                        OutlinedTextField(
                            value = municipalidades.find { it.id == selectedMunicipalidadId }?.nombre ?: "",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMunicipalidad)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            isError = municipalidadError
                        )

                        ExposedDropdownMenu(
                            expanded = expandedMunicipalidad,
                            onDismissRequest = { expandedMunicipalidad = false }
                        ) {
                            municipalidades.forEach { municipalidad ->
                                DropdownMenuItem(
                                    text = { Text(municipalidad.nombre) },
                                    onClick = {
                                        selectedMunicipalidadId = municipalidad.id
                                        municipalidadError = false
                                        expandedMunicipalidad = false
                                    }
                                )
                            }
                        }
                    }

                    if (municipalidadError) {
                        Text(
                            text = "Seleccione una municipalidad",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TurismoTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = "Dirección (opcional)",
                    leadingIcon = Icons.Default.Home
                )

                Spacer(modifier = Modifier.height(8.dp))

                TurismoTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = "Teléfono (opcional)",
                    leadingIcon = Icons.Default.Phone
                )

                Spacer(modifier = Modifier.height(8.dp))

                TurismoTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email (opcional)",
                    leadingIcon = Icons.Default.Email
                )

                Spacer(modifier = Modifier.height(8.dp))

                TurismoTextField(
                    value = sitioWeb,
                    onValueChange = { sitioWeb = it },
                    label = "Sitio Web (opcional)",
                    leadingIcon = Icons.Default.Web
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción (opcional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = productos,
                    onValueChange = { productos = it },
                    label = { Text("Productos (opcional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = servicios,
                    onValueChange = { servicios = it },
                    label = { Text("Servicios (opcional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mostrar mensajes de error
                if (createUpdateState is Result.Error) {
                    Text(
                        text = (createUpdateState as Result.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (deleteState is Result.Error) {
                    Text(
                        text = (deleteState as Result.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Botón de guardar
                Button(
                    onClick = {
                        // Validación
                        nombreEmpresaError = nombreEmpresa.isBlank()
                        rubroError = rubro.isBlank()
                        municipalidadError = selectedMunicipalidadId <= 0

                        if (!nombreEmpresaError && !rubroError && !municipalidadError) {
                            val request = EmprendedorRequest(
                                nombreEmpresa = nombreEmpresa,
                                rubro = rubro,
                                direccion = direccion.ifBlank { null },
                                telefono = telefono.ifBlank { null },
                                email = email.ifBlank { null },
                                sitioWeb = sitioWeb.ifBlank { null },
                                descripcion = descripcion.ifBlank { null },
                                productos = productos.ifBlank { null },
                                servicios = servicios.ifBlank { null },
                                municipalidadId = selectedMunicipalidadId
                            )

                            scope.launch {
                                if (isEditing) {
                                    emprendedorViewModel.updateEmprendedor(emprendedorId, request)
                                } else {
                                    emprendedorViewModel.createEmprendedor(request)
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isEditing) "Actualizar" else "Crear")
                }

                // Botón de eliminar solo si estamos editando
                if (isEditing) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showDeleteConfirmDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Eliminar Emprendedor")
                        }
                    }
                }
            }
        }
    }
}