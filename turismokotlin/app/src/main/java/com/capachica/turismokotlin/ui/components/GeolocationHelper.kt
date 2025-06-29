package com.capachica.turismokotlin.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.tasks.await

/**
 * Componente para manejar la geolocalización del usuario
 * Requiere las siguientes dependencias en build.gradle.kts:
 * implementation "com.google.android.gms:play-services-location:21.0.1"
 */
@Composable
fun GeolocationHelper(
    onLocationObtained: (Double, Double) -> Unit,
    onError: (String) -> Unit,
    onPermissionDenied: () -> Unit
) {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    
    // Verificar permisos al inicializar
    LaunchedEffect(Unit) {
        permissionGranted = hasLocationPermission(context)
    }
    
    // Launcher para solicitar permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                           permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        
        if (!permissionGranted) {
            onPermissionDenied()
        }
    }
    
    // Función para solicitar permisos
    fun requestLocationPermission() {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
    
    // Función para obtener ubicación actual
    fun getCurrentLocation() {
        if (!permissionGranted) {
            requestLocationPermission()
            return
        }
        
        if (!isLocationEnabled(context)) {
            onError("Los servicios de ubicación están deshabilitados")
            return
        }
        
        isLoading = true
        
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        
        try {
            // Solicitar ubicación actual
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location: Location? ->
                isLoading = false
                location?.let {
                    onLocationObtained(it.latitude, it.longitude)
                } ?: run {
                    onError("No se pudo obtener la ubicación actual")
                }
            }.addOnFailureListener { exception ->
                isLoading = false
                onError("Error al obtener ubicación: ${exception.message}")
            }
        } catch (e: SecurityException) {
            isLoading = false
            onError("Permisos de ubicación no concedidos")
        }
    }
    
    // Botón de geolocalización
    OutlinedButton(
        onClick = { getCurrentLocation() },
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = androidx.compose.ui.Modifier.size(18.dp))
        } else {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Obtener mi ubicación",
                modifier = androidx.compose.ui.Modifier.size(18.dp)
            )
        }
        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.width(8.dp))
        Text(if (isLoading) "Obteniendo..." else "Mi ubicación")
    }
}

/**
 * Verifica si la aplicación tiene permisos de ubicación
 */
fun hasLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED ||
    ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

/**
 * Verifica si los servicios de ubicación están habilitados
 */
fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
           locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}

/**
 * Composable para mostrar diálogo de permisos de ubicación
 */
@Composable
fun LocationPermissionDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onRequestPermission: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Permisos de Ubicación") },
            text = {
                Text(
                    "Esta aplicación necesita acceso a tu ubicación para poder " +
                    "encontrar emprendedores y servicios cercanos. " +
                    "¿Deseas conceder los permisos?"
                )
            },
            confirmButton = {
                Button(onClick = onRequestPermission) {
                    Text("Conceder")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Composable para mostrar diálogo cuando los servicios de ubicación están deshabilitados
 */
@Composable
fun LocationServicesDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Icon(
                    imageVector = Icons.Default.LocationOff,
                    contentDescription = null
                )
            },
            title = { Text("Servicios de Ubicación Deshabilitados") },
            text = {
                Text(
                    "Los servicios de ubicación están deshabilitados en tu dispositivo. " +
                    "Para usar esta función, necesitas habilitarlos en la configuración."
                )
            },
            confirmButton = {
                Button(onClick = onOpenSettings) {
                    Text("Abrir Configuración")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
    }
}