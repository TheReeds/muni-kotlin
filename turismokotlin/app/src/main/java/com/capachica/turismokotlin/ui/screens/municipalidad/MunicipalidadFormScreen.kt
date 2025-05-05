package com.capachica.turismokotlin.ui.screens.municipalidad

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
import com.capachica.turismokotlin.data.model.MunicipalidadRequest
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.components.LoadingScreen
import com.capachica.turismokotlin.ui.components.TurismoAppBar
import com.capachica.turismokotlin.ui.components.TurismoTextField
import com.capachica.turismokotlin.ui.viewmodel.MunicipalidadViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MunicipalidadFormScreen(
    municipalidadId: Long,
    onSuccess: () -> Unit,
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val viewModel: MunicipalidadViewModel = viewModel(factory = factory)
    val municipalidadState by viewModel.municipalidadState.collectAsState()
    val createUpdateState by viewModel.createUpdateState.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()

    val isEditing = municipalidadId > 0
    val scope = rememberCoroutineScope()

    // Para manejar la visibilidad del formulario
    var formReady by remember { mutableStateOf(!isEditing) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // Campos del formulario
    var nombre by remember { mutableStateOf("") }
    var departamento by remember { mutableStateOf("") }
    var provincia by remember { mutableStateOf("") }
    var distrito by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var sitioWeb by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    // Validación
    var nombreError by remember { mutableStateOf(false) }
    var departamentoError by remember { mutableStateOf(false) }
    var provinciaError by remember { mutableStateOf(false) }
    var distritoError by remember { mutableStateOf(false) }

    // Inicialización para limpiar estados
    LaunchedEffect(Unit) {
        viewModel.resetStates()

        // Solo cargamos datos si estamos editando
        if (isEditing) {
            viewModel.getMunicipalidadById(municipalidadId)
        }
    }

    // Manejar carga de la municipalidad (para edición)
    LaunchedEffect(municipalidadState) {
        if (isEditing && municipalidadState is Result.Success) {
            val municipalidad = (municipalidadState as Result.Success).data

            // Asignar valores a los campos
            nombre = municipalidad.nombre
            departamento = municipalidad.departamento
            provincia = municipalidad.provincia
            distrito = municipalidad.distrito
            direccion = municipalidad.direccion ?: ""
            telefono = municipalidad.telefono ?: ""
            sitioWeb = municipalidad.sitioWeb ?: ""
            descripcion = municipalidad.descripcion ?: ""

            formReady = true
        } else if (isEditing && municipalidadState is Result.Error) {
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
            text = { Text("¿Está seguro que desea eliminar esta municipalidad? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.deleteMunicipalidad(municipalidadId)
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
                title = if (isEditing) "Editar Municipalidad" else "Crear Municipalidad",
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
                    value = nombre,
                    onValueChange = {
                        nombre = it
                        nombreError = false
                    },
                    label = "Nombre de la municipalidad",
                    isError = nombreError,
                    errorMessage = "Ingrese el nombre de la municipalidad",
                    leadingIcon = Icons.Default.LocationCity
                )

                Spacer(modifier = Modifier.height(8.dp))

                TurismoTextField(
                    value = departamento,
                    onValueChange = {
                        departamento = it
                        departamentoError = false
                    },
                    label = "Departamento",
                    isError = departamentoError,
                    errorMessage = "Ingrese el departamento",
                    leadingIcon = Icons.Default.Map
                )

                Spacer(modifier = Modifier.height(8.dp))

                TurismoTextField(
                    value = provincia,
                    onValueChange = {
                        provincia = it
                        provinciaError = false
                    },
                    label = "Provincia",
                    isError = provinciaError,
                    errorMessage = "Ingrese la provincia",
                    leadingIcon = Icons.Default.Map
                )

                Spacer(modifier = Modifier.height(8.dp))

                TurismoTextField(
                    value = distrito,
                    onValueChange = {
                        distrito = it
                        distritoError = false
                    },
                    label = "Distrito",
                    isError = distritoError,
                    errorMessage = "Ingrese el distrito",
                    leadingIcon = Icons.Default.Map
                )

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
                        nombreError = nombre.isBlank()
                        departamentoError = departamento.isBlank()
                        provinciaError = provincia.isBlank()
                        distritoError = distrito.isBlank()

                        if (!nombreError && !departamentoError && !provinciaError && !distritoError) {
                            val request = MunicipalidadRequest(
                                nombre = nombre,
                                departamento = departamento,
                                provincia = provincia,
                                distrito = distrito,
                                direccion = direccion.ifBlank { null },
                                telefono = telefono.ifBlank { null },
                                sitioWeb = sitioWeb.ifBlank { null },
                                descripcion = descripcion.ifBlank { null }
                            )

                            scope.launch {
                                if (isEditing) {
                                    viewModel.updateMunicipalidad(municipalidadId, request)
                                } else {
                                    viewModel.createMunicipalidad(request)
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
                            Text("Eliminar Municipalidad")
                        }
                    }
                }
            }
        }
    }
}