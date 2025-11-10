package com.linli.blackcatnews.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linli.blackcatnews.domain.model.BilingualParagraph
import com.linli.blackcatnews.domain.model.BilingualParagraphType
import com.linli.blackcatnews.domain.model.ReadingMode
import com.linli.dictionary.presentation.DictionaryViewModel
import com.linli.dictionary.presentation.wordbank.WordBankViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

/**
 * 顯示帶有單字提示的文章內容，並整合字典功能。
 * - 支援英文單字的彈出提示功能
 * - 支援中英文不同閱讀模式切換
 * - 直接使用 DictionaryViewModel 查詢單字
 *
 * @param paragraph 要顯示的文章段落
 * @param readingMode 閱讀模式（英文、中文或英文中文對照）
 * @param viewModel 字典視圖模型，用於查詢單字
 * @param wordBankViewModel 生字本視圖模型，用於儲存單字
 * @param modifier 組件修飾用
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ArticleWithWordTooltip(
    paragraph: BilingualParagraph,
    readingMode: ReadingMode,
    viewModel: DictionaryViewModel,
    wordBankViewModel: WordBankViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    // Only apply tooltip functionality to text paragraphs
    if (paragraph.type != BilingualParagraphType.TEXT) {
        BilingualTextView(
            paragraph = paragraph,
            readingMode = readingMode,
            modifier = modifier
        )
        return
    }

    // Store text layout result to determine character positions
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    // Store layout coordinates for the Box that receives taps and the Text that renders content
    var boxCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var textCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    // Currently selected word and its information
    var selectedWord by remember { mutableStateOf("") }

    // Dictionary state
    val dictionaryState = viewModel.state.collectAsState().value

    // Tooltip visibility and position
    var showTooltip by remember { mutableStateOf(false) }
    var tooltipOffset by remember { mutableStateOf(IntOffset(0, 0)) }

    // Main container
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        when (readingMode) {
            ReadingMode.ENGLISH_ONLY -> {
                // English only mode with tooltip
                SelectionContainer {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .onGloballyPositioned { coords ->
                                // Anchor for converting tap positions to window coordinates
                                boxCoordinates = coords
                            }
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = { /* Let SelectionContainer handle this */ },
                                    onTap = { offsetInBox ->
                                        handleWordTapAbsolute(
                                            offsetInBox = offsetInBox,
                                            text = paragraph.english ?: "",
                                            textLayoutResult = textLayoutResult,
                                            boxCoordinates = boxCoordinates,
                                            textCoordinates = textCoordinates,
                                            onWordSelected = { word ->
                                                selectedWord = word
                                                viewModel.lookupWord(word)
                                            },
                                            onTooltipPositioned = { position ->
                                                tooltipOffset = position
                                                showTooltip = true
                                            }
                                        )
                                    }
                                )
                            }
                    ) {
                        Text(
                            text = paragraph.english ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            onTextLayout = { textLayoutResult = it },
                            modifier = Modifier.onGloballyPositioned { coords ->
                                // Coordinates of the actual text layout
                                textCoordinates = coords
                            }
                        )
                    }
                }
            }

            ReadingMode.CHINESE_ONLY -> {
                // Chinese only mode without tooltip
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Text(
                        text = paragraph.chinese ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            ReadingMode.STACKED -> {
                // Stacked mode (English on top with tooltip, Chinese below)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // English part with tooltip
                    Column {
                        Text(
                            text = "🇬🇧 English",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        SelectionContainer {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onGloballyPositioned { coords ->
                                        boxCoordinates = coords
                                    }
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onLongPress = { /* Let SelectionContainer handle this */ },
                                            onTap = { offsetInBox ->
                                                handleWordTapAbsolute(
                                                    offsetInBox = offsetInBox,
                                                    text = paragraph.english ?: "",
                                                    textLayoutResult = textLayoutResult,
                                                    boxCoordinates = boxCoordinates,
                                                    textCoordinates = textCoordinates,
                                                    onWordSelected = { word ->
                                                        selectedWord = word
                                                        viewModel.lookupWord(word)
                                                    },
                                                    onTooltipPositioned = { position ->
                                                        tooltipOffset = position
                                                        showTooltip = true
                                                    }
                                                )
                                            }
                                        )
                                    }
                            ) {
                                Text(
                                    text = paragraph.english ?: "",
                                    style = MaterialTheme.typography.bodyLarge,
                                    onTextLayout = { textLayoutResult = it },
                                    modifier = Modifier.onGloballyPositioned { coords ->
                                        textCoordinates = coords
                                    }
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        Modifier, 1.dp,
                        MaterialTheme.colorScheme.outlineVariant
                    )

                    // Chinese part without tooltip
                    Column {
                        Text(
                            text = "🇹🇼 中文",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = paragraph.chinese ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            ReadingMode.SIDE_BY_SIDE -> {
                // Side by side mode (English left with tooltip, Chinese right)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // English column with tooltip
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "🇬🇧 English",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            SelectionContainer {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onGloballyPositioned { coords ->
                                            boxCoordinates = coords
                                        }
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onLongPress = { /* Let SelectionContainer handle this */ },
                                                onTap = { offsetInBox ->
                                                    handleWordTapAbsolute(
                                                        offsetInBox = offsetInBox,
                                                        text = paragraph.english ?: "",
                                                        textLayoutResult = textLayoutResult,
                                                        boxCoordinates = boxCoordinates,
                                                        textCoordinates = textCoordinates,
                                                        onWordSelected = { word ->
                                                            selectedWord = word
                                                            viewModel.lookupWord(word)
                                                        },
                                                        onTooltipPositioned = { position ->
                                                            tooltipOffset = position
                                                            showTooltip = true
                                                        }
                                                    )
                                                }
                                            )
                                        }
                                ) {
                                    Text(
                                        text = paragraph.english ?: "",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontSize = 16.sp,
                                        onTextLayout = { textLayoutResult = it },
                                        modifier = Modifier.onGloballyPositioned { coords ->
                                            textCoordinates = coords
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Chinese column without tooltip
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "🇹🇼 中文",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Text(
                                text = paragraph.chinese ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // 單字提示 Popup
        if (showTooltip) {
            DictionaryTooltip(
                state = dictionaryState,
                selectedWord = selectedWord,
                offset = tooltipOffset,
                onDismiss = { showTooltip = false },
                onSaveWord = {
                    wordBankViewModel.addWord(selectedWord)
                    showTooltip = false
                }
            )
        }
    }
}

/**
 * 處理 Box 內的點擊事件並計算點擊位置所對應的單字。
 *
 * @param offsetInBox 點擊位置在 Box 內的相對座標。
 * @param text 文章文字內容。
 * @param textLayoutResult Text 的佈局結果，用於取得座標相關資料。
 * @param boxCoordinates Box 的座標資訊。
 * @param textCoordinates Text 的座標資訊。
 * @param onWordSelected 選到單字時的回呼。
 * @param onTooltipPositioned 計算好提示框座標時的回呼。
 * @return 是否成功處理點擊事件。
 */
private fun handleWordTapAbsolute(
    offsetInBox: Offset,
    text: String,
    textLayoutResult: TextLayoutResult?,
    boxCoordinates: LayoutCoordinates?,
    textCoordinates: LayoutCoordinates?,
    onWordSelected: (String) -> Unit,
    onTooltipPositioned: (IntOffset) -> Unit
): Boolean {
    val layout = textLayoutResult ?: return false
    val boxCoords = boxCoordinates ?: return false
    val textCoords = textCoordinates ?: return false

    // 1. Box local -> Text local 座標（不經過 window，直接用 layout tree 做轉換）
    val tapInTextLocal = textCoords.localPositionOf(
        sourceCoordinates = boxCoords,
        relativeToSource = offsetInBox
    )

    // 2. 用 TextLayoutResult 判斷點到哪個字元 index
    val position = layout.getOffsetForPosition(tapInTextLocal)

    // 3. 找出這個 index 所在的完整單字
    val word = findWordAt(text, position)

    // 粗略判斷為 "真的英文單字"
    if (word.isEmpty() ||
        !word.any { it.isLetter() } ||
        !word.any { it.code < 128 } // 英文
    ) {
        return false
    }

    // 通知外層選到這個字
    onWordSelected(word)

    // 4. 取得該字元在 Text-local 座標系中的 bounding box
    val charRect = layout.getBoundingBox(position)

    // 5. 取字元 bounding box 的右上角當錨點（Text local）
    val anchorInTextLocal = Offset(charRect.right, charRect.top + 150f)

    // 6. Text local 轉 Box local(座標)
    val anchorInBoxLocal = boxCoords.localPositionOf(
        sourceCoordinates = textCoords,
        relativeToSource = anchorInTextLocal
    )

    // 7. Box 座標系中略往上（不蓋住文字）
    val tooltipOffset = IntOffset(
        anchorInBoxLocal.x.roundToInt(),
        (anchorInBoxLocal.y - 24f).roundToInt()
    )

    onTooltipPositioned(tooltipOffset)
    return true
}

/**
 * 於指定文字中查找指定索引所對應的完整單字。
 *
 * @param text 文字內容
 * @param index 觸發查找的字元索引
 * @return 找到的單字或空字串
 */
private fun findWordAt(text: String, index: Int): String {
    if (text.isEmpty() || index < 0 || index >= text.length) return ""

    // 檢查該位置是否屬於單字內部
    if (!isWordCharacter(text[index])) return ""

    // 向左找單字起點
    var start = index
    var end = index
    while (start > 0 && isWordCharacter(text[start - 1])) {
        start--
    }
    // 向右找單字結束
    while (end < text.length - 1 && isWordCharacter(text[end + 1])) {
        end++
    }
    return text.substring(start, end + 1)
}

/**
 * 判斷字元是否屬於單字內容（字母、數字、撇號、連字號）
 *
 * @param char 欲判斷的字元
 * @return 是否為單字的一部分
 */
private fun isWordCharacter(char: Char): Boolean {
    return char.isLetterOrDigit() || char == '\'' || char == '-'
}