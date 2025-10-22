package com.linli.authentication.presentation

import androidx.compose.runtime.Composable

/**
 * 由平台端提供實作：Android 顯示 Google/Facebook，iOS 顯示 Apple/Google。
 */
@Composable
expect fun SocialMediaButtonListPlatformSpecificUI(
    onAppleClicked: () -> Unit,
    onGoogleClicked: () -> Unit,
    onFacebookClicked: () -> Unit
)
