package com.capachica.turismokotlin.ui.screens.servicios

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
import com.capachica.turismokotlin.data.model.Servicio
import com.capachica.turismokotlin.ui.components.ServiceCard
import com.capachica.turismokotlin.ui.viewmodel.CartViewModel
import androidx.compose.runtime.Composable
import com.capachica.turismokotlin.ui.viewmodel.ServiciosViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiciosScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEmprendedor: (Long) -> Unit,
    onNavigateToServicioDetail: (Long) -> Unit,
    onNavigateToCart: () -> Unit,
    viewModel: ServiciosViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cartItemCount by cartViewModel.itemCount.collectAsStateWithLifecycle(initialValue = 0)

    var searchQuery by remember { mutableStateOf("") }
    var showSearchResults by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedServicio by remember { mutableStateOf<Servicio?>(null) }

    // Manejar mensajes del carrito
    LaunchedEffect(cartViewModel.uiState.collectAsState().value.successMessage) {
        cartViewModel.uiState.value.successMessage?.let {
            delay(2000)
            cartViewModel.clearMessages()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Servicios Turísticos") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
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
                        viewModel.searchServicios(it)
                        showSearchResults = true
                    } else {
                        showSearchResults = false
                        viewModel.clearSearchResults()
                    }
                },
                label = { Text("Buscar servicios...") },
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

        // Mensaje de éxito del carrito
        cartViewModel.uiState.collectAsState().value.successMessage?.let { message ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
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
                    Button(onClick = { viewModel.loadServicios() }) {
                        Text("Reintentar")
                    }
                }
            }

            showSearchResults -> {
                ServiciosSearchResults(
                    searchResults = uiState.searchResults,
                    isSearching = uiState.isSearching,
                    onNavigateToEmprendedor = onNavigateToEmprendedor,
                    onNavigateToServicioDetail = onNavigateToServicioDetail,
                    onAddToCart = { servicio ->
                        selectedServicio = servicio
                        showDatePicker = true
                    }
                )
            }

            else -> {
                ServiciosContent(
                    servicios = uiState.servicios,
                    onNavigateToEmprendedor = onNavigateToEmprendedor,
                    onNavigateToServicioDetail = onNavigateToServicioDetail,
                    onAddToCart = { servicio ->
                        selectedServicio = servicio
                        showDatePicker = true
                    }
                )
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker && selectedServicio != null) {
        DatePickerDialog(
            servicio = selectedServicio!!,
            onDismiss = {
                showDatePicker = false
                selectedServicio = null
            },
            onConfirm = { servicio, date, notas ->
                cartViewModel.addToCart(
                    servicioId = servicio.id,
                    cantidad = 1,
                    fechaServicio = date,
                    notasEspeciales = notas
                )
                showDatePicker = false
                selectedServicio = null
            }
        )
    }
}

@Composable
private fun ServiciosContent(
    servicios: List<Servicio>,
    onNavigateToEmprendedor: (Long) -> Unit,
    onNavigateToServicioDetail: (Long) -> Unit,
    onAddToCart: (Servicio) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Todos los Servicios (${servicios.size})",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        items(servicios) { servicio ->
            ServiceCard(
                servicio = servicio,
                onAddToCart = { onAddToCart(servicio) },
                onNavigateToEmprendedor = { onNavigateToEmprendedor(servicio.emprendedor.id) },
                onNavigateToDetail = { onNavigateToServicioDetail(servicio.id) }
            )
        }
    }
}

@Composable
private fun ServiciosSearchResults(
    searchResults: List<Servicio>,
    isSearching: Boolean,
    onNavigateToEmprendedor: (Long) -> Unit,
    onNavigateToServicioDetail: (Long) -> Unit,
    onAddToCart: (Servicio) -> Unit
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
                            text = "No se encontraron servicios",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        } else {
            items(searchResults) { servicio ->
                ServiceCard(
                    servicio = servicio,
                    onAddToCart = { onAddToCart(servicio) },
                    onNavigateToEmprendedor = { onNavigateToEmprendedor(servicio.emprendedor.id) },
                    onNavigateToDetail = { onNavigateToServicioDetail(servicio.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    servicio: Servicio,
    onDismiss: () -> Unit,
    onConfirm: (Servicio, String, String?) -> Unit
) {
    var selectedDate by remember { mutableStateOf("") }
    var notasEspeciales by remember { mutableStateOf("") }

    // Fecha por defecto (mañana)
    LaunchedEffect(Unit) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        selectedDate = dateFormat.format(calendar.time)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar al Carrito") },
        text = {
            Column {
                Text(
                    text = servicio.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "S/ ${servicio.precio}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = { selectedDate = it },
                    label = { Text("Fecha del servicio *") },
                    placeholder = { Text("YYYY-MM-DD") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = notasEspeciales,
                    onValueChange = { notasEspeciales = it },
                    label = { Text("Notas especiales (opcional)") },
                    placeholder = { Text("Solicitudes específicas...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedDate.isNotBlank()) {
                        onConfirm(
                            servicio,
                            selectedDate,
                            notasEspeciales.takeIf { it.isNotBlank() }
                        )
                    }
                },
                enabled = selectedDate.isNotBlank()
            ) {
                Text("Agregar al Carrito")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}