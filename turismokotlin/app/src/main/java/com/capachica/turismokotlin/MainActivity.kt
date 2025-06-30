package com.capachica.turismokotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.capachica.turismokotlin.navigation.TurismoNavigation
import com.capachica.turismokotlin.ui.theme.TurismoKotlinTheme
import com.capachica.turismokotlin.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TurismoKotlinTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val isLoggedIn by authViewModel.isLoggedIn.collectAsState(initial = false)
                    val userRoles by authViewModel.userRoles.collectAsState(initial = emptySet())

                    TurismoNavigation(
                        navController = navController,
                        isLoggedIn = isLoggedIn,
                        userRoles = userRoles
                    )
                }
            }
        }
    }
}
