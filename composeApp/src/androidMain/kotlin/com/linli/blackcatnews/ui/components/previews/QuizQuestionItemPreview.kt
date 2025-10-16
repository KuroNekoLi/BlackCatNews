package com.linli.blackcatnews.ui.components.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.linli.blackcatnews.domain.model.QuizQuestion
import com.linli.blackcatnews.ui.components.QuizQuestionItem
import com.linli.blackcatnews.ui.theme.AppTheme

@Preview(showBackground = true, name = "Quiz Question - Not Submitted")
@Composable
private fun QuizQuestionNotSubmittedPreview() {
    AppTheme {
        QuizQuestionItem(
            questionNumber = 1,
            question = getSampleQuestion(),
            selectedAnswer = 0,
            onAnswerSelected = {},
            isSubmitted = false
        )
    }
}

@Preview(showBackground = true, name = "Quiz Question - Correct Answer")
@Composable
private fun QuizQuestionCorrectAnswerPreview() {
    AppTheme {
        QuizQuestionItem(
            questionNumber = 2,
            question = getSampleQuestion(),
            selectedAnswer = 0, // Correct answer
            onAnswerSelected = {},
            isSubmitted = true
        )
    }
}

@Preview(showBackground = true, name = "Quiz Question - Wrong Answer")
@Composable
private fun QuizQuestionWrongAnswerPreview() {
    AppTheme {
        QuizQuestionItem(
            questionNumber = 3,
            question = getSampleQuestion(),
            selectedAnswer = 2, // Wrong answer
            onAnswerSelected = {},
            isSubmitted = true
        )
    }
}

@Preview(showBackground = true, name = "Quiz Question - No Selection")
@Composable
private fun QuizQuestionNoSelectionPreview() {
    AppTheme {
        QuizQuestionItem(
            questionNumber = 4,
            question = getSampleQuestion(),
            selectedAnswer = null,
            onAnswerSelected = {},
            isSubmitted = false
        )
    }
}

private fun getSampleQuestion(): QuizQuestion = QuizQuestion(
    id = "sample",
    question = "What is the main benefit of renewable energy sources?",
    options = listOf(
        "Lower environmental impact",
        "Higher cost efficiency",
        "Faster implementation",
        "Better technology"
    ),
    correctAnswerIndex = 0,
    correctAnswerKey = "A",
    explanation = "Renewable energy has a significantly lower environmental impact compared to fossil fuels."
)