package com.capachica.turismokotlin.ui.screens.Planes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.capachica.turismokotlin.ui.viewmodel.CartViewModel
import androidx.compose.runtime.Composable
import com.capachica.turismokotlin.data.model.Plan
import com.capachica.turismokotlin.ui.components.PlanCard
import com.capachica.turismokotlin.ui.components.PlanListItem
import com.capachica.turismokotlin.ui.viewmodel.PlanesUiState
import com.capachica.turismokotlin.ui.viewmodel.PlanesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPlan: (Long) -> Unit,
    onNavigateToCart: () -> Unit,
    viewModel: PlanesViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cartItemCount by cartViewModel.itemCount.collectAsStateWithLifecycle(initialValue = 0)

    var searchQuery by remember { mutableStateOf("") }
    var showSearchResults by remember { mutableStateOf(false) }
    var showPriceFilter by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Planes Turísticos") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                IconButton(onClick = { showPriceFilter = !showPriceFilter }) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filtros")
                }

                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge { Text("$cartItemCount") }
                        }
                    }
                ) {
                    IconButton(onClick = onNavigateToCart) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito")
                    }
                }
            }
        )

        // Barra de búsqueda
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    if (it.isNotEmpty()) {
                        viewModel.searchPlanes(it)
                        showSearchResults = true
                    } else {
                        showSearchResults = false
                        viewModel.clearSearchResults()
                    }
                },
                label = { Text("Buscar planes...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            showSearchResults = false
                            viewModel.clearSearchResults()
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true
            )
        }

        // Filtro de precio
        if (showPriceFilter) {
            PriceFilterCard(
                onApplyFilter = { min, max ->
                    viewModel.getPlanesByPrecio(min, max)
                    showPriceFilter = false
                },
                onDismiss = { showPriceFilter = false }
            )
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
                    Button(onClick = { viewModel.loadPlanes() }) {
                        Text("Reintentar")
                    }
                }
            }

            showSearchResults -> {
                PlanesSearchResults(
                    searchResults = uiState.searchResults,
                    isSearching = uiState.isSearching,
                    onNavigateToPlan = onNavigateToPlan
                )
            }

            uiState.filteredPlanes.isNotEmpty() -> {
                PlanesFilteredResults(
                    filteredPlanes = uiState.filteredPlanes,
                    onNavigateToPlan = onNavigateToPlan,
                    onClearFilter = { viewModel.clearSearchResults() }
                )
            }

            else -> {
                PlanesContent(
                    uiState = uiState,
                    onNavigateToPlan = onNavigateToPlan
                )
            }
        }
    }
}

@Composable
private fun PlanesContent(
    uiState: PlanesUiState,
    onNavigateToPlan: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Planes Populares
        if (uiState.planesPopulares.isNotEmpty()) {
            item {
                Text(
                    text = "Planes Populares",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(uiState.planesPopulares) { plan ->
                        PlanCard(
                            plan = plan,
                            onClick = { onNavigateToPlan(plan.id) },
                            onAddToCart = { /* Para planes usaremos reserva directa */ }
                        )
                    }
                }
            }
        }

        // Todos los Planes
        item {
            Text(
                text = "Todos los Planes (${uiState.planes.size})",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        items(uiState.planes) { plan ->
            PlanListItem(
                plan = plan,
                onClick = { onNavigateToPlan(plan.id) },
                onAddToCart = { /* Para planes usaremos reserva directa */ }
            )
        }
    }
}

@Composable
private fun PlanesSearchResults(
    searchResults: List<Plan>,
    isSearching: Boolean,
    onNavigateToPlan: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Resultados de búsqueda (${searchResults.size})",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                if (isSearching) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                }
            }
        }

        if (searchResults.isEmpty() && !isSearching) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "No se encontraron planes",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        } else {
            items(searchResults) { plan ->
                PlanListItem(
                    plan = plan,
                    onClick = { onNavigateToPlan(plan.id) },
                    onAddToCart = { /* Para planes usaremos reserva directa */ }
                )
            }
        }
    }
}

@Composable
private fun PlanesFilteredResults(
    filteredPlanes: List<Plan>,
    onNavigateToPlan: (Long) -> Unit,
    onClearFilter: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Planes filtrados (${filteredPlanes.size})",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = onClearFilter) {
                    Text("Limpiar filtro")
                }
            }
        }

        items(filteredPlanes) { plan ->
            PlanListItem(
                plan = plan,
                onClick = { onNavigateToPlan(plan.id) },
                onAddToCart = { /* Para planes usaremos reserva directa */ }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PriceFilterCard(
    onApplyFilter: (Double, Double) -> Unit,
    onDismiss: () -> Unit
) {
    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Filtrar por Precio",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = minPrice,
                    onValueChange = { minPrice = it },
                    label = { Text("Precio mín.") },
                    placeholder = { Text("0") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = maxPrice,
                    onValueChange = { maxPrice = it },
                    label = { Text("Precio máx.") },
                    placeholder = { Text("1000") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        val min = minPrice.toDoubleOrNull() ?: 0.0
                        val max = maxPrice.toDoubleOrNull() ?: 10000.0
                        onApplyFilter(min, max)
                    },
                    modifier = Modifier.weight(1f),
                    enabled = minPrice.isNotBlank() || maxPrice.isNotBlank()
                ) {
                    Text("Aplicar")
                }
            }
        }
    }
}