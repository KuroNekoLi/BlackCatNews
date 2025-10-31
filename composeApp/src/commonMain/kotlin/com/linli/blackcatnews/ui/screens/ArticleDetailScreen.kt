package com.linli.blackcatnews.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.linli.blackcatnews.domain.model.BilingualContent
import com.linli.blackcatnews.domain.model.BilingualParagraph
import com.linli.blackcatnews.domain.model.BilingualText
import com.linli.blackcatnews.domain.model.GlossaryItem
import com.linli.blackcatnews.domain.model.GrammarPoint
import com.linli.blackcatnews.domain.model.PhraseIdiom
import com.linli.blackcatnews.domain.model.Quiz
import com.linli.blackcatnews.domain.model.QuizQuestion
import com.linli.blackcatnews.domain.model.ReadingMode
import com.linli.blackcatnews.domain.model.SentencePattern
import com.linli.blackcatnews.presentation.viewmodel.ArticleDetailViewModel
import com.linli.blackcatnews.ui.components.ArticleHeader
import com.linli.blackcatnews.ui.components.BilingualTextView
import com.linli.blackcatnews.ui.components.QuizPanel
import com.linli.blackcatnews.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 文章詳情頁面
 * 支持雙語閱讀和學習功能
 * Scaffold 和 TopBar 由 AppNavigation 統一管理
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    viewModel: ArticleDetailViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val article = uiState.article ?: return
    var readingMode by remember { mutableStateOf(ReadingMode.ENGLISH_ONLY) }
    var isQuizExpanded by remember { mutableStateOf(false) }
    val userAnswers = remember { mutableStateMapOf<Int, Int>() }
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
            glossary = article.glossary,
            grammarPoints = article.grammarPoints,
            sentencePatterns = article.sentencePatterns,
            phrases = article.phrases,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

