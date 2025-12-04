package com.linli.blackcatnews.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.linli.blackcatnews.domain.model.GlossaryItem
import com.linli.blackcatnews.domain.model.GrammarPoint
import com.linli.blackcatnews.domain.model.PhraseIdiom
import com.linli.blackcatnews.domain.model.QuizQuestion
import com.linli.blackcatnews.domain.model.SentencePattern
import com.linli.blackcatnews.ui.theme.AppTheme
import com.linli.blackcatnews.ui.theme.correctBg
import com.linli.blackcatnews.ui.theme.correctFg
import com.linli.blackcatnews.ui.theme.wrongBg
import com.linli.blackcatnews.ui.theme.wrongFg
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 測驗面板組件
 * 右下角可展開的測驗區域
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizPanel(
    isExpanded: Boolean, // Deprecated: Kept for signature compatibility, ignored in new layout
    onExpandChange: (Boolean) -> Unit, // Deprecated
    quiz: List<QuizQuestion>,
    userAnswers: MutableMap<Int, Int>,
    isSubmitted: Boolean,
    onSubmit: () -> Unit,
    onReset: () -> Unit,
    glossary: List<GlossaryItem> = emptyList(),
    grammarPoints: List<GrammarPoint> = emptyList(),
    sentencePatterns: List<SentencePattern> = emptyList(),
    phrases: List<PhraseIdiom> = emptyList(),
    savedWords: Set<String> = emptySet(),
    onAddWordToWordBank: (String) -> Unit = {},
    ensureAuthenticated: () -> Boolean = { true },
    playingAudioId: String? = null,
    onPlayAudio: (text: String, id: String, languageTag: String?) -> Unit = { _, _, _ -> },
    onStopAudio: () -> Unit = {},
    isTtsSupported: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Local state to track if quiz has started
    var isQuizStarted by remember { mutableStateOf(false) }
    var currentQuestionIndex by remember { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. Quiz Section
        if (quiz.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.size(8.dp))
                        Text(
                            text = "測驗時間",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (!isQuizStarted && !isSubmitted) {
                        // Start Screen
                        Text(
                            text = "準備好測試你的理解程度了嗎？共有 ${quiz.size} 題。",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Button(
                            onClick = {
                                if (ensureAuthenticated()) {
                                    isQuizStarted = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("開始測驗")
                        }
                    } else {
                        // Quiz Content
                        // Progress Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Question ${currentQuestionIndex + 1} of ${quiz.size}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (isSubmitted) {
                                TextButton(onClick = onReset) { Text("重試") }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        // Question Indicators (Circles)
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 12.dp)
                        ) {
                            itemsIndexed(quiz) { index, _ ->
                                val answered = userAnswers.containsKey(index)
                                val isCorrect =
                                    answered && userAnswers[index] == quiz[index].correctAnswerIndex

                                val backgroundColor = when {
                                    isSubmitted && isCorrect -> correctBg
                                    isSubmitted && answered -> wrongBg
                                    index == currentQuestionIndex -> MaterialTheme.colorScheme.primaryContainer
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                                val contentColor = when {
                                    isSubmitted && isCorrect -> correctFg
                                    isSubmitted && answered -> wrongFg
                                    index == currentQuestionIndex -> MaterialTheme.colorScheme.onPrimaryContainer
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }

                                Surface(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .clickable { currentQuestionIndex = index },
                                    shape = CircleShape,
                                    color = backgroundColor
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${index + 1}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = contentColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // Current Question
                        val question = quiz[currentQuestionIndex]
                        QuizQuestionItem(
                            questionNumber = currentQuestionIndex + 1,
                            question = question,
                            selectedAnswer = userAnswers[currentQuestionIndex],
                            onAnswerSelected = { answerIndex ->
                                if (!isSubmitted) {
                                    userAnswers[currentQuestionIndex] = answerIndex
                                }
                            },
                            isSubmitted = isSubmitted
                        )

                        // Navigation Buttons
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(
                                onClick = { if (currentQuestionIndex > 0) currentQuestionIndex-- },
                                enabled = currentQuestionIndex > 0
                            ) {
                                Text("上一題")
                            }

                            if (currentQuestionIndex < quiz.size - 1) {
                                Button(
                                    onClick = { currentQuestionIndex++ },
                                    enabled = userAnswers.containsKey(currentQuestionIndex)
                                ) {
                                    Text("下一題")
                                }
                            } else if (!isSubmitted) {
                                Button(
                                    onClick = onSubmit,
                                    enabled = userAnswers.size == quiz.size
                                ) {
                                    Icon(Icons.Filled.Send, contentDescription = null)
                                    Spacer(Modifier.size(8.dp))
                                    Text("送出答案")
                                }
                            }
                        }

                        // Explanation (Only shown after submission)
                        if (isSubmitted) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Surface(
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "解析",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (!question.explanationEnglish.isNullOrBlank()) {
                                        Text(
                                            text = question.explanationEnglish,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    if (!question.explanationChinese.isNullOrBlank()) {
                                        Text(
                                            text = question.explanationChinese,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Vocabulary Summary Section
        if (glossary.isNotEmpty() || grammarPoints.isNotEmpty() || phrases.isNotEmpty() || sentencePatterns.isNotEmpty()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(
                        text = "本課重點",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Glossary Items
                if (glossary.isNotEmpty()) {
                    SectionHeader(title = "單字 (${glossary.size})")
                    glossary.forEach { item ->
                        VocabularyCard(
                            item = item,
                            savedWords = savedWords,
                            onAddWordToWordBank = onAddWordToWordBank,
                            playingAudioId = playingAudioId,
                            onPlayAudio = onPlayAudio,
                            onStopAudio = onStopAudio,
                            isTtsSupported = isTtsSupported
                        )
                    }
                }

                // Grammar Points
                if (grammarPoints.isNotEmpty()) {
                    SectionHeader(title = "文法 (${grammarPoints.size})")
                    grammarPoints.forEach { gp ->
                        GrammarCard(gp)
                    }
                }

                // Sentence Patterns
                if (sentencePatterns.isNotEmpty()) {
                    SectionHeader(title = "句型 (${sentencePatterns.size})")
                    sentencePatterns.forEach { sp ->
                        SentencePatternCard(sp)
                    }
                }

                // Phrases
                if (phrases.isNotEmpty()) {
                    SectionHeader(title = "片語 (${phrases.size})")
                    phrases.forEach { phrase ->
                        PhraseCard(phrase)
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun VocabularyCard(
    item: GlossaryItem,
    savedWords: Set<String>,
    onAddWordToWordBank: (String) -> Unit,
    playingAudioId: String?,
    onPlayAudio: (text: String, id: String, languageTag: String?) -> Unit,
    onStopAudio: () -> Unit,
    isTtsSupported: Boolean
) {
    val normalizedWord = item.word.trim()
    val isSaved = savedWords.any { it.equals(normalizedWord, ignoreCase = true) }
    val wordPlayId = "word:${item.word}"
    val isWordPlaying = playingAudioId == wordPlayId

    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = item.word,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (!item.partOfSpeech.isNullOrBlank()) {
                        Text(
                            text = item.partOfSpeech,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = {
                            if (isWordPlaying) onStopAudio()
                            else if (isTtsSupported) onPlayAudio(item.word, wordPlayId, "en")
                        },
                        modifier = Modifier.size(24.dp),
                        enabled = isTtsSupported
                    ) {
                        Icon(
                            imageVector = if (isWordPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = "Play",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isSaved) {
                        IconButton(onClick = { onAddWordToWordBank(normalizedWord) }) {
                            Icon(
                                imageVector = Icons.Filled.BookmarkAdd,
                                contentDescription = "Save",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Icon(
                        imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!item.definitionChinese.isNullOrBlank()) {
                Text(
                    text = item.definitionChinese,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))
                    if (!item.definitionEnglish.isNullOrBlank()) {
                        Text(
                            text = "English Definition:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = item.definitionEnglish,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (!item.exampleEnglish.isNullOrBlank()) {
                        Text(
                            text = "Example:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = item.exampleEnglish,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                        if (!item.exampleChinese.isNullOrBlank()) {
                            Text(
                                text = item.exampleChinese,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhraseCard(phrase: PhraseIdiom) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.7f
            )
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = phrase.phraseEnglish,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (!phrase.explanationChinese.isNullOrBlank()) {
                        Text(
                            text = phrase.explanationChinese,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))
                    if (!phrase.explanationEnglish.isNullOrBlank()) {
                        Text(
                            text = phrase.explanationEnglish,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    if (!phrase.exampleEnglish.isNullOrBlank()) {
                        Text(
                            text = "Example:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = phrase.exampleEnglish,
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                        if (!phrase.exampleChinese.isNullOrBlank()) {
                            Text(
                                text = phrase.exampleChinese,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GrammarCard(gp: GrammarPoint) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(
                alpha = 0.2f
            )
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "文法: ${gp.rule}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    if (!gp.explanationChinese.isNullOrBlank()) {
                        Text(
                            text = gp.explanationChinese,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))
                    if (!gp.explanationEnglish.isNullOrBlank()) {
                        Text(
                            text = gp.explanationEnglish,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    if (!gp.exampleEnglish.isNullOrBlank()) {
                        Text(
                            text = "Example:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = gp.exampleEnglish,
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                        if (!gp.exampleChinese.isNullOrBlank()) {
                            Text(
                                text = gp.exampleChinese,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SentencePatternCard(sp: SentencePattern) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                alpha = 0.2f
            )
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "句型: ${sp.patternEnglish}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    if (!sp.explanationChinese.isNullOrBlank()) {
                        Text(
                            text = sp.explanationChinese,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))
                    if (!sp.explanationEnglish.isNullOrBlank()) {
                        Text(
                            text = sp.explanationEnglish,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    if (!sp.exampleEnglish.isNullOrBlank()) {
                        Text(
                            text = "Example:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = sp.exampleEnglish,
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                        if (!sp.exampleChinese.isNullOrBlank()) {
                            Text(
                                text = sp.exampleChinese,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Quiz Panel - Collapsed State")
@Composable
private fun QuizPanelCollapsedPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            QuizPanel(
                isExpanded = false,
                onExpandChange = {},
                quiz = getSampleQuizQuestions(),
                userAnswers = mutableMapOf(),
                isSubmitted = false,
                onSubmit = {},
                onReset = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Quiz Panel - Expanded with Answers")
@Composable
private fun QuizPanelExpandedPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            QuizPanel(
                isExpanded = true,
                onExpandChange = {},
                quiz = getSampleQuizQuestions(),
                userAnswers = mutableMapOf(0 to 0, 1 to 1),
                isSubmitted = false,
                onSubmit = {},
                onReset = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Quiz Panel - Perfect Score")
@Composable
private fun QuizPanelPerfectScorePreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            QuizPanel(
                isExpanded = true,
                onExpandChange = {},
                quiz = getSampleQuizQuestions(),
                userAnswers = mutableMapOf(0 to 0, 1 to 1), // All correct answers
                isSubmitted = true,
                onSubmit = {},
                onReset = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Quiz Panel - Mixed Results")
@Composable
private fun QuizPanelMixedResultsPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            QuizPanel(
                isExpanded = true,
                onExpandChange = {},
                quiz = getSampleQuizQuestions(),
                userAnswers = mutableMapOf(0 to 0, 1 to 2), // First correct, second incorrect
                isSubmitted = true,
                onSubmit = {},
                onReset = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Quiz Panel - All Wrong")
@Composable
private fun QuizPanelAllWrongPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            QuizPanel(
                isExpanded = true,
                onExpandChange = {},
                quiz = getSampleQuizQuestions(),
                userAnswers = mutableMapOf(0 to 2, 1 to 3), // All wrong answers
                isSubmitted = true,
                onSubmit = {},
                onReset = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Quiz Panel - Interactive State")
@Composable
private fun QuizPanelInteractivePreview() {
    AppTheme {
        val isExpanded = remember { mutableStateOf(false) }
        val userAnswers = remember { mutableStateMapOf<Int, Int>() }
        val isSubmitted = remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            QuizPanel(
                isExpanded = isExpanded.value,
                onExpandChange = { isExpanded.value = it },
                quiz = getSampleQuizQuestions(),
                userAnswers = userAnswers,
                isSubmitted = isSubmitted.value,
                onSubmit = { isSubmitted.value = true },
                onReset = {
                    userAnswers.clear()
                    isSubmitted.value = false
                }
            )
        }
    }
}

@Preview(showBackground = true, name = "Quiz Panel - Long Quiz")
@Composable
private fun QuizPanelLongQuizPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            QuizPanel(
                isExpanded = true,
                onExpandChange = {},
                quiz = getLongQuizQuestions(),
                userAnswers = mutableMapOf(0 to 0, 1 to 1, 2 to 2, 3 to 0),
                isSubmitted = false,
                onSubmit = {},
                onReset = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Quiz Panel - Single Question")
@Composable
private fun QuizPanelSingleQuestionPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            QuizPanel(
                isExpanded = true,
                onExpandChange = {},
                quiz = getSingleQuizQuestion(),
                userAnswers = mutableMapOf(0 to 1),
                isSubmitted = true,
                onSubmit = {},
                onReset = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Quiz Panel - No Quiz Available")
@Composable
private fun QuizPanelEmptyPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            QuizPanel(
                isExpanded = true,
                onExpandChange = {},
                quiz = emptyList(),
                userAnswers = mutableMapOf(),
                isSubmitted = false,
                onSubmit = {},
                onReset = {}
            )
        }
    }
}

private fun getSampleQuizQuestions(): List<QuizQuestion> = listOf(
    QuizQuestion(
        id = "1",
        questionEnglish = "What is the main topic of the article?",
        questionChinese = "本文的主題是什麼？",
        options = listOf(
            "Technology trends",
            "Climate change",
            "Economic policy",
            "Social media"
        ),
        correctAnswerIndex = 0,
        correctAnswerKey = "A",
        explanationEnglish = "The article focuses on technology trends and their impact on society.",
        explanationChinese = "本文著重於科技趨勢及其社會影響。"
    ),
    QuizQuestion(
        id = "2",
        questionEnglish = "According to the author, what should we do?",
        questionChinese = "作者認為我們應該怎麼做？",
        options = listOf(
            "Wait and see",
            "Take immediate action",
            "Ignore the problem",
            "Ask for help"
        ),
        correctAnswerIndex = 1,
        correctAnswerKey = "B",
        explanationEnglish = "The author emphasizes the need for immediate action to address current challenges.",
        explanationChinese = "作者強調應立即採取行動應對當前挑戰。"
    )
)

private fun getLongQuizQuestions(): List<QuizQuestion> = listOf(
    QuizQuestion(
        id = "1",
        questionEnglish = "What is artificial intelligence primarily used for in healthcare?",
        questionChinese = "人工智慧在醫療領域主要用於什麼？",
        options = listOf(
            "Diagnostic assistance and treatment planning",
            "Replacing doctors completely",
            "Entertainment purposes only",
            "Administrative tasks only"
        ),
        correctAnswerIndex = 0,
        correctAnswerKey = "A",
        explanationEnglish = "AI is primarily used to assist with diagnostics and treatment planning.",
        explanationChinese = "AI 主要用於協助診斷及治療規劃。"
    ),
    QuizQuestion(
        id = "2",
        questionEnglish = "Which technology enables machines to learn from data?",
        questionChinese = "哪項技術可讓機器從數據學習？",
        options = listOf(
            "Basic programming",
            "Machine learning",
            "Simple databases",
            "Static algorithms"
        ),
        correctAnswerIndex = 1,
        correctAnswerKey = "B",
        explanationEnglish = "Machine learning allows machines to learn and improve from data.",
        explanationChinese = "機器學習允許機器從數據中學習與進步。"
    ),
    QuizQuestion(
        id = "3",
        questionEnglish = "What is the main benefit of AI in medical imaging?",
        questionChinese = "AI 在醫學影像的最大好處是什麼？",
        options = listOf(
            "Faster image processing",
            "Cheaper equipment",
            "Enhanced accuracy in detection",
            "Simpler user interfaces"
        ),
        correctAnswerIndex = 2,
        correctAnswerKey = "C",
        explanationEnglish = "AI significantly enhances accuracy in detecting medical conditions from images.",
        explanationChinese = "AI 大幅提升醫學影像診斷的準確性。"
    ),
    QuizQuestion(
        id = "4",
        questionEnglish = "How does AI contribute to personalized medicine?",
        questionChinese = "AI 如何促進個人化醫療？",
        options = listOf(
            "By analyzing individual patient data",
            "By using the same treatment for everyone",
            "By reducing consultation time",
            "By eliminating the need for testing"
        ),
        correctAnswerIndex = 0,
        correctAnswerKey = "A",
        explanationEnglish = "AI analyzes individual patient data to create personalized treatment plans.",
        explanationChinese = "AI 透過分析個人資料設計專屬治療方案。"
    )
)

private fun getSingleQuizQuestion(): List<QuizQuestion> = listOf(
    QuizQuestion(
        id = "single",
        questionEnglish = "What does AI stand for in technology?",
        questionChinese = "AI 在科技領域代表什麼？",
        options = listOf(
            "Automated Intelligence",
            "Artificial Intelligence",
            "Advanced Integration",
            "Algorithmic Implementation"
        ),
        correctAnswerIndex = 1,
        correctAnswerKey = "B",
        explanationEnglish = "AI stands for Artificial Intelligence, referring to machine systems that can perform tasks typically requiring human intelligence.",
        explanationChinese = "AI 指的是人工智慧，是指能執行人類智能任務的機器系統。"
    )
)
