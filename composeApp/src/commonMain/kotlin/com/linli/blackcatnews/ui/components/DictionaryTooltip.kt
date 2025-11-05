package com.linli.blackcatnews.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import com.linli.dictionary.presentation.DictionaryState

/**
 * 字典提示工具列。根據字典狀態顯示不同的內容：加載中、出錯或單字定義。
 *
 * @param state 字典視圖模型的狀態
 * @param selectedWord 當前選中的單字
 * @param offset 提示框的偏移位置
 * @param onDismiss 關閉提示框的回調
 * @param onSaveWord 儲存單字的回調
 * @param onPronounceClick 點擊發音按鈕的回調
 */
@Composable
fun DictionaryTooltip(
    state: DictionaryState,
    selectedWord: String,
    offset: IntOffset,
    onDismiss: () -> Unit,
    onSaveWord: () -> Unit,
    onPronounceClick: (String) -> Unit = {}
) {
    Popup(
        offset = offset,
        onDismissRequest = onDismiss
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            // 顯示內容根據 DictionaryState 狀態進行分支
            when {
                state.isLoading -> {
                    LoadingTooltipContent(word = selectedWord, onDismiss = onDismiss)
                }

                state.error != null -> {
                    ErrorTooltipContent(
                        word = selectedWord,
                        error = state.error ?: "Unknown error",
                        onDismiss = onDismiss
                    )
                }

                // Smart cast: 必須先將 word 抽取並判空後再比對 .word
                state.word != null -> {
                    val w = state.word
                    if (w != null && w.word.equals(selectedWord, ignoreCase = true)) {
                        DictionaryTooltipContent(
                            word = w,
                            onDismiss = onDismiss,
                            onSaveWord = onSaveWord,
                            onPronounceClick = onPronounceClick
                        )
                    } else {
                        LoadingTooltipContent(word = selectedWord, onDismiss = onDismiss)
                    }
                }

                else -> {
                    LoadingTooltipContent(word = selectedWord, onDismiss = onDismiss)
                }
            }
        }
    }
}