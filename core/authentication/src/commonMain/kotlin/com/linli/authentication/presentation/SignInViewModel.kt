package com.linli.authentication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linli.authentication.ProviderType
import com.linli.authentication.domain.SignInUIClient
import com.linli.authentication.domain.usecase.SignInUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 單向資料流 ViewModel：
 * 1) UI 呼叫 dispatch(Action)
 * 2) VM 轉為 Result（含 IO）並丟進 reducer
 * 3) reducer 純函式更新 state
 *
 * 改進後的設計：
 * - UI 層建立 UIClients (使用 Builder)
 * - ViewModel 接收 UIClients Map (通過 parametersOf 注入)
 * - UseCase 處理業務邏輯
 */
class SignInViewModel(
    private val signInUseCase: SignInUseCase,
    private val uiClients: Map<ProviderType, SignInUIClient>
) : ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state: StateFlow<SignInState> = _state.asStateFlow()

    // 單次事件（如導航）
    private val _effect = MutableSharedFlow<SignInEffect>()
    val effect: SharedFlow<SignInEffect> = _effect // 公開讓 UI 可以監聽

    /**
     * 分發 Action（單一入口）
     */
    fun dispatch(action: SignInAction) {
        viewModelScope.launch {
            try {
                // 轉成 Result
                val result = when (action) {
                    is SignInAction.ClickApple -> performSignIn(ProviderType.Apple)
                    is SignInAction.ClickGoogle -> performSignIn(ProviderType.Google)
                    is SignInAction.ClickFacebook -> SignInResult.Failure("Facebook 登入尚未實作")
                    SignInAction.DismissError -> SignInResult.Failure("")
                }

                // 更新 state（使用 reducer）
                _state.value = reduceSignIn(_state.value, result)

                // 處理副作用（如導航）
                if (result is SignInResult.Success) {
                    _effect.emit(SignInEffect.NavigateHome)
                } else if (result is SignInResult.Failure && result.message.isNotEmpty()) {
                    _effect.emit(SignInEffect.ShowSnackbar(result.message))
                }
            } catch (e: Exception) {
                _state.value =
                    reduceSignIn(_state.value, SignInResult.Failure("發生錯誤: ${e.message}"))
            }
        }
    }

    /**
     * 執行登入（統一邏輯）
     *
     * ViewModel 提供 UIClient Map 給 UseCase
     * UseCase 負責：
     * 1. 檢查是否支援
     * 2. 取得使用者憑證
     * 3. 驗證憑證
     * 4. 執行 Firebase 登入
     * 5. 返回結果
     *
     * @param providerType 認證供應商類型
     */
    private suspend fun performSignIn(providerType: ProviderType): SignInResult {
        return try {
            emit(SignInResult.Loading)

            // UseCase 處理所有業務邏輯（包括檢查支援）
            val result = signInUseCase(providerType, uiClients)

            result.fold(
                onSuccess = { session -> SignInResult.Success(session) },
                onFailure = { e ->
                    SignInResult.Failure(
                        e.message ?: "${providerType.name} 登入失敗"
                    )
                }
            )
        } catch (e: Exception) {
            SignInResult.Failure("${providerType.name} 登入發生錯誤: ${e.message}")
        }
    }

    /**
     * 立即更新 state（用於顯示載入狀態）
     */
    private fun emit(result: SignInResult) {
        _state.value = reduceSignIn(_state.value, result)
    }
}

/* ---- 輔助：UI 端的易用封裝 ---- */

fun SignInViewModel.onAppleClick() = dispatch(SignInAction.ClickApple)
fun SignInViewModel.onGoogleClick() = dispatch(SignInAction.ClickGoogle)
fun SignInViewModel.onErrorShown() = dispatch(SignInAction.DismissError)
