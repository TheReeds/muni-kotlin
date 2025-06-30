// HomeScreen.kt actualizado con botón de logout
package com.capachica.turismokotlin.ui.screens.home

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
import com.capachica.turismokotlin.data.model.Plan
import com.capachica.turismokotlin.data.model.Servicio
import com.capachica.turismokotlin.ui.components.EmprendedorCard
import com.capachica.turismokotlin.ui.components.PlanCard
import com.capachica.turismokotlin.ui.components.PlanListItem
import com.capachica.turismokotlin.ui.components.ServiceCard
import com.capachica.turismokotlin.ui.viewmodel.AuthViewModel
import com.capachica.turismokotlin.ui.viewmodel.CartViewModel
import com.capachica.turismokotlin.ui.viewmodel.HomeUiState
import com.capachica.turismokotlin.ui.viewmodel.HomeViewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToPlan: (Long) -> Unit,
    onNavigateToEmprendedor: (Long) -> Unit,
    onNavigateToServicioDetail: (Long) -> Unit = {},
    onNavigateToMap: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToGestion: () -> Unit = {},
    onNavigateToAdminDashboard: () -> Unit = {},
    onNavigateToEmprendedorDashboard: () -> Unit = {},
    onNavigateToMunicipalidadDashboard: () -> Unit = {},
    onLogout: () -> Unit = {}, // NUEVO - Callback para logout
    homeViewModel: HomeViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val cartItemCount by cartViewModel.itemCount.collectAsStateWithLifecycle(initialValue = 0)
    val userRoles by authViewModel.userRoles.collectAsState(initial = emptySet())

    var searchQuery by remember { mutableStateOf("") }
    var showSearchResults by remember { mutableStateOf(false) }
    var searchType by remember { mutableStateOf("planes") }
    var showLogoutDialog by remember { mutableStateOf(false) } // NUEVO

    // Para manejar el dialog de agregar servicio al carrito
    var showAddServiceDialog by remember { mutableStateOf(false) }
    var selectedServicio by remember { mutableStateOf<Servicio?>(null) }

    LaunchedEffect(Unit) {
        homeViewModel.refreshData()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top App Bar
        TopAppBar(
            title = { Text("Turismo Perú") },
            actions = {
                // Botón de carrito con badge
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge {
                                Text("$cartItemCount")
                            }
                        }
                    }
                ) {
                    IconButton(onClick = onNavigateToCart) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito")
                    }
                }

                // Menú desplegable con perfil y logout
                var showMenu by remember { mutableStateOf(false) }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Mi Perfil") },
                            onClick = {
                                showMenu = false
                                onNavigateToProfile()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.AccountCircle, contentDescription = null)
                            }
                        )

                        Divider()

                        DropdownMenuItem(
                            text = { Text("Cerrar Sesión") },
                            onClick = {
                                showMenu = false
                                showLogoutDialog = true
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Logout,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = MaterialTheme.colorScheme.error
                            )
                        )
                    }
                }
            }
        )

        // Barra de búsqueda con filtros
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        if (it.isNotEmpty()) {
                            if (searchType == "planes") {
                                homeViewModel.searchPlanes(it)
                            } else {
                                homeViewModel.searchServicios(it)
                            }
                            showSearchResults = true
                        } else {
                            showSearchResults = false
                            homeViewModel.clearSearchResults()
                        }
                    },
                    label = { Text("Buscar ${if (searchType == "planes") "planes" else "servicios"}") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                searchQuery = ""
                                showSearchResults = false
                                homeViewModel.clearSearchResults()
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Filtros de búsqueda
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        onClick = {
                            searchType = "planes"
                            if (searchQuery.isNotEmpty()) {
                                homeViewModel.clearSearchResults()
                                homeViewModel.searchPlanes(searchQuery)
                            }
                        },
                        label = { Text("Planes") },
                        selected = searchType == "planes",
                        leadingIcon = if (searchType == "planes") {
                            { Icon(Icons.Default.Check, contentDescription = null) }
                        } else null
                    )

                    FilterChip(
                        onClick = {
                            searchType = "servicios"
                            if (searchQuery.isNotEmpty()) {
                                homeViewModel.clearSearchResults()
                                homeViewModel.searchServicios(searchQuery)
                            }
                        },
                        label = { Text("Servicios") },
                        selected = searchType == "servicios",
                        leadingIcon = if (searchType == "servicios") {
                            { Icon(Icons.Default.Check, contentDescription = null) }
                        } else null
                    )
                }
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
                    Button(onClick = { homeViewModel.refreshData() }) {
                        Text("Reintentar")
                    }
                }
            }

            showSearchResults -> {
                SearchResultsContent(
                    searchType = searchType,
                    searchResultsPlanes = uiState.searchResults,
                    searchResultsServicios = uiState.searchResultsServicios,
                    isSearching = uiState.isSearching,
                    onNavigateToPlan = onNavigateToPlan,
                    onNavigateToEmprendedor = onNavigateToEmprendedor,
                    onAddServiceToCart = { servicio ->
                        selectedServicio = servicio
                        showAddServiceDialog = true
                    },
                    onNavigateToServicioDetail = onNavigateToServicioDetail
                )
            }

            else -> {
                MainHomeContent(
                    uiState = uiState,
                    onNavigateToPlan = onNavigateToPlan,
                    onNavigateToEmprendedor = onNavigateToEmprendedor,
                    onNavigateToServicioDetail = onNavigateToServicioDetail,
                    onNavigateToMap = onNavigateToMap,
                    onNavigateToChat = onNavigateToChat,
                    onNavigateToAdminDashboard = onNavigateToAdminDashboard,
                    onNavigateToEmprendedorDashboard = onNavigateToEmprendedorDashboard,
                    onNavigateToMunicipalidadDashboard = onNavigateToMunicipalidadDashboard,
                    onAddServiceToCart = { servicio ->
                        selectedServicio = servicio
                        showAddServiceDialog = true
                    },
                    userRoles = userRoles,
                    onNavigateToGestion = onNavigateToGestion
                )
            }
        }
    }

    // Dialog para agregar servicio al carrito
    if (showAddServiceDialog && selectedServicio != null) {
        AddServiceToCartDialog(
            servicio = selectedServicio!!,
            onDismiss = {
                showAddServiceDialog = false
                selectedServicio = null
            },
            onConfirm = { servicio, fechaServicio, notasEspeciales ->
                cartViewModel.addToCart(
                    servicioId = servicio.id,
                    cantidad = 1,
                    fechaServicio = fechaServicio,
                    notasEspeciales = notasEspeciales
                )
                showAddServiceDialog = false
                selectedServicio = null
            }
        )
    }

    // Dialog de confirmación de logout
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Logout,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar Sesión")
                }
            },
            text = {
                Text("¿Estás seguro de que deseas cerrar sesión?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        authViewModel.logout()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Cerrar Sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun MainHomeContent(
    uiState: HomeUiState,
    onNavigateToPlan: (Long) -> Unit,
    onNavigateToEmprendedor: (Long) -> Unit,
    onNavigateToServicioDetail: (Long) -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToGestion: () -> Unit,
    onNavigateToAdminDashboard: () -> Unit,
    onNavigateToEmprendedorDashboard: () -> Unit,
    onNavigateToMunicipalidadDashboard: () -> Unit,
    onAddServiceToCart: (Servicio) -> Unit,
    userRoles: Collection<String>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Acciones rápidas
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                // Mapa
                item {
                    QuickActionCard(
                        title = "Mapa",
                        icon = Icons.Default.Map,
                        onClick = onNavigateToMap
                    )
                }

                // Chat
                item {
                    QuickActionCard(
                        title = "Chat",
                        icon = Icons.Default.Chat,
                        onClick = onNavigateToChat
                    )
                }

                // Gestión de Servicios/Planes (solo para emprendedores y admin)
                if (userRoles.contains("ROLE_EMPRENDEDOR") || userRoles.contains("ROLE_ADMIN")) {
                    item {
                        QuickActionCard(
                            title = "Gestión",
                            icon = Icons.Default.BusinessCenter,
                            onClick = onNavigateToGestion
                        )
                    }
                }

                // Panel de Admin (solo si es admin)
                if (userRoles.contains("ROLE_ADMIN")) {
                    item {
                        QuickActionCard(
                            title = "Admin",
                            icon = Icons.Default.AdminPanelSettings,
                            onClick = onNavigateToAdminDashboard
                        )
                    }
                }

                // Panel de Municipalidad (solo si es municipalidad)
                if (userRoles.contains("ROLE_MUNICIPALIDAD")) {
                    item {
                        QuickActionCard(
                            title = "Municipalidad",
                            icon = Icons.Default.LocationCity,
                            onClick = onNavigateToMunicipalidadDashboard
                        )
                    }
                }
            }
        }

        // Planes populares
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
                            onAddToCart = { /* Los planes se reservan directamente */ }
                        )
                    }
                }
            }
        }

        // Categorías
        if (uiState.categorias.isNotEmpty()) {
            item {
                Text(
                    text = "Categorías",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(uiState.categorias) { categoria ->
                        FilterChip(
                            onClick = { /* Filtrar por categoría */ },
                            label = { Text(categoria.nombre) },
                            selected = false,
                            leadingIcon = {
                                Badge {
                                    Text("${categoria.cantidadEmprendedores}")
                                }
                            }
                        )
                    }
                }
            }
        }

        // Servicios destacados
        if (uiState.servicios.isNotEmpty()) {
            item {
                Text(
                    text = "Servicios Destacados",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            items(uiState.servicios.take(5)) { servicio ->
                ServiceCard(
                    servicio = servicio,
                    onAddToCart = { onAddServiceToCart(servicio) },
                    onNavigateToEmprendedor = { onNavigateToEmprendedor(servicio.emprendedor.id) },
                    onNavigateToDetail = { onNavigateToServicioDetail(servicio.id) }
                )
            }
        }

        // Emprendedores destacados
        if (uiState.emprendedores.isNotEmpty()) {
            item {
                Text(
                    text = "Emprendedores Destacados",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(uiState.emprendedores.take(10)) { emprendedor ->
                        EmprendedorCard(
                            emprendedor = emprendedor,
                            onClick = { onNavigateToEmprendedor(emprendedor.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(80.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun SearchResultsContent(
    searchType: String,
    searchResultsPlanes: List<Plan>,
    searchResultsServicios: List<Servicio>,
    isSearching: Boolean,
    onNavigateToPlan: (Long) -> Unit,
    onNavigateToEmprendedor: (Long) -> Unit,
    onNavigateToServicioDetail: (Long) -> Unit,
    onAddServiceToCart: (Servicio) -> Unit
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
                val resultCount = if (searchType == "planes") searchResultsPlanes.size else searchResultsServicios.size
                Text(
                    text = "Resultados de búsqueda ($resultCount)",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                if (isSearching) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                }
            }
        }

        if (searchType == "planes") {
            if (searchResultsPlanes.isEmpty() && !isSearching) {
                item {
                    EmptySearchCard("No se encontraron planes")
                }
            } else {
                items(searchResultsPlanes) { plan ->
                    PlanListItem(
                        plan = plan,
                        onClick = { onNavigateToPlan(plan.id) },
                        onAddToCart = { /* Los planes se reservan directamente */ }
                    )
                }
            }
        } else {
            if (searchResultsServicios.isEmpty() && !isSearching) {
                item {
                    EmptySearchCard("No se encontraron servicios")
                }
            } else {
                items(searchResultsServicios) { servicio ->
                    ServiceCard(
                        servicio = servicio,
                        onAddToCart = { onAddServiceToCart(servicio) },
                        onNavigateToEmprendedor = { onNavigateToEmprendedor(servicio.emprendedor.id) },
                        onNavigateToDetail = { onNavigateToServicioDetail(servicio.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptySearchCard(message: String) {
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
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun AddServiceToCartDialog(
    servicio: Servicio,
    onDismiss: () -> Unit,
    onConfirm: (Servicio, String, String?) -> Unit
) {
    var fechaServicio by remember { mutableStateOf("") }
    var notasEspeciales by remember { mutableStateOf("") }

    // Fecha por defecto (mañana)
    LaunchedEffect(Unit) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        fechaServicio = dateFormat.format(calendar.time)
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
                    value = fechaServicio,
                    onValueChange = { fechaServicio = it },
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
                    if (fechaServicio.isNotBlank()) {
                        onConfirm(
                            servicio,
                            fechaServicio,
                            notasEspeciales.takeIf { it.isNotBlank() }
                        )
                    }
                },
                enabled = fechaServicio.isNotBlank()
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