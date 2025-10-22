package com.linli.authentication.presentation

/**
 * 純函數：把 Result 壓成新的 State（不做 IO）
 * 只負責「可持久 UI 狀態」；一次性事件交給 Effect 管。
 */
internal fun reduceSignIn(
    prev: SignInState,
    result: SignInResult
): SignInState = when (result) {
    SignInResult.Loading -> prev.copy(isLoading = true, error = null)
    is SignInResult.Success -> prev.copy(isLoading = false, user = result.user, error = null)
    is SignInResult.Failure -> prev.copy(isLoading = false, error = result.message)
}
