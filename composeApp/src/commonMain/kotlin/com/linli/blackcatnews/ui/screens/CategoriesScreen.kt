package com.linli.blackcatnews.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * 分類屏幕
 * 顯示所有新聞分類
 * Scaffold 和 TopBar 由 AppNavigation 統一管理
 */
@Composable
fun CategoriesScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("分類頁面 - 開發中")
    }
}