@Preview(showBackground = true, name = "Article Detail Screen - Complete Layout")
@Composable
private fun ArticleDetailScreenLayoutPreview() {
    AppTheme {
        val readingMode = remember { mutableStateOf(ReadingMode.ENGLISH_ONLY) }
        val isQuizExpanded = remember { mutableStateOf(false) }
        val userAnswers = remember { mutableStateMapOf<Int, Int>() }
        val isQuizSubmitted = remember { mutableStateOf(false) }
        val sampleArticle = getSampleArticleData()

        Box(modifier = Modifier.fillMaxSize()) {
            // 主內容區域 - 模擬 ArticleDetailScreen 的佈局
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = if (isQuizExpanded.value) 400.dp else 80.dp)
            ) {
                // 文章標題區域
                ArticleHeader(
                    title = sampleArticle.title,
                    source = sampleArticle.source,
                    publishTime = sampleArticle.publishTime,
                    imageUrl = sampleArticle.imageUrl,
                    readingMode = readingMode.value,
                    onReadingModeChange = { readingMode.value = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 文章內容
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    sampleArticle.content.paragraphs.forEach { paragraph ->
                        BilingualTextView(
                            paragraph = paragraph,
                            readingMode = readingMode.value
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // 右下角測驗按鈕和展開面板
            QuizPanel(
                isExpanded = isQuizExpanded.value,
                onExpandChange = { isQuizExpanded.value = it },
                quiz = sampleArticle.quiz?.questions ?: listOf(),
                userAnswers = userAnswers,
                isSubmitted = isQuizSubmitted.value,
                onSubmit = { isQuizSubmitted.value = true },
                onReset = {
                    userAnswers.clear()
                    isQuizSubmitted.value = false
                },
                glossary = sampleArticle.glossary,
                grammarPoints = sampleArticle.grammarPoints,
                sentencePatterns = sampleArticle.sentencePatterns,
                phrases = sampleArticle.phrases,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

@Preview(showBackground = true, name = "Article Detail - Reading Modes")
@Composable
private fun ArticleDetailReadingModesPreview() {
    AppTheme {
        val readingMode = remember { mutableStateOf(ReadingMode.STACKED) }
        val sampleArticle = getSampleArticleData()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 文章標題區域
            ArticleHeader(
                title = sampleArticle.title,
                source = sampleArticle.source,
                publishTime = sampleArticle.publishTime,
                imageUrl = sampleArticle.imageUrl,
                readingMode = readingMode.value,
                onReadingModeChange = { readingMode.value = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 文章內容預覽
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                sampleArticle.content.paragraphs.take(2).forEach { paragraph ->
                    BilingualTextView(
                        paragraph = paragraph,
                        readingMode = readingMode.value
                    )
                }
            }
        }
    }
}

/**
 * 創建範例文章數據用於 Preview
 */
private data class SampleArticleData(
    val title: BilingualText,
    val content: BilingualContent,
    val source: String,
    val publishTime: String,
    val imageUrl: String?,
    val quiz: Quiz?,
    val glossary: List<GlossaryItem>,
    val grammarPoints: List<GrammarPoint>,
    val sentencePatterns: List<SentencePattern>,
    val phrases: List<PhraseIdiom>
)

private fun getSampleArticleData(): SampleArticleData = SampleArticleData(
    title = BilingualText(
        english = "AI Revolution in Healthcare: Transforming Patient Care",
        chinese = "醫療保健中的AI革命：改變患者護理"
    ),
    content = BilingualContent(
        paragraphs = listOf(
            BilingualParagraph(
                english = "The healthcare industry is experiencing a technological renaissance, with artificial intelligence (AI) at the forefront of this transformation. From diagnostic imaging to drug discovery, AI is reshaping how medical professionals approach patient care.",
                chinese = "醫療保健行業正在經歷技術復興，人工智慧（AI）處於這一轉變的前沿。從診斷成像到藥物發現，AI正在重塑醫療專業人員處理患者護理的方式。",
                order = 1
            ),
            BilingualParagraph(
                english = "Machine learning algorithms can now analyze medical images with remarkable accuracy, often detecting conditions that might be missed by human eyes. Radiology departments worldwide are integrating AI tools to enhance diagnostic precision.",
                chinese = "機器學習算法現在可以以驚人的準確性分析醫學影像，通常能檢測到人眼可能遺漏的病症。全世界的放射科都在整合AI工具以提高診斷精度。",
                order = 2
            ),
            BilingualParagraph(
                english = "Personalized medicine is another area where AI excels. By analyzing vast amounts of patient data, including genetic information and medical history, AI systems can recommend tailored treatment plans.",
                chinese = "個人化醫療是AI擅長的另一個領域。通過分析大量患者數據，包括基因信息和病史，AI系統可以推薦定制的治療計劃。",
                order = 3
            ),
            BilingualParagraph(
                english = "The future promises even more exciting developments. AI-powered virtual assistants may soon help patients manage chronic conditions, while predictive analytics could identify health risks before symptoms appear.",
                chinese = "未來承諾更令人興奮的發展。AI驅動的虛擬助手可能很快就會幫助患者管理慢性疾病，而預測分析可以在症狀出現之前識別健康風險。",
                order = 4
            )
        )
    ),
    source = "Medical Technology Today",
    publishTime = "2024年3月15日",
    imageUrl = "https://example.com/ai-healthcare.jpg",
    quiz = Quiz(
        questions = listOf(
            QuizQuestion(
                id = "1",
                questionEnglish = "What is the primary advantage of AI in medical imaging?",
                questionChinese = "AI在醫學成像中的主要優點是什麼？",
                options = listOf(
                    "Enhanced diagnostic precision and reduced interpretation time",
                    "Completely replacing human radiologists",
                    "Making medical equipment cheaper",
                    "Eliminating the need for medical training"
                ),
                correctAnswerIndex = 0,
                correctAnswerKey = "A",
                explanationEnglish = "AI in medical imaging primarily enhances diagnostic precision and reduces interpretation time while working alongside human professionals.",
                explanationChinese = "AI在醫學成像中主要提高診斷精度和減少解釋時間，同時與人類專業人員合作。"
            ),
            QuizQuestion(
                id = "2",
                questionEnglish = "How does AI contribute to personalized medicine?",
                questionChinese = "AI如何為個人化醫療做出貢獻？",
                options = listOf(
                    "By making all treatments the same for everyone",
                    "By analyzing patient data to recommend tailored treatment plans",
                    "By reducing the cost of medicine",
                    "By eliminating the need for doctors"
                ),
                correctAnswerIndex = 1,
                correctAnswerKey = "B",
                explanationEnglish = "AI contributes to personalized medicine by analyzing vast amounts of patient data to recommend treatment plans tailored to individual patients.",
                explanationChinese = "AI通過分析大量患者數據推薦個性化的治療計劃，為個人化醫療做出貢獻。"
            )
        )
    ),
    glossary = listOf(
        GlossaryItem(
            word = "hysterectomy",
            partOfSpeech = "noun",
            translation = "子宮切除術",
            pronunciation = null,
            definitionEnglish = "A surgical operation to remove all or part of the uterus.",
            definitionChinese = "一種移除全部或部分子宮的外科手術。",
            exampleEnglish = "The patient underwent a hysterectomy due to severe health issues.",
            exampleChinese = "患者因嚴重健康問題接受了子宮切除術。",
            audioUrl = null
        )
    ),
    grammarPoints = listOf(
        GrammarPoint(
            rule = "Passive Voice",
            explanationEnglish = "The passive voice is used when the focus is on the action or when the subject is unknown or irrelevant.",
            explanationChinese = "被動語態用於當重點在於動作或當主語未知或不相關時。",
            exampleEnglish = "The report was published by the committee.",
            exampleChinese = "報告由委員會發布。"
        )
    ),
    sentencePatterns = listOf(
        SentencePattern(
            patternEnglish = "It is (adjective) that...",
            explanationEnglish = "This pattern is used to express opinions or judgments.",
            explanationChinese = "此句型用於表達意見或判斷。",
            exampleEnglish = "It is surprising that the issue was not addressed earlier.",
            exampleChinese = "令人驚訝的是，這個問題沒有早些解決。"
        )
    ),
    phrases = listOf(
        PhraseIdiom(
            phraseEnglish = "under caution",
            explanationEnglish = "A formal warning given by police to someone suspected of a crime.",
            explanationChinese = "警方對涉嫌犯罪的人給予的正式警告。",
            exampleEnglish = "The suspect was interviewed under caution by the police.",
            exampleChinese = "嫌疑人被警方在警告下進行了面試。"
        )
    )
)