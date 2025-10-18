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