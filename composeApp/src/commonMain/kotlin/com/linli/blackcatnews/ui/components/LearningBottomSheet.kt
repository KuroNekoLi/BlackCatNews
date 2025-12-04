package com.linli.blackcatnews.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.linli.blackcatnews.domain.model.GlossaryItem
import com.linli.blackcatnews.domain.model.GrammarPoint
import com.linli.blackcatnews.domain.model.PhraseIdiom
import com.linli.blackcatnews.domain.model.SentencePattern
import com.linli.blackcatnews.tts.DEFAULT_LANGUAGE_TAG
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 學習輔助 BottomSheet，提供重點單字、文法、句型、片語等資料 Tab 切換顯示
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningBottomSheet(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    glossary: List<GlossaryItem>,
    grammarPoints: List<GrammarPoint>,
    sentencePatterns: List<SentencePattern>,
    phrases: List<PhraseIdiom>,
    savedWords: Set<String> = emptySet(),
    onAddWordToWordBank: (String) -> Unit = {},
    playingAudioId: String? = null,
    onPlayAudio: (text: String, id: String, languageTag: String?) -> Unit = { _, _, _ -> },
    onStopAudio: () -> Unit = {},
    isTtsSupported: Boolean = false,
) {
    val isPreview = LocalInspectionMode.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val tabTitles = listOf("重點單字", "文法說明", "句型說明", "片語／習語")
    LaunchedEffect(isOpen) {
        if (isOpen) {
            // 先以「部分展開」呈現，使用者可再往上拖至全展開
            sheetState.partialExpand()
        }
    }
    if (isOpen) {
        if (isPreview) {
            // 在 Preview 中，ModalBottomSheet 屬於 Dialog 類型，預覽不支援顯示。
            // 這裡用 Surface 模擬一個「半展開」的 Bottom Sheet，方便設計時預覽 UI。
            Box(
                Modifier.fillMaxWidth().fillMaxHeight(0.75f),
                contentAlignment = Alignment.BottomCenter
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    tonalElevation = 3.dp
                ) {
                    SheetBody(
                        selectedTabIndexState = remember { mutableStateOf(0) },
                        tabTitles = tabTitles,
                        glossary = glossary,
                        grammarPoints = grammarPoints,
                        sentencePatterns = sentencePatterns,
                        phrases = phrases,
                        savedWords = savedWords,
                        onAddWordToWordBank = onAddWordToWordBank,
                        playingAudioId = playingAudioId,
                        onPlayAudio = onPlayAudio,
                        onStopAudio = onStopAudio,
                        isTtsSupported = isTtsSupported,
                        onDismiss = onDismiss
                    )
                }
            }
        } else {
            ModalBottomSheet(
                onDismissRequest = onDismiss,
                sheetState = sheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() },
            ) {
                SheetBody(
                    selectedTabIndexState = remember { mutableStateOf(0) },
                    tabTitles = tabTitles,
                    glossary = glossary,
                    grammarPoints = grammarPoints,
                    sentencePatterns = sentencePatterns,
                    phrases = phrases,
                    savedWords = savedWords,
                    onAddWordToWordBank = onAddWordToWordBank,
                    playingAudioId = playingAudioId,
                    onPlayAudio = onPlayAudio,
                    onStopAudio = onStopAudio,
                    isTtsSupported = isTtsSupported,
                    onDismiss = onDismiss
                )
            }
        }
    }
}

