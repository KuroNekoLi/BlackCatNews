package com.linli.blackcatnews.data

import com.linli.blackcatnews.data.mapper.ArticleMapper
import com.linli.blackcatnews.data.remote.dto.AiArticlesResponseDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json

class ArticleMapperTest {

    private val jsonFormatter: Json = Json {
        ignoreUnknownKeys = true
    }

    @Test
    fun `mock response should map to ArticleDetail`() {
        val response: AiArticlesResponseDto = jsonFormatter.decodeFromString(
            MockData.mockResponse
        )

        assertTrue(response.articles.isNotEmpty(), "Mock response should contain articles")

        val firstArticleDto = response.articles.first()
        val entity = ArticleMapper.dtoToEntity(firstArticleDto)
        val detail = ArticleMapper.entityToArticleDetail(entity)

        assertEquals(firstArticleDto.title, detail.title.english)
        assertEquals(firstArticleDto.sourceName, detail.source)
        assertTrue(detail.content.paragraphs.isNotEmpty(), "Article content should have paragraphs")
    }
}
