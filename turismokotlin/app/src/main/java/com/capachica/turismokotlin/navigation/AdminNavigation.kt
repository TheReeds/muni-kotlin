package com.capachica.turismokotlin.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.capachica.turismokotlin.ui.screens.admin.AdminCategoriasScreen
import com.capachica.turismokotlin.ui.screens.admin.AdminDashboardScreen
import com.capachica.turismokotlin.ui.screens.admin.AdminEmprendedoresScreen
import com.capachica.turismokotlin.ui.screens.admin.AdminMunicipalidadesScreen
import com.capachica.turismokotlin.ui.screens.admin.AdminUsuariosScreen

@Composable
fun AdminNavigation(
    navController: NavHostController,
    userRoles: Collection<String>
) {
    NavHost(
        navController = navController,
        startDestination = "admin_dashboard"
    ) {
        // Dashboard principal de admin
        composable("admin_dashboard") {
            AdminDashboardScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToUsuarios = { navController.navigate("admin_usuarios") },
                onNavigateToEmprendedores = { navController.navigate("admin_emprendedores") },
                onNavigateToCategorias = { navController.navigate("admin_categorias") },
                onNavigateToMunicipalidades = { navController.navigate("admin_municipalidades") }
            )
        }

        // Gestión de usuarios
        composable("admin_usuarios") {
            AdminUsuariosScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Gestión de emprendedores
        composable("admin_emprendedores") {
            AdminEmprendedoresScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Gestión de categorías
        composable("admin_categorias") {
            AdminCategoriasScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Gestión de municipalidades
        composable("admin_municipalidades") {
            AdminMunicipalidadesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}