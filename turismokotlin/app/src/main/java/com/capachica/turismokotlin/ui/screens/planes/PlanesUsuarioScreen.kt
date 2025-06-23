package com.capachica.turismokotlin.ui.screens.planes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capachica.turismokotlin.data.model.*
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.components.EmptyListPlaceholder
import com.capachica.turismokotlin.ui.components.ErrorScreen
import com.capachica.turismokotlin.ui.components.LoadingScreen
import com.capachica.turismokotlin.ui.components.TurismoAppBar
import com.capachica.turismokotlin.ui.viewmodel.PlanTuristicoViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanesUsuarioScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToReserva: (Long) -> Unit,
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val viewModel: PlanTuristicoViewModel = viewModel(factory = factory)
    val planesState by viewModel.planesState.collectAsState()
    
    var showFilters by remember { mutableStateOf(false) }
    var selectedNivel by remember { mutableStateOf<NivelDificultad?>(null) }
    var selectedEstado by remember { mutableStateOf(EstadoPlan.ACTIVO) }
    var searchText by remember { mutableStateOf("") }
    
    // Cargar datos al inicio
    LaunchedEffect(Unit) {
        viewModel.getPlanesByEstado(EstadoPlan.ACTIVO)
    }
    
    // Aplicar filtros cuando cambien
    LaunchedEffect(selectedNivel, selectedEstado, searchText) {
        when {
            searchText.isNotEmpty() -> viewModel.searchPlanes(searchText)
            selectedNivel != null -> viewModel.getPlanesByNivelDificultad(selectedNivel!!)
            else -> viewModel.getPlanesByEstado(selectedEstado)
        }
    }
    
    Scaffold(
        topBar = {
            TurismoAppBar(
                title = "Planes Turísticos",
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filtros"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Barra de búsqueda
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Buscar planes...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            
            // Filtros desplegables
            if (showFilters) {
                FilterSection(
                    selectedNivel = selectedNivel,
                    onNivelChange = { selectedNivel = it },
                    onClearFilters = {
                        selectedNivel = null
                        searchText = ""
                    }
                )
            }
            
            // Lista de planes
            when (val state = planesState) {
                is Result.Loading -> LoadingScreen()
                is Result.Error -> ErrorScreen(
                    message = state.message,
                    onRetry = { viewModel.getPlanesByEstado(EstadoPlan.ACTIVO) }
                )
                is Result.Success -> {
                    if (state.data.isEmpty()) {
                        EmptyListPlaceholder(
                            message = "No hay planes disponibles",
                            buttonText = "Recargar",
                            onButtonClick = { viewModel.getPlanesByEstado(EstadoPlan.ACTIVO) }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            items(state.data) { plan ->
                                PlanTuristicoCard(
                                    plan = plan,
                                    onCardClick = { onNavigateToDetail(plan.id) },
                                    onReservarClick = { onNavigateToReserva(plan.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterSection(
    selectedNivel: NivelDificultad?,
    onNivelChange: (NivelDificultad?) -> Unit,
    onClearFilters: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Filtros",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Filtro por nivel de dificultad
            Text("Nivel de Dificultad:", style = MaterialTheme.typography.bodyMedium)
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items(NivelDificultad.values()) { nivel ->
                    FilterChip(
                        onClick = {
                            onNivelChange(if (selectedNivel == nivel) null else nivel)
                        },
                        label = { Text(nivel.name) },
                        selected = selectedNivel == nivel
                    )
                }
            }
            
            // Botón limpiar filtros
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onClearFilters) {
                    Text("Limpiar Filtros")
                }
            }
        }
    }
}

@Composable
fun PlanTuristicoCard(
    plan: PlanTuristico,
    onCardClick: () -> Unit,
    onReservarClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onCardClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header con imagen y título
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Placeholder para imagen
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Landscape,
                            contentDescription = null,
                            modifier = Modifier
                                .size(60.dp)
                                .padding(10.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = plan.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = plan.municipalidad.nombre,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${plan.duracionDias} día${if (plan.duracionDias > 1) "s" else ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Descripción
            if (!plan.descripcion.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = plan.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Información adicional
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when (plan.nivelDificultad) {
                                NivelDificultad.FACIL -> Icons.Default.SentimentSatisfied
                                NivelDificultad.MODERADO -> Icons.Default.SentimentNeutral
                                NivelDificultad.DIFICIL -> Icons.Default.SentimentDissatisfied
                                NivelDificultad.EXTREMO -> Icons.Default.Warning
                            },
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = when (plan.nivelDificultad) {
                                NivelDificultad.FACIL -> MaterialTheme.colorScheme.primary
                                NivelDificultad.MODERADO -> MaterialTheme.colorScheme.tertiary
                                NivelDificultad.DIFICIL -> MaterialTheme.colorScheme.error
                                NivelDificultad.EXTREMO -> MaterialTheme.colorScheme.error
                            }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = plan.nivelDificultad.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Máx. ${plan.capacidadMaxima} personas",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${plan.precioTotal}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "por persona",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Botón de reserva
            Button(
                onClick = onReservarClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.BookOnline,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reservar")
            }
        }
    }
}