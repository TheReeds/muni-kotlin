package com.capachica.turismokotlin.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import com.capachica.turismokotlin.ui.components.TurismoBottomNavigationBar
import com.capachica.turismokotlin.ui.screens.Planes.PlanesScreen
import com.capachica.turismokotlin.ui.screens.admin.*
import com.capachica.turismokotlin.ui.screens.auth.LoginScreen
import com.capachica.turismokotlin.ui.screens.auth.RegisterScreen
import com.capachica.turismokotlin.ui.screens.cart.CartScreen
import com.capachica.turismokotlin.ui.screens.chat.ChatScreen
import com.capachica.turismokotlin.ui.screens.checkout.CheckoutScreen
import com.capachica.turismokotlin.ui.screens.details.EmprendedorDetailScreen
import com.capachica.turismokotlin.ui.screens.details.PlanDetailScreen
import com.capachica.turismokotlin.ui.screens.details.ServicioDetailScreen
import com.capachica.turismokotlin.ui.screens.gestion.CrearPlanScreen
import com.capachica.turismokotlin.ui.screens.gestion.CrearServicioScreen
import com.capachica.turismokotlin.ui.screens.gestion.EditarPlanScreen
import com.capachica.turismokotlin.ui.screens.gestion.EditarServicioScreen
import com.capachica.turismokotlin.ui.screens.home.HomeScreen
import com.capachica.turismokotlin.ui.screens.map.MapScreen
import com.capachica.turismokotlin.ui.screens.reservas.ReservasScreen
import com.capachica.turismokotlin.ui.screens.servicios.ServiciosScreen
import com.capachica.turismokotlin.ui.screens.gestion.GestionScreen
import com.capachica.turismokotlin.ui.screens.gestion.GestionServiciosScreen
import com.capachica.turismokotlin.ui.screens.gestion.GestionPlanesScreen
import com.capachica.turismokotlin.ui.screens.gestion.GestionReservasScreen

