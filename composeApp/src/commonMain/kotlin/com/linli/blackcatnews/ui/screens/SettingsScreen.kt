package com.linli.blackcatnews.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.linli.blackcatnews.presentation.viewmodel.SessionStatus
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
            val primaryActionLabel = when (uiState.sessionStatus) {
                SessionStatus.AUTHENTICATED -> "登出"
                SessionStatus.ANONYMOUS, SessionStatus.VISITOR -> "登入"
            }
            val primaryAction = {
                when (uiState.sessionStatus) {
                    SessionStatus.AUTHENTICATED -> viewModel.signOut()
                    SessionStatus.ANONYMOUS, SessionStatus.VISITOR -> onNavigateToSignIn()
                }
            }

            Text(
                text = "帳號",
                style = MaterialTheme.typography.titleMedium
            )
            UserInfoBanner(
                name = if (uiState.isAuthenticated) uiState.userName else "訪客",
                email = if (uiState.isAuthenticated) uiState.userEmail else null,
                isAuthed = uiState.isAuthenticated,
                isAnonymous = uiState.isAnonymous,
                actionLabel = primaryActionLabel,
                onActionClick = primaryAction,
                isLoading = uiState.isLoading
            )

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

@Composable
private fun UserInfoBanner(
    name: String,
    email: String?,
    isAuthed: Boolean,
    isAnonymous: Boolean,
    actionLabel: String,
    onActionClick: () -> Unit,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isAuthed) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f)
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        if (isAuthed) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.firstOrNull()?.uppercase() ?: "V",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isAuthed) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = when {
                        isAuthed -> name.ifBlank { "使用者" }
                        isAnonymous -> "匿名登入"
                        else -> "訪客"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                val secondary = when {
                    isAuthed && email.isNullOrBlank() -> "未提供 email"
                    isAuthed -> email
                    isAnonymous -> "目前是匿名帳號，可登入以同步收藏與進度"
                    else -> "登入以同步收藏與學習進度"
                }
                if (!secondary.isNullOrBlank()) {
                    Text(
                        text = secondary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Button(
                onClick = onActionClick,
                enabled = !isLoading,
                colors = if (isAuthed) ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ) else ButtonDefaults.buttonColors(),
                modifier = Modifier.height(40.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = if (isAuthed) MaterialTheme.colorScheme.onErrorContainer
                        else MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(actionLabel)
                }
            }
        }
    }
}
