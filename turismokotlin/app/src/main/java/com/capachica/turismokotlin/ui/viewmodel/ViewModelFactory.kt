package com.capachica.turismokotlin.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capachica.turismokotlin.data.api.ApiClient
import com.capachica.turismokotlin.data.local.AppDatabase
import com.capachica.turismokotlin.data.local.SessionManager
import com.capachica.turismokotlin.data.repository.AuthRepository
import com.capachica.turismokotlin.data.repository.CategoriaRepository
import com.capachica.turismokotlin.data.repository.EmprendedorRepository
import com.capachica.turismokotlin.data.repository.MunicipalidadRepository
import com.capachica.turismokotlin.data.repository.ServicioTuristicoRepository
import com.capachica.turismokotlin.data.repository.PlanTuristicoRepository
import com.capachica.turismokotlin.data.repository.ReservaRepository
import com.capachica.turismokotlin.data.repository.PagoRepository
import com.capachica.turismokotlin.data.repository.UserRepository

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Configuración de servicios comunes
        val sessionManager = SessionManager(context)
        val apiService = ApiClient.getApiService(sessionManager)

        // Configuración de base de datos
        val database = AppDatabase.getInstance(context)
        val municipalidadDao = database.municipalidadDao()
        val emprendedorDao = database.emprendedorDao()
        val emprendedorMunicipalidadDao = database.emprendedorMunicipalidadDao()

        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                val repository = AuthRepository(apiService, sessionManager)
                AuthViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MunicipalidadViewModel::class.java) -> {
                val repository = MunicipalidadRepository(
                    apiService,
                    municipalidadDao,
                    emprendedorMunicipalidadDao
                )
                MunicipalidadViewModel(repository) as T
            }
            modelClass.isAssignableFrom(EmprendedorViewModel::class.java) -> {
                val repository = EmprendedorRepository(
                    apiService,
                    emprendedorDao,
                    municipalidadDao,
                    emprendedorMunicipalidadDao
                )
                EmprendedorViewModel(repository) as T
            }
            modelClass.isAssignableFrom(CategoriaViewModel::class.java) -> {
                val repository = CategoriaRepository(apiService)
                CategoriaViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ServicioTuristicoViewModel::class.java) -> {
                val repository = ServicioTuristicoRepository(apiService)
                ServicioTuristicoViewModel(repository) as T
            }
            modelClass.isAssignableFrom(PlanTuristicoViewModel::class.java) -> {
                val repository = PlanTuristicoRepository(apiService)
                PlanTuristicoViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ReservaViewModel::class.java) -> {
                val repository = ReservaRepository(apiService)
                ReservaViewModel(repository) as T
            }
            modelClass.isAssignableFrom(PagoViewModel::class.java) -> {
                val repository = PagoRepository(apiService)
                PagoViewModel(repository) as T
            }
            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                val repository = UserRepository(apiService)
                UserViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("ViewModel no encontrado")
        }
    }
}
