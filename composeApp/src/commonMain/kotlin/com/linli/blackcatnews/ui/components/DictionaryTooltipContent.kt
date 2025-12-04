package com.linli.blackcatnews.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.linli.dictionary.domain.model.Word
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 字典提示工具列的內容。
 *
 * @param word 要顯示的單字資訊
 * @param onDismiss 關閉提示框的回調
 * @param onSaveWord 儲存單字的回調
 * @param isSaved 該單字是否已存在於單字庫中，若為 true 則不再顯示加入按鈕
 * @param onPronounceClick 點擊發音按鈕的回調
 * @param onPlayExample 點擊例句朗讀按鈕的回調
 */
@Composable
fun DictionaryTooltipContent(
    word: Word,
    onDismiss: () -> Unit,
    onSaveWord: () -> Unit,
    isSaved: Boolean = false,
    onPronounceClick: (String) -> Unit = {},
    onPlayExample: (String) -> Unit = {}
) {
    ElevatedCard(
        modifier = Modifier.width(320.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 單字及操作按鈕
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = word.word,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                // 新增至生字本（已存在則隱藏）
                if (!isSaved) {
                    IconButton(onClick = onSaveWord) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "新增到生字本"
                        )
                    }
                }

                // 關閉提示框
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "關閉提示框"
                    )
                }
            }

            // 發音部分
            val pronunciations = word.pronunciations
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
            ) {
                // 英式發音
                if (pronunciations.uk.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "UK: [${pronunciations.uk}]",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "英式發音",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .size(18.dp)
                                .clickable { onPronounceClick("uk") }
                        )
                    }
                }

                // 美式發音
                if (pronunciations.us.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "US: [${pronunciations.us}]",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "美式發音",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .size(18.dp)
                                .clickable { onPronounceClick("us") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 顯示多個詞性及定義（內容可能較長，因此限制高度並可捲動）
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            val entries = word.entries
            Column(
                modifier = Modifier
                    .heightIn(max = 260.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                for ((index, entry) in entries.withIndex()) {
                    if (index > 0) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }

                    // 詞性
                    val partOfSpeech = entry.partOfSpeech
                    if (partOfSpeech.isNotEmpty()) {
                        Text(
                            text = partOfSpeech,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // 定義列表
                    val definitions = entry.definitions
                    for ((defIndex, definition) in definitions.withIndex()) {
                        if (defIndex > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // 英文定義
                        val enDef = definition.enDefinition
                        if (enDef.isNotEmpty()) {
                            Text(
                                text = enDef,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        // 中文定義
                        val zhDef = definition.zhDefinition
                        if (zhDef.isNotEmpty()) {
                            Text(
                                text = zhDef,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // 顯示例句（如有）
                        val examples = definition.examples
                        if (!examples.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            for (example in examples) {
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 8.dp, bottom = 4.dp)
                                ) {
                                    Text(
                                        text = "• $example",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontStyle = FontStyle.Italic,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "朗讀例句",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .size(18.dp)
                                            .clickable { onPlayExample(example) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewDictionaryTooltipContent() {
    val word = Word(
        word = "adjust",
        pronunciations = Word.Pronunciations(
            uk = "əˈdʒʌst",
            us = "əˈdʒʌst"
        ),
        entries = listOf(
            Word.Entry(
                partOfSpeech = "verb",
                definitions = listOf(
                    Word.Definition(
                        enDefinition = "to change something slightly",
                        zhDefinition = "調整，調節",
                        examples = listOf("You can adjust the height of the chair.")
                    )
                )
            )
        )
    )

    DictionaryTooltipContent(
        word = word,
        onDismiss = {},
        onSaveWord = {},
        onPlayExample = {}
    )
}


@Preview
@Composable
fun PreviewDictionaryTooltipContentWithMultipleEntries() {
    // Create a preview Word with multiple entries
    val word = Word(
        word = "adjust",
        pronunciations = Word.Pronunciations(
            uk = "əˈdʒʌst",
            us = "əˈdʒʌst"
        ),
        entries = listOf(
            Word.Entry(
                partOfSpeech = "verb",
                definitions = listOf(
                    Word.Definition(
                        enDefinition = "to change something slightly",
                        zhDefinition = "調整，調節",
                        examples = listOf("You can adjust the height of the chair.")
                    )
                )
            ),
            Word.Entry(
                partOfSpeech = "noun",
                definitions = listOf(
                    Word.Definition(
                        enDefinition = "a small alteration",
                        zhDefinition = "小的調整",
                        examples = listOf("We need to make some adjustments to the plan.")
                    )
                )
            )
        )
    )

    DictionaryTooltipContent(
        word = word,
        onDismiss = {},
        onSaveWord = {},
        onPlayExample = {}
    )
}