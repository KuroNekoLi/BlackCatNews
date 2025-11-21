package com.linli.blackcatnews.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linli.authentication.domain.usecase.GetCurrentUserUseCase
import com.linli.authentication.domain.usecase.SignOutUseCase
import com.linli.blackcatnews.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SessionStatus {
    VISITOR,
    ANONYMOUS,
    AUTHENTICATED
}

data class SettingsUiState(
    val prefersChinese: Boolean = false,
    val isLoading: Boolean = false,
    val userName: String = "訪客",
    val userEmail: String? = null,
    val isAuthenticated: Boolean = false, // true = 已登入帳號
    val isAnonymous: Boolean = false,
    val sessionStatus: SessionStatus = SessionStatus.VISITOR
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
    private val signOutUseCase: SignOutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<SettingsUiState> = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<SettingsEffect>()
    val effect: SharedFlow<SettingsEffect> = _effect

    init {
        observePreferences()
        loadCurrentUser()
    }

    private fun observePreferences() {
        viewModelScope.launch {
            preferencesRepository.prefersChinese().collect { prefersChinese ->
                _uiState.value = _uiState.value.copy(prefersChinese = prefersChinese)
            }
        }
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val user = getCurrentUserUseCase()
            val isAnon = user?.providerIds?.any { it.equals("anonymous", ignoreCase = true) } == true
            val isAuthed = user != null && !isAnon

            val (name, email, status) = when {
                user == null -> Triple("訪客", null, SessionStatus.VISITOR)
                isAnon -> Triple("匿名使用者", null, SessionStatus.ANONYMOUS)
                else -> Triple(user.email?.substringBefore('@') ?: "使用者", user.email, SessionStatus.AUTHENTICATED)
            }

            _uiState.value = _uiState.value.copy(
                userName = name,
                userEmail = email,
                isAuthenticated = isAuthed,
                isAnonymous = isAnon,
                sessionStatus = status
            )
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
                        // 登出成功，留在設定頁並重置狀態
                        _uiState.value = _uiState.value.copy(
                            isAuthenticated = false,
                            userName = "訪客",
                            userEmail = null,
                            isAnonymous = false,
                            sessionStatus = SessionStatus.VISITOR
                        )
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
