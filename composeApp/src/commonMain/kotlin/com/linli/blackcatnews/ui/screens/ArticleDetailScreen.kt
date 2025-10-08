package com.linli.blackcatnews.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.linli.blackcatnews.domain.model.ArticleDetail
import com.linli.blackcatnews.domain.model.BilingualContent
import com.linli.blackcatnews.domain.model.BilingualParagraph
import com.linli.blackcatnews.domain.model.BilingualText
import com.linli.blackcatnews.domain.model.GlossaryItem
import com.linli.blackcatnews.domain.model.GrammarPoint
import com.linli.blackcatnews.domain.model.NewsCategory
import com.linli.blackcatnews.domain.model.Quiz
import com.linli.blackcatnews.domain.model.QuizQuestion
import com.linli.blackcatnews.domain.model.ReadingMode
import com.linli.blackcatnews.ui.components.BilingualTextView
import com.linli.blackcatnews.ui.components.GlossaryCard
import com.linli.blackcatnews.ui.components.GrammarPointCard

/**
 * 文章詳情頁面
 * 支持雙語閱讀和學習功能
 * Scaffold 和 TopBar 由 AppNavigation 統一管理
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    articleId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 模擬從 ViewModel 或 Repository 獲取數據
    val article = remember { getSampleArticleDetail() }

    // 閱讀模式狀態
    var readingMode by remember { mutableStateOf(ReadingMode.ENGLISH_ONLY) }

    // 測驗展開狀態
    var isQuizExpanded by remember { mutableStateOf(false) }

    // 用戶的答案選擇 (題目索引 -> 選項索引)
    val userAnswers = remember { mutableStateMapOf<Int, Int>() }

    // 是否已提交答案
    var isQuizSubmitted by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        // 主內容區域
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = if (isQuizExpanded) 400.dp else 80.dp) // 為測驗面板留出空間
        ) {
            // 文章標題區域
            ArticleHeader(
                title = article.title,
                source = article.source,
                publishTime = article.publishTime,
                imageUrl = article.imageUrl,
                readingMode = readingMode,
                onReadingModeChange = { readingMode = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 文章內容
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                article.content.paragraphs.forEach { paragraph ->
                    BilingualTextView(
                        paragraph = paragraph,
                        readingMode = readingMode
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // 右下角測驗按鈕和展開面板
        QuizPanel(
            isExpanded = isQuizExpanded,
            onExpandChange = { isQuizExpanded = it },
            quiz = article.quiz?.questions ?: listOf(),
            userAnswers = userAnswers,
            isSubmitted = isQuizSubmitted,
            onSubmit = { isQuizSubmitted = true },
            onReset = {
                userAnswers.clear()
                isQuizSubmitted = false
            },
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

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
                            userAnswers[index] == quiz[index].correctAnswer
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

/**
 * 單個測驗題目
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
            text = "$questionNumber. ${question.question}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        // 選項
        question.options.forEachIndexed { index, option ->
            val isSelected = selectedAnswer == index
            val isCorrect = index == question.correctAnswer

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

/**
 * 文章頭部（標題、元信息、模式切換）
 */
