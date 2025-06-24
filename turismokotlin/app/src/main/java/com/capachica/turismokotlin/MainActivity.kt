package com.capachica.turismokotlin

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.capachica.turismokotlin.ui.screens.auth.LoginScreen
import com.capachica.turismokotlin.ui.screens.auth.RegisterScreen
import com.capachica.turismokotlin.ui.screens.categorias.CategoriaDetailScreen
import com.capachica.turismokotlin.ui.screens.categorias.CategoriaFormScreen
import com.capachica.turismokotlin.ui.screens.categorias.CategoriasScreen
import com.capachica.turismokotlin.ui.screens.emprendedor.EmprendedorDetailScreen
import com.capachica.turismokotlin.ui.screens.emprendedor.EmprendedorFormScreen
import com.capachica.turismokotlin.ui.screens.emprendedor.EmprendedoresScreen
import com.capachica.turismokotlin.ui.screens.home.HomeScreen
import com.capachica.turismokotlin.ui.screens.municipalidad.MunicipalidadDetailScreen
import com.capachica.turismokotlin.ui.screens.municipalidad.MunicipalidadFormScreen
import com.capachica.turismokotlin.ui.screens.municipalidad.MunicipalidadesScreen
import com.capachica.turismokotlin.ui.screens.planes.PlanesUsuarioScreen
import com.capachica.turismokotlin.ui.screens.planes.PlanDetailScreen
import com.capachica.turismokotlin.ui.screens.reservas.ReservaFormScreen
import com.capachica.turismokotlin.ui.screens.reservas.MisReservasScreen
import com.capachica.turismokotlin.ui.screens.reservas.ReservaDetailScreen
import com.capachica.turismokotlin.ui.screens.admin.AdminDashboardScreen
import com.capachica.turismokotlin.ui.screens.admin.AdminReservasScreen
import com.capachica.turismokotlin.ui.screens.admin.AdminPlanesScreen
import com.capachica.turismokotlin.ui.screens.admin.AdminServiciosScreen
import com.capachica.turismokotlin.ui.screens.admin.UserManagementScreen
import com.capachica.turismokotlin.ui.screens.admin.UserDetailScreen
import com.capachica.turismokotlin.ui.screens.servicios.ServicioStoreScreen
import com.capachica.turismokotlin.ui.screens.servicios.ServicioDetailScreen
import com.capachica.turismokotlin.ui.screens.servicios.ServicioFormScreen
import com.capachica.turismokotlin.ui.screens.servicios.MisServiciosScreen
import com.capachica.turismokotlin.ui.screens.pagos.PagosScreen
import com.capachica.turismokotlin.ui.theme.TurismoKotlinTheme
import com.capachica.turismokotlin.ui.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TurismoKotlinTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TurismoApp(ViewModelFactory(applicationContext))
                }
            }
        }
    }
}

// Lista de rutas para ayudar a evitar errores tipográficos
object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val MUNICIPALIDADES = "municipalidades"
    const val MUNICIPALIDAD_DETAIL = "municipalidad_detail/{id}"
    const val MUNICIPALIDAD_FORM = "municipalidad_form/{id}"
    const val EMPRENDEDORES = "emprendedores"
    const val EMPRENDEDORES_BY_MUNICIPALIDAD = "emprendedores_by_municipalidad/{municipalidadId}"
    const val EMPRENDEDOR_DETAIL = "emprendedor_detail/{id}"
    const val EMPRENDEDOR_FORM = "emprendedor_form/{id}"
    const val CATEGORIAS = "categorias"
    const val CATEGORIA_DETAIL = "categoria_detail/{id}"
    const val CATEGORIA_FORM = "categoria_form/{id}"
    const val EMPRENDEDORES_BY_CATEGORIA = "emprendedores_by_categoria/{categoriaId}"
    
    // Nuevas rutas para el módulo de turismo
    const val PLANES_USUARIO = "planes_usuario"
    const val PLAN_DETAIL = "plan_detail/{id}"
    const val RESERVA_FORM = "reserva_form/{planId}"
    const val MIS_RESERVAS = "mis_reservas"
    const val RESERVA_DETAIL = "reserva_detail/{id}"
    
    // Rutas administrativas
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val ADMIN_RESERVAS = "admin_reservas"
    const val ADMIN_PLANES = "admin_planes"
    const val ADMIN_SERVICIOS = "admin_servicios"
    const val USER_MANAGEMENT = "user_management"
    const val USER_DETAIL = "user_detail/{userId}"
    
    // Rutas adicionales
    const val SERVICIOS_STORE = "servicios_store"
    const val SERVICIO_DETAIL = "servicio_detail/{id}"
    const val SERVICIO_FORM = "servicio_form/{id}"
    const val MIS_SERVICIOS = "mis_servicios"
    const val PAGOS = "pagos"
}

