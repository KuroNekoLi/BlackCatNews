package com.linli.blackcatnews.data.mapper

import com.linli.blackcatnews.data.local.entity.ArticleEntity
import com.linli.blackcatnews.data.remote.dto.*
import com.linli.blackcatnews.domain.model.*
import kotlinx.datetime.Clock

/**
 * 文章數據映射器
 *
 * 負責在不同層之間進行數據轉換：
 * - DTO (Data Transfer Object) → Entity (Database)
 * - Entity (Database) → Domain (Business Logic)
 * - DTO → Domain（用於快速展示）
 *
 * 遵循單向數據流和分層架構原則
 */
object ArticleMapper {

    /**
     * 將 API 響應 DTO 轉換為數據庫 Entity
     *
     * @param dto API 響應的數據傳輸對象
     * @param isFavorite 是否為收藏（默認 false）
     * @return ArticleEntity 數據庫實體
     */
    fun dtoToEntity(dto: AiArticleDto, isFavorite: Boolean = false): ArticleEntity {
        val currentTime = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        return ArticleEntity(
            id = dto.id,
            title = dto.title,
            summary = dto.summary,
            content = dto.content,
            imageUrl = dto.imageUrl,
            source = dto.source,
            publishTime = dto.publishTime,
            section = dto.section,
            url = dto.url,
            language = dto.language,

            // 雙語內容
            translatedTitle = dto.translatedTitle,
            translatedSummary = dto.translatedSummary,
            translatedContent = dto.translatedContent,

            // 學習功能 - 暫時存為 null，後續可以實現 JSON 序列化
            glossary = mapGlossaryToJson(dto.glossary),
            grammarPoints = mapGrammarPointsToJson(dto.grammarPoints),
            quiz = mapQuizToJson(dto.quiz),

            // 元數據
            isFavorite = isFavorite,
            createdAt = currentTime,
            updatedAt = currentTime
        )
    }

    /**
     * 將數據庫 Entity 轉換為領域層 NewsItem（列表項）
     *
     * @param entity 數據庫實體
     * @return NewsItem 領域模型
     */
    fun entityToNewsItem(entity: ArticleEntity): NewsItem {
        return NewsItem(
            id = entity.id,
            title = entity.title,
            summary = entity.summary,
            imageUrl = entity.imageUrl,
            source = entity.source,
            publishTime = entity.publishTime,
            category = mapSectionToCategory(entity.section)
        )
    }

    /**
     * 將數據庫 Entity 轉換為領域層 ArticleDetail（詳情）
     *
     * @param entity 數據庫實體
     * @return ArticleDetail 領域模型
     */
    fun entityToArticleDetail(entity: ArticleEntity): ArticleDetail {
        // 構建雙語內容（段落級別）
        val paragraphs = splitContentToParagraphs(
            englishContent = entity.content,
            chineseContent = entity.translatedContent
        )

        return ArticleDetail(
            id = entity.id,
            title = BilingualText(
                english = entity.title,
                chinese = entity.translatedTitle ?: entity.title
            ),
            summary = BilingualText(
                english = entity.summary,
                chinese = entity.translatedSummary ?: entity.summary
            ),
            content = BilingualContent(paragraphs = paragraphs),
            imageUrl = entity.imageUrl,
            source = entity.source,
            publishTime = entity.publishTime,
            category = mapSectionToCategory(entity.section),
            glossary = parseJsonToGlossary(entity.glossary),
            grammarPoints = parseJsonToGrammarPoints(entity.grammarPoints),
            quiz = parseJsonToQuiz(entity.quiz)
        )
    }

    /**
     * 將 DTO 直接轉換為 Domain NewsItem（用於快速展示）
     *
     * @param dto API 響應的數據傳輸對象
     * @return NewsItem 領域模型
     */
    fun dtoToNewsItem(dto: AiArticleDto): NewsItem {
        return NewsItem(
            id = dto.id,
            title = dto.title,
            summary = dto.summary,
            imageUrl = dto.imageUrl,
            source = dto.source,
            publishTime = dto.publishTime,
            category = mapSectionToCategory(dto.section)
        )
    }