@Composable
private fun ArticleHeader(
    title: BilingualText,
    source: String,
    publishTime: String,
    imageUrl: String?,
    readingMode: ReadingMode,
    onReadingModeChange: (ReadingMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 模式切換按鈕組
        ReadingModeSelector(
            currentMode = readingMode,
            onModeChange = onReadingModeChange,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 標題（根據模式顯示）
        when (readingMode) {
            ReadingMode.ENGLISH_ONLY -> {
                Text(
                    text = title.english,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            ReadingMode.CHINESE_ONLY -> {
                Text(
                    text = title.chinese,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            else -> {
                Text(
                    text = title.english,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title.chinese,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 元信息
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = source,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = publishTime,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = "📊 8 min read",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Divider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

/**
 * 閱讀模式選擇器
 */
@Composable
private fun ReadingModeSelector(
    currentMode: ReadingMode,
    onModeChange: (ReadingMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = currentMode == ReadingMode.ENGLISH_ONLY,
            onClick = { onModeChange(ReadingMode.ENGLISH_ONLY) },
            label = { Text("🇬🇧 EN") }
        )
        FilterChip(
            selected = currentMode == ReadingMode.CHINESE_ONLY,
            onClick = { onModeChange(ReadingMode.CHINESE_ONLY) },
            label = { Text("🇹🇼 中") }
        )
        FilterChip(
            selected = currentMode == ReadingMode.STACKED,
            onClick = { onModeChange(ReadingMode.STACKED) },
            label = { Text("⬍ 對照") }
        )
        FilterChip(
            selected = currentMode == ReadingMode.SIDE_BY_SIDE,
            onClick = { onModeChange(ReadingMode.SIDE_BY_SIDE) },
            label = { Text("⬌ 並排") }
        )
    }
}

/**
 * 學習工具類型
 */
enum class LearningTool {
    GLOSSARY,  // 重點單字
    GRAMMAR,   // 文法說明
    QUIZ,      // 閱讀測驗
    PROGRESS   // 學習進度
}

/**
 * 學習工具內容（在 Bottom Sheet 中顯示）
 */
@Composable
private fun LearningToolContent(
    tool: LearningTool,
    article: ArticleDetail,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 標題
        Text(
            text = when (tool) {
                LearningTool.GLOSSARY -> "📚 重點單字"
                LearningTool.GRAMMAR -> "📝 文法說明"
                LearningTool.QUIZ -> "✅ 閱讀測驗"
                LearningTool.PROGRESS -> "📊 學習進度"
            },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 內容
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (tool) {
                LearningTool.GLOSSARY -> {
                    items(article.glossary) { item ->
                        GlossaryCard(item = item)
                    }
                }

                LearningTool.GRAMMAR -> {
                    items(article.grammarPoints) { point ->
                        GrammarPointCard(point = point)
                    }
                }

                LearningTool.QUIZ -> {
                    // 測驗功能將在下一部分實現
                    item {
                        Text("測驗功能開發中...")
                    }
                }

                LearningTool.PROGRESS -> {
                    item {
                        Text("學習進度功能開發中...")
                    }
                }
            }
        }
    }
}

/**
 * 生成模擬文章數據
 */
private fun getSampleArticleDetail(): ArticleDetail {
    return ArticleDetail(
        id = "1",
        title = BilingualText(
            english = "AI Revolution: How Artificial Intelligence is Transforming Global Industries",
            chinese = "人工智能革命：人工智能如何改變全球產業"
        ),
        summary = BilingualText(
            english = "Artificial intelligence is revolutionizing industries worldwide...",
            chinese = "人工智能正在全球範圍內改變各個行業..."
        ),
        content = BilingualContent(
            paragraphs = listOf(
                BilingualParagraph(
                    english = "The rapid advancement of artificial intelligence has fundamentally altered the landscape of modern business. Companies across various sectors are integrating AI technologies to enhance efficiency, reduce costs, and create innovative products.",
                    chinese = "人工智能的快速發展從根本上改變了現代商業的格局。各行各業的公司都在整合人工智能技術，以提高效率、降低成本並創造創新產品。",
                    order = 0
                ),
                BilingualParagraph(
                    english = "According to recent studies, AI-driven automation is expected to boost global GDP by approximately 14% by 2030. This transformative technology is not only improving operational processes but also opening new opportunities for job creation in emerging fields.",
                    chinese = "根據最近的研究，預計到2030年，人工智能驅動的自動化將使全球GDP增長約14%。這項變革性技術不僅改善了運營流程，還為新興領域的就業創造帶來了新機遇。",
                    order = 1
                ),
                BilingualParagraph(
                    english = "However, experts emphasize the importance of responsible AI development. As these technologies become more prevalent, addressing ethical concerns and ensuring equitable access remain critical challenges for policymakers and industry leaders alike.",
                    chinese = "然而，專家強調負責任的人工智能發展的重要性。隨著這些技術變得更加普及，解決倫理問題和確保公平獲取仍然是政策制定者和行業領導者面臨的關鍵挑戰。",
                    order = 2
                )
            )
        ),
        imageUrl = null,
        source = "Tech News",
        publishTime = "2 hours ago",
        category = NewsCategory.TECH,
        glossary = listOf(
            GlossaryItem(
                word = "artificial intelligence",
                translation = "人工智能",
                pronunciation = "/ˌɑːrtɪˈfɪʃl ɪnˈtelɪdʒəns/",
                example = "Artificial intelligence is transforming healthcare."
            ),
            GlossaryItem(
                word = "automation",
                translation = "自動化",
                pronunciation = "/ˌɔːtəˈmeɪʃn/",
                example = "Automation has increased factory productivity."
            ),
            GlossaryItem(
                word = "emerging",
                translation = "新興的",
                pronunciation = "/ɪˈmɜːrdʒɪŋ/",
                example = "Emerging technologies are reshaping society."
            )
        ),
        grammarPoints = listOf(
            GrammarPoint(
                title = "Present Perfect Tense",
                explanation = "用於描述過去發生但與現在相關的動作",
                examples = listOf(
                    "AI has fundamentally altered the landscape.",
                    "Companies have integrated AI technologies."
                )
            ),
            GrammarPoint(
                title = "Passive Voice",
                explanation = "被動語態用於強調動作本身而非執行者",
                examples = listOf(
                    "AI is expected to boost global GDP.",
                    "Ethical concerns must be addressed."
                )
            )
        ),
        quiz = Quiz(
            questions = listOf(
                QuizQuestion(
                    id = "q1",
                    question = "According to the article, by what percentage is AI expected to boost global GDP by 2030?",
                    options = listOf(
                        "10%",
                        "14%",
                        "20%",
                        "25%"
                    ),
                    correctAnswer = 1,
                    explanation = "The article states that AI-driven automation is expected to boost global GDP by approximately 14% by 2030."
                ),
                QuizQuestion(
                    id = "q2",
                    question = "What is emphasized as important for AI development?",
                    options = listOf(
                        "Speed of development",
                        "Cost reduction",
                        "Responsible development",
                        "Global competition"
                    ),
                    correctAnswer = 2,
                    explanation = "Experts emphasize the importance of responsible AI development."
                ),
                QuizQuestion(
                    id = "q3",
                    question = "Which of the following is NOT mentioned as a benefit of AI integration?",
                    options = listOf(
                        "Enhanced efficiency",
                        "Reduced costs",
                        "Increased salaries",
                        "Innovative products"
                    ),
                    correctAnswer = 2,
                    explanation = "The article mentions efficiency, cost reduction, and innovation, but not increased salaries."
                ),
                QuizQuestion(
                    id = "q4",
                    question = "What challenge is mentioned for policymakers?",
                    options = listOf(
                        "Funding research",
                        "Training workers",
                        "Ensuring equitable access",
                        "Building infrastructure"
                    ),
                    correctAnswer = 2,
                    explanation = "The article states that ensuring equitable access remains a critical challenge for policymakers."
                ),
                QuizQuestion(
                    id = "q5",
                    question = "What is AI doing to operational processes?",
                    options = listOf(
                        "Replacing them",
                        "Improving them",
                        "Complicating them",
                        "Delaying them"
                    ),
                    correctAnswer = 1,
                    explanation = "The article mentions that AI is improving operational processes."
                )
            )
        )
    )
}

enum class ReadingMode {
    ENGLISH_ONLY,
    CHINESE_ONLY,
    STACKED,
    SIDE_BY_SIDE
}

enum class NewsCategory {
    TECH
}