@Composable
fun TurismoNavigation(
    navController: NavHostController,
    isLoggedIn: Boolean,
    userRoles: Collection<String>
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = {
            if (isLoggedIn && shouldShowBottomBar(currentRoute)) {
                TurismoBottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigateToHome = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    onNavigateToServicios = {
                        navController.navigate("servicios") {
                            popUpTo("home") { saveState = true }
                        }
                    },
                    onNavigateToPlanes = {
                        navController.navigate("planes") {
                            popUpTo("home") { saveState = true }
                        }
                    },
                    onNavigateToReservas = {
                        navController.navigate("reservas") {
                            popUpTo("home") { saveState = true }
                        }
                    },
                    onNavigateToProfile = {
                        navController.navigate("profile") {
                            popUpTo("home") { saveState = true }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) "home" else "login",
            modifier = Modifier.padding(paddingValues)
        ) {
            // Auth screens
            composable("login") {
                LoginScreen(
                    onNavigateToRegister = { navController.navigate("register") },
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            composable("register") {
                RegisterScreen(
                    onNavigateToLogin = { navController.navigate("login") },
                    onRegisterSuccess = {
                        navController.navigate("home") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                )
            }

            // Main screens
            composable("home") {
                HomeScreen(
                    onNavigateToPlan = { planId ->
                        navController.navigate("plan_detail/$planId")
                    },
                    onNavigateToEmprendedor = { emprendedorId ->
                        navController.navigate("emprendedor_detail/$emprendedorId")
                    },
                    onNavigateToServicioDetail = { servicioId ->
                        navController.navigate("servicio_detail/$servicioId")
                    },
                    onNavigateToMap = { navController.navigate("map") },
                    onNavigateToCart = { navController.navigate("cart") },
                    onNavigateToProfile = { navController.navigate("profile") },
                    onNavigateToChat = { navController.navigate("chat") },
                    onNavigateToGestion = { // NUEVO
                        navController.navigate("gestion")
                    },
                    onNavigateToAdminDashboard = {
                        if (userRoles.contains("ROLE_ADMIN")) {
                            navController.navigate("admin_dashboard")
                        }
                    },
                    onNavigateToEmprendedorDashboard = {
                        if (userRoles.contains("ROLE_EMPRENDEDOR")) {
                            navController.navigate("emprendedor_dashboard")
                        }
                    },
                    onNavigateToMunicipalidadDashboard = {
                        if (userRoles.contains("ROLE_MUNICIPALIDAD")) {
                            navController.navigate("municipalidad_dashboard")
                        }
                    }
                )
            }

            composable("servicios") {
                ServiciosScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEmprendedor = { emprendedorId ->
                        navController.navigate("emprendedor_detail/$emprendedorId")
                    },
                    onNavigateToServicioDetail = { servicioId ->
                        navController.navigate("servicio_detail/$servicioId")
                    },
                    onNavigateToCart = { navController.navigate("cart") }
                )
            }

            composable(
                "servicio_detail/{servicioId}",
                arguments = listOf(navArgument("servicioId") { type = NavType.LongType })
            ) { backStackEntry ->
                val servicioId = backStackEntry.arguments?.getLong("servicioId") ?: 0L
                ServicioDetailScreen(
                    servicioId = servicioId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEmprendedor = { emprendedorId ->
                        navController.navigate("emprendedor_detail/$emprendedorId")
                    },
                    onNavigateToChat = { emprendedorId ->
                        navController.navigate("chat/emprendedor/$emprendedorId")
                    }
                )
            }

            composable("planes") {
                PlanesScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToPlan = { planId ->
                        navController.navigate("plan_detail/$planId")
                    },
                    onNavigateToCart = { navController.navigate("cart") }
                )
            }

            composable("reservas") {
                ReservasScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToReservaDetail = { reservaId ->
                        navController.navigate("reserva_detail/$reservaId")
                    }
                )
            }

            composable("map") {
                MapScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEmprendedor = { emprendedorId ->
                        navController.navigate("emprendedor_detail/$emprendedorId")
                    }
                )
            }

            composable("cart") {
                CartScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCheckout = { navController.navigate("checkout") }
                )
            }

            composable("checkout") {
                CheckoutScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToReservas = {
                        navController.navigate("reservas") {
                            popUpTo("home") { saveState = true }
                        }
                    }
                )
            }

            // Detail screens
            composable(
                "plan_detail/{planId}",
                arguments = listOf(navArgument("planId") { type = NavType.LongType })
            ) { backStackEntry ->
                val planId = backStackEntry.arguments?.getLong("planId") ?: 0L
                PlanDetailScreen(
                    planId = planId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                "emprendedor_detail/{emprendedorId}",
                arguments = listOf(navArgument("emprendedorId") { type = NavType.LongType })
            ) { backStackEntry ->
                val emprendedorId = backStackEntry.arguments?.getLong("emprendedorId") ?: 0L
                EmprendedorDetailScreen(
                    emprendedorId = emprendedorId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Reserva detail (placeholder por ahora)
            composable(
                "reserva_detail/{reservaId}",
                arguments = listOf(navArgument("reservaId") { type = NavType.LongType })
            ) { backStackEntry ->
                val reservaId = backStackEntry.arguments?.getLong("reservaId") ?: 0L
                // TODO: Implementar ReservaDetailScreen si es necesario
                // Por ahora una pantalla simple
                Surface {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Detalle de Reserva #$reservaId - En desarrollo")
                    }
                }
            }

            // Chat screen básico
            composable("chat") {
                ChatScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToPlans = { navController.navigate("planes") },
                    onNavigateToServices = { navController.navigate("servicios") }
                )
            }

            // Chat con emprendedor específico
            composable(
                "chat/emprendedor/{emprendedorId}",
                arguments = listOf(navArgument("emprendedorId") { type = NavType.LongType })
            ) { backStackEntry ->
                val emprendedorId = backStackEntry.arguments?.getLong("emprendedorId") ?: 0L
                ChatScreen(
                    emprendedorId = emprendedorId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToPlans = { navController.navigate("planes") },
                    onNavigateToServices = { navController.navigate("servicios") }
                )
            }

            // Profile screen (placeholder)
            composable("profile") {
                // TODO: Implementar ProfileScreen
                // Por ahora una pantalla simple
                Surface {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Perfil - En desarrollo")
                    }
                }
            }

            // Admin screens (solo si tiene rol ADMIN)
            if (userRoles.contains("ROLE_ADMIN")) {
                composable("admin_dashboard") {
                    AdminDashboardScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToUsuarios = { navController.navigate("admin_usuarios") },
                        onNavigateToEmprendedores = { navController.navigate("admin_emprendedores") },
                        onNavigateToCategorias = { navController.navigate("admin_categorias") },
                        onNavigateToMunicipalidades = { navController.navigate("admin_municipalidades") }
                    )
                }

                composable("admin_usuarios") {
                    AdminUsuariosScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable("admin_emprendedores") {
                    AdminEmprendedoresScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable("admin_categorias") {
                    AdminCategoriasScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable("admin_municipalidades") {
                    AdminMunicipalidadesScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }

            // Emprendedor screens (solo si tiene rol EMPRENDEDOR)
            if (userRoles.contains("ROLE_EMPRENDEDOR")) {
                composable("emprendedor_dashboard") {
                    EmprendedorDashboardScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }

            // Municipalidad screens (solo si tiene rol MUNICIPALIDAD)
            if (userRoles.contains("ROLE_MUNICIPALIDAD")) {
                composable("municipalidad_dashboard") {
                    MunicipalidadDashboardScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
            // Gestión principal (solo para emprendedores y admin)
            if (userRoles.contains("ROLE_EMPRENDEDOR") || userRoles.contains("ROLE_ADMIN")) {
                composable("gestion") {
                    GestionScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToServicios = { navController.navigate("gestion_servicios") },
                        onNavigateToPlanes = { navController.navigate("gestion_planes") },
                        onNavigateToReservas = { navController.navigate("gestion_reservas") }
                    )
                }

                // Gestión de servicios
                composable("gestion_servicios") {
                    GestionServiciosScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToCrearServicio = { navController.navigate("crear_servicio") },
                        onNavigateToEditarServicio = { servicioId ->
                            navController.navigate("editar_servicio/$servicioId")
                        }
                    )
                }

                // Gestión de planes (solo admin y municipalidades)
                if (userRoles.contains("ROLE_ADMIN") || userRoles.contains("ROLE_MUNICIPALIDAD")) {
                    composable("gestion_planes") {
                        GestionPlanesScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToCrearPlan = { navController.navigate("crear_plan") },
                            onNavigateToEditarPlan = { planId ->
                                navController.navigate("editar_plan/$planId")
                            }
                        )
                    }
                }

                // Gestión de reservas
                composable("gestion_reservas") {
                    GestionReservasScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToReservaDetail = { reservaId ->
                            navController.navigate("reserva_detail/$reservaId")
                        }
                    )
                }

                // Crear servicio
                composable("crear_servicio") {
                    CrearServicioScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onServicioCreado = {
                            navController.popBackStack()
                        }
                    )
                }

                // Editar servicio
                composable(
                    "editar_servicio/{servicioId}",
                    arguments = listOf(navArgument("servicioId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val servicioId = backStackEntry.arguments?.getLong("servicioId") ?: 0L
                    EditarServicioScreen(
                        servicioId = servicioId,
                        onNavigateBack = { navController.popBackStack() },
                        onServicioActualizado = {
                            navController.popBackStack()
                        }
                    )
                }

                // Crear plan (solo admin y municipalidades)
                if (userRoles.contains("ROLE_ADMIN") || userRoles.contains("ROLE_MUNICIPALIDAD")) {
                    composable("crear_plan") {
                        CrearPlanScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onPlanCreado = {
                                navController.popBackStack()
                            }
                        )
                    }

                    // Editar plan
                    composable(
                        "editar_plan/{planId}",
                        arguments = listOf(navArgument("planId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val planId = backStackEntry.arguments?.getLong("planId") ?: 0L
                        EditarPlanScreen(
                            planId = planId,
                            onNavigateBack = { navController.popBackStack() },
                            onPlanActualizado = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun shouldShowBottomBar(currentRoute: String?): Boolean {
    val routesWithBottomBar = setOf(
        "home",
        "servicios",
        "planes",
        "reservas",
        "profile"
    )
    return currentRoute in routesWithBottomBar
}