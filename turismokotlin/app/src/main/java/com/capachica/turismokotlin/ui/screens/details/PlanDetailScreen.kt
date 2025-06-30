package com.capachica.turismokotlin.ui.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.capachica.turismokotlin.data.model.MetodoPago
import com.capachica.turismokotlin.data.model.Plan
import com.capachica.turismokotlin.data.model.ServicioPlan
import com.capachica.turismokotlin.ui.viewmodel.PlanDetailViewModel
import com.capachica.turismokotlin.ui.viewmodel.PlanReservaUiState
import com.capachica.turismokotlin.ui.viewmodel.PlanReservaViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanDetailScreen(
    planId: Long,
    onNavigateBack: () -> Unit,
    onAddToCart: () -> Unit = {}, // Ya no se usa pero mantenemos compatibilidad
    viewModel: PlanDetailViewModel = hiltViewModel(),
    reservaViewModel: PlanReservaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val reservaState by reservaViewModel.uiState.collectAsState()

    var showReservaDialog by remember { mutableStateOf(false) }

    LaunchedEffect(planId) {
        viewModel.loadPlanDetails(planId)
    }

    // Manejar éxito de reserva
    LaunchedEffect(reservaState.reservaCreada) {
        reservaState.reservaCreada?.let {
            showReservaDialog = false
            // Aquí podrías navegar a reservas o mostrar un mensaje de éxito
        }
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

        uiState.error != null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Error: ${uiState.error}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
                Button(onClick = { viewModel.loadPlanDetails(planId) }) {
                    Text("Reintentar")
                }
            }
        }

        uiState.plan != null -> {
            PlanDetailContent(
                plan = uiState.plan!!,
                onNavigateBack = onNavigateBack,
                onReservar = { showReservaDialog = true }
            )
        }
    }

    // Dialog de reserva
    if (showReservaDialog && uiState.plan != null) {
        ReservaPlanDialog(
            plan = uiState.plan!!,
            reservaState = reservaState,
            onDismiss = {
                showReservaDialog = false
                reservaViewModel.clearMessages()
            },
            onConfirm = { cantidad, fechaInicio, observaciones, contactoEmergencia, telefonoEmergencia, metodoPago ->
                reservaViewModel.crearReservaPlan(
                    planId = uiState.plan!!.id,
                    cantidad = cantidad,
                    fechaInicio = fechaInicio,
                    observaciones = observaciones,
                    contactoEmergencia = contactoEmergencia,
                    telefonoEmergencia = telefonoEmergencia,
                    metodoPago = metodoPago
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanDetailContent(
    plan: Plan,
    onNavigateBack: () -> Unit,
    onReservar: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box {
            // Imagen principal
            AsyncImage(
                model = plan.imagenPrincipalUrl ?: "https://via.placeholder.com/400x250",
                contentDescription = plan.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )

            // Top bar transparente
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                        )
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { /* Compartir */ },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                        )
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir")
                    }
                    IconButton(
                        onClick = { /* Favorito */ },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                        )
                    ) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorito")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Información básica
                Column {
                    Text(
                        text = plan.nombre,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AssistChip(
                            onClick = { },
                            label = { Text(plan.nivelDificultad.name) },
                            leadingIcon = { Icon(Icons.Default.TrendingUp, contentDescription = null) }
                        )

                        AssistChip(
                            onClick = { },
                            label = { Text("${plan.duracionDias} días") },
                            leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) }
                        )

                        AssistChip(
                            onClick = { },
                            label = { Text("Máx. ${plan.capacidadMaxima}") },
                            leadingIcon = { Icon(Icons.Default.People, contentDescription = null) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = plan.descripcion,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            item {
                // Precio y reservas
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Precio por persona",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "S/ ${plan.precioTotal}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${plan.totalReservas} reservas",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = plan.estado.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            item {
                // Información del creador
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Creado por",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(8.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "${plan.usuarioCreador.nombre} ${plan.usuarioCreador.apellido}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = plan.municipalidad.nombre,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Itinerario
            if (!plan.itinerario.isNullOrEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Itinerario",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = plan.itinerario,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            // Servicios incluidos
            if (plan.servicios.isNotEmpty()) {
                item {
                    Text(
                        text = "Servicios Incluidos (${plan.servicios.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(plan.servicios) { servicioPlan ->
                    ServicioPlanCard(servicioPlan = servicioPlan)
                }
            }

            // Incluye / No incluye
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!plan.incluye.isNullOrEmpty()) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Incluye",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = plan.incluye,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }

                    if (!plan.noIncluye.isNullOrEmpty()) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Cancel,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "No incluye",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = plan.noIncluye,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }

            // Recomendaciones y requisitos
            if (!plan.recomendaciones.isNullOrEmpty() || !plan.requisitos.isNullOrEmpty()) {
                item {
                    Column {
                        if (!plan.recomendaciones.isNullOrEmpty()) {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Recomendaciones",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = plan.recomendaciones,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        if (!plan.requisitos.isNullOrEmpty()) {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Requisitos",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = plan.requisitos,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Botón de reserva
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Button(
                onClick = onReservar,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = plan.estado.name != "INACTIVO" && plan.estado.name != "AGOTADO"
            ) {
                Icon(Icons.Default.BookOnline, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reservar Plan - S/ ${plan.precioTotal}")
            }
        }
    }
}

@Composable
private fun ServicioPlanCard(
    servicioPlan: ServicioPlan,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Día ${servicioPlan.diaDelPlan} - Orden ${servicioPlan.ordenEnElDia}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${servicioPlan.horaInicio} - ${servicioPlan.horaFin}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = servicioPlan.servicio.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = servicioPlan.servicio.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = servicioPlan.servicio.emprendedor?.nombreEmpresa ?: "Empresa no disponible",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )


                if (servicioPlan.precioEspecial > 0) {
                    Text(
                        text = "S/ ${servicioPlan.precioEspecial}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (servicioPlan.esOpcional || servicioPlan.esPersonalizable) {
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (servicioPlan.esOpcional) {
                        AssistChip(
                            onClick = { },
                            label = { Text("Opcional") }
                        )
                    }
                    if (servicioPlan.esPersonalizable) {
                        AssistChip(
                            onClick = { },
                            label = { Text("Personalizable") }
                        )
                    }
                }
            }

            servicioPlan.notas?.let { notas ->
                if (notas.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Nota: $notas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}

@Composable
private fun ReservaPlanDialog(
    plan: Plan,
    reservaState: PlanReservaUiState,
    onDismiss: () -> Unit,
    onConfirm: (Int, String, String?, String?, String?, MetodoPago) -> Unit
) {
    var cantidad by remember { mutableStateOf(1) }
    var fechaInicio by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }
    var contactoEmergencia by remember { mutableStateOf("") }
    var telefonoEmergencia by remember { mutableStateOf("") }
    var metodoPago by remember { mutableStateOf(MetodoPago.EFECTIVO) }

    // Fecha por defecto (próxima semana)
    LaunchedEffect(Unit) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 7)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        fechaInicio = dateFormat.format(calendar.time)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Reservar Plan",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = plan.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "S/ ${plan.precioTotal} por persona",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    OutlinedTextField(
                        value = cantidad.toString(),
                        onValueChange = { newValue ->
                            newValue.toIntOrNull()?.let {
                                if (it > 0 && it <= plan.capacidadMaxima) {
                                    cantidad = it
                                }
                            }
                        },
                        label = { Text("Número de personas *") },
                        supportingText = { Text("Máximo: ${plan.capacidadMaxima}") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = fechaInicio,
                        onValueChange = { fechaInicio = it },
                        label = { Text("Fecha de inicio *") },
                        placeholder = { Text("YYYY-MM-DD") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = contactoEmergencia,
                        onValueChange = { contactoEmergencia = it },
                        label = { Text("Contacto de emergencia") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = telefonoEmergencia,
                        onValueChange = { telefonoEmergencia = it },
                        label = { Text("Teléfono de emergencia") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    Text(
                        text = "Método de pago",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    MetodoPago.values().forEach { metodo ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = metodoPago == metodo,
                                    onClick = { metodoPago = metodo }
                                )
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = metodoPago == metodo,
                                onClick = { metodoPago = metodo }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (metodo) {
                                    MetodoPago.EFECTIVO -> "Efectivo"
                                    MetodoPago.TARJETA -> "Tarjeta"
                                    MetodoPago.TRANSFERENCIA -> "Transferencia"
                                    MetodoPago.YAPE -> "Yape"
                                    MetodoPago.PLIN -> "Plin"
                                }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = observaciones,
                        onValueChange = { observaciones = it },
                        label = { Text("Observaciones (opcional)") },
                        placeholder = { Text("Solicitudes especiales...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total a pagar:")
                            Text(
                                text = "S/ ${plan.precioTotal * cantidad}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                if (reservaState.error != null) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = reservaState.error,
                                modifier = Modifier.padding(12.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        cantidad,
                        fechaInicio,
                        observaciones.takeIf { it.isNotBlank() },
                        contactoEmergencia.takeIf { it.isNotBlank() },
                        telefonoEmergencia.takeIf { it.isNotBlank() },
                        metodoPago
                    )
                },
                enabled = fechaInicio.isNotBlank() && !reservaState.isLoading
            ) {
                if (reservaState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Confirmar Reserva")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// Necesitarás crear este ViewModel para manejar reservas de planes
data class PlanReservaUiState(
    val isLoading: Boolean = false,
    val reservaCreada: Any? = null, // Cambiar por el tipo correcto de reserva
    val error: String? = null
)