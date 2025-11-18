package com.linli.blackcatnews.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.linli.blackcatnews.presentation.viewmodel.SettingsEffect
import com.linli.blackcatnews.presentation.viewmodel.SettingsViewModel

/**
 * 設定屏幕
 * 顯示應用設定選項
 * Scaffold 和 TopBar 由 AppNavigation 統一管理
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle side effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SettingsEffect.NavigateToSignIn -> onNavigateToSignIn()
                is SettingsEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Language preference section
            Text(
                text = "語言偏好",
                style = MaterialTheme.typography.titleMedium
            )
            RowSettingItem(
                title = "優先顯示中文內容",
                description = "控制首頁與新聞列表的語言顯示方式",
                isChecked = uiState.prefersChinese,
                onToggle = { viewModel.setLanguagePreference(it) }
            )

            // Divider
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Account section
            Text(
                text = "帳號",
                style = MaterialTheme.typography.titleMedium
            )

            // Sign out button
            Button(
                onClick = { viewModel.signOut() },
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                } else {
                    Text("登出")
                }
            }

            // Divider
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Feedback section
            Text(
                text = "意見反饋",
                style = MaterialTheme.typography.titleMedium
            )
            FeedbackButton()

            // Divider
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Spacer(Modifier.weight(1f))
            // App version display at the bottom
            val versionName = "1.0"
            val displayVersion = "1.0"
            val platform = "ios"
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "版本號：$versionName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = displayVersion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "平台：$platform",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Snackbar for error messages
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun RowSettingItem(
    title: String,
    description: String,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.outlineVariant
            )
        )
    }
}

/**
 * 提供快速發送 email 意見反饋的按鈕
 * 使用 mailto 連結調用系統 email 客戶端
 */
@Composable
private fun FeedbackButton() {
    val uriHandler = LocalUriHandler.current
    Button(
        onClick = {
            uriHandler.openUri("mailto:alice.margatroid.love@gmail.com?subject=BlackCatNews%20意見回饋")
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("發送意見反饋 Email")
    }
}
