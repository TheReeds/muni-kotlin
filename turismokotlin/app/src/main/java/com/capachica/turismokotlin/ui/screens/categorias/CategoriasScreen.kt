package com.capachica.turismokotlin.ui.screens.categorias

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capachica.turismokotlin.data.model.Categoria
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.components.EmptyListPlaceholder
import com.capachica.turismokotlin.ui.components.ErrorScreen
import com.capachica.turismokotlin.ui.components.LoadingScreen
import com.capachica.turismokotlin.ui.components.TurismoAppBar
import com.capachica.turismokotlin.ui.viewmodel.CategoriaViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriasScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEmprendedores: (Long) -> Unit,
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val viewModel: CategoriaViewModel = viewModel(factory = factory)
    val categoriasState by viewModel.categoriasState.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()

    // Para mostrar diálogo de confirmación
    val showDeleteConfirmDialog = remember { mutableStateOf(false) }
    val categoriaToDelete = remember { mutableStateOf<Categoria?>(null) }

    // Scope para lanzar corrutinas
    val scope = rememberCoroutineScope()

    // Cargar datos al inicio
    LaunchedEffect(Unit) {
        viewModel.getAllCategorias()
    }

    // Recargar datos después de eliminar
    LaunchedEffect(deleteState) {
        if (deleteState is Result.Success && (deleteState as Result.Success).data) {
            viewModel.getAllCategorias()
        }
    }

    // Diálogo de confirmación para eliminar
    if (showDeleteConfirmDialog.value && categoriaToDelete.value != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmDialog.value = false
                categoriaToDelete.value = null
            },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Está seguro que desea eliminar la categoría '${categoriaToDelete.value?.nombre}'? Esta acción no eliminará los emprendedores asociados, pero quedarán sin categoría.") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            categoriaToDelete.value?.id?.let { viewModel.deleteCategoria(it) }
                            showDeleteConfirmDialog.value = false
                            categoriaToDelete.value = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showDeleteConfirmDialog.value = false
                        categoriaToDelete.value = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TurismoAppBar(
                title = "Categorías",
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = onNavigateToCreate) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Añadir categoría"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear categoría"
                )
            }
        }
    ) { paddingValues ->
        when (val state = categoriasState) {
            is Result.Loading -> LoadingScreen()
            is Result.Error -> ErrorScreen(
                message = state.message,
                onRetry = { viewModel.getAllCategorias() }
            )
            is Result.Success -> {
                if (state.data.isEmpty()) {
                    EmptyListPlaceholder(
                        message = "No hay categorías registradas",
                        buttonText = "Crear Categoría",
                        onButtonClick = onNavigateToCreate
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp)
                    ) {
                        items(state.data) { categoria ->
                            CategoriaListItem(
                                categoria = categoria,
                                onClick = { onNavigateToDetail(categoria.id) },
                                onEmprendedoresClick = { onNavigateToEmprendedores(categoria.id) },
                                onDelete = {
                                    categoriaToDelete.value = categoria
                                    showDeleteConfirmDialog.value = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoriaListItem(
    categoria: Categoria,
    onClick: () -> Unit,
    onEmprendedoresClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Category,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = categoria.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Emprendedores: ${categoria.cantidadEmprendedores}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Botón de eliminar
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (categoria.descripcion != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = categoria.descripcion,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(1f))
                OutlinedButton(onClick = onEmprendedoresClick) {
                    Text("Ver Emprendedores")
                }
            }
        }
    }
}