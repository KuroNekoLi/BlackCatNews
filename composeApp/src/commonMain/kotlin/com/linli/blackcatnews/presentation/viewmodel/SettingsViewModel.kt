package com.linli.blackcatnews.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linli.blackcatnews.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val prefersChinese: Boolean = false
)

class SettingsViewModel(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<SettingsUiState> = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observePreferences()
    }

    private fun observePreferences() {
        viewModelScope.launch {
            preferencesRepository.prefersChinese().collect { prefersChinese ->
                _uiState.value = SettingsUiState(prefersChinese)
            }
        }
    }

    fun setLanguagePreference(useChinese: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setPrefersChinese(useChinese)
        }
    }
}
