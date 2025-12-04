package com.linli.blackcatnews.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.linli.blackcatnews.domain.model.BilingualParagraphType
import com.linli.blackcatnews.domain.model.ReadingMode
import com.linli.blackcatnews.presentation.viewmodel.ArticleDetailViewModel
import com.linli.blackcatnews.presentation.viewmodel.TtsViewModel
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
    onSetTopBarActions: (@Composable RowScope.() -> Unit) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dictionaryViewModel: DictionaryViewModel = koinViewModel<DictionaryViewModel>()
    val wordBankViewModel: WordBankViewModel = koinViewModel()
    val ttsViewModel: TtsViewModel = koinViewModel()

    val uiState by viewModel.uiState.collectAsState()
    val wordBankState by wordBankViewModel.uiState.collectAsState()
    val article = uiState.article ?: return

    val playbackController = ttsViewModel.playbackController
    val playingParagraphIndex by ttsViewModel.playingParagraphIndex.collectAsState()
    val isFullTextPlaying by ttsViewModel.isFullTextPlaying.collectAsState()
    val highlightState by ttsViewModel.highlightState.collectAsState()

    var readingMode by remember { mutableStateOf(ReadingMode.ENGLISH_ONLY) }
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

    LaunchedEffect(isFullTextPlaying, article) {
        onSetTopBarActions {
            IconButton(onClick = {
                if (isFullTextPlaying) {
                    ttsViewModel.stopFullTextPlayback()
                } else {
                    ttsViewModel.startFullTextPlayback(article)
                }
            }) {
                Icon(
                    imageVector = if (isFullTextPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isFullTextPlaying) "Stop Reading" else "Read Aloud"
                )
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { onSetTopBarActions {} }
    }

    val scrollState = rememberScrollState()
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 32.dp)
        ) {
            ArticleHeader(
                title = article.title,
                source = article.source,
                url = article.url,
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
                article.content.paragraphs.forEachIndexed { index, paragraph ->
                    when (paragraph.type) {
                        BilingualParagraphType.TEXT -> ArticleWithWordTooltip(
                            paragraph = paragraph,
                            readingMode = readingMode,
                            viewModel = dictionaryViewModel,
                            highlightRange = if (index == playingParagraphIndex) highlightState else null,
                            isPlaying = index == playingParagraphIndex
                        )

                        else -> BilingualTextView(
                            paragraph = paragraph,
                            readingMode = readingMode
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider()

            QuizPanel(
                isExpanded = false,
                onExpandChange = { },
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
                playingAudioId = playbackController.playingItemId,
                onPlayAudio = { text, id, languageTag ->
                    ttsViewModel.playSingle(
                        text = text,
                        id = id,
                        languageTag = languageTag ?: "en-US"
                    )
                },
                onStopAudio = { ttsViewModel.stop() },
                isTtsSupported = true
            )
        }
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
