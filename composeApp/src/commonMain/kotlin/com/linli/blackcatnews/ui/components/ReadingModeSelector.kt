package com.linli.blackcatnews.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.linli.blackcatnews.domain.model.ReadingMode

/**
 * 閱讀模式選擇器組件
 * 提供四種不同的閱讀模式選擇
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingModeSelector(
    currentMode: ReadingMode,
    onModeChange: (ReadingMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = currentMode == ReadingMode.ENGLISH_ONLY,
            onClick = { onModeChange(ReadingMode.ENGLISH_ONLY) },
            label = { Text("🇬🇧 EN") }
        )
        FilterChip(
            selected = currentMode == ReadingMode.CHINESE_ONLY,
            onClick = { onModeChange(ReadingMode.CHINESE_ONLY) },
            label = { Text("🇹🇼 中") }
        )
        FilterChip(
            selected = currentMode == ReadingMode.STACKED,
            onClick = { onModeChange(ReadingMode.STACKED) },
            label = { Text("⬍ 對照") }
        )
        FilterChip(
            selected = currentMode == ReadingMode.SIDE_BY_SIDE,
            onClick = { onModeChange(ReadingMode.SIDE_BY_SIDE) },
            label = { Text("⬌ 並排") }
        )
    }
}