package com.linli.blackcatnews.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * 收藏屏幕
 * 顯示用戶收藏的新聞
 * Scaffold 和 TopBar 由 AppNavigation 統一管理
 */
@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("收藏頁面 - 開發中")
    }
}
