package com.linli.blackcatnews.ui.components.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.linli.blackcatnews.domain.model.QuizQuestion
import com.linli.blackcatnews.ui.components.QuizPanel
import com.linli.blackcatnews.ui.theme.AppTheme

@Preview(showBackground = true, name = "Quiz Panel - Collapsed")
@Composable
private fun QuizPanelCollapsedPreview() {
    AppTheme {
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

@Preview(showBackground = true, name = "Quiz Panel - Expanded")
@Composable
private fun QuizPanelExpandedPreview() {
    AppTheme {
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

@Preview(showBackground = true, name = "Quiz Panel - Submitted")
@Composable
private fun QuizPanelSubmittedPreview() {
    AppTheme {
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
        explanation = "The article focuses on technology trends."
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
        explanation = "The author emphasizes the need for immediate action."
    )
)