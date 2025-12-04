package com.linli.dictionary.data.repository

import com.linli.dictionary.data.local.dao.DictionaryDao
import com.linli.dictionary.data.local.entity.EntryEntity
import com.linli.dictionary.data.local.entity.WordEntity
import com.linli.dictionary.data.mapper.toDomain
import com.linli.dictionary.data.remote.DictionaryApi
import com.linli.dictionary.domain.model.ReviewCard
import com.linli.dictionary.domain.model.ReviewMetadata
import com.linli.dictionary.domain.model.ReviewState
import com.linli.dictionary.domain.model.Word
import com.linli.dictionary.domain.repository.WordBankRepository
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import com.linli.dictionary.data.local.entity.ReviewState as EntityReviewState

/**
 * WordBankRepository 的實現類，負責管理用戶的單字庫。
 * 通過在 WordEntity 上添加 isInWordBank 標記來區別單字是否存在於單字庫中。
 */
@OptIn(ExperimentalTime::class)
class WordBankRepositoryImpl(
    private val api: DictionaryApi,
    private val dao: DictionaryDao
) : WordBankRepository {

    private val json = Json { ignoreUnknownKeys = true }

    /**
     * 獲取使用者單字庫中所有的單字。
     *
     * @return 儲存在單字庫中的單字列表。
     */
    override suspend fun getSavedWords(): List<Word> {
        return dao.getWordsInWordBank().map { entity ->
            entity.toDomainWord()
        }
    }

    /**
     * 將單字添加到單字庫。
     *
     * @param word 要添加的單字。
     * @return 操作結果，成功時為 Unit，失敗時包含例外。
     */
    override suspend fun addWordToWordBank(word: String): Result<Unit> {
        return try {
            // 先檢查單字是否存在於本地數據庫
            val localWordEntity = dao.getWordByName(word)

            if (localWordEntity != null) {
                val initializedReview = localWordEntity.copy(
                    isInWordBank = true,
                    reviewState = EntityReviewState.NEW,
                    reviewReps = 0,
                    reviewLapses = 0,
                    reviewStability = DEFAULT_STABILITY,
                    reviewDifficulty = DEFAULT_DIFFICULTY,
                    reviewDueAt = Clock.System.now().toEpochMilliseconds(),
                    reviewLastReviewedAt = null,
                    reviewScheduledDays = 0,
                    reviewElapsedDays = 0
                )
                dao.insertWord(initializedReview)
                Result.success(Unit)
            } else {
                // 如果單字不在數據庫中，則先從 API 獲取單字定義
                val remoteWord = api.lookupWord(word).toDomain()

                // 將單字保存到數據庫，並標記為單字庫中的單字
                val wordEntity = WordEntity(
                    word = remoteWord.word,
                    ukPronunciation = remoteWord.pronunciations.uk,
                    usPronunciation = remoteWord.pronunciations.us,
                    entriesJson = convertEntriesToJson(remoteWord),
                    isInWordBank = true,
                    reviewState = EntityReviewState.NEW,
                    reviewStability = DEFAULT_STABILITY,
                    reviewDifficulty = DEFAULT_DIFFICULTY
                )
                dao.insertWord(wordEntity)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 從單字庫移除單字。
     *
     * @param word 要移除的單字。
     * @return 操作結果，成功時為 Unit，失敗時包含例外。
     */
    override suspend fun removeWordFromWordBank(word: String): Result<Unit> {
        return try {
            val affectedRows = dao.removeFromWordBank(word)
            if (affectedRows > 0) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("從單字庫移除單字失敗"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 檢查單字是否已在單字庫中。
     *
     * @param word 要檢查的單字。
     * @return 如果單字在單字庫中則返回 true，否則返回 false。
     */
    override suspend fun isWordInWordBank(word: String): Boolean {
        val wordEntity = dao.getWordByName(word)
        return wordEntity?.isInWordBank ?: false
    }

    /**
     * 獲取單字庫中的單字數量。
     *
     * @return 單字庫中的單字總數。
     */
    override suspend fun getWordBankCount(): Int {
        return dao.getWordBankCount()
    }

    /**
     * 取得到期需要複習的單字。
     */
    override suspend fun getDueReviewCards(now: Instant): List<ReviewCard> {
        val epochMillis = now.toEpochMilliseconds()
        return dao.getDueReviewWords(epochMillis).map { it.toReviewCard() }
    }

    /**
     * 取得指定單字的複習卡片。
     */
    override suspend fun getReviewCard(word: String): ReviewCard? {
        val entity = dao.getWordByName(word)
        if (entity == null || !entity.isInWordBank) return null
        return entity.toReviewCard()
    }

    /**
     * 儲存更新後的複習卡片狀態。
     */
    override suspend fun saveReviewCard(card: ReviewCard) {
        val existing = dao.getWordByName(card.word.word) ?: return
        val updated = existing.copy(
            reviewDifficulty = card.metadata.difficulty,
            reviewStability = card.metadata.stability,
            reviewReps = card.metadata.reps,
            reviewLapses = card.metadata.lapses,
            reviewState = card.metadata.state.toEntityState(),
            reviewDueAt = card.metadata.dueAt.toEpochMilliseconds(),
            reviewLastReviewedAt = card.metadata.lastReviewedAt?.toEpochMilliseconds(),
            reviewScheduledDays = card.metadata.scheduledDays,
            reviewElapsedDays = card.metadata.elapsedDays,
            timestamp = Clock.System.now().toEpochMilliseconds()
        )
        dao.insertWord(updated)
    }

    /**
     * 重置單字的學習進度。
     */
    override suspend fun resetWordProgress(word: String) {
        val existing = dao.getWordByName(word) ?: return
        val reset = existing.copy(
            reviewState = EntityReviewState.NEW,
            reviewReps = 0,
            reviewLapses = 0,
            reviewStability = DEFAULT_STABILITY,
            reviewDifficulty = DEFAULT_DIFFICULTY,
            reviewDueAt = Clock.System.now().toEpochMilliseconds(),
            reviewLastReviewedAt = null,
            reviewScheduledDays = 0,
            reviewElapsedDays = 0,
            timestamp = Clock.System.now().toEpochMilliseconds()
        )
        dao.insertWord(reset)
    }

    /**
     * 將 WordEntity 轉換為領域模型 Word
     */
    private fun WordEntity.toDomainWord(): Word {
        return Word(
            word = word,
            pronunciations = Word.Pronunciations(
                uk = ukPronunciation,
                us = usPronunciation
            ),
            entries = json.decodeFromString<List<EntryEntity>>(entriesJson)
                .map { entryEntity ->
                    Word.Entry(
                        partOfSpeech = entryEntity.partOfSpeech,
                        definitions = entryEntity.definitions.map { defEntity ->
                            Word.Definition(
                                enDefinition = defEntity.enDefinition,
                                zhDefinition = defEntity.zhDefinition,
                                examples = defEntity.examples
                            )
                        }
                    )
                }
        )
    }

    /**
     * 將 WordEntity 轉換為包含複習狀態的 ReviewCard
     */
    private fun WordEntity.toReviewCard(): ReviewCard {
        return ReviewCard(
            word = toDomainWord(),
            metadata = ReviewMetadata(
                dueAt = Instant.fromEpochMilliseconds(reviewDueAt),
                lastReviewedAt = reviewLastReviewedAt?.let { Instant.fromEpochMilliseconds(it) },
                stability = reviewStability,
                difficulty = reviewDifficulty,
                reps = reviewReps,
                lapses = reviewLapses,
                state = reviewState.toDomainState(),
                scheduledDays = reviewScheduledDays,
                elapsedDays = reviewElapsedDays
            )
        )
    }

    /**
     * 將領域模型 Word 的 entries 轉換為 JSON 字串
     */
    private fun convertEntriesToJson(word: Word): String {
        // 將領域模型 Word 的 entries 轉換為 EntryEntity 列表
        val entries = word.entries.map { entry ->
            EntryEntity(
                partOfSpeech = entry.partOfSpeech,
                definitions = entry.definitions.map { def ->
                    com.linli.dictionary.data.local.entity.DefinitionEntity(
                        enDefinition = def.enDefinition,
                        zhDefinition = def.zhDefinition,
                        examples = def.examples
                    )
                }
            )
        }
        return json.encodeToString(entries)
    }

    private fun EntityReviewState.toDomainState(): ReviewState {
        return when (this) {
            EntityReviewState.NEW -> ReviewState.NEW
            EntityReviewState.LEARNING -> ReviewState.LEARNING
            EntityReviewState.REVIEW -> ReviewState.REVIEW
        }
    }

    private fun ReviewState.toEntityState(): EntityReviewState {
        return when (this) {
            ReviewState.NEW -> EntityReviewState.NEW
            ReviewState.LEARNING -> EntityReviewState.LEARNING
            ReviewState.REVIEW -> EntityReviewState.REVIEW
        }
    }

    private companion object {
        const val DEFAULT_DIFFICULTY = 5.0
        const val DEFAULT_STABILITY = 0.5
    }
}
