package com.linli.authentication.presentation

import com.linli.authentication.AuthCredential
import com.linli.authentication.domain.model.UserSession

/**
 * UDF/MVI 合約：登入畫面的狀態管理
 */

/**
 * UI 狀態
 */
data class SignInState(
    val isLoading: Boolean = false,
    val user: UserSession? = null,
    val error: String? = null
)

/**
 * 使用者意圖（UI 事件）
 */
sealed interface SignInAction {
    data object ClickApple : SignInAction
    data object ClickGoogle : SignInAction
    data class ClickFacebook(val credential: AuthCredential) : SignInAction
    data object DismissError : SignInAction
}

/**
 * 中間結果（邏輯層輸出給 reducer）
 */
sealed interface SignInResult {
    data object Loading : SignInResult
    data class Success(val user: UserSession) : SignInResult
    data class Failure(val message: String) : SignInResult
}

/**
 * 一次性副作用（SnackBar、導航、Toast 等）
 */
sealed interface SignInEffect {
    data class ShowSnackbar(val message: String) : SignInEffect
    data object NavigateHome : SignInEffect
}
