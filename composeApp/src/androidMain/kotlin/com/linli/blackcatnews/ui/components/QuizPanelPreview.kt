package com.linli.blackcatnews.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.linli.blackcatnews.domain.model.QuizQuestion
import com.linli.blackcatnews.ui.theme.AppTheme

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

// Sample data functions
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