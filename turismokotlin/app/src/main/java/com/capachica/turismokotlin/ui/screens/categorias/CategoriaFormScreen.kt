package com.capachica.turismokotlin.ui.screens.categorias

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capachica.turismokotlin.data.model.CategoriaRequest
import com.capachica.turismokotlin.data.repository.Result
import com.capachica.turismokotlin.ui.components.ErrorScreen
import com.capachica.turismokotlin.ui.components.LoadingScreen
import com.capachica.turismokotlin.ui.components.TurismoAppBar
import com.capachica.turismokotlin.ui.components.TurismoTextField
import com.capachica.turismokotlin.ui.viewmodel.CategoriaViewModel
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaFormScreen(
    categoriaId: Long,
    onSuccess: () -> Unit,
    onBack: () -> Unit,
    factory: ViewModelFactory
) {
    val viewModel: CategoriaViewModel = viewModel(factory = factory)
    val categoriaState by viewModel.categoriaState.collectAsState()
    val createUpdateState by viewModel.createUpdateState.collectAsState()

    val isCreating = categoriaId == 0L
    val screenTitle = if (isCreating) "Crear Categoría" else "Editar Categoría"

    // Form fields
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    // Form validation
    var nombreError by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    // Scope for coroutines
    val scope = rememberCoroutineScope()

    // Load data if editing
    LaunchedEffect(categoriaId) {
        if (!isCreating) {
            viewModel.getCategoriaById(categoriaId)
        }
    }

    // Observe state for editing
    LaunchedEffect(categoriaState) {
        if (categoriaState is Result.Success && !isCreating) {
            val categoria = (categoriaState as Result.Success).data
            nombre = categoria.nombre
            descripcion = categoria.descripcion ?: ""
        }
    }

    // Observe create/update result
    LaunchedEffect(createUpdateState) {
        if (createUpdateState is Result.Success) {
            onSuccess()
        }
    }

    // Validation function
    fun validateForm(): Boolean {
        var isValid = true

        if (nombre.isBlank()) {
            nombreError = "El nombre es obligatorio"
            isValid = false
        } else {
            nombreError = ""
        }

        return isValid
    }

    // Submit function
    fun submitForm() {
        if (validateForm()) {
            isSubmitting = true

            val request = CategoriaRequest(
                nombre = nombre.trim(),
                descripcion = descripcion.takeIf { it.isNotBlank() }?.trim()
            )

            scope.launch {
                if (isCreating) {
                    viewModel.createCategoria(request)
                } else {
                    viewModel.updateCategoria(categoriaId, request)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TurismoAppBar(
                title = screenTitle,
                onBackClick = onBack
            )
        }
    ) { paddingValues ->
        // Show loading during initial loading of data for editing
        if (!isCreating && categoriaState is Result.Loading) {
            LoadingScreen()
            return@Scaffold
        }

        // Show error if data loading fails
        if (!isCreating && categoriaState is Result.Error) {
            ErrorScreen(
                message = (categoriaState as Result.Error).message,
                onRetry = { viewModel.getCategoriaById(categoriaId) }
            )
            return@Scaffold
        }

        // Form content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .padding(bottom = 56.dp) // Extra padding for button
        ) {
            TurismoTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = "Nombre de la Categoría",
                isError = nombreError.isNotEmpty(),
                errorMessage = nombreError,
                leadingIcon = Icons.Default.Category,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción (opcional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                leadingIcon = { Icon(imageVector = Icons.Default.Description, contentDescription = null) },
                minLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Submit button
            Button(
                onClick = { submitForm() },
                enabled = !isSubmitting,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(if (isCreating) "Crear Categoría" else "Actualizar Categoría")
            }

            // Show error during submission
            if (createUpdateState is Result.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = (createUpdateState as Result.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}