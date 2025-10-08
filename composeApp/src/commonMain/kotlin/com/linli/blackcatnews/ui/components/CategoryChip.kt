package com.linli.blackcatnews.ui.components

import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.linli.blackcatnews.domain.model.NewsCategory

/**
 * 分類 Chip 組件
 * 用於選擇新聞分類
 */
@Composable
fun CategoryChip(
    category: NewsCategory,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(category.displayName) },
        modifier = modifier
    )
}
