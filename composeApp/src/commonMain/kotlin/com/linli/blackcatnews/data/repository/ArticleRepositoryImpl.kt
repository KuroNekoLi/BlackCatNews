package com.linli.blackcatnews.data.repository

import com.linli.blackcatnews.data.local.dao.ArticleDao
import com.linli.blackcatnews.data.local.entity.ArticleEntity
import com.linli.blackcatnews.data.mapper.ArticleMapper
import com.linli.blackcatnews.data.remote.api.NewsApiService
import com.linli.blackcatnews.domain.model.ArticleDetail
import com.linli.blackcatnews.domain.model.ArticleSection
import com.linli.blackcatnews.domain.model.NewsItem
import com.linli.blackcatnews.domain.repository.ArticleRepository
import com.linli.blackcatnews.domain.repository.Result
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

/**
 * 文章數據倉庫實作
 *
 * 採用 SSOT（Single Source of Truth）原則：
 * - 所有 UI 均從 Room Database 讀取數據
 * - 遠端 API 僅負責更新本地數據庫
 * - 透過 Flow 建立響應式資料流，確保 UI 能即時收到數據更新
 *
 * @property newsApiService 新聞 API 服務
 * @property articleDao 文章資料存取物件
 * @property ioDispatcher 用於資料操作的協程派發器
 */
class ArticleRepositoryImpl(
    private val newsApiService: NewsApiService,
    private val articleDao: ArticleDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ArticleRepository {

    override fun getRandomArticles(
        count: Int,
        section: ArticleSection?,
        forceRefresh: Boolean
    ): Flow<Result<List<NewsItem>>> {
        val targetSectionValue = section?.value
        val normalizedCount = count.normalize()
        val databaseFlow = if (targetSectionValue == null) {
            articleDao.getAllArticles()
        } else {
            articleDao.getArticlesBySection(targetSectionValue, normalizedCount)
        }

        return databaseFlow
            .map { entities ->
                val limitedEntities = if (targetSectionValue == null) {
                    entities.take(normalizedCount)
                } else {
                    entities
                }
                mapEntitiesToNewsResult(limitedEntities)
            }
            .onStart {
                emit(Result.Loading)
                val shouldRefresh = forceRefresh || !hasLocalArticles(section)
                if (shouldRefresh) {
                    when (val refreshResult = performRefresh(section, normalizedCount)) {
                        is Result.Error -> emit(
                            Result.Error(
                                refreshResult.exception,
                                refreshResult.message
                            )
                        )

                        else -> Unit
                    }
                }
            }
            .catch { throwable ->
                if (throwable is CancellationException) throw throwable
                emit(Result.Error(throwable, throwable.message ?: "無法取得文章列表"))
            }
    }

    override fun getArticlesBySection(
        section: ArticleSection,
        count: Int,
        forceRefresh: Boolean
    ): Flow<Result<List<NewsItem>>> {
        return getRandomArticles(count = count, section = section, forceRefresh = forceRefresh)
    }

    override fun getArticleDetail(articleId: String): Flow<Result<ArticleDetail>> {
        return articleDao.getArticleById(articleId)
            .map { entity ->
                if (entity != null) {
                    Result.Success(ArticleMapper.entityToArticleDetail(entity))
                } else {
                    Result.Error(NoSuchElementException("找不到文章：$articleId"), "找不到指定文章")
                }
            }
            .onStart { emit(Result.Loading) }
            .catch { throwable ->
                if (throwable is CancellationException) throw throwable
                emit(Result.Error(throwable, throwable.message ?: "無法取得文章詳情"))
            }
    }

    override suspend fun refreshArticles(section: ArticleSection?, count: Int): Result<Unit> {
        return performRefresh(section, count.normalize())
    }

    override fun getFavoriteArticles(): Flow<Result<List<NewsItem>>> {
        return articleDao.getFavoriteArticles()
            .map { entities -> mapEntitiesToNewsResult(entities) }
            .onStart { emit(Result.Loading) }
            .catch { throwable ->
                if (throwable is CancellationException) throw throwable
                emit(Result.Error(throwable, throwable.message ?: "無法取得收藏文章"))
            }
    }

    override suspend fun addToFavorites(articleId: String): Result<Unit> {
        return withContext(ioDispatcher) {
            try {
                val existingEntity = articleDao.getArticleByIdOnce(articleId)
                    ?: return@withContext Result.Error(
                        IllegalArgumentException("文章不存在：$articleId"),
                        "找不到指定文章"
                    )

                articleDao.updateFavoriteStatus(
                    id = existingEntity.id,
                    isFavorite = true,
                    updatedAt = Clock.System.now().toEpochMilliseconds()
                )
                Result.Success(Unit)
            } catch (throwable: Throwable) {
                Result.Error(throwable, throwable.message ?: "無法加入收藏")
            }
        }
    }

    override suspend fun removeFromFavorites(articleId: String): Result<Unit> {
        return withContext(ioDispatcher) {
            try {
                val existingEntity = articleDao.getArticleByIdOnce(articleId)
                    ?: return@withContext Result.Error(
                        IllegalArgumentException("文章不存在：$articleId"),
                        "找不到指定文章"
                    )

                articleDao.updateFavoriteStatus(
                    id = existingEntity.id,
                    isFavorite = false,
                    updatedAt = Clock.System.now().toEpochMilliseconds()
                )
                Result.Success(Unit)
            } catch (throwable: Throwable) {
                Result.Error(throwable, throwable.message ?: "無法取消收藏")
            }
        }
    }

    override suspend fun clearCache(): Result<Unit> {
        return withContext(ioDispatcher) {
            try {
                articleDao.deleteAllArticles()
                Result.Success(Unit)
            } catch (throwable: Throwable) {
                Result.Error(throwable, throwable.message ?: "無法清除緩存")
            }
        }
    }

    /**
     * 將資料表實體轉換為新聞列表結果
     */
    private fun mapEntitiesToNewsResult(entities: List<ArticleEntity>): Result<List<NewsItem>> {
        val items = entities.map { entity -> ArticleMapper.entityToNewsItem(entity) }
        return Result.Success(items)
    }

    /**
     * 檢查本地是否已有對應分類的文章
     */
    private suspend fun hasLocalArticles(section: ArticleSection?): Boolean {
        return withContext(ioDispatcher) {
            if (section == null) {
                articleDao.getArticleCount() > 0
            } else {
                articleDao.getArticleCountBySection(section.value) > 0
            }
        }
    }

    /**
     * 執行遠端同步並更新本地資料庫
     */
    private suspend fun performRefresh(section: ArticleSection?, count: Int): Result<Unit> {
        return withContext(ioDispatcher) {
            try {
                val fetchCount = count.normalize()
                val dtoList = newsApiService.getRandomArticles(
                    count = fetchCount,
                    section = section?.value
                )

                if (dtoList.isEmpty()) {
                    return@withContext Result.Success(Unit)
                }

                val entities = dtoList.map { dto ->
                    val existingEntity = articleDao.getArticleByIdOnce(dto.id.toString())
                    ArticleMapper.dtoToEntity(dto, existingEntity)
                }

                articleDao.insertArticles(entities)

                Result.Success(Unit)
            } catch (throwable: Throwable) {
                Result.Error(throwable, throwable.message ?: "遠端同步失敗")
            }
        }
    }

    /**
     * 確保抓取數量有意義
     */
    private fun Int.normalize(): Int = if (this <= 0) DEFAULT_FETCH_COUNT else this

    companion object {
        private const val DEFAULT_FETCH_COUNT: Int = 10
    }
}
