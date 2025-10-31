package com.linli.blackcatnews.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.linli.blackcatnews.domain.model.QuizQuestion
import com.linli.blackcatnews.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 單個測驗題目組件
 * 顯示題目、選項和答案狀態
 */
@Composable
fun QuizQuestionItem(
    questionNumber: Int,
    question: QuizQuestion,
    selectedAnswer: Int?,
    onAnswerSelected: (Int) -> Unit,
    isSubmitted: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 題目
        Text(
            text = "$questionNumber. " + (question.questionEnglish.ifBlank {
                question.questionChinese ?: ""
            }),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        // 選項
        question.options.forEachIndexed { index, option ->
            val isSelected = selectedAnswer == index
            val isCorrect = index == question.correctAnswerIndex

            // 確定選項的顏色
            val backgroundColor = when {
                !isSubmitted -> {
                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surface
                }

                isCorrect -> Color(0xFF4CAF50).copy(alpha = 0.2f) // 綠色背景
                isSelected && !isCorrect -> Color(0xFFE53935).copy(alpha = 0.2f) // 紅色背景
                else -> MaterialTheme.colorScheme.surface
            }

            val textColor = when {
                !isSubmitted -> MaterialTheme.colorScheme.onSurface
                isCorrect -> Color(0xFF2E7D32) // 深綠色文字
                isSelected && !isCorrect -> Color(0xFFC62828) // 深紅色文字
                else -> MaterialTheme.colorScheme.onSurface
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = backgroundColor
                ),
                border = if (isSelected) CardDefaults.outlinedCardBorder() else null,
                onClick = { if (!isSubmitted) onAnswerSelected(index) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 選項標記
                    Text(
                        text = ('A' + index).toString(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )

                    // 選項內容
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor,
                        modifier = Modifier.weight(1f)
                    )

                    // 提交後顯示對錯圖標
                    if (isSubmitted) {
                        Text(
                            text = if (isCorrect) "✓" else if (isSelected) "✗" else "",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                    }
                }
            }
        }
    }
}

// Sample data for testing
internal fun getSampleQuestionData(): QuizQuestion = QuizQuestion(
    id = "1",
    questionEnglish = "Sample question in English",
    questionChinese = "範例問題中文版本",
    options = listOf("A", "B", "C"),
    correctAnswerIndex = 0,
    correctAnswerKey = "A",
    explanationEnglish = "Sample explanation in English",
    explanationChinese = "範例解析中文版本"
)


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
    id = "1",
    questionEnglish = "Android sample question",
    questionChinese = "Android範例問題 (中文)",
    options = listOf("A", "B", "C"),
    correctAnswerIndex = 0,
    correctAnswerKey = "A",
    explanationEnglish = "Android sample explanation in English",
    explanationChinese = "Android範例解析 (中文)"
)