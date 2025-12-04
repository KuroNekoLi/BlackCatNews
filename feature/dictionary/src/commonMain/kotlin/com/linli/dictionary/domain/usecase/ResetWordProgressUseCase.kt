package com.linli.dictionary.domain.usecase

import com.linli.dictionary.domain.repository.WordBankRepository

/**
 * 重置單字學習進度的 UseCase
 */
class ResetWordProgressUseCase(
    private val repository: WordBankRepository
) {
    suspend operator fun invoke(word: String): Result<Unit> {
        return try {
            repository.resetWordProgress(word)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
