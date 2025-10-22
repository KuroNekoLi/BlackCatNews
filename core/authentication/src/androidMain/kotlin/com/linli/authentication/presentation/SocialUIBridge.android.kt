package com.linli.authentication.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Android 平台的社群登入按鈕
 * 不顯示 Apple 登入（Apple 只在 iOS 支援）
 *
 * 改進後的設計：
 * - UIClient 在 UI 層創建（有 Activity）
 * - 這裡只負責顯示按鈕
 */
@Composable
actual fun SocialMediaButtonListPlatformSpecificUI(
    onAppleClicked: () -> Unit,
    onGoogleClicked: () -> Unit,
    onFacebookClicked: () -> Unit
) {
    Column {
        Button(
            onClick = onGoogleClicked,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
            Text("使用 Google 登入")
        }
        Button(
            onClick = onFacebookClicked,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
            Text("使用 Facebook 登入")
        }
    }
}