@Composable
fun TurismoApp(factory: ViewModelFactory) {
    val navController = rememberNavController()

    // Configurar listener para navegación
    SetupNavigationLogging(navController)

    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        // Autenticación
        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    Log.d(TAG, "Navegando a registro desde login")
                    navController.navigate(Routes.REGISTER)
                },
                onLoginSuccess = {
                    Log.d(TAG, "Login exitoso, navegando a home")
                    navController.navigateToTop(Routes.HOME)
                },
                factory = factory
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = {
                    Log.d(TAG, "Navegando a login desde registro")
                    navController.navigateToTop(Routes.LOGIN)
                },
                onRegisterSuccess = {
                    Log.d(TAG, "Registro exitoso, navegando a home")
                    navController.navigateToTop(Routes.HOME)
                },
                factory = factory
            )
        }

        // Pantalla principal
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToMunicipalidades = {
                    navController.navigate(Routes.MUNICIPALIDADES)
                },
                onNavigateToEmprendedores = {
                    navController.navigate(Routes.EMPRENDEDORES)
                },
                onNavigateToCategorias = {
                    navController.navigate(Routes.CATEGORIAS)
                },
                onNavigateToPlanes = {
                    navController.navigate(Routes.PLANES_USUARIO)
                },
                onNavigateToMisReservas = {
                    navController.navigate(Routes.MIS_RESERVAS)
                },
                onNavigateToServicios = {
                    navController.navigate(Routes.SERVICIOS_STORE)
                },
                onNavigateToAdmin = {
                    navController.navigate(Routes.ADMIN_DASHBOARD)
                },
                onLogout = {
                    navController.navigateToTop(Routes.LOGIN)
                },
                factory = factory
            )
        }

        // Municipalidades
        composable(Routes.MUNICIPALIDADES) {
            MunicipalidadesScreen(
                onNavigateToDetail = { id ->
                    Log.d(TAG, "Navegando al detalle de municipalidad: $id")
                    navController.navigate(Routes.MUNICIPALIDAD_DETAIL.replace("{id}", id.toString()))
                },
                onNavigateToCreate = {
                    Log.d(TAG, "Navegando al formulario de crear municipalidad")
                    // Usamos navegación directa al formulario
                    val route = Routes.MUNICIPALIDAD_FORM.replace("{id}", "0")
                    navController.navigate(route)
                },
                onBack = {
                    Log.d(TAG, "Volviendo desde municipalidades")
                    navController.popBackStack()
                },
                factory = factory
            )
        }

        composable(
            route = Routes.MUNICIPALIDAD_DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            MunicipalidadDetailScreen(
                municipalidadId = id,
                onNavigateToEdit = {
                    Log.d(TAG, "Navegando al formulario de editar municipalidad: $id")
                    val route = Routes.MUNICIPALIDAD_FORM.replace("{id}", id.toString())
                    navController.navigate(route)
                },
                onNavigateToEmprendedores = {
                    Log.d(TAG, "Navegando a emprendedores de municipalidad: $id")
                    val route = Routes.EMPRENDEDORES_BY_MUNICIPALIDAD.replace("{municipalidadId}", id.toString())
                    navController.navigate(route)
                },
                onBack = {
                    Log.d(TAG, "Volviendo desde detalle de municipalidad")
                    navController.popBackStack()
                },
                factory = factory
            )
        }

        composable(
            route = Routes.MUNICIPALIDAD_FORM,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L

            MunicipalidadFormScreen(
                municipalidadId = id,
                onSuccess = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
                factory = factory
            )
        }

        // Emprendedores
        composable(Routes.EMPRENDEDORES) {
            EmprendedoresScreen(
                onNavigateToDetail = { id ->
                    Log.d(TAG, "Navegando al detalle de emprendedor: $id")
                    val route = Routes.EMPRENDEDOR_DETAIL.replace("{id}", id.toString())
                    navController.navigate(route)
                },
                onNavigateToCreate = {
                    Log.d(TAG, "Navegando al formulario de crear emprendedor")
                    val route = Routes.EMPRENDEDOR_FORM.replace("{id}", "0")
                    navController.navigate(route)
                },
                onBack = {
                    Log.d(TAG, "Volviendo desde emprendedores")
                    navController.popBackStack()
                },
                factory = factory
            )
        }

        composable(
            route = Routes.EMPRENDEDORES_BY_MUNICIPALIDAD,
            arguments = listOf(navArgument("municipalidadId") { type = NavType.LongType })
        ) { backStackEntry ->
            val municipalidadId = backStackEntry.arguments?.getLong("municipalidadId") ?: 0L
            EmprendedoresScreen(
                municipalidadId = municipalidadId,
                onNavigateToDetail = { id ->
                    Log.d(TAG, "Navegando al detalle de emprendedor: $id")
                    val route = Routes.EMPRENDEDOR_DETAIL.replace("{id}", id.toString())
                    navController.navigate(route)
                },
                onNavigateToCreate = {
                    Log.d(TAG, "Navegando al formulario de crear emprendedor para municipalidad: $municipalidadId")
                    val route = Routes.EMPRENDEDOR_FORM.replace("{id}", "0")
                    navController.navigate(route)
                },
                onBack = {
                    Log.d(TAG, "Volviendo desde emprendedores de municipalidad")
                    navController.popBackStack()
                },
                factory = factory
            )
        }

        composable(
            route = Routes.EMPRENDEDOR_DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            EmprendedorDetailScreen(
                emprendedorId = id,
                onNavigateToEdit = {
                    val route = Routes.EMPRENDEDOR_FORM.replace("{id}", id.toString())
                    navController.navigate(route)
                },
                onNavigateToMunicipalidad = { municipalidadId ->
                    val route = Routes.MUNICIPALIDAD_DETAIL.replace("{id}", municipalidadId.toString())
                    navController.navigate(route)
                },
                onNavigateToCategoria = { categoriaId ->
                    val route = Routes.CATEGORIA_DETAIL.replace("{id}", categoriaId.toString())
                    navController.navigate(route)
                },
                onBack = {
                    navController.popBackStack()
                },
                factory = factory
            )
        }

        composable(
            route = Routes.EMPRENDEDOR_FORM,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L

            EmprendedorFormScreen(
                emprendedorId = id,
                onSuccess = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
                factory = factory
            )
        }
        // Categorías
        composable(Routes.CATEGORIAS) {
            CategoriasScreen(
                onNavigateToDetail = { id ->
                    Log.d(TAG, "Navegando al detalle de categoría: $id")
                    navController.navigate(Routes.CATEGORIA_DETAIL.replace("{id}", id.toString()))
                },
                onNavigateToCreate = {
                    Log.d(TAG, "Navegando al formulario de crear categoría")
                    val route = Routes.CATEGORIA_FORM.replace("{id}", "0")
                    navController.navigate(route)
                },
                onNavigateToEmprendedores = { categoriaId ->
                    Log.d(TAG, "Navegando a emprendedores de categoría: $categoriaId")
                    val route = Routes.EMPRENDEDORES_BY_CATEGORIA.replace("{categoriaId}", categoriaId.toString())
                    navController.navigate(route)
                },
                onBack = {
                    Log.d(TAG, "Volviendo desde categorías")
                    navController.popBackStack()
                },
                factory = factory
            )
        }

        composable(
            route = Routes.CATEGORIA_DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            CategoriaDetailScreen(
                categoriaId = id,
                onNavigateToEdit = {
                    Log.d(TAG, "Navegando al formulario de editar categoría: $id")
                    val route = Routes.CATEGORIA_FORM.replace("{id}", id.toString())
                    navController.navigate(route)
                },
                onNavigateToEmprendedores = {
                    Log.d(TAG, "Navegando a emprendedores de categoría: $id")
                    val route = Routes.EMPRENDEDORES_BY_CATEGORIA.replace("{categoriaId}", id.toString())
                    navController.navigate(route)
                },
                onBack = {
                    Log.d(TAG, "Volviendo desde detalle de categoría")
                    navController.popBackStack()
                },
                factory = factory
            )
        }

        composable(
            route = Routes.CATEGORIA_FORM,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            CategoriaFormScreen(
                categoriaId = id,
                onSuccess = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
                factory = factory
            )
        }

        composable(
            route = Routes.EMPRENDEDORES_BY_CATEGORIA,
            arguments = listOf(navArgument("categoriaId") { type = NavType.LongType })
        ) { backStackEntry ->
            val categoriaId = backStackEntry.arguments?.getLong("categoriaId") ?: 0L
            EmprendedoresScreen(
                categoriaId = categoriaId,
                onNavigateToDetail = { id ->
                    Log.d(TAG, "Navegando al detalle de emprendedor: $id")
                    val route = Routes.EMPRENDEDOR_DETAIL.replace("{id}", id.toString())
                    navController.navigate(route)
                },
                onNavigateToCreate = {
                    Log.d(TAG, "Navegando al formulario de crear emprendedor para categoría: $categoriaId")
                    val route = Routes.EMPRENDEDOR_FORM.replace("{id}", "0")
                    navController.navigate(route)
                },
                onBack = {
                    Log.d(TAG, "Volviendo desde emprendedores de categoría")
                    navController.popBackStack()
                },
                factory = factory
            )
        }

        // Nuevas rutas del módulo de turismo
        
        // Planes turísticos para usuarios
        composable(Routes.PLANES_USUARIO) {
            PlanesUsuarioScreen(
                onNavigateToDetail = { id ->
                    val route = Routes.PLAN_DETAIL.replace("{id}", id.toString())
                    navController.navigate(route)
                },
                onNavigateToReserva = { planId ->
                    val route = Routes.RESERVA_FORM.replace("{planId}", planId.toString())
                    navController.navigate(route)
                },
                onBack = {
                    navController.popBackStack()
                },
                factory = factory
            )
        }
        
        // Detalle de plan turístico
        composable(
            route = Routes.PLAN_DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            PlanDetailScreen(
                planId = id,
                onNavigateToReserva = { planId ->
                    val route = Routes.RESERVA_FORM.replace("{planId}", planId.toString())
                    navController.navigate(route)
                },
                onBack = {
                    navController.popBackStack()
                },
                factory = factory
            )
        }
        
        // Formulario de reserva
        composable(
            route = Routes.RESERVA_FORM,
            arguments = listOf(navArgument("planId") { type = NavType.LongType })
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getLong("planId") ?: 0L
            ReservaFormScreen(
                planId = planId,
                onSuccess = {
                    navController.navigate(Routes.MIS_RESERVAS) {
                        popUpTo(Routes.PLANES_USUARIO) { inclusive = false }
                    }
                },
                onBack = {
                    navController.popBackStack()
                },
                factory = factory
            )
        }
        
        // Mis reservas
        composable(Routes.MIS_RESERVAS) {
            MisReservasScreen(
                onNavigateToDetail = { id ->
                    val route = Routes.RESERVA_DETAIL.replace("{id}", id.toString())
                    navController.navigate(route)
                },
                onBack = {
                    navController.popBackStack()
                },
                factory = factory
            )
        }
        
        // Detalle de reserva
        composable(
            route = Routes.RESERVA_DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            ReservaDetailScreen(
                reservaId = id,
                onBack = {
                    navController.popBackStack()
                },
                factory = factory
            )
        }
        
        // Tienda de servicios
        composable(Routes.SERVICIOS_STORE) {
            ServicioStoreScreen(
                onNavigateToDetail = { id ->
                    Log.d(TAG, "Navegando al detalle del servicio: $id")
                    val route = Routes.SERVICIO_DETAIL.replace("{id}", id.toString())
                    navController.navigate(route)
                },
                onBack = {
                    navController.popBackStack()
                },
                factory = factory
            )
        }
        
        // Detalle de servicio
        composable(
            route = Routes.SERVICIO_DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            ServicioDetailScreen(
                servicioId = id,
                onNavigateToEdit = {
                    val route = Routes.SERVICIO_FORM.replace("{id}", id.toString())
                    navController.navigate(route)
                },
                onNavigateToEmprendedor = { emprendedorId ->
                    val route = Routes.EMPRENDEDOR_DETAIL.replace("{id}", emprendedorId.toString())
                    navController.navigate(route)
                },
                onBack = {
                    navController.popBackStack()
                },
                // TODO: Implementar lógica para determinar si puede editar según el rol del usuario
                canEdit = true,
                factory = factory
            )
        }
        
        // Formulario de servicio
        composable(
            route = Routes.SERVICIO_FORM,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            ServicioFormScreen(
                servicioId = id,
                onSuccess = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                },
                factory = factory
            )
        }
        
        // Mis servicios (para emprendedores)
        composable(Routes.MIS_SERVICIOS) {
            MisServiciosScreen(
                onNavigateToDetail = { id ->
                    val route = Routes.SERVICIO_DETAIL.replace("{id}", id.toString())
                    navController.navigate(route)
                },
                onNavigateToCreate = {
                    val route = Routes.SERVICIO_FORM.replace("{id}", "0")
                    navController.navigate(route)
                },
                onBack = {
                    navController.popBackStack()
                },
                factory = factory
            )
        }
        
        // Panel administrativo
        composable(Routes.ADMIN_DASHBOARD) {
            AdminDashboardScreen(
                onNavigateToReservas = {
                    navController.navigate(Routes.ADMIN_RESERVAS)
                },
                onNavigateToPlanes = {
                    navController.navigate(Routes.ADMIN_PLANES)
                },
                onNavigateToServicios = {
                    navController.navigate(Routes.ADMIN_SERVICIOS)
                },
                onNavigateToPagos = {
                    navController.navigate(Routes.PAGOS)
                },
                onNavigateToUsers = {
                    navController.navigate(Routes.USER_MANAGEMENT)
                },
                onBack = {
                    navController.popBackStack()
                },
                factory = factory
            )
        }
        
        // Administración de reservas
        composable(Routes.ADMIN_RESERVAS) {
            AdminReservasScreen(
                onNavigateToDetail = { id ->
                    val route = Routes.RESERVA_DETAIL.replace("{id}", id.toString())
                    navController.navigate(route)
                },
                onBack = {
                    navController.popBackStack()
                },
                factory = factory
            )
        }
        
        // Administración de planes
        composable(Routes.ADMIN_PLANES) {
            AdminPlanesScreen(
                onNavigateToDetail = { id ->
                    val route = Routes.PLAN_DETAIL.replace("{id}", id.toString())
                    navController.navigate(route)
                },
                onNavigateToCreate = {
                    Log.d(TAG, "Navegando al formulario de crear plan")
                    // TODO: Implementar ruta para crear plan
                },
                onBack = {
                    navController.popBackStack()
                },
                factory = factory
            )
        }
        
        // Administración de servicios
        composable(Routes.ADMIN_SERVICIOS) {
            AdminServiciosScreen(
                onNavigateToDetail = { id ->
                    Log.d(TAG, "Navegando al detalle del servicio: $id")
                    val route = Routes.SERVICIO_DETAIL.replace("{id}", id.toString())
                    navController.navigate(route)
                },
                onNavigateToCreate = {
                    Log.d(TAG, "Navegando al formulario de crear servicio")
                    val route = Routes.SERVICIO_FORM.replace("{id}", "0")
                    navController.navigate(route)
                },
                onBack = {
                    navController.popBackStack()
                },
                factory = factory
            )
        }
        
        // Gestión de usuarios
        composable(Routes.USER_MANAGEMENT) {
            UserManagementScreen(
                onNavigateToUserDetail = { userId ->
                    val route = Routes.USER_DETAIL.replace("{userId}", userId.toString())
                    navController.navigate(route)
                },
                onBack = {
                    navController.popBackStack()
                },
                factory = factory
            )
        }
        
        composable(
            route = Routes.USER_DETAIL,
            arguments = listOf(navArgument("userId") { type = NavType.LongType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
            UserDetailScreen(
                userId = userId,
                onBack = {
                    navController.popBackStack()
                },
                factory = factory
            )
        }
        
        // Gestión de pagos
        composable(Routes.PAGOS) {
            PagosScreen(
                onNavigateToPagoDetail = { id ->
                    Log.d(TAG, "Navegando al detalle del pago: $id")
                },
                onBack = {
                    navController.popBackStack()
                },
                factory = factory
            )
        }
    }
}

// Extensión para mejorar la navegación
fun NavController.navigateToTop(route: String) {
    this.navigate(route) {
        // Pop hasta la pantalla inicial
        popUpTo(this@navigateToTop.graph.findStartDestination().id) {
            inclusive = true
        }
        // Evitar múltiples copias de la misma pantalla
        launchSingleTop = true
    }
}

// Función para configurar el logging de navegación
@Composable
fun SetupNavigationLogging(navController: NavHostController) {
    navController.addOnDestinationChangedListener { _, destination, arguments ->
        Log.d("Navigation", "Navegando a: ${destination.route}, Args: $arguments")
    }
}