package com.linli.blackcatnews.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.linli.blackcatnews.domain.model.QuizQuestion

/**
 * 測驗面板組件
 * 右下角可展開的測驗區域
 */
@Composable
fun QuizPanel(
    isExpanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    quiz: List<QuizQuestion>,
    userAnswers: MutableMap<Int, Int>,
    isSubmitted: Boolean,
    onSubmit: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.End
    ) {
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
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Text(
                text = if (isExpanded) "✕" else "✅",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

internal fun getSampleQuizData(): List<QuizQuestion> = listOf(
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