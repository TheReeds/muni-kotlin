package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.AuthResponse
import com.capachica.turismokotlin.data.model.RegisterRequest
import com.capachica.turismokotlin.data.repository.AuthRepository
import com.capachica.turismokotlin.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    // Inicializando con estado Initial en lugar de Loading
    private val _loginState = MutableStateFlow<Result<AuthResponse>>(Result.Success(AuthResponse("", "", 0, "", "", emptyList())))
    val loginState: StateFlow<Result<AuthResponse>> = _loginState

    private val _registerState = MutableStateFlow<Result<AuthResponse>>(Result.Success(AuthResponse("", "", 0, "", "", emptyList())))
    val registerState: StateFlow<Result<AuthResponse>> = _registerState

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    init {
        // Verificar estado de login al iniciar el ViewModel
        checkLoginStatus()
    }

    fun login(username: String, password: String) {
        _loginState.value = Result.Loading
        viewModelScope.launch {
            repository.login(username, password).collect { result ->
                _loginState.value = result
                if (result is Result.Success) {
                    _isLoggedIn.value = true
                }
            }
        }
    }

    fun register(request: RegisterRequest) {
        _registerState.value = Result.Loading
        viewModelScope.launch {
            repository.register(request).collect { result ->
                _registerState.value = result
                if (result is Result.Success) {
                    _isLoggedIn.value = true
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _isLoggedIn.value = false
            // Reiniciar estados
            _loginState.value = Result.Success(AuthResponse("", "", 0, "", "", emptyList()))
            _registerState.value = Result.Success(AuthResponse("", "", 0, "", "", emptyList()))
        }
    }

    fun checkLoginStatus() {
        viewModelScope.launch {
            repository.isUserLoggedIn().collect { isLoggedIn ->
                _isLoggedIn.value = isLoggedIn
            }
        }
    }
}