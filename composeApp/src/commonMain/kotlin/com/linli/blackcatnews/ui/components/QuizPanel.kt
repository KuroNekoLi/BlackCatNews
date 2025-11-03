package com.linli.blackcatnews.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.linli.blackcatnews.domain.model.GlossaryItem
import com.linli.blackcatnews.domain.model.GrammarPoint
import com.linli.blackcatnews.domain.model.PhraseIdiom
import com.linli.blackcatnews.domain.model.QuizQuestion
import com.linli.blackcatnews.domain.model.SentencePattern
import com.linli.blackcatnews.ui.theme.AppTheme
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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
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
                    .heightIn(max = 350.dp),
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
                    // 測驗標題
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✅ 閱讀測驗",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (isSubmitted) {
                            TextButton(onClick = onReset) {
                                Text("重新測驗")
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // 題目列表（可滾動）
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        quiz.forEachIndexed { index, question ->
                            QuizQuestionItem(
                                questionNumber = index + 1,
                                question = question,
                                selectedAnswer = userAnswers[index],
                                onAnswerSelected = { answerIndex ->
                                    if (!isSubmitted) {
                                        userAnswers[index] = answerIndex
                                    }
                                },
                                isSubmitted = isSubmitted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 送出按鈕
                    if (!isSubmitted) {
                        Button(
                            onClick = onSubmit,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = userAnswers.size == quiz.size
                        ) {
                            Text("送出答案")
                        }
                    } else {
                        // 顯示分數
                        val correctCount = quiz.indices.count { index ->
                            userAnswers[index] == quiz[index].correctAnswerIndex
                        }
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Text(
                                text = "✨ 得分：$correctCount / ${quiz.size}",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
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
        question = "What is the main topic of the article?",
        options = listOf(
            "Technology trends",
            "Climate change",
            "Economic policy",
            "Social media"
        ),
        correctAnswerIndex = 0,
        correctAnswerKey = "A",
        explanation = "The article focuses on technology trends and their impact on society."
    ),
    QuizQuestion(
        id = "2",
        question = "According to the author, what should we do?",
        options = listOf(
            "Wait and see",
            "Take immediate action",
            "Ignore the problem",
            "Ask for help"
        ),
        correctAnswerIndex = 1,
        correctAnswerKey = "B",
        explanation = "The author emphasizes the need for immediate action to address current challenges."
    )
)

private fun getLongQuizQuestions(): List<QuizQuestion> = listOf(
    QuizQuestion(
        id = "1",
        question = "What is artificial intelligence primarily used for in healthcare?",
        options = listOf(
            "Diagnostic assistance and treatment planning",
            "Replacing doctors completely",
            "Entertainment purposes only",
            "Administrative tasks only"
        ),
        correctAnswerIndex = 0,
        correctAnswerKey = "A",
        explanation = "AI is primarily used to assist with diagnostics and treatment planning."
    ),
    QuizQuestion(
        id = "2",
        question = "Which technology enables machines to learn from data?",
        options = listOf(
            "Basic programming",
            "Machine learning",
            "Simple databases",
            "Static algorithms"
        ),
        correctAnswerIndex = 1,
        correctAnswerKey = "B",
        explanation = "Machine learning allows machines to learn and improve from data."
    ),
    QuizQuestion(
        id = "3",
        question = "What is the main benefit of AI in medical imaging?",
        options = listOf(
            "Faster image processing",
            "Cheaper equipment",
            "Enhanced accuracy in detection",
            "Simpler user interfaces"
        ),
        correctAnswerIndex = 2,
        correctAnswerKey = "C",
        explanation = "AI significantly enhances accuracy in detecting medical conditions from images."
    ),
    QuizQuestion(
        id = "4",
        question = "How does AI contribute to personalized medicine?",
        options = listOf(
            "By analyzing individual patient data",
            "By using the same treatment for everyone",
            "By reducing consultation time",
            "By eliminating the need for testing"
        ),
        correctAnswerIndex = 0,
        correctAnswerKey = "A",
        explanation = "AI analyzes individual patient data to create personalized treatment plans."
    )
)

private fun getSingleQuizQuestion(): List<QuizQuestion> = listOf(
    QuizQuestion(
        id = "single",
        question = "What does AI stand for in technology?",
        options = listOf(
            "Automated Intelligence",
            "Artificial Intelligence",
            "Advanced Integration",
            "Algorithmic Implementation"
        ),
        correctAnswerIndex = 1,
        correctAnswerKey = "B",
        explanation = "AI stands for Artificial Intelligence, referring to machine systems that can perform tasks typically requiring human intelligence."
    )
)