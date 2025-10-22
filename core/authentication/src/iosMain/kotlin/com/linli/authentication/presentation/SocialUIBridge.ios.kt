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
 * iOS 平台的社群登入按鈕
 * 顯示 Apple 和 Google 登入
 */
@Composable
actual fun SocialMediaButtonListPlatformSpecificUI(
    onAppleClicked: () -> Unit,
    onGoogleClicked: () -> Unit,
    onFacebookClicked: () -> Unit
) {
    Column {
        Button(
            onClick = onAppleClicked,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
            Text("使用 Apple 登入")
        }
        Button(
            onClick = onGoogleClicked,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
            Text("使用 Google 登入")
        }
    }
}
