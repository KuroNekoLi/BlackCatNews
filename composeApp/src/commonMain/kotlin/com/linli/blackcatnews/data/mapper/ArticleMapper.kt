package com.linli.blackcatnews.data.mapper

import com.linli.blackcatnews.data.local.entity.ArticleEntity
import com.linli.blackcatnews.data.local.entity.ComprehensionQuestionEntity
import com.linli.blackcatnews.data.local.entity.GrammarItemEntity
import com.linli.blackcatnews.data.local.entity.PhraseIdiomEntity
import com.linli.blackcatnews.data.local.entity.SentencePatternEntity
import com.linli.blackcatnews.data.local.entity.VocabularyItemEntity
import com.linli.blackcatnews.data.remote.dto.AiArticleDto
import com.linli.blackcatnews.data.remote.dto.ComprehensionQuestionDto
import com.linli.blackcatnews.data.remote.dto.GrammarItemDto
import com.linli.blackcatnews.data.remote.dto.PhraseIdiomDto
import com.linli.blackcatnews.data.remote.dto.SentencePatternDto
import com.linli.blackcatnews.data.remote.dto.VocabularyItemDto
import com.linli.blackcatnews.domain.model.ArticleDetail
import com.linli.blackcatnews.domain.model.BilingualContent
import com.linli.blackcatnews.domain.model.BilingualParagraph
import com.linli.blackcatnews.domain.model.BilingualText
import com.linli.blackcatnews.domain.model.GlossaryItem
import com.linli.blackcatnews.domain.model.GrammarPoint
import com.linli.blackcatnews.domain.model.NewsCategory
import com.linli.blackcatnews.domain.model.NewsItem
import com.linli.blackcatnews.domain.model.PhraseIdiom
import com.linli.blackcatnews.domain.model.Quiz
import com.linli.blackcatnews.domain.model.QuizQuestion
import com.linli.blackcatnews.domain.model.SentencePattern
import kotlinx.datetime.Clock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * 文章資料映射器
 *
 * 負責處理不同層之間的資料轉換：
 * - DTO ↔ Entity（遠端 ↔ 本地資料庫）
 * - Entity ↔ Domain（本地資料庫 ↔ 領域模型）
 */
object ArticleMapper {

    private val jsonFormatter: Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    /**
     * 將遠端 DTO 轉換為 Room Entity，並保留既有收藏與建立時間
     */
    fun dtoToEntity(dto: AiArticleDto, existing: ArticleEntity? = null): ArticleEntity {
        val now = Clock.System.now().toEpochMilliseconds()
        val createdAt = existing?.createdAt ?: now
        val summaryText = dto.summaryEnglish?.takeIf { it.isNotBlank() }
            ?: dto.cleanedTextEnglish.lineSequence().firstOrNull()?.take(200)?.trim().orEmpty()

        return ArticleEntity(
            id = dto.id.toString(),
            title = dto.title,
            titleZh = dto.titleZh,
            summary = summaryText,
            summaryZh = dto.summaryChinese,
            contentHtml = dto.optimizedHtml ?: dto.contentHtml,
            cleanedText = dto.cleanedTextEnglish,
            cleanedTextZh = dto.cleanedTextChinese,
            imageUrl = extractHeroImage(dto.contentHtml),
            sourceName = dto.sourceName,
            publishTime = dto.publishedAt,
            section = dto.section,
            url = dto.originalUrl,
            language = dto.language,
            learningVocabulary = dto.explanation?.vocabulary?.map { it.toEntity() } ?: emptyList(),
            learningGrammar = dto.explanation?.grammar?.map { it.toEntity() } ?: emptyList(),
            learningSentencePatterns = dto.explanation?.sentencePatterns?.map { it.toEntity() }
                ?: emptyList(),
            learningPhrases = dto.explanation?.phrasesIdioms?.map { it.toEntity() } ?: emptyList(),
            learningQuiz = dto.explanation?.comprehensionMcq?.mapIndexed { index, dtoQuestion ->
                dtoQuestion.toEntity(index)
            } ?: emptyList(),
            isFavorite = existing?.isFavorite ?: false,
            createdAt = createdAt,
            updatedAt = now
        )
    }

