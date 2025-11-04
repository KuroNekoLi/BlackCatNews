package com.linli.blackcatnews.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.linli.blackcatnews.domain.model.BilingualParagraph
import com.linli.blackcatnews.domain.model.BilingualParagraphType
import com.linli.blackcatnews.domain.model.ReadingMode
import kotlin.math.roundToInt

/**
 * Data class representing a dictionary entry for a word
 */
data class WordInfo(
    val word: String,
    val definition: String,
    val example: String = ""
)

/**
 * A composable that displays bilingual text with word tooltip functionality.
 * - Short press: Shows a dictionary tooltip for the tapped word
 * - Long press: Maintains system text selection functionality
 *
 * @param paragraph The bilingual paragraph to display
 * @param readingMode The current reading mode (English only, Chinese only, or both)
 * @param onLookupWord Optional callback to lookup word definitions
 * @param modifier Modifier for the component
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ArticleWithWordTooltip(
    paragraph: BilingualParagraph,
    readingMode: ReadingMode,
    onLookupWord: (String) -> WordInfo = { WordInfo(it, "Definition not found") },
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
    var wordInfo by remember { mutableStateOf<WordInfo?>(null) }

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
                                                wordInfo = onLookupWord(word)
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
                                                        wordInfo = onLookupWord(word)
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
                                                            wordInfo = onLookupWord(word)
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

        // Word tooltip popup
        if (showTooltip && wordInfo != null) {
            WordTooltip(
                wordInfo = wordInfo!!,
                offset = tooltipOffset,
                onDismiss = { showTooltip = false },
                onSaveWord = {
                    // Handle saving word to vocabulary
                    // Add implementation here if needed
                    showTooltip = false
                }
            )
        }
    }
}


/**
 * Handle a tap inside the English text container using Box-local coordinates.
 *
 * @param offsetInBox The tap offset relative to the Box that received the gesture.
 * @param text The full text content.
 * @param textLayoutResult Layout result for the Text.
 * @param boxCoordinates Layout coordinates for the Box (Popup parent, used for local positioning).
 * @param textCoordinates Layout coordinates for the Text (used to convert between Box and text-local space).
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

    // 3. 找出這個 index 所在的整個單字
    val word = findWordAt(text, position)

    // 確認這是一個「像單字」的 token，可以依需求調整規則
    if (word.isEmpty() ||
        !word.any { it.isLetter() } ||
        !word.any { it.code < 128 } // 粗略判斷為英文單字（ASCII）
    ) {
        return false
    }

    // 通知外層選到這個字
    onWordSelected(word)

    // 4. 取得該字元在 Text-local 座標系中的 bounding box
    val charRect = layout.getBoundingBox(position)

    // 5. 取字元 bounding box 的右上角當錨點（Text local）
    val anchorInTextLocal = Offset(charRect.right, charRect.top + 150f)

    // 6. 將錨點從 Text local 轉成 Box local（Popup 的 offset 以 Box 為座標系）
    val anchorInBoxLocal = boxCoords.localPositionOf(
        sourceCoordinates = textCoords,
        relativeToSource = anchorInTextLocal
    )

    // 7. 在 Box 座標系中稍微往上移一點，避免蓋住文字本身
    val tooltipOffset = IntOffset(
        anchorInBoxLocal.x.roundToInt(),
        (anchorInBoxLocal.y - 24f).roundToInt()
    )

    onTooltipPositioned(tooltipOffset)
    return true
}

/**
 * Tooltip component that displays word information
 */
@Composable
private fun WordTooltip(
    wordInfo: WordInfo,
    offset: IntOffset,
    onDismiss: () -> Unit,
    onSaveWord: () -> Unit
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
            ElevatedCard(
                modifier = Modifier.width(280.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Word and actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = wordInfo.word,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        // Add to vocabulary button
                        IconButton(onClick = onSaveWord) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add to vocabulary"
                            )
                        }

                        // Close button
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close tooltip"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Word definition
                    Text(
                        text = wordInfo.definition,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    // Example sentence if available
                    if (wordInfo.example.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Example: ${wordInfo.example}",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

/**
 * Finds the word at the specified index in the given text
 */
private fun findWordAt(text: String, index: Int): String {
    // Handle edge cases
    if (text.isEmpty() || index < 0 || index >= text.length) return ""

    // Check if the character at index is part of a word
    if (!isWordCharacter(text[index])) return ""

    // Find word boundaries
    var start = index
    var end = index

    // Find start of word
    while (start > 0 && isWordCharacter(text[start - 1])) {
        start--
    }

    // Find end of word
    while (end < text.length - 1 && isWordCharacter(text[end + 1])) {
        end++
    }

    return text.substring(start, end + 1)
}

/**
 * Determines if a character is part of a word
 * Words can contain letters, digits, apostrophes and hyphens
 */
private fun isWordCharacter(char: Char): Boolean {
    return char.isLetterOrDigit() || char == '\'' || char == '-'
}