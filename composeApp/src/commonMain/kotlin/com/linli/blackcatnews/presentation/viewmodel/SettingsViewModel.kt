package com.linli.blackcatnews.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linli.authentication.domain.usecase.SignOutUseCase
import com.linli.blackcatnews.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val prefersChinese: Boolean = false,
    val isLoading: Boolean = false
)

/**
 * 設定畫面的副作用
 */
sealed interface SettingsEffect {
    data object NavigateToSignIn : SettingsEffect
    data class ShowError(val message: String) : SettingsEffect
}

class SettingsViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<SettingsUiState> = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<SettingsEffect>()
    val effect: SharedFlow<SettingsEffect> = _effect

    init {
        observePreferences()
    }

    private fun observePreferences() {
        viewModelScope.launch {
            preferencesRepository.prefersChinese().collect { prefersChinese ->
                _uiState.value = _uiState.value.copy(prefersChinese = prefersChinese)
            }
        }
    }

    fun setLanguagePreference(useChinese: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setPrefersChinese(useChinese)
        }
    }

    /**
     * 執行登出
     */
    fun signOut() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val result = signOutUseCase()

                result.fold(
                    onSuccess = {
                        // 登出成功，導航到登入畫面
                        _effect.emit(SettingsEffect.NavigateToSignIn)
                    },
                    onFailure = { error ->
                        // 登出失敗，顯示錯誤訊息
                        _effect.emit(SettingsEffect.ShowError(error.message ?: "登出失敗"))
                    }
                )
            } catch (e: Exception) {
                _effect.emit(SettingsEffect.ShowError("登出發生錯誤: ${e.message}"))
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}
