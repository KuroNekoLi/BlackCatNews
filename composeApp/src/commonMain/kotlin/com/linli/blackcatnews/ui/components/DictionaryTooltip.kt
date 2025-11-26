package com.linli.blackcatnews.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import com.linli.dictionary.domain.model.Word
import com.linli.dictionary.presentation.DictionaryState
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 字典提示工具列。根據字典狀態顯示不同的內容：加載中、出錯或單字定義。
 *
 * @param state 字典視圖模型的狀態
 * @param selectedWord 當前選中的單字
 * @param offset 提示框的偏移位置
 * @param onDismiss 關閉提示框的回調
 * @param onSaveWord 儲存單字的回調
 * @param isWordSaved 當前選中單字是否已存在於單字庫中
 * @param onPronounceClick 點擊發音按鈕的回調
 * @param onPlayExample 點擊播放例句按鈕的回調
 */
@Composable
fun DictionaryTooltip(
    state: DictionaryState,
    selectedWord: String,
    offset: IntOffset,
    onDismiss: () -> Unit,
    onSaveWord: () -> Unit,
    isWordSaved: Boolean = false,
    onPronounceClick: (String) -> Unit = {},
    onPlayExample: (String) -> Unit = {}
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
                            isSaved = isWordSaved,
                            onPronounceClick = onPronounceClick,
                            onPlayExample = onPlayExample
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

/**
 * 預覽用 Composable，顯示加載中狀態
 */
@Preview
@Composable
fun PreviewDictionaryTooltipLoading() {
    MaterialTheme {
        // 模擬加載中狀態
        val loadingState = DictionaryState(isLoading = true)
        DictionaryTooltip(
            state = loadingState,
            selectedWord = "loading",
            offset = IntOffset(0, 0),
            onDismiss = {},
            onSaveWord = {},
            onPlayExample = {}
        )
    }
}

/**
 * 預覽用 Composable，顯示錯誤狀態
 */
@Preview
@Composable
fun PreviewDictionaryTooltipError() {
    MaterialTheme {
        // 模擬錯誤狀態
        val errorState = DictionaryState(error = "無法連接到字典服務")
        DictionaryTooltip(
            state = errorState,
            selectedWord = "error",
            offset = IntOffset(0, 0),
            onDismiss = {},
            onSaveWord = {},
            onPlayExample = {}
        )
    }
}

/**
 * 預覽用 Composable，顯示成功查詢的狀態
 */
@Preview
@Composable
fun PreviewDictionaryTooltipSuccess() {
    MaterialTheme {
        // 模擬成功查詢的狀態
        val word = Word(
            word = "success",
            pronunciations = Word.Pronunciations(
                uk = "/sək'ses/",
                us = "/sək'ses/"
            ),
            entries = listOf(
                Word.Entry(
                    partOfSpeech = "n.",
                    definitions = listOf(
                        Word.Definition(
                            enDefinition = "success",
                            zhDefinition = "成功、順利",
                            examples = listOf("His new book was a great success.")
                        )
                    )
                )
            )
        )

        val successState = DictionaryState(word = word)
        DictionaryTooltip(
            state = successState,
            selectedWord = "success",
            offset = IntOffset(0, 0),
            onDismiss = {},
            onSaveWord = {},
            onPlayExample = {}
        )
    }
}