    /**
     * 將 DTO 直接轉換為 Domain ArticleDetail
     *
     * @param dto API 響應的數據傳輸對象
     * @return ArticleDetail 領域模型
     */
    fun dtoToArticleDetail(dto: AiArticleDto): ArticleDetail {
        val paragraphs = splitContentToParagraphs(
            englishContent = dto.content,
            chineseContent = dto.translatedContent
        )

        return ArticleDetail(
            id = dto.id,
            title = BilingualText(
                english = dto.title,
                chinese = dto.translatedTitle ?: dto.title
            ),
            summary = BilingualText(
                english = dto.summary,
                chinese = dto.translatedSummary ?: dto.summary
            ),
            content = BilingualContent(paragraphs = paragraphs),
            imageUrl = dto.imageUrl,
            source = dto.source,
            publishTime = dto.publishTime,
            category = mapSectionToCategory(dto.section),
            glossary = dto.glossary?.map { it.toDomain() } ?: emptyList(),
            grammarPoints = dto.grammarPoints?.map { it.toDomain() } ?: emptyList(),
            quiz = dto.quiz?.toDomain()
        )
    }

    // ==================== 私有輔助方法 ====================

    /**
     * 將 section 字符串映射為 NewsCategory 枚舉
     */
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

    /**
     * 將英文和中文內容分割為段落
     */
    private fun splitContentToParagraphs(
        englishContent: String?,
        chineseContent: String?
    ): List<BilingualParagraph> {
        if (englishContent.isNullOrBlank()) return emptyList()

        val englishParagraphs = englishContent.split("\n\n").filter { it.isNotBlank() }
        val chineseParagraphs =
            chineseContent?.split("\n\n")?.filter { it.isNotBlank() } ?: emptyList()

        return englishParagraphs.mapIndexed { index, english ->
            BilingualParagraph(
                english = english.trim(),
                chinese = chineseParagraphs.getOrNull(index)?.trim() ?: "",
                order = index
            )
        }
    }

    /**
     * 將 Glossary 列表轉換為 JSON 字符串（簡化版）
     */
    private fun mapGlossaryToJson(glossary: List<GlossaryItemDto>?): String? {
        if (glossary.isNullOrEmpty()) return null
        // TODO: 使用 kotlinx.serialization
        return glossary.joinToString(",") { item ->
            """{"word":"${item.word}","translation":"${item.translation}","pronunciation":"${item.pronunciation}","example":"${item.example}"}"""
        }
    }

    /**
     * 將 GrammarPoints 列表轉換為 JSON 字符串（簡化版）
     */
    private fun mapGrammarPointsToJson(grammarPoints: List<GrammarPointDto>?): String? {
        if (grammarPoints.isNullOrEmpty()) return null
        // TODO: 使用 kotlinx.serialization
        return null
    }

    /**
     * 將 Quiz 轉換為 JSON 字符串（簡化版）
     */
    private fun mapQuizToJson(quiz: QuizDto?): String? {
        if (quiz == null) return null
        // TODO: 使用 kotlinx.serialization
        return null
    }

    /**
     * 從 JSON 字符串解析 Glossary 列表
     */
    private fun parseJsonToGlossary(json: String?): List<GlossaryItem> {
        if (json.isNullOrBlank()) return emptyList()
        // TODO: 實現 JSON 解析
        return emptyList()
    }

    /**
     * 從 JSON 字符串解析 GrammarPoints 列表
     */
    private fun parseJsonToGrammarPoints(json: String?): List<GrammarPoint> {
        if (json.isNullOrBlank()) return emptyList()
        // TODO: 實現 JSON 解析
        return emptyList()
    }

    /**
     * 從 JSON 字符串解析 Quiz
     */
    private fun parseJsonToQuiz(json: String?): Quiz? {
        if (json.isNullOrBlank()) return null
        // TODO: 實現 JSON 解析
        return null
    }

    // ==================== DTO 到 Domain 擴展函數 ====================

    /**
     * 將 GlossaryItemDto 轉換為 Domain 模型
     */
    private fun GlossaryItemDto.toDomain() = GlossaryItem(
        word = word,
        translation = translation,
        pronunciation = pronunciation,
        example = example,
        audioUrl = audioUrl
    )

    /**
     * 將 GrammarPointDto 轉換為 Domain 模型
     */
    private fun GrammarPointDto.toDomain() = GrammarPoint(
        title = title,
        explanation = explanation,
        examples = examples
    )

    /**
     * 將 QuizDto 轉換為 Domain 模型
     */
    private fun QuizDto.toDomain() = Quiz(
        questions = questions.map { it.toDomain() }
    )

    /**
     * 將 QuizQuestionDto 轉換為 Domain 模型
     */
    private fun QuizQuestionDto.toDomain() = QuizQuestion(
        id = id,
        question = question,
        options = options,
        correctAnswer = correctAnswer,
        explanation = explanation
    )
}
