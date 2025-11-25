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
import com.linli.blackcatnews.domain.model.BilingualParagraphType
import com.linli.blackcatnews.domain.model.ReadingMode
import com.linli.blackcatnews.presentation.viewmodel.ArticleDetailViewModel
import com.linli.blackcatnews.tts.DEFAULT_LANGUAGE_TAG
import com.linli.blackcatnews.tts.rememberTextToSpeechManager
import com.linli.blackcatnews.tts.rememberTtsPlaybackController
import com.linli.blackcatnews.ui.common.LoginRequiredDialog
import com.linli.blackcatnews.ui.components.ArticleHeader
import com.linli.blackcatnews.ui.components.ArticleWithWordTooltip
import com.linli.blackcatnews.ui.components.BilingualTextView
import com.linli.blackcatnews.ui.components.QuizPanel
import com.linli.dictionary.presentation.DictionaryViewModel
import com.linli.dictionary.presentation.wordbank.WordBankViewModel
import org.koin.compose.viewmodel.koinViewModel

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
    onRequireSignIn: () -> Boolean,
    onNavigateToSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dictionaryViewModel: DictionaryViewModel = koinViewModel<DictionaryViewModel>()
    val wordBankViewModel: WordBankViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val wordBankState by wordBankViewModel.uiState.collectAsState()
    val article = uiState.article ?: return
    val textToSpeechManager = rememberTextToSpeechManager()
    val ttsPlaybackController = rememberTtsPlaybackController(textToSpeechManager)
    var readingMode by remember { mutableStateOf(ReadingMode.ENGLISH_ONLY) }
    var isQuizExpanded by remember { mutableStateOf(false) }
    val userAnswers = remember { mutableStateMapOf<Int, Int>() }
    var isQuizSubmitted by remember { mutableStateOf(false) }
    var showLoginDialog by remember { mutableStateOf(false) }
    val ensureAuthenticated = remember(onRequireSignIn) {
        {
            val authed = onRequireSignIn()
            if (!authed) {
                showLoginDialog = true
            }
            authed
        }
    }

    val scrollState = rememberScrollState()
    Box(modifier = modifier.fillMaxSize()) {
        // 主內容區域
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = if (isQuizExpanded) 400.dp else 80.dp) // 為測驗面板留出空間
        ) {
            // 文章標題區域
            ArticleHeader(
                title = article.title,
                source = article.source,
                publishTime = article.publishTime,
                imageUrl = article.imageUrl,
                readingMode = readingMode,
                onReadingModeChange = { readingMode = it },
                onBackClick = {
                    onBackClick()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 文章內容
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                article.content.paragraphs.forEach { paragraph ->
                    when (paragraph.type) {
                        BilingualParagraphType.TEXT -> ArticleWithWordTooltip(
                            paragraph = paragraph,
                            readingMode = readingMode,
                            viewModel = dictionaryViewModel
                        )

                        else -> BilingualTextView(
                            paragraph = paragraph,
                            readingMode = readingMode
                        )
                    }
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
            savedWords = wordBankState.savedWords.map { it.word.lowercase() }.toSet(),
            onAddWordToWordBank = { word ->
                val normalizedWord = word.trim()
                if (normalizedWord.isNotEmpty()) {
                    wordBankViewModel.addWord(normalizedWord)
                }
            },
            ensureAuthenticated = ensureAuthenticated,
            playingAudioId = ttsPlaybackController.playingItemId,
            onPlayAudio = { text, id, languageTag ->
                ttsPlaybackController.play(
                    text = text,
                    id = id,
                    languageTag = languageTag ?: DEFAULT_LANGUAGE_TAG
                )
            },
            onStopAudio = { ttsPlaybackController.stop() },
            isTtsSupported = textToSpeechManager != null,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }

    if (showLoginDialog) {
        LoginRequiredDialog(
            onConfirm = {
                showLoginDialog = false
                onNavigateToSignIn()
            },
            onDismiss = { showLoginDialog = false }
        )
    }
}