    /**
     * 將 Entity 轉換為領域層列表項目
     */
    fun entityToNewsItem(entity: ArticleEntity): NewsItem {
        return NewsItem(
            id = entity.id,
            title = entity.title,
            summary = entity.summary,
            imageUrl = entity.imageUrl,
            source = entity.sourceName,
            publishTime = entity.publishTime,
            category = mapSectionToCategory(entity.section)
        )
    }

    /**
     * 將 Entity 轉換為領域層文章詳情
     */
    fun entityToArticleDetail(entity: ArticleEntity): ArticleDetail {
        return ArticleDetail(
            id = entity.id,
            title = BilingualText(
                english = entity.title,
                chinese = entity.titleZh ?: entity.title
            ),
            summary = BilingualText(
                english = entity.summary,
                chinese = entity.summaryZh ?: entity.summary
            ),
            content = BilingualContent(
                paragraphs = splitContentToParagraphs(
                    englishContent = entity.cleanedText,
                    chineseContent = entity.cleanedTextZh
                )
            ),
            imageUrl = entity.imageUrl,
            source = entity.sourceName,
            publishTime = entity.publishTime,
            category = mapSectionToCategory(entity.section),
            glossary = entity.learningVocabulary.map { it.toDomain() },
            grammarPoints = entity.learningGrammar.map { it.toDomain() },
            sentencePatterns = entity.learningSentencePatterns.map { it.toDomain() },
            phrases = entity.learningPhrases.map { it.toDomain() },
            quiz = entity.learningQuiz.takeIf { it.isNotEmpty() }?.let { questions ->
                Quiz(questions.map { it.toDomain() })
            }
        )
    }

    /**
     * DTO 直接轉換為列表展示使用
     */
    fun dtoToNewsItem(dto: AiArticleDto): NewsItem {
        val summaryText = dto.summaryEnglish?.takeIf { it.isNotBlank() }
            ?: dto.cleanedTextEnglish.lineSequence().firstOrNull()?.take(200)?.trim().orEmpty()

        return NewsItem(
            id = dto.id.toString(),
            title = dto.title,
            summary = summaryText,
            imageUrl = extractHeroImage(dto.contentHtml),
            source = dto.sourceName,
            publishTime = dto.publishedAt,
            category = mapSectionToCategory(dto.section)
        )
    }

    // region 私有輔助邏輯

    private fun mapSectionToCategory(section: String): NewsCategory {
        return when (section.lowercase()) {
            "world" -> NewsCategory.WORLD
            "technology", "tech" -> NewsCategory.TECH
            "business" -> NewsCategory.BUSINESS
            "sports" -> NewsCategory.SPORTS
            "entertainment" -> NewsCategory.ENTERTAINMENT
            "health" -> NewsCategory.HEALTH
            "science" -> NewsCategory.SCIENCE
            else -> NewsCategory.LATEST
        }
    }

    private fun splitContentToParagraphs(
        englishContent: String?,
        chineseContent: String?
    ): List<BilingualParagraph> {
        if (englishContent.isNullOrBlank()) return emptyList()

        val englishSegments = englishContent
            .split(Regex("\n{2,}"))
            .mapNotNull { it.trim().takeIf(String::isNotEmpty) }
        val chineseSegments = chineseContent
            ?.split(Regex("\n{2,}"))
            ?.mapNotNull { it.trim().takeIf(String::isNotEmpty) }
            ?: emptyList()

        return englishSegments.mapIndexed { index, englishParagraph ->
            BilingualParagraph(
                english = englishParagraph,
                chinese = chineseSegments.getOrNull(index) ?: englishParagraph,
                order = index
            )
        }
    }

    private fun extractHeroImage(contentHtml: String): String? {
        // 簡易解析，尋找第一個 img src
        val regex = Regex("<img[^>]*src=\"([^\"]+)\"")
        val match = regex.find(contentHtml)
        return match?.groups?.get(1)?.value
    }

    private fun VocabularyItemDto.toEntity(): VocabularyItemEntity {
        return VocabularyItemEntity(
            partOfSpeech = partOfSpeech,
            wordEnglish = wordEnglish,
            wordChinese = wordChinese,
            definitionEnglish = definitionEnglish,
            definitionChinese = definitionChinese,
            exampleEnglish = exampleEnglish,
            exampleChinese = exampleChinese,
            pronunciation = null
        )
    }

