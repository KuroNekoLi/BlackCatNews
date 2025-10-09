package com.linli.blackcatnews.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.linli.blackcatnews.presentation.viewmodel.SettingsViewModel

/**
 * 設定屏幕
 * 顯示應用設定選項
 * Scaffold 和 TopBar 由 AppNavigation 統一管理
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    Column(
        modifier = modifier
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
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
