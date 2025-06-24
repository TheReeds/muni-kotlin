package com.capachica.turismokotlin.ui.screens.planes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
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
import com.capachica.turismokotlin.ui.components.LoadingScreen
import com.capachica.turismokotlin.ui.components.TurismoAppBar
import com.capachica.turismokotlin.ui.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanFormScreen(
    planId: Long = 0L, // 0 = crear nuevo, >0 = editar existente
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val planViewModel: PlanTuristicoViewModel = viewModel(factory = factory)
    val servicioViewModel: ServicioTuristicoViewModel = viewModel(factory = factory)
    val municipalidadViewModel: MunicipalidadViewModel = viewModel(factory = factory)
    val authViewModel: AuthViewModel = viewModel(factory = factory)
    
    val planState by planViewModel.planState.collectAsState()
    val createUpdateState by planViewModel.createUpdateState.collectAsState()
    val serviciosState by servicioViewModel.serviciosState.collectAsState()
    val municipalidadesState by municipalidadViewModel.municipalidadesState.collectAsState()
    val userRoles by authViewModel.userRoles.collectAsState()
    
    // Estados del formulario
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var duracionDias by remember { mutableStateOf("") }
    var capacidadMaxima by remember { mutableStateOf("") }
    var nivelDificultad by remember { mutableStateOf(NivelDificultad.FACIL) }
    var imagenPrincipalUrl by remember { mutableStateOf("") }
    var itinerario by remember { mutableStateOf("") }
    var incluye by remember { mutableStateOf("") }
    var noIncluye by remember { mutableStateOf("") }
    var recomendaciones by remember { mutableStateOf("") }
    var requisitos by remember { mutableStateOf("") }
    var selectedMunicipalidadId by remember { mutableStateOf<Long?>(null) }
    
    // Estados para servicios seleccionados
    var selectedServicios by remember { mutableStateOf<List<ServicioPlanFormData>>(emptyList()) }
    var showServiceDialog by remember { mutableStateOf(false) }
    
    // Estados de UI
    var showMunicipalidadDropdown by remember { mutableStateOf(false) }
    var showDificultadDropdown by remember { mutableStateOf(false) }
    
    val isEditing = planId > 0L
    val title = if (isEditing) "Editar Plan" else "Crear Plan"
    val isAdmin = userRoles.contains("ROLE_ADMIN")
    val shouldShowMunicipalidadSelector = isAdmin
    
    // Cargar datos iniciales
    LaunchedEffect(Unit) {
        servicioViewModel.getAllServicios()
        municipalidadViewModel.getAllMunicipalidades()
        
        if (isEditing) {
            planViewModel.getPlanById(planId)
        } else {
            planViewModel.resetStates()
        }
    }
    
    // Llenar formulario cuando se carga el plan para editar
    LaunchedEffect(planState) {
        if (isEditing && planState is Result.Success) {
            val plan = (planState as Result.Success<PlanTuristico>).data
            nombre = plan.nombre
            descripcion = plan.descripcion ?: ""
            duracionDias = plan.duracionDias.toString()
            capacidadMaxima = plan.capacidadMaxima.toString()
            nivelDificultad = plan.nivelDificultad
            imagenPrincipalUrl = plan.imagenPrincipalUrl ?: ""
            itinerario = plan.itinerario ?: ""
            incluye = plan.incluye ?: ""
            noIncluye = plan.noIncluye ?: ""
            recomendaciones = plan.recomendaciones ?: ""
            requisitos = plan.requisitos ?: ""
            selectedMunicipalidadId = plan.municipalidad.id
            
            // Convertir servicios existentes a formato del formulario
            selectedServicios = plan.servicios.map { servicioPlan ->
                ServicioPlanFormData(
                    servicio = servicioPlan.servicio,
                    diaDelPlan = servicioPlan.diaDelPlan,
                    ordenEnElDia = servicioPlan.ordenEnElDia,
                    horaInicio = servicioPlan.horaInicio,
                    horaFin = servicioPlan.horaFin,
                    precioEspecial = servicioPlan.precioEspecial,
                    notas = servicioPlan.notas,
                    esOpcional = servicioPlan.esOpcional,
                    esPersonalizable = servicioPlan.esPersonalizable
                )
            }
        }
    }
    
    // Manejar resultado de crear/actualizar
    LaunchedEffect(createUpdateState) {
        if (createUpdateState is Result.Success) {
            onBack()
        }
    }
    
    Scaffold(
        topBar = {
            TurismoAppBar(
                title = title,
                onBackClick = onBack,
                actions = {
                    TextButton(
                        onClick = {
                            val request = PlanTuristicoRequest(
                                nombre = nombre,
                                descripcion = descripcion.takeIf { it.isNotBlank() },
                                duracionDias = duracionDias.toIntOrNull() ?: 1,
                                capacidadMaxima = capacidadMaxima.toIntOrNull() ?: 1,
                                nivelDificultad = nivelDificultad,
                                imagenPrincipalUrl = imagenPrincipalUrl.takeIf { it.isNotBlank() },
                                itinerario = itinerario.takeIf { it.isNotBlank() },
                                incluye = incluye.takeIf { it.isNotBlank() },
                                noIncluye = noIncluye.takeIf { it.isNotBlank() },
                                recomendaciones = recomendaciones.takeIf { it.isNotBlank() },
                                requisitos = requisitos.takeIf { it.isNotBlank() },
                                municipalidadId = if (isAdmin) selectedMunicipalidadId else null,
                                servicios = selectedServicios.map { it.toServicioPlanRequest() }
                            )
                            
                            if (isEditing) {
                                planViewModel.updatePlan(planId, request)
                            } else {
                                planViewModel.createPlan(request)
                            }
                        },
                        enabled = nombre.isNotBlank() && 
                                 duracionDias.toIntOrNull() != null && 
                                 capacidadMaxima.toIntOrNull() != null &&
                                 (!isAdmin || selectedMunicipalidadId != null) &&
                                 selectedServicios.isNotEmpty()
                    ) {
                        Text(if (isEditing) "Actualizar" else "Crear")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (createUpdateState) {
            is Result.Loading -> {
                LoadingScreen()
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Información básica
                    item {
                        BasicInfoSection(
                            nombre = nombre,
                            onNombreChange = { nombre = it },
                            descripcion = descripcion,
                            onDescripcionChange = { descripcion = it },
                            duracionDias = duracionDias,
                            onDuracionChange = { duracionDias = it },
                            capacidadMaxima = capacidadMaxima,
                            onCapacidadChange = { capacidadMaxima = it },
                            imagenPrincipalUrl = imagenPrincipalUrl,
                            onImagenChange = { imagenPrincipalUrl = it }
                        )
                    }
                    
                    // Municipalidad y dificultad
                    item {
                        ConfigurationSection(
                            municipalidadesState = municipalidadesState,
                            selectedMunicipalidadId = selectedMunicipalidadId,
                            onMunicipalidadChange = { selectedMunicipalidadId = it },
                            nivelDificultad = nivelDificultad,
                            onNivelChange = { nivelDificultad = it },
                            shouldShowMunicipalidadSelector = shouldShowMunicipalidadSelector
                        )
                    }
                    
                    // Detalles del plan
                    item {
                        PlanDetailsSection(
                            itinerario = itinerario,
                            onItinerarioChange = { itinerario = it },
                            incluye = incluye,
                            onIncluyeChange = { incluye = it },
                            noIncluye = noIncluye,
                            onNoIncluyeChange = { noIncluye = it },
                            recomendaciones = recomendaciones,
                            onRecomendacionesChange = { recomendaciones = it },
                            requisitos = requisitos,
                            onRequisitosChange = { requisitos = it }
                        )
                    }
                    
                    // Servicios del plan
                    item {
                        ServicesSection(
                            selectedServicios = selectedServicios,
                            onAddService = { showServiceDialog = true },
                            onRemoveService = { service ->
                                selectedServicios = selectedServicios.filter { it != service }
                            },
                            onEditService = { service, newData ->
                                selectedServicios = selectedServicios.map { 
                                    if (it == service) newData else it 
                                }
                            }
                        )
                    }
                    
                    // Mostrar errores
                    createUpdateState?.let { state ->
                        if (state is Result.Error) {
                            item {
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
                                            Icons.Default.Error,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = state.message,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Dialog para seleccionar servicios
    if (showServiceDialog) {
        ServiceSelectionDialog(
            serviciosState = serviciosState,
            onDismiss = { showServiceDialog = false },
            onServiceSelected = { service ->
                val newService = ServicioPlanFormData(
                    servicio = service,
                    diaDelPlan = 1,
                    ordenEnElDia = selectedServicios.count { it.diaDelPlan == 1 } + 1,
                    horaInicio = null,
                    horaFin = null,
                    precioEspecial = null,
                    notas = null,
                    esOpcional = false,
                    esPersonalizable = false
                )
                selectedServicios = selectedServicios + newService
                showServiceDialog = false
            }
        )
    }
}

// Clase de datos para el formulario
data class ServicioPlanFormData(
    val servicio: ServicioTuristico,
    val diaDelPlan: Int,
    val ordenEnElDia: Int,
    val horaInicio: String?,
    val horaFin: String?,
    val precioEspecial: Double?,
    val notas: String?,
    val esOpcional: Boolean,
    val esPersonalizable: Boolean
) {
    fun toServicioPlanRequest(): ServicioPlanRequest {
        return ServicioPlanRequest(
            servicioId = servicio.id,
            diaDelPlan = diaDelPlan,
            ordenEnElDia = ordenEnElDia,
            horaInicio = horaInicio,
            horaFin = horaFin,
            precioEspecial = precioEspecial,
            notas = notas,
            esOpcional = esOpcional,
            esPersonalizable = esPersonalizable
        )
    }
}

@Composable
fun BasicInfoSection(
    nombre: String,
    onNombreChange: (String) -> Unit,
    descripcion: String,
    onDescripcionChange: (String) -> Unit,
    duracionDias: String,
    onDuracionChange: (String) -> Unit,
    capacidadMaxima: String,
    onCapacidadChange: (String) -> Unit,
    imagenPrincipalUrl: String,
    onImagenChange: (String) -> Unit
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Información Básica",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = nombre,
                onValueChange = onNombreChange,
                label = { Text("Nombre del Plan *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = descripcion,
                onValueChange = onDescripcionChange,
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = duracionDias,
                    onValueChange = onDuracionChange,
                    label = { Text("Duración (días) *") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = capacidadMaxima,
                    onValueChange = onCapacidadChange,
                    label = { Text("Capacidad máx. *") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = imagenPrincipalUrl,
                onValueChange = onImagenChange,
                label = { Text("URL de imagen principal") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationSection(
    municipalidadesState: Result<List<Municipalidad>>,
    selectedMunicipalidadId: Long?,
    onMunicipalidadChange: (Long?) -> Unit,
    nivelDificultad: NivelDificultad,
    onNivelChange: (NivelDificultad) -> Unit,
    shouldShowMunicipalidadSelector: Boolean = true
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Configuración",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Selector de municipalidad (solo para ADMIN)
            if (shouldShowMunicipalidadSelector) {
                when (municipalidadesState) {
                is Result.Success -> {
                    var expanded by remember { mutableStateOf(false) }
                    val selectedMunicipalidad = municipalidadesState.data.find { it.id == selectedMunicipalidadId }
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedMunicipalidad?.nombre ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Municipalidad *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            municipalidadesState.data.forEach { municipalidad ->
                                DropdownMenuItem(
                                    text = { Text(municipalidad.nombre) },
                                    onClick = {
                                        onMunicipalidadChange(municipalidad.id)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                is Result.Loading -> {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Cargando municipalidades...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                is Result.Error -> {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Error al cargar municipalidades") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = true
                    )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

            }
            
            // Selector de nivel de dificultad
            var expandedDificultad by remember { mutableStateOf(false) }
            
            ExposedDropdownMenuBox(
                expanded = expandedDificultad,
                onExpandedChange = { expandedDificultad = !expandedDificultad }
            ) {
                OutlinedTextField(
                    value = nivelDificultad.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Nivel de Dificultad *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDificultad) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                
                ExposedDropdownMenu(
                    expanded = expandedDificultad,
                    onDismissRequest = { expandedDificultad = false }
                ) {
                    NivelDificultad.values().forEach { nivel ->
                        DropdownMenuItem(
                            text = { Text(nivel.name) },
                            onClick = {
                                onNivelChange(nivel)
                                expandedDificultad = false
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun PlanDetailsSection(
    itinerario: String,
    onItinerarioChange: (String) -> Unit,
    incluye: String,
    onIncluyeChange: (String) -> Unit,
    noIncluye: String,
    onNoIncluyeChange: (String) -> Unit,
    recomendaciones: String,
    onRecomendacionesChange: (String) -> Unit,
    requisitos: String,
    onRequisitosChange: (String) -> Unit
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Detalles del Plan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = itinerario,
                onValueChange = onItinerarioChange,
                label = { Text("Itinerario") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = incluye,
                onValueChange = onIncluyeChange,
                label = { Text("Incluye") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = noIncluye,
                onValueChange = onNoIncluyeChange,
                label = { Text("No incluye") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = recomendaciones,
                onValueChange = onRecomendacionesChange,
                label = { Text("Recomendaciones") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = requisitos,
                onValueChange = onRequisitosChange,
                label = { Text("Requisitos") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )
        }
    }
}

@Composable
fun ServicesSection(
    selectedServicios: List<ServicioPlanFormData>,
    onAddService: () -> Unit,
    onRemoveService: (ServicioPlanFormData) -> Unit,
    onEditService: (ServicioPlanFormData, ServicioPlanFormData) -> Unit
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Servicios del Plan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(
                    onClick = onAddService
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Agregar")
                }
            }
            
            if (selectedServicios.isEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No hay servicios agregados. Agrega al menos un servicio al plan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                
                selectedServicios.sortedWith(
                    compareBy<ServicioPlanFormData> { it.diaDelPlan }.thenBy { it.ordenEnElDia }
                ).forEach { servicio ->
                    ServiceCard(
                        servicio = servicio,
                        onRemove = { onRemoveService(servicio) },
                        onEdit = { newData -> onEditService(servicio, newData) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceCard(
    servicio: ServicioPlanFormData,
    onRemove: () -> Unit,
    onEdit: (ServicioPlanFormData) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = servicio.servicio.nombre,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Día ${servicio.diaDelPlan} - Orden: ${servicio.ordenEnElDia}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (servicio.horaInicio != null || servicio.horaFin != null) {
                        Text(
                            text = "Horario: ${servicio.horaInicio ?: "?"} - ${servicio.horaFin ?: "?"}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    if (servicio.precioEspecial != null) {
                        Text(
                            text = "Precio especial: S/. ${servicio.precioEspecial}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Row {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = onRemove) {
                        Icon(
                            Icons.Default.Delete, 
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            if (servicio.esOpcional || servicio.esPersonalizable) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    if (servicio.esOpcional) {
                        item {
                            AssistChip(
                                onClick = { },
                                label = { Text("Opcional") }
                            )
                        }
                    }
                    if (servicio.esPersonalizable) {
                        item {
                            AssistChip(
                                onClick = { },
                                label = { Text("Personalizable") }
                            )
                        }
                    }
                }
            }
        }
    }
    
    if (showEditDialog) {
        ServiceEditDialog(
            servicio = servicio,
            onDismiss = { showEditDialog = false },
            onSave = { newData ->
                onEdit(newData)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun ServiceSelectionDialog(
    serviciosState: Result<List<ServicioTuristico>>,
    onDismiss: () -> Unit,
    onServiceSelected: (ServicioTuristico) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Servicio") },
        text = {
            when (serviciosState) {
                is Result.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is Result.Success -> {
                    if (serviciosState.data.isEmpty()) {
                        Text("No hay servicios disponibles")
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                        ) {
                            items(serviciosState.data.filter { it.estado == EstadoServicio.ACTIVO }) { servicio ->
                                Card(
                                    onClick = { onServiceSelected(servicio) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Text(
                                            text = servicio.nombre,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = servicio.tipo.name,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "S/. ${servicio.precio} - ${servicio.duracionHoras}h",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        servicio.emprendedor.municipalidad?.let { municipalidad ->
                                            Text(
                                                text = municipalidad.nombre,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is Result.Error -> {
                    Text(
                        text = serviciosState.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun ServiceEditDialog(
    servicio: ServicioPlanFormData,
    onDismiss: () -> Unit,
    onSave: (ServicioPlanFormData) -> Unit
) {
    var diaDelPlan by remember { mutableStateOf(servicio.diaDelPlan.toString()) }
    var ordenEnElDia by remember { mutableStateOf(servicio.ordenEnElDia.toString()) }
    var horaInicio by remember { mutableStateOf(servicio.horaInicio ?: "") }
    var horaFin by remember { mutableStateOf(servicio.horaFin ?: "") }
    var precioEspecial by remember { mutableStateOf(servicio.precioEspecial?.toString() ?: "") }
    var notas by remember { mutableStateOf(servicio.notas ?: "") }
    var esOpcional by remember { mutableStateOf(servicio.esOpcional) }
    var esPersonalizable by remember { mutableStateOf(servicio.esPersonalizable) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Servicio: ${servicio.servicio.nombre}") },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = diaDelPlan,
                            onValueChange = { diaDelPlan = it },
                            label = { Text("Día") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = ordenEnElDia,
                            onValueChange = { ordenEnElDia = it },
                            label = { Text("Orden") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }
                
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = horaInicio,
                            onValueChange = { horaInicio = it },
                            label = { Text("Hora inicio") },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("09:00") }
                        )
                        OutlinedTextField(
                            value = horaFin,
                            onValueChange = { horaFin = it },
                            label = { Text("Hora fin") },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("17:00") }
                        )
                    }
                }
                
                item {
                    OutlinedTextField(
                        value = precioEspecial,
                        onValueChange = { precioEspecial = it },
                        label = { Text("Precio especial") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        placeholder = { Text("Dejar vacío para usar precio normal") }
                    )
                }
                
                item {
                    OutlinedTextField(
                        value = notas,
                        onValueChange = { notas = it },
                        label = { Text("Notas") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 3
                    )
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = esOpcional,
                            onCheckedChange = { esOpcional = it }
                        )
                        Text(
                            text = "Servicio opcional",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = esPersonalizable,
                            onCheckedChange = { esPersonalizable = it }
                        )
                        Text(
                            text = "Personalizable",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val newData = servicio.copy(
                        diaDelPlan = diaDelPlan.toIntOrNull() ?: servicio.diaDelPlan,
                        ordenEnElDia = ordenEnElDia.toIntOrNull() ?: servicio.ordenEnElDia,
                        horaInicio = horaInicio.takeIf { it.isNotBlank() },
                        horaFin = horaFin.takeIf { it.isNotBlank() },
                        precioEspecial = precioEspecial.toDoubleOrNull(),
                        notas = notas.takeIf { it.isNotBlank() },
                        esOpcional = esOpcional,
                        esPersonalizable = esPersonalizable
                    )
                    onSave(newData)
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
