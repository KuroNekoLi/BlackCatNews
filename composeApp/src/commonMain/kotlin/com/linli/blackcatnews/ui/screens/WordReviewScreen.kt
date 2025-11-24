package com.linli.blackcatnews.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.linli.dictionary.domain.model.ReviewCard
import com.linli.dictionary.domain.model.ReviewRating
import com.linli.dictionary.presentation.wordbank.WordReviewViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WordReviewScreen(
    viewModel: WordReviewViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Progress Indicator
        if (uiState.totalDueCount > 0) {
            LinearProgressIndicator(
                progress = {
                    uiState.reviewedCount.toFloat() / uiState.totalDueCount.toFloat()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading && uiState.currentCard == null -> {
                    CircularProgressIndicator()
                }

                uiState.sessionCompleted -> {
                    ReviewCompletedContent(
                        reviewedCount = uiState.reviewedCount,
                        onBackClick = onBackClick,
                        onRefresh = viewModel::refreshQueue
                    )
                }

                uiState.currentCard != null -> {
                    ReviewCardContent(
                        card = uiState.currentCard!!,
                        isSubmitting = uiState.isLoading,
                        onGrade = viewModel::gradeCurrentCard
                    )
                }

                // No cards due
                uiState.totalDueCount == 0 && !uiState.isLoading -> {
                    NoDueCardsContent(
                        onBackClick = onBackClick,
                        onRefresh = viewModel::refreshQueue
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewCardContent(
    card: ReviewCard,
    isSubmitting: Boolean,
    onGrade: (ReviewRating) -> Unit
) {
    // Reset showDefinition when card changes
    var showDefinition by remember(card.word.word) { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Card Area
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Front of card
                Text(
                    text = card.word.word,
                    style = MaterialTheme.typography.displayMedium,
                    textAlign = TextAlign.Center
                )

                val firstEntry = card.word.entries.firstOrNull()
                if (firstEntry != null) {
                    Text(
                        text = firstEntry.partOfSpeech,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Back of card (Answer)
                if (showDefinition) {
                    Spacer(modifier = Modifier.height(24.dp))

                    val definition = firstEntry?.definitions?.firstOrNull()
                    if (definition != null) {
                        Text(
                            text = definition.zhDefinition,
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center
                        )

                        if (definition.examples.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "例句:",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = definition.examples.first(),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Area
        if (!showDefinition) {
            Button(
                onClick = { showDefinition = true },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("顯示答案")
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RatingButton(
                        text = "忘記",
                        subText = "Again",
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        onClick = { onGrade(ReviewRating.AGAIN) },
                        enabled = !isSubmitting,
                        modifier = Modifier.weight(1f)
                    )
                    RatingButton(
                        text = "困難",
                        subText = "Hard",
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        onClick = { onGrade(ReviewRating.HARD) },
                        enabled = !isSubmitting,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RatingButton(
                        text = "良好",
                        subText = "Good",
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        onClick = { onGrade(ReviewRating.GOOD) },
                        enabled = !isSubmitting,
                        modifier = Modifier.weight(1f)
                    )
                    RatingButton(
                        text = "簡單",
                        subText = "Easy",
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        onClick = { onGrade(ReviewRating.EASY) },
                        enabled = !isSubmitting,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun RatingButton(
    text: String,
    subText: String,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(64.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = text, style = MaterialTheme.typography.titleMedium)
            Text(text = subText, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun ReviewCompletedContent(
    reviewedCount: Int,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🎉",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "恭喜完成！",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "本次複習了 $reviewedCount 個單字",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onBackClick) {
            Text("返回")
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onRefresh) {
            Text("再次檢查")
        }
    }
}

@Composable
private fun NoDueCardsContent(
    onBackClick: () -> Unit,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "☕",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "目前沒有需要複習的單字",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onBackClick) {
            Text("返回")
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onRefresh) {
            Text("重新整理")
        }
    }
}
