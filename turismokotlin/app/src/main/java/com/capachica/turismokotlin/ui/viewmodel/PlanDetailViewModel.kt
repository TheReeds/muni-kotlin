package com.capachica.turismokotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capachica.turismokotlin.data.model.Plan
import com.capachica.turismokotlin.data.repository.PlanesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanDetailViewModel @Inject constructor(
    private val planesRepository: PlanesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlanDetailUiState())
    val uiState: StateFlow<PlanDetailUiState> = _uiState.asStateFlow()

    fun loadPlanDetails(planId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            planesRepository.getPlanById(planId)
                .onSuccess { plan ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        plan = plan
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class PlanDetailUiState(
    val isLoading: Boolean = false,
    val plan: Plan? = null,
    val error: String? = null
)