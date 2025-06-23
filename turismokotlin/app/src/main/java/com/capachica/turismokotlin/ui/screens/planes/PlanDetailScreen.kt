package com.capachica.turismokotlin.ui.screens.planes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capachica.turismokotlin.data.model.*
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.components.ErrorScreen
import com.capachica.turismokotlin.ui.components.LoadingScreen
import com.capachica.turismokotlin.ui.components.TurismoAppBar
import com.capachica.turismokotlin.ui.viewmodel.PlanTuristicoViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanDetailScreen(
    planId: Long,
    onNavigateToReserva: (Long) -> Unit,
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val viewModel: PlanTuristicoViewModel = viewModel(factory = factory)
    val planState by viewModel.planState.collectAsState()
    
    // Cargar datos al inicio
    LaunchedEffect(planId) {
        viewModel.getPlanById(planId)
    }
    
    Scaffold(
        topBar = {
            TurismoAppBar(
                title = "Detalle del Plan",
                onBackClick = onBack
            )
        }
    ) { paddingValues ->
        when (val state = planState) {
            is Result.Loading -> LoadingScreen()
            is Result.Error -> ErrorScreen(
                message = state.message,
                onRetry = { viewModel.getPlanById(planId) }
            )
            is Result.Success -> {
                PlanDetailContent(
                    plan = state.data,
                    onNavigateToReserva = onNavigateToReserva,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun PlanDetailContent(
    plan: PlanTuristico,
    onNavigateToReserva: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header del plan
        item {
            PlanHeaderCard(plan = plan)
        }
        
        // Información general
        item {
            PlanInfoCard(plan = plan)
        }
        
        // Itinerario de servicios
        item {
            Text(
                text = "Itinerario",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        items(plan.servicios.groupBy { it.diaDelPlan }.toList()) { (dia, servicios) ->
            DiaItinerarioCard(
                dia = dia,
                servicios = servicios.sortedBy { it.ordenEnElDia }
            )
        }
        
        // Información adicional
        if (!plan.incluye.isNullOrEmpty()) {
            item {
                InfoCard(
                    title = "Incluye",
                    content = plan.incluye,
                    icon = Icons.Default.CheckCircle,
                    iconColor = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        if (!plan.noIncluye.isNullOrEmpty()) {
            item {
                InfoCard(
                    title = "No Incluye",
                    content = plan.noIncluye,
                    icon = Icons.Default.Cancel,
                    iconColor = MaterialTheme.colorScheme.error
                )
            }
        }
        
        if (!plan.requisitos.isNullOrEmpty()) {
            item {
                InfoCard(
                    title = "Requisitos",
                    content = plan.requisitos,
                    icon = Icons.Default.Assignment,
                    iconColor = MaterialTheme.colorScheme.tertiary
                )
            }
        }
        
        if (!plan.recomendaciones.isNullOrEmpty()) {
            item {
                InfoCard(
                    title = "Recomendaciones",
                    content = plan.recomendaciones,
                    icon = Icons.Default.Lightbulb,
                    iconColor = MaterialTheme.colorScheme.secondary
                )
            }
        }
        
        // Botón de reserva
        item {
            Button(
                onClick = { onNavigateToReserva(plan.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.BookOnline,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Reservar por $${plan.precioTotal}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun PlanHeaderCard(plan: PlanTuristico) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = plan.nombre,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${plan.municipalidad.nombre}, ${plan.municipalidad.distrito}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            if (!plan.descripcion.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = plan.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun PlanInfoCard(plan: PlanTuristico) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Información General",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            InfoRow(
                icon = Icons.Default.Schedule,
                label = "Duración",
                value = "${plan.duracionDias} día${if (plan.duracionDias > 1) "s" else ""}"
            )
            
            InfoRow(
                icon = Icons.Default.Group,
                label = "Capacidad máxima",
                value = "${plan.capacidadMaxima} personas"
            )
            
            InfoRow(
                icon = when (plan.nivelDificultad) {
                    NivelDificultad.FACIL -> Icons.Default.SentimentSatisfied
                    NivelDificultad.MODERADO -> Icons.Default.SentimentNeutral
                    NivelDificultad.DIFICIL -> Icons.Default.SentimentDissatisfied
                    NivelDificultad.EXTREMO -> Icons.Default.Warning
                },
                label = "Dificultad",
                value = plan.nivelDificultad.name
            )
            
            InfoRow(
                icon = Icons.Default.AttachMoney,
                label = "Precio por persona",
                value = "$${plan.precioTotal}"
            )
            
            InfoRow(
                icon = Icons.Default.Star,
                label = "Total de reservas",
                value = "${plan.totalReservas}"
            )
        }
    }
}

@Composable
fun DiaItinerarioCard(
    dia: Int,
    servicios: List<ServicioPlan>
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Día $dia",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            servicios.forEach { servicio ->
                ServicioItinerarioItem(servicio = servicio)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ServicioItinerarioItem(servicio: ServicioPlan) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Hora
        if (!servicio.horaInicio.isNullOrEmpty()) {
            Text(
                text = servicio.horaInicio,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(60.dp)
            )
        } else {
            Spacer(modifier = Modifier.width(60.dp))
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = servicio.servicio.nombre,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            if (!servicio.servicio.descripcion.isNullOrEmpty()) {
                Text(
                    text = servicio.servicio.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (servicio.esOpcional) {
                Text(
                    text = "Opcional",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            
            if (!servicio.notas.isNullOrEmpty()) {
                Text(
                    text = "Nota: ${servicio.notas}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    content: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = iconColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}