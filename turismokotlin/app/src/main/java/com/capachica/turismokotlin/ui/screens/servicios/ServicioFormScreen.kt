package com.capachica.turismokotlin.ui.screens.servicios

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capachica.turismokotlin.data.model.*
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.components.*
import com.capachica.turismokotlin.ui.viewmodel.ServicioTuristicoViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicioFormScreen(
    servicioId: Long = 0L, // 0 = crear nuevo, > 0 = editar existente
    onSuccess: () -> Unit,
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val viewModel: ServicioTuristicoViewModel = viewModel(factory = factory)
    val servicioState by viewModel.servicioState.collectAsState()
    val createUpdateState by viewModel.createUpdateState.collectAsState()
    
    val isEditing = servicioId > 0L
    
    // Estados del formulario
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var duracionHoras by remember { mutableStateOf("") }
    var capacidadMaxima by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf(TipoServicio.TOUR) }
    var ubicacion by remember { mutableStateOf("") }
    var requisitos by remember { mutableStateOf("") }
    var incluye by remember { mutableStateOf("") }
    var noIncluye by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf("") }
    
    // Estados de validación
    var showErrors by remember { mutableStateOf(false) }
    var isFormValid by remember { mutableStateOf(false) }
    
    // Validación del formulario
    LaunchedEffect(nombre, precio, duracionHoras, capacidadMaxima) {
        isFormValid = nombre.isNotBlank() && 
                precio.isNotBlank() && precio.toDoubleOrNull() != null &&
                duracionHoras.isNotBlank() && duracionHoras.toIntOrNull() != null &&
                capacidadMaxima.isNotBlank() && capacidadMaxima.toIntOrNull() != null
    }
    
    // Cargar datos si es edición
    LaunchedEffect(servicioId) {
        if (isEditing) {
            viewModel.getServicioById(servicioId)
        }
    }
    
    // Poblar formulario con datos existentes
    LaunchedEffect(servicioState) {
        if (isEditing && servicioState is Result.Success) {
            val servicio = servicioState.data
            nombre = servicio.nombre
            descripcion = servicio.descripcion ?: ""
            precio = servicio.precio.toString()
            duracionHoras = servicio.duracionHoras.toString()
            capacidadMaxima = servicio.capacidadMaxima.toString()
            tipo = servicio.tipo
            ubicacion = servicio.ubicacion ?: ""
            requisitos = servicio.requisitos ?: ""
            incluye = servicio.incluye ?: ""
            noIncluye = servicio.noIncluye ?: ""
            imagenUrl = servicio.imagenUrl ?: ""
        }
    }
    
    // Manejar resultado de creación/actualización
    LaunchedEffect(createUpdateState) {
        if (createUpdateState is Result.Success) {
            onSuccess()
        }
    }
    
    Scaffold(
        topBar = {
            TurismoAppBar(
                title = if (isEditing) "Editar Servicio" else "Nuevo Servicio",
                onBackClick = onBack,
                actions = {
                    TextButton(
                        onClick = {
                            if (!isFormValid) {
                                showErrors = true
                                return@TextButton
                            }
                            
                            val request = ServicioTuristicoRequest(
                                nombre = nombre.trim(),
                                descripcion = descripcion.trim().takeIf { it.isNotEmpty() },
                                precio = precio.toDouble(),
                                duracionHoras = duracionHoras.toInt(),
                                capacidadMaxima = capacidadMaxima.toInt(),
                                tipo = tipo,
                                ubicacion = ubicacion.trim().takeIf { it.isNotEmpty() },
                                requisitos = requisitos.trim().takeIf { it.isNotEmpty() },
                                incluye = incluye.trim().takeIf { it.isNotEmpty() },
                                noIncluye = noIncluye.trim().takeIf { it.isNotEmpty() },
                                imagenUrl = imagenUrl.trim().takeIf { it.isNotEmpty() }
                            )
                            
                            if (isEditing) {
                                viewModel.updateServicio(servicioId, request)
                            } else {
                                viewModel.createServicio(request)
                            }
                        },
                        enabled = createUpdateState !is Result.Loading
                    ) {
                        if (createUpdateState is Result.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(if (isEditing) "Actualizar" else "Crear")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mostrar errores de API
            if (createUpdateState is Result.Error) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = createUpdateState.message,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            
            // Información básica
            ServicioBasicInfoForm(
                nombre = nombre,
                onNombreChange = { nombre = it },
                descripcion = descripcion,
                onDescripcionChange = { descripcion = it },
                showErrors = showErrors
            )
            
            // Información comercial
            ServicioCommercialInfoForm(
                precio = precio,
                onPrecioChange = { precio = it },
                duracionHoras = duracionHoras,
                onDuracionChange = { duracionHoras = it },
                capacidadMaxima = capacidadMaxima,
                onCapacidadChange = { capacidadMaxima = it },
                showErrors = showErrors
            )
            
            // Tipo y ubicación
            ServicioTypeLocationForm(
                tipo = tipo,
                onTipoChange = { tipo = it },
                ubicacion = ubicacion,
                onUbicacionChange = { ubicacion = it }
            )
            
            // Información adicional
            ServicioAdditionalInfoForm(
                requisitos = requisitos,
                onRequisitosChange = { requisitos = it },
                incluye = incluye,
                onIncluyeChange = { incluye = it },
                noIncluye = noIncluye,
                onNoIncluyeChange = { noIncluye = it },
                imagenUrl = imagenUrl,
                onImagenUrlChange = { imagenUrl = it }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ServicioBasicInfoForm(
    nombre: String,
    onNombreChange: (String) -> Unit,
    descripcion: String,
    onDescripcionChange: (String) -> Unit,
    showErrors: Boolean
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Información Básica",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            OutlinedTextField(
                value = nombre,
                onValueChange = onNombreChange,
                label = { Text("Nombre del Servicio *") },
                modifier = Modifier.fillMaxWidth(),
                isError = showErrors && nombre.isBlank(),
                supportingText = {
                    if (showErrors && nombre.isBlank()) {
                        Text("El nombre es obligatorio")
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = null
                    )
                }
            )
            
            OutlinedTextField(
                value = descripcion,
                onValueChange = onDescripcionChange,
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@Composable
fun ServicioCommercialInfoForm(
    precio: String,
    onPrecioChange: (String) -> Unit,
    duracionHoras: String,
    onDuracionChange: (String) -> Unit,
    capacidadMaxima: String,
    onCapacidadChange: (String) -> Unit,
    showErrors: Boolean
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Información Comercial",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            OutlinedTextField(
                value = precio,
                onValueChange = onPrecioChange,
                label = { Text("Precio por Persona *") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = showErrors && (precio.isBlank() || precio.toDoubleOrNull() == null),
                supportingText = {
                    if (showErrors && precio.isBlank()) {
                        Text("El precio es obligatorio")
                    } else if (showErrors && precio.toDoubleOrNull() == null) {
                        Text("Ingrese un precio válido")
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
                        contentDescription = null
                    )
                }
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = duracionHoras,
                    onValueChange = onDuracionChange,
                    label = { Text("Duración (horas) *") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = showErrors && (duracionHoras.isBlank() || duracionHoras.toIntOrNull() == null),
                    supportingText = {
                        if (showErrors && duracionHoras.isBlank()) {
                            Text("Obligatorio")
                        } else if (showErrors && duracionHoras.toIntOrNull() == null) {
                            Text("Número válido")
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null
                        )
                    }
                )
                
                OutlinedTextField(
                    value = capacidadMaxima,
                    onValueChange = onCapacidadChange,
                    label = { Text("Capacidad Máx. *") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = showErrors && (capacidadMaxima.isBlank() || capacidadMaxima.toIntOrNull() == null),
                    supportingText = {
                        if (showErrors && capacidadMaxima.isBlank()) {
                            Text("Obligatorio")
                        } else if (showErrors && capacidadMaxima.toIntOrNull() == null) {
                            Text("Número válido")
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicioTypeLocationForm(
    tipo: TipoServicio,
    onTipoChange: (TipoServicio) -> Unit,
    ubicacion: String,
    onUbicacionChange: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Tipo y Ubicación",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            var expanded by remember { mutableStateOf(false) }
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = tipo.name.replace("_", " "),
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Tipo de Servicio") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = when (tipo) {
                                TipoServicio.ALOJAMIENTO -> Icons.Default.Hotel
                                TipoServicio.TRANSPORTE -> Icons.Default.DirectionsBus
                                TipoServicio.ALIMENTACION -> Icons.Default.Restaurant
                                TipoServicio.GUIA_TURISTICO -> Icons.Default.Person
                                TipoServicio.TOUR -> Icons.Default.Tour
                                TipoServicio.AVENTURA -> Icons.Default.Hiking
                                TipoServicio.CULTURAL -> Icons.Default.Museum
                                TipoServicio.GASTRONOMICO -> Icons.Default.Restaurant
                                TipoServicio.WELLNESS -> Icons.Default.Spa
                                else -> Icons.Default.Star
                            },
                            contentDescription = null
                        )
                    }
                )
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    TipoServicio.values().forEach { tipoOption ->
                        DropdownMenuItem(
                            text = { Text(tipoOption.name.replace("_", " ")) },
                            onClick = {
                                onTipoChange(tipoOption)
                                expanded = false
                            }
                        )
                    }
                }
            }
            
            OutlinedTextField(
                value = ubicacion,
                onValueChange = onUbicacionChange,
                label = { Text("Ubicación") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@Composable
fun ServicioAdditionalInfoForm(
    requisitos: String,
    onRequisitosChange: (String) -> Unit,
    incluye: String,
    onIncluyeChange: (String) -> Unit,
    noIncluye: String,
    onNoIncluyeChange: (String) -> Unit,
    imagenUrl: String,
    onImagenUrlChange: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Información Adicional",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            OutlinedTextField(
                value = requisitos,
                onValueChange = onRequisitosChange,
                label = { Text("Requisitos") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null
                    )
                }
            )
            
            OutlinedTextField(
                value = incluye,
                onValueChange = onIncluyeChange,
                label = { Text("¿Qué incluye?") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null
                    )
                }
            )
            
            OutlinedTextField(
                value = noIncluye,
                onValueChange = onNoIncluyeChange,
                label = { Text("¿Qué no incluye?") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = null
                    )
                }
            )
            
            OutlinedTextField(
                value = imagenUrl,
                onValueChange = onImagenUrlChange,
                label = { Text("URL de la Imagen") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null
                    )
                }
            )
        }
    }
}