    private fun GrammarItemDto.toEntity(): GrammarItemEntity {
        return GrammarItemEntity(
            ruleEnglish = ruleEnglish,
            explanationEnglish = explanationEnglish,
            explanationChinese = explanationChinese,
            exampleEnglish = exampleEnglish,
            exampleChinese = exampleChinese
        )
    }

    private fun SentencePatternDto.toEntity(): SentencePatternEntity {
        return SentencePatternEntity(
            patternEnglish = patternEnglish,
            explanationEnglish = explanationEnglish,
            explanationChinese = explanationChinese,
            exampleEnglish = exampleEnglish,
            exampleChinese = exampleChinese
        )
    }

    private fun PhraseIdiomDto.toEntity(): PhraseIdiomEntity {
        return PhraseIdiomEntity(
            phraseEnglish = phraseEnglish,
            explanationEnglish = explanationEnglish,
            explanationChinese = explanationChinese,
            exampleEnglish = exampleEnglish,
            exampleChinese = exampleChinese
        )
    }

    private fun ComprehensionQuestionDto.toEntity(index: Int): ComprehensionQuestionEntity {
        return ComprehensionQuestionEntity(
            id = (index + 1).toString(),
            questionEnglish = questionEnglish,
            questionChinese = questionChinese,
            options = options.toSortedMap().values.toList(),
            correctAnswerIndex = answerKeyLetterToIndex(),
            correctAnswerKey = answerKey,
            explanationEnglish = explanationEnglish,
            explanationChinese = explanationChinese
        )
    }

    private fun VocabularyItemEntity.toDomain(): GlossaryItem {
        return GlossaryItem(
            word = wordEnglish,
            partOfSpeech = partOfSpeech,
            translation = wordChinese ?: definitionChinese ?: wordEnglish,
            pronunciation = pronunciation,
            definitionEnglish = definitionEnglish,
            definitionChinese = definitionChinese,
            exampleEnglish = exampleEnglish,
            exampleChinese = exampleChinese,
            audioUrl = null
        )
    }

    private fun GrammarItemEntity.toDomain(): GrammarPoint {
        return GrammarPoint(
            rule = ruleEnglish,
            explanationEnglish = explanationEnglish,
            explanationChinese = explanationChinese,
            exampleEnglish = exampleEnglish,
            exampleChinese = exampleChinese
        )
    }

    private fun SentencePatternEntity.toDomain(): SentencePattern {
        return SentencePattern(
            patternEnglish = patternEnglish,
            explanationEnglish = explanationEnglish,
            explanationChinese = explanationChinese,
            exampleEnglish = exampleEnglish,
            exampleChinese = exampleChinese
        )
    }

    private fun PhraseIdiomEntity.toDomain(): PhraseIdiom {
        return PhraseIdiom(
            phraseEnglish = phraseEnglish,
            explanationEnglish = explanationEnglish,
            explanationChinese = explanationChinese,
            exampleEnglish = exampleEnglish,
            exampleChinese = exampleChinese
        )
    }

    private fun ComprehensionQuestionEntity.toDomain(): QuizQuestion {
        return QuizQuestion(
            id = id,
            question = questionEnglish,
            options = options,
            correctAnswerIndex = correctAnswerIndex,
            correctAnswerKey = correctAnswerKey,
            explanation = explanationChinese ?: explanationEnglish.orEmpty()
        )
    }

    private fun Map<String, String>.toSortedMap(): Map<String, String> {
        return entries.sortedBy { it.key }.associate { it.key to it.value }
    }

    private fun ComprehensionQuestionDto.answerKeyLetterToIndex(): Int {
        val normalized = answerKey.trim().uppercase()
        return normalized.firstOrNull()?.let { letter -> letter - 'A' } ?: -1
    }

    fun serializeVocabulary(items: List<VocabularyItemDto>?): String? = null
    fun serializeGrammar(items: List<GrammarItemDto>?): String? = null
    fun serializeSentencePatterns(items: List<SentencePatternDto>?): String? = null
    fun serializePhrases(items: List<PhraseIdiomDto>?): String? = null
    fun serializeQuiz(items: List<ComprehensionQuestionDto>?): String? = null
}
