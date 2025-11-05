package com.linli.blackcatnews.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
    isExpanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    quiz: List<QuizQuestion>,
    userAnswers: MutableMap<Int, Int>,
    isSubmitted: Boolean,
    onSubmit: () -> Unit,
    onReset: () -> Unit,
    glossary: List<GlossaryItem> = emptyList(),
    grammarPoints: List<GrammarPoint> = emptyList(),
    sentencePatterns: List<SentencePattern> = emptyList(),
    phrases: List<PhraseIdiom> = emptyList(),
    modifier: Modifier = Modifier
) {
    // 狀態：學習輔助 bottom sheet 展開、tab切換
    var isLearnSheetOpen by remember { mutableStateOf(false) }
    rememberModalBottomSheetState(skipPartiallyExpanded = true)
    listOf("重點單字", "文法說明", "句型說明", "片語／習語")
    rememberCoroutineScope()
    var currentIndex by remember { mutableStateOf(0) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        val cardMaxHeight = maxHeight * 0.85f

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            // 學習輔助 FloatingActionButton
            FloatingActionButton(
                onClick = { isLearnSheetOpen = true },
                modifier = Modifier.size(56.dp).padding(bottom = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.School,
                    contentDescription = "學習輔助"
                )
            }
            // 使用獨立LearningBottomSheet Composable
            LearningBottomSheet(
                isOpen = isLearnSheetOpen,
                onDismiss = { isLearnSheetOpen = false },
                glossary = glossary,
                grammarPoints = grammarPoints,
                sentencePatterns = sentencePatterns,
                phrases = phrases
            )
            // 展開的測驗內容
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = cardMaxHeight),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // 測驗標題（漸層區塊 + 右側重置）
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            MaterialTheme.colorScheme.secondaryContainer
                                        )
                                    )
                                )
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.MenuBook,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(Modifier.size(8.dp))
                                    Text(
                                        text = "閱讀測驗",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(Modifier.size(10.dp))
                                    Surface(
                                        color = MaterialTheme.colorScheme.surface,
                                        tonalElevation = 2.dp,
                                        shape = CircleShape,
                                        border = BorderStroke(
                                            1.dp,
                                            MaterialTheme.colorScheme.outlineVariant
                                        )
                                    ) {
                                        val answeredCount = userAnswers.size
                                        Text(
                                            text = "${answeredCount}/${quiz.size}",
                                            modifier = Modifier.padding(
                                                horizontal = 10.dp,
                                                vertical = 4.dp
                                            ),
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }
                                }
                                if (isSubmitted) {
                                    TextButton(onClick = onReset) { Text("重新測驗") }
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        // 題目導覽：圓形題號列（更友善的定位與跳題）
                        if (quiz.isNotEmpty()) {
                            val correctBg = correctBg
                            val correctFg = correctFg
                            val incorrectBg = wrongBg
                            val incorrectFg = wrongFg
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                itemsIndexed(quiz) { index, _ ->
                                    val answered = userAnswers.containsKey(index)
                                    val isCorrect =
                                        answered && userAnswers[index] == quiz[index].correctAnswerIndex
                                    val bg = when {
                                        isSubmitted && isCorrect -> correctBg
                                        isSubmitted && !isCorrect && answered -> incorrectBg
                                        index == currentIndex -> MaterialTheme.colorScheme.secondaryContainer
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                    val fg = when {
                                        isSubmitted && isCorrect -> correctFg
                                        isSubmitted && !isCorrect && answered -> incorrectFg
                                        index == currentIndex -> MaterialTheme.colorScheme.onSecondaryContainer
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                    Surface(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .clickable { currentIndex = index },
                                        shape = CircleShape,
                                        color = bg,
                                        border = BorderStroke(
                                            1.dp,
                                            MaterialTheme.colorScheme.outlineVariant
                                        )
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${index + 1}",
                                                color = fg,
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            // 顏色圖例
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LegendDot(MaterialTheme.colorScheme.secondaryContainer, "目前題")
                                LegendDot(correctBg, "答對")
                                LegendDot(incorrectBg, "答錯")
                                LegendDot(MaterialTheme.colorScheme.surfaceVariant, "未作答")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // 單題顯示（不再整頁直向捲動）
                        if (quiz.isNotEmpty()) {
                            val question = quiz[currentIndex]
                            QuizQuestionItem(
                                questionNumber = currentIndex + 1,
                                question = question,
                                selectedAnswer = userAnswers[currentIndex],
                                onAnswerSelected = { answerIndex ->
                                    if (!isSubmitted) {
                                        userAnswers[currentIndex] = answerIndex
                                    }
                                },
                                isSubmitted = isSubmitted
                            )

                            // 解析區塊（提交後顯示）
                            if (isSubmitted) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Surface(
                                    shape = MaterialTheme.shapes.medium,
                                    color = MaterialTheme.colorScheme.tertiaryContainer,
                                    tonalElevation = 1.dp,
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "題目中文：",
                                            style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.onTertiaryContainer)
                                        )
                                        Text(
                                            text = question.questionChinese.orEmpty(),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                        val hasEn =
                                            question.explanationEnglish?.isNotBlank() == true
                                        val hasZh =
                                            question.explanationChinese?.isNotBlank() == true
                                        if (hasEn || hasZh) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Filled.School,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                                                )
                                                Spacer(Modifier.size(6.dp))
                                                Text(
                                                    text = "解析：",
                                                    style = MaterialTheme.typography.labelLarge.copy(
                                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                                    )
                                                )
                                            }
                                            if (hasEn) {
                                                Text(
                                                    text = question.explanationEnglish.orEmpty(),
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                                )
                                            }
                                            if (hasZh) {
                                                Text(
                                                    text = question.explanationChinese.orEmpty(),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Text("目前沒有題目", style = MaterialTheme.typography.bodyLarge)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (!isSubmitted) {
                            Button(
                                onClick = onSubmit,
                                modifier = Modifier.fillMaxWidth(),
                                enabled = userAnswers.size == quiz.size,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Filled.Send, contentDescription = null)
                                Spacer(Modifier.size(8.dp))
                                Text("送出答案")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 展開/收起按鈕
            FloatingActionButton(
                onClick = { onExpandChange(!isExpanded) },
                modifier = Modifier.size(56.dp),
            ) {
                if (isExpanded) {
                    Text(
                        text = "✕",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.MenuBook,
                        contentDescription = "開啟測驗",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            // end Column
        }
        // end BoxWithConstraints
    }
}

@Composable
private fun LegendDot(colorBg: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(12.dp),
            shape = CircleShape,
            color = colorBg,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {}
        Spacer(Modifier.size(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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