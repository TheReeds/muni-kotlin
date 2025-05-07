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