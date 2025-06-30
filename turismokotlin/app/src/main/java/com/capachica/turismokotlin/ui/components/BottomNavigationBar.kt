package com.capachica.turismokotlin.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RoomService
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TurismoBottomNavigationBar(
    currentRoute: String?,
    onNavigateToHome: () -> Unit,
    onNavigateToServicios: () -> Unit,
    onNavigateToPlanes: () -> Unit,
    onNavigateToReservas: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth()
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Inicio") },
            selected = currentRoute == "home",
            onClick = onNavigateToHome
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.RoomService, contentDescription = null) },
            label = { Text("Servicios") },
            selected = currentRoute == "servicios",
            onClick = onNavigateToServicios
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Map, contentDescription = null) },
            label = { Text("Planes") },
            selected = currentRoute == "planes",
            onClick = onNavigateToPlanes
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.BookmarkBorder, contentDescription = null) },
            label = { Text("Reservas") },
            selected = currentRoute == "reservas",
            onClick = onNavigateToReservas
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Perfil") },
            selected = currentRoute == "profile",
            onClick = onNavigateToProfile
        )
    }
}