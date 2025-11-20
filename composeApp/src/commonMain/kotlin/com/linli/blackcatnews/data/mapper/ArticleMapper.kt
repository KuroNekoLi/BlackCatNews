package com.linli.blackcatnews.data.mapper

import com.linli.blackcatnews.data.local.entity.ArticleEntity
import com.linli.blackcatnews.data.local.entity.ComprehensionQuestionEntity
import com.linli.blackcatnews.data.local.entity.GrammarItemEntity
import com.linli.blackcatnews.data.local.entity.PhraseIdiomEntity
import com.linli.blackcatnews.data.local.entity.SentencePatternEntity
import com.linli.blackcatnews.data.local.entity.VocabularyItemEntity
import com.linli.blackcatnews.data.remote.dto.AiArticleDto
import com.linli.blackcatnews.data.remote.dto.ComprehensionQuestionDto
import com.linli.blackcatnews.data.remote.dto.McqOptionDto
import com.linli.blackcatnews.data.remote.dto.GrammarItemDto
import com.linli.blackcatnews.data.remote.dto.PhraseIdiomDto
import com.linli.blackcatnews.data.remote.dto.SentencePatternDto
import com.linli.blackcatnews.data.remote.dto.VocabularyItemDto
import com.linli.blackcatnews.domain.model.ArticleDetail
import com.linli.blackcatnews.domain.model.BilingualContent
import com.linli.blackcatnews.domain.model.BilingualParagraph
import com.linli.blackcatnews.domain.model.BilingualParagraphType
import com.linli.blackcatnews.domain.model.BilingualText
import com.linli.blackcatnews.domain.model.GlossaryItem
import com.linli.blackcatnews.domain.model.GrammarPoint
import com.linli.blackcatnews.domain.model.NewsCategory
import com.linli.blackcatnews.domain.model.NewsItem
import com.linli.blackcatnews.domain.model.PhraseIdiom
import com.linli.blackcatnews.domain.model.Quiz
import com.linli.blackcatnews.domain.model.QuizQuestion
import com.linli.blackcatnews.domain.model.SentencePattern
import com.linli.blackcatnews.model.Block
import com.linli.blackcatnews.utils.decodeHtmlEntities
import com.linli.blackcatnews.utils.parseHtmlToArticle
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * 文章資料映射器
 *
 * 負責處理不同層之間的資料轉換：
 * - DTO ↔ Entity（遠端 ↔ 本地資料庫）
 * - Entity ↔ Domain（本地資料庫 ↔ 領域模型）
 */
object ArticleMapper {

