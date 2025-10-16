package com.linli.blackcatnews.ui.components.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.linli.blackcatnews.domain.model.ReadingMode
import com.linli.blackcatnews.ui.components.ReadingModeSelector
import com.linli.blackcatnews.ui.theme.AppTheme

@Preview(showBackground = true, name = "Reading Mode Selector - English Selected")
@Composable
private fun ReadingModeSelectorEnglishPreview() {
    AppTheme {
        ReadingModeSelector(
            currentMode = ReadingMode.ENGLISH_ONLY,
            onModeChange = {}
        )
    }
}

@Preview(showBackground = true, name = "Reading Mode Selector - Chinese Selected")
@Composable
private fun ReadingModeSelectorChinesePreview() {
    AppTheme {
        ReadingModeSelector(
            currentMode = ReadingMode.CHINESE_ONLY,
            onModeChange = {}
        )
    }
}

@Preview(showBackground = true, name = "Reading Mode Selector - Stacked Selected")
@Composable
private fun ReadingModeSelectorStackedPreview() {
    AppTheme {
        ReadingModeSelector(
            currentMode = ReadingMode.STACKED,
            onModeChange = {}
        )
    }
}

@Preview(showBackground = true, name = "Reading Mode Selector - Side by Side Selected")
@Composable
private fun ReadingModeSelectorSideBySidePreview() {
    AppTheme {
        ReadingModeSelector(
            currentMode = ReadingMode.SIDE_BY_SIDE,
            onModeChange = {}
        )
    }
}