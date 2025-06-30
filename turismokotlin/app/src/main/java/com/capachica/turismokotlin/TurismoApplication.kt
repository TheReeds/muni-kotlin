package com.capachica.turismokotlin

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TurismoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializaciones adicionales si son necesarias
    }
}