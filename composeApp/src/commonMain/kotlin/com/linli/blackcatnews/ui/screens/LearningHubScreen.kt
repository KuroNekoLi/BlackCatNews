package com.linli.blackcatnews.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linli.blackcatnews.presentation.viewmodel.ArticleDetailViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningHubScreen(
    articleId: String,
    onNavigateToArticle: () -> Unit,
    onBackClick: () -> Unit,
    onSetTopBarActions: (@Composable RowScope.() -> Unit) -> Unit,
    viewModel: ArticleDetailViewModel = koinViewModel { parametersOf(articleId) }
) {
    val uiState by viewModel.uiState.collectAsState()
    val article = uiState.article

    // Trigger article load if needed (though viewModel init should handle it)
    LaunchedEffect(articleId) {
        // viewModel.loadArticle(articleId) // Assuming auto-init
    }

    // Set TopBar Actions
    LaunchedEffect(article) {
        onSetTopBarActions {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(50),
                modifier = Modifier.padding(end = 16.dp)
            ) {
                val categoryName = article?.category?.displayName ?: "Loading"
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { onSetTopBarActions {} }
    }

    if (article == null) {
        // Loading or Error state
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading...")
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Article Summary Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "本篇文章",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = article.title.english,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "單字 x${article.glossary.size} · 片語 x${article.phrases.size} · 文法 x${article.grammarPoints.size} · 測驗 x${article.quiz?.questions?.size ?: 0}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(
                thickness = 8.dp,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )

            // Learning Modules
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Vocabulary Module
                LearningModuleCard(
                    icon = "📘",
                    title = "重點單字 & 片語",
                    subtitle = "${article.glossary.size} 個單字 · ${article.phrases.size} 個片語 · 可加入單字本",
                    badge = "建議先看",
                    onActionClick = { /* Navigate to Vocab view or expand */ }
                ) {
                    // Preview content
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        article.glossary.take(3).forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.word,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                TextButton(onClick = { /* Add to word bank */ }) {
                                    Text("＋ 加入", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }

                // Grammar Module
                LearningModuleCard(
                    icon = "🧩",
                    title = "文法 & 句型解析",
                    subtitle = "${article.grammarPoints.size} 個關鍵句型 · 含例句與中文解說",
                    onActionClick = { /* Expand grammar */ }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        article.grammarPoints.take(2).forEach { gp ->
                            Text(
                                text = "• ${gp.rule}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Quiz Module
                LearningModuleCard(
                    icon = "🧠",
                    title = "閱讀測驗",
                    subtitle = "${article.quiz?.questions?.size ?: 0} 題閱讀理解 · 約 5 分鐘",
                    badge = "尚未完成",
                    onActionClick = { /* Navigate to Quiz */ }
                ) {
                    Text(
                        text = "測試你對本文重點、推論與細節的理解程度。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Button(
                        onClick = onNavigateToArticle, // Currently leads to article which contains quiz
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("開始閱讀 & 測驗")
                    }
                }
            }
        }
    }
}

@Composable
private fun LearningModuleCard(
    icon: String,
    title: String,
    subtitle: String,
    badge: String? = null,
    onActionClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = icon, fontSize = 24.sp)
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (badge != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = badge,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            content()

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onActionClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("查看詳細 →")
            }
        }
    }
}