@Composable
private fun SheetBody(
    selectedTabIndexState: androidx.compose.runtime.MutableState<Int>,
    tabTitles: List<String>,
    glossary: List<GlossaryItem>,
    grammarPoints: List<GrammarPoint>,
    sentencePatterns: List<SentencePattern>,
    phrases: List<PhraseIdiom>,
    savedWords: Set<String>,
    onAddWordToWordBank: (String) -> Unit,
    playingAudioId: String?,
    onPlayAudio: (text: String, id: String, languageTag: String?) -> Unit,
    onStopAudio: () -> Unit,
    isTtsSupported: Boolean,
    onDismiss: () -> Unit,
) {
    var selectedTabIndex by selectedTabIndexState
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        // Sticky TabRow header
        // (Handled in LazyColumn stickyHeader below)
        when (selectedTabIndex) {
            0 -> {
                if (glossary.isEmpty()) {
                    Text("無資料", style = MaterialTheme.typography.bodyLarge)
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        stickyHeader {
                            Surface(tonalElevation = 3.dp) {
                                TabRow(selectedTabIndex = selectedTabIndex) {
                                    tabTitles.forEachIndexed { idx, title ->
                                        Tab(
                                            selected = selectedTabIndex == idx,
                                            onClick = { selectedTabIndex = idx },
                                            text = {
                                                Text(
                                                    title,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        items(glossary) { item ->
                            val normalizedWord = item.word.trim()
                            val isSaved =
                                savedWords.any { it.equals(normalizedWord, ignoreCase = true) }
                            val wordPlayId = "word:${item.word}"
                            val examplePlayId = "example:${item.word}"
                            val isWordPlaying = playingAudioId == wordPlayId
                            val isExamplePlaying = playingAudioId == examplePlayId
                            val exampleToSpeak = when {
                                !item.exampleEnglish.isNullOrBlank() -> item.exampleEnglish
                                !item.exampleChinese.isNullOrBlank() -> item.exampleChinese
                                else -> null
                            }
                            val exampleLanguageTag = when {
                                !item.exampleEnglish.isNullOrBlank() -> DEFAULT_LANGUAGE_TAG
                                !item.exampleChinese.isNullOrBlank() -> "zh-TW"
                                else -> DEFAULT_LANGUAGE_TAG
                            }
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(Modifier.fillMaxWidth().padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            // 單字標題
                                            Text(
                                                text = buildString {
                                                    append(item.word)
                                                    if (!item.partOfSpeech.isNullOrBlank()) append(" (${item.partOfSpeech})")
                                                },
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                            IconButton(
                                                onClick = {
                                                    if (isWordPlaying) {
                                                        onStopAudio()
                                                    } else if (isTtsSupported) {
                                                        onPlayAudio(
                                                            item.word,
                                                            wordPlayId,
                                                            DEFAULT_LANGUAGE_TAG
                                                        )
                                                    }
                                                },
                                                enabled = isTtsSupported
                                            ) {
                                                Icon(
                                                    imageVector = if (isWordPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                                    contentDescription = "播放單字",
                                                    tint = if (isWordPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (!isSaved) {
                                                IconButton(
                                                    onClick = { onAddWordToWordBank(normalizedWord) }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.BookmarkAdd,
                                                        contentDescription = "加入單字庫",
                                                        tint = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    // 發音
                                    if (!item.pronunciation.isNullOrBlank()) {
                                        Text(
                                            text = item.pronunciation!!,
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }
                                    // 解釋（中英）
                                    if (!item.definitionEnglish.isNullOrBlank() || !item.definitionChinese.isNullOrBlank()) {
                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                                                    append(
                                                        "解釋："
                                                    )
                                                }
                                            },
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                        if (!item.definitionEnglish.isNullOrBlank()) {
                                            Text(
                                                text = item.definitionEnglish,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                        if (!item.definitionChinese.isNullOrBlank()) {
                                            Text(
                                                text = item.definitionChinese,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                    // 例句（中英）
                                    if (!item.exampleEnglish.isNullOrBlank() || !item.exampleChinese.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                                                    append(
                                                        "例句："
                                                    )
                                                }
                                            },
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                        exampleToSpeak?.let { speakable ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = speakable,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                IconButton(
                                                    onClick = {
                                                        if (isExamplePlaying) {
                                                            onStopAudio()
                                                        } else if (isTtsSupported) {
                                                            onPlayAudio(
                                                                speakable,
                                                                examplePlayId,
                                                                exampleLanguageTag
                                                            )
                                                        }
                                                    },
                                                    enabled = isTtsSupported
                                                ) {
                                                    Icon(
                                                        imageVector = if (isExamplePlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                                        contentDescription = "播放例句",
                                                        tint = if (isExamplePlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                        if (!item.exampleChinese.isNullOrBlank() && item.exampleChinese != exampleToSpeak) {
                                            Text(
                                                text = item.exampleChinese,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }

            1 -> {
                if (grammarPoints.isEmpty()) {
                    Text("無資料")
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        stickyHeader {
                            Surface(tonalElevation = 3.dp) {
                                TabRow(selectedTabIndex = selectedTabIndex) {
                                    tabTitles.forEachIndexed { idx, title ->
                                        Tab(
                                            selected = selectedTabIndex == idx,
                                            onClick = { selectedTabIndex = idx },
                                            text = {
                                                Text(
                                                    title,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        items(grammarPoints) { gp ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Box(Modifier.fillMaxWidth().padding(12.dp)) {
                                    Column(Modifier.align(Alignment.CenterStart)) {
                                        Text(
                                            text = gp.rule,
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                        if (gp.explanationEnglish.isNotBlank() || gp.explanationChinese.isNotBlank()) {
                                            Text(
                                                text = buildAnnotatedString {
                                                    withStyle(
                                                        SpanStyle(
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                    ) { append("解釋：") }
                                                },
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            )
                                            if (gp.explanationEnglish.isNotBlank())
                                                Text(
                                                    text = gp.explanationEnglish,
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                            if (gp.explanationChinese.isNotBlank())
                                                Text(
                                                    text = gp.explanationChinese,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                        }
                                        if (gp.exampleEnglish.isNotBlank() || gp.exampleChinese.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = buildAnnotatedString {
                                                    withStyle(
                                                        SpanStyle(
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                    ) { append("例句：") }
                                                },
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            )
                                            if (gp.exampleEnglish.isNotBlank())
                                                Text(
                                                    text = gp.exampleEnglish,
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                            if (gp.exampleChinese.isNotBlank())
                                                Text(
                                                    text = gp.exampleChinese,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }

            2 -> {
                if (sentencePatterns.isEmpty()) {
                    Text("無資料")
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        stickyHeader {
                            Surface(tonalElevation = 3.dp) {
                                TabRow(selectedTabIndex = selectedTabIndex) {
                                    tabTitles.forEachIndexed { idx, title ->
                                        Tab(
                                            selected = selectedTabIndex == idx,
                                            onClick = { selectedTabIndex = idx },
                                            text = {
                                                Text(
                                                    title,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        items(sentencePatterns) { sp ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Box(Modifier.fillMaxWidth().padding(12.dp)) {
                                    Column(Modifier.align(Alignment.CenterStart)) {
                                        Text(
                                            text = sp.patternEnglish,
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                        if (sp.explanationEnglish.isNotBlank() || sp.explanationChinese.isNotBlank()) {
                                            Text(
                                                text = buildAnnotatedString {
                                                    withStyle(
                                                        SpanStyle(
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                    ) { append("解釋：") }
                                                },
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            )
                                            if (sp.explanationEnglish.isNotBlank())
                                                Text(
                                                    text = sp.explanationEnglish,
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                            if (sp.explanationChinese.isNotBlank())
                                                Text(
                                                    text = sp.explanationChinese,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                        }
                                        if (sp.exampleEnglish.isNotBlank() || sp.exampleChinese.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = buildAnnotatedString {
                                                    withStyle(
                                                        SpanStyle(
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                    ) { append("例句：") }
                                                },
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            )
                                            if (sp.exampleEnglish.isNotBlank())
                                                Text(
                                                    text = sp.exampleEnglish,
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                            if (sp.exampleChinese.isNotBlank())
                                                Text(
                                                    text = sp.exampleChinese,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }

            3 -> {
                if (phrases.isEmpty()) {
                    Text("無資料")
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        stickyHeader {
                            Surface(tonalElevation = 3.dp) {
                                TabRow(selectedTabIndex = selectedTabIndex) {
                                    tabTitles.forEachIndexed { idx, title ->
                                        Tab(
                                            selected = selectedTabIndex == idx,
                                            onClick = { selectedTabIndex = idx },
                                            text = {
                                                Text(
                                                    title,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        items(phrases) { p ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Box(Modifier.fillMaxWidth().padding(12.dp)) {
                                    Column(Modifier.align(Alignment.CenterStart)) {
                                        Text(
                                            text = p.phraseEnglish,
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                        if (p.explanationEnglish.isNotBlank() || p.explanationChinese.isNotBlank()) {
                                            Text(
                                                text = buildAnnotatedString {
                                                    withStyle(
                                                        SpanStyle(
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                    ) { append("解釋：") }
                                                },
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            )
                                            if (p.explanationEnglish.isNotBlank())
                                                Text(
                                                    text = p.explanationEnglish,
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                            if (p.explanationChinese.isNotBlank())
                                                Text(
                                                    text = p.explanationChinese,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                        }
                                        if (p.exampleEnglish.isNotBlank() || p.exampleChinese.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = buildAnnotatedString {
                                                    withStyle(
                                                        SpanStyle(
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                    ) { append("例句：") }
                                                },
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            )
                                            if (p.exampleEnglish.isNotBlank())
                                                Text(
                                                    text = p.exampleEnglish,
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                            if (p.exampleChinese.isNotBlank())
                                                Text(
                                                    text = p.exampleChinese,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Learning BottomSheet Preview")
@Composable
private fun LearningBottomSheetPreview() {
    val glossary = listOf(
        GlossaryItem(
            word = "hysterectomy",
            partOfSpeech = "noun",
            translation = "子宮切除術",
            pronunciation = null,
            definitionEnglish = "A surgical operation to remove all or part of the uterus.",
            definitionChinese = "一種移除全部或部分子宮的外科手術。",
            exampleEnglish = "The patient underwent a hysterectomy due to severe health issues.",
            exampleChinese = "患者因嚴重健康問題接受了子宮切除術。",
            audioUrl = null
        )
    )
    val grammar = listOf(
        GrammarPoint(
            rule = "Passive Voice",
            explanationEnglish = "The passive voice is used when the focus is on the action or when the subject is unknown or irrelevant.",
            explanationChinese = "被動語態用於當重點在於動作或當主語未知或不相關時。",
            exampleEnglish = "The report was published by the committee.",
            exampleChinese = "報告由委員會發布。"
        )
    )
    val patterns = listOf(
        SentencePattern(
            patternEnglish = "It is (adjective) that...",
            explanationEnglish = "This pattern is used to express opinions or judgments.",
            explanationChinese = "此句型用於表達意見或判斷。",
            exampleEnglish = "It is surprising that the issue was not addressed earlier.",
            exampleChinese = "令人驚訝的是，這個問題沒有早些解決。"
        )
    )
    val phrases = listOf(
        PhraseIdiom(
            phraseEnglish = "under caution",
            explanationEnglish = "A formal warning given by police to someone suspected of a crime.",
            explanationChinese = "警方對涉嫌犯罪的人給予的正式警告。",
            exampleEnglish = "The suspect was interviewed under caution by the police.",
            exampleChinese = "嫌疑人被警方在警告下進行了面試。"
        )
    )
    MaterialTheme {
        LearningBottomSheet(
            isOpen = true,
            onDismiss = {},
            glossary = glossary,
            grammarPoints = grammar,
            sentencePatterns = patterns,
            phrases = phrases
        )
    }
}
