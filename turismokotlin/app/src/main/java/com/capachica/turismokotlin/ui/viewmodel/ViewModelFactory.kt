package com.capachica.turismokotlin.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capachica.turismokotlin.data.api.ApiClient
import com.capachica.turismokotlin.data.local.AppDatabase
import com.capachica.turismokotlin.data.local.SessionManager
import com.capachica.turismokotlin.data.repository.AuthRepository
import com.capachica.turismokotlin.data.repository.EmprendedorRepository
import com.capachica.turismokotlin.data.repository.MunicipalidadRepository

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
            else -> throw IllegalArgumentException("ViewModel no encontrado")
        }
    }
}