    /**
     * 將遠端 DTO 轉換為 Room Entity，並保留既有收藏與建立時間
     */
    @OptIn(ExperimentalTime::class)
    fun dtoToEntity(dto: AiArticleDto, existing: ArticleEntity? = null): ArticleEntity {
        val now = Clock.System.now().toEpochMilliseconds()
        val createdAt = existing?.createdAt ?: now
        val summaryText = dto.summaryFrom?.takeIf { it.isNotBlank() }
            ?: dto.summaryEnglishLegacy?.takeIf { it.isNotBlank() }
            ?: dto.content?.cleanedTextFrom?.lineSequence()?.firstOrNull()?.take(200)?.trim()
                .orEmpty()

        val normalizedChineseContent = dto.content?.optimizedHtmlTo?.takeIf { it.isNotBlank() }
            ?: dto.content?.cleanedTextTo

        return ArticleEntity(
            id = dto.id.toString(),
            title = (dto.titleFrom ?: dto.title ?: "").decodeHtmlEntities(),
            titleZh = (dto.titleTo ?: dto.titleZh ?: dto.title)?.decodeHtmlEntities(),
            summary = summaryText.decodeHtmlEntities(),
            summaryZh = (dto.summaryTo ?: dto.summaryChineseLegacy)?.decodeHtmlEntities(),
            contentHtml = dto.content?.optimizedHtmlFrom ?: dto.contentHtml.orEmpty(),
            cleanedText = dto.content?.cleanedTextFrom.orEmpty(),
            cleanedTextZh = normalizedChineseContent,
            imageUrl = dto.contentHtml?.let { extractHeroImage(it) },
            sourceName = dto.sourceName.orEmpty(),
            publishTime = dto.publishedAt.orEmpty(),
            section = dto.section.orEmpty(),
            url = dto.originalUrl,
            language = dto.fromLanguage,
            learningVocabulary = dto.content?.explanation?.vocabulary?.map { it.toEntity() }
                ?: emptyList(),
            learningGrammar = dto.content?.explanation?.grammar?.map { it.toEntity() } ?: emptyList(),
            learningSentencePatterns = dto.content?.explanation?.sentencePatterns?.map { it.toEntity() }
                ?: emptyList(),
            learningPhrases = dto.content?.explanation?.phrasesIdioms?.map { it.toEntity() }
                ?: emptyList(),
            learningQuiz = dto.content?.explanation?.comprehensionMcq?.mapIndexed { index, dtoQuestion ->
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
            titleZh = entity.titleZh,
            summary = entity.summary,
            summaryZh = entity.summaryZh,
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
        val englishHtml = entity.contentHtml
        val chineseHtml = entity.cleanedTextZh?.takeIf { it.contains("<p") }
        val englishArticleData = parseHtmlToArticle(englishHtml)
        val chineseArticleData = chineseHtml?.let { parseHtmlToArticle(it) }
        val fallbackChineseSegments = if (chineseArticleData == null) {
            createFallbackChineseSegments(entity.cleanedTextZh)
        } else {
            emptyList()
        }
        val maxBlocks = maxOf(
            englishArticleData.blocks.size,
            chineseArticleData?.blocks?.size ?: 0
        )
        val paragraphs = buildList {
            for (order in 0 until maxBlocks) {
                val englishBlock = englishArticleData.blocks.getOrNull(order)
                val chineseBlock = chineseArticleData?.blocks?.getOrNull(order)
                createBilingualParagraph(
                    englishBlock = englishBlock,
                    chineseBlock = chineseBlock,
                    fallbackChineseSegments = fallbackChineseSegments,
                    fallbackIndex = order
                )?.let(::add)
            }
        }
        return ArticleDetail(
            id = entity.id,
            title = BilingualText(
                english = entity.title.decodeHtmlEntities(),
                chinese = (entity.titleZh ?: entity.title).decodeHtmlEntities()
            ),
            summary = BilingualText(
                english = entity.summary.decodeHtmlEntities(),
                chinese = (entity.summaryZh ?: entity.summary).decodeHtmlEntities()
            ),
            content = BilingualContent(
                paragraphs = paragraphs
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
        val summaryText = dto.summaryFrom?.takeIf { it.isNotBlank() }
            ?: dto.summaryEnglishLegacy?.takeIf { it.isNotBlank() }
            ?: dto.content?.cleanedTextFrom?.lineSequence()?.firstOrNull()?.take(200)?.trim()
                .orEmpty()

        return NewsItem(
            id = dto.id.toString(),
            title = (dto.titleFrom ?: dto.title ?: "").decodeHtmlEntities(),
            titleZh = (dto.titleTo ?: dto.titleZh ?: dto.title)?.decodeHtmlEntities(),
            summary = summaryText.decodeHtmlEntities(),
            summaryZh = (dto.summaryTo ?: dto.summaryChineseLegacy)?.decodeHtmlEntities(),
            imageUrl = dto.contentHtml?.let { extractHeroImage(it) },
            source = dto.sourceName.orEmpty(),
            publishTime = dto.publishedAt.orEmpty(),
            category = mapSectionToCategory(dto.section)
        )
    }

    // region 私有輔助邏輯

    private fun mapSectionToCategory(section: String?): NewsCategory {
        return when (section?.lowercase()) {
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

    private fun extractHeroImage(contentHtml: String): String? {
        // 簡易解析，尋找第一個 img src
        val regex = Regex("<img[^>]*src=\"([^\"]+)\"")
        val match = regex.find(contentHtml)
        return match?.groups?.get(1)?.value
    }

    private fun VocabularyItemDto.toEntity(): VocabularyItemEntity {
        return VocabularyItemEntity(
            partOfSpeech = partOfSpeech,
            wordEnglish = wordFrom,
            wordChinese = wordTo,
            definitionEnglish = definitionFrom,
            definitionChinese = definitionTo,
            exampleEnglish = exampleFrom,
            exampleChinese = exampleTo,
            pronunciation = null
        )
    }

    private fun GrammarItemDto.toEntity(): GrammarItemEntity {
        return GrammarItemEntity(
            ruleEnglish = ruleFrom,
            explanationEnglish = explanationFrom,
            explanationChinese = explanationTo,
            exampleEnglish = exampleFrom,
            exampleChinese = exampleTo
        )
    }

    private fun SentencePatternDto.toEntity(): SentencePatternEntity {
        return SentencePatternEntity(
            patternEnglish = patternFrom,
            explanationEnglish = explanationFrom,
            explanationChinese = explanationTo,
            exampleEnglish = exampleFrom,
            exampleChinese = exampleTo
        )
    }

    private fun PhraseIdiomDto.toEntity(): PhraseIdiomEntity {
        return PhraseIdiomEntity(
            phraseEnglish = phraseFrom,
            explanationEnglish = explanationFrom,
            explanationChinese = explanationTo,
            exampleEnglish = exampleFrom,
            exampleChinese = exampleTo
        )
    }

    private fun ComprehensionQuestionDto.toEntity(index: Int): ComprehensionQuestionEntity {
        return ComprehensionQuestionEntity(
            id = (index + 1).toString(),
            questionEnglish = questionFrom,
            questionChinese = questionTo,
            options = options.toSortedList().mapNotNull { it.second },
            correctAnswerIndex = answerKeyLabelToIndex(),
            correctAnswerKey = answerKey ?: "",
            explanationEnglish = explanationFrom,
            explanationChinese = explanationTo
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
            questionEnglish = questionEnglish,
            questionChinese = questionChinese,
            options = options,
            correctAnswerIndex = correctAnswerIndex,
            correctAnswerKey = correctAnswerKey,
            explanationEnglish = explanationEnglish,
            explanationChinese = explanationChinese
        )
    }

    private fun List<McqOptionDto>.toSortedList(): List<Pair<String, String?>> {
        return sortedBy { it.label }.map { option ->
            option.label to (option.textTo ?: option.textFrom)
        }
    }

    private fun ComprehensionQuestionDto.answerKeyLabelToIndex(): Int {
        val normalized = answerKey?.trim()?.uppercase() ?: ""
        val sortedLabels = options.map { it.label.uppercase() }.sorted()
        return sortedLabels.indexOf(normalized)
    }

    fun serializeVocabulary(items: List<VocabularyItemDto>?): String? = null
    fun serializeGrammar(items: List<GrammarItemDto>?): String? = null
    fun serializeSentencePatterns(items: List<SentencePatternDto>?): String? = null
    fun serializePhrases(items: List<PhraseIdiomDto>?): String? = null
    fun serializeQuiz(items: List<ComprehensionQuestionDto>?): String? = null

    private fun createFallbackChineseSegments(cleanedTextZh: String?): List<String> {
        if (cleanedTextZh.isNullOrBlank()) return emptyList()

        return if (cleanedTextZh.contains("<p")) {
            val regex = Regex("<p[^>]*>(.*?)</p>", RegexOption.IGNORE_CASE)
            val matches = regex.findAll(cleanedTextZh)
            matches.mapNotNull { matchResult ->
                val paragraphContent = matchResult.groups[1]?.value
                paragraphContent
                    ?.replace(Regex("<[^>]+>"), "")
                    ?.decodeHtmlEntities()
                    ?.trim()
                    ?.takeIf { it.isNotEmpty() }
            }.toList()
        } else {
            cleanedTextZh.split(Regex("\n{2,}"))
                .mapNotNull { it.trim().takeIf(String::isNotEmpty) }
        }
    }

    private fun createBilingualParagraph(
        englishBlock: Block?,
        chineseBlock: Block?,
        fallbackChineseSegments: List<String>,
        fallbackIndex: Int
    ): BilingualParagraph? {
        return when {
            englishBlock != null -> mapEnglishBlock(
                englishBlock = englishBlock,
                chineseBlock = chineseBlock,
                fallbackChineseSegments = fallbackChineseSegments,
                index = fallbackIndex
            )

            chineseBlock != null -> mapChineseOnlyBlock(
                chineseBlock = chineseBlock,
                fallbackChineseSegments = fallbackChineseSegments,
                index = fallbackIndex
            )

            else -> null
        }
    }

    private fun mapEnglishBlock(
        englishBlock: Block,
        chineseBlock: Block?,
        fallbackChineseSegments: List<String>,
        index: Int
    ): BilingualParagraph? {
        return when (englishBlock) {
            is Block.Paragraph -> {
                val chineseText = (chineseBlock as? Block.Paragraph)?.text?.decodeHtmlEntities()
                    ?: fallbackChineseSegments.getOrNull(index)
                BilingualParagraph(
                    type = BilingualParagraphType.TEXT,
                    english = englishBlock.text.decodeHtmlEntities(),
                    chinese = chineseText,
                    order = index,
                    originalHtml = englishBlock.originalHtml
                    // Placeholder for future: summary for list items if desired
                )
            }

            is Block.Heading -> {
                val chineseText = (chineseBlock as? Block.Heading)?.text?.decodeHtmlEntities()
                    ?: fallbackChineseSegments.getOrNull(index)
                BilingualParagraph(
                    type = BilingualParagraphType.HEADING,
                    english = englishBlock.text.decodeHtmlEntities(),
                    chinese = chineseText,
                    order = index,
                    headingLevel = englishBlock.level,
                    originalHtml = englishBlock.originalHtml
                )
            }

            is Block.ImageBlock -> {
                BilingualParagraph(
                    type = BilingualParagraphType.IMAGE,
                    order = index,
                    imageUrl = englishBlock.src,
                    imageAlt = englishBlock.alt?.decodeHtmlEntities(),
                    imageCaption = englishBlock.caption?.decodeHtmlEntities(),
                    originalHtml = englishBlock.originalHtml
                )
            }

            is Block.UnorderedList -> {
                val chineseItems = (chineseBlock as? Block.UnorderedList)?.items?.map {
                    it.decodeHtmlEntities()
                } ?: emptyList()
                BilingualParagraph(
                    type = BilingualParagraphType.UNORDERED_LIST,
                    order = index,
                    listItems = englishBlock.items.map { it.decodeHtmlEntities() },
                    listItemsChinese = chineseItems,
                    originalHtml = englishBlock.originalHtml
                )
            }

            is Block.OrderedList -> {
                val chineseItems = (chineseBlock as? Block.OrderedList)?.items?.map {
                    it.decodeHtmlEntities()
                } ?: emptyList()
                BilingualParagraph(
                    type = BilingualParagraphType.ORDERED_LIST,
                    order = index,
                    listItems = englishBlock.items.map { it.decodeHtmlEntities() },
                    listItemsChinese = chineseItems,
                    originalHtml = englishBlock.originalHtml
                )
            }

            is Block.HtmlFallback -> BilingualParagraph(
                type = BilingualParagraphType.HTML_FALLBACK,
                order = index,
                originalHtml = englishBlock.html
            )

        }
    }

    private fun mapChineseOnlyBlock(
        chineseBlock: Block,
        fallbackChineseSegments: List<String>,
        index: Int
    ): BilingualParagraph? {
        return when (chineseBlock) {
            is Block.Paragraph -> BilingualParagraph(
                type = BilingualParagraphType.TEXT,
                english = fallbackChineseSegments.getOrNull(index),
                chinese = chineseBlock.text.decodeHtmlEntities(),
                order = index,
                originalHtml = chineseBlock.originalHtml
            )

            is Block.Heading -> BilingualParagraph(
                type = BilingualParagraphType.HEADING,
                english = fallbackChineseSegments.getOrNull(index),
                chinese = chineseBlock.text.decodeHtmlEntities(),
                order = index,
                headingLevel = chineseBlock.level,
                originalHtml = chineseBlock.originalHtml
            )

            is Block.ImageBlock -> BilingualParagraph(
                type = BilingualParagraphType.IMAGE,
                order = index,
                imageUrl = chineseBlock.src,
                imageAlt = chineseBlock.alt?.decodeHtmlEntities(),
                imageCaption = chineseBlock.caption?.decodeHtmlEntities(),
                originalHtml = chineseBlock.originalHtml
            )

            is Block.UnorderedList -> BilingualParagraph(
                type = BilingualParagraphType.UNORDERED_LIST,
                order = index,
                listItems = emptyList(),
                listItemsChinese = chineseBlock.items.map { it.decodeHtmlEntities() },
                originalHtml = chineseBlock.originalHtml
            )

            is Block.OrderedList -> BilingualParagraph(
                type = BilingualParagraphType.ORDERED_LIST,
                order = index,
                listItems = emptyList(),
                listItemsChinese = chineseBlock.items.map { it.decodeHtmlEntities() },
                originalHtml = chineseBlock.originalHtml
            )

            is Block.HtmlFallback -> BilingualParagraph(
                type = BilingualParagraphType.HTML_FALLBACK,
                order = index,
                originalHtml = chineseBlock.html
            )

            else -> null
        }
    }
}
