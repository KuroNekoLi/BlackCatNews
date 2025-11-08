package com.linli.dictionary.domain.usecase

import com.linli.dictionary.domain.repository.WordBankRepository

/**
 * 從單字庫中移除單字。
 *
 * 此用例負責將指定的單字從使用者的單字庫中移除。
 */
class RemoveWordFromWordBankUseCase(private val wordBankRepository: WordBankRepository) {

    /**
     * 執行移除單字的操作。
     *
     * @param word 要從單字庫移除的單字。
     * @return 操作結果，成功返回 Unit，失敗返回 Exception。
     */
    suspend operator fun invoke(word: String): Result<Unit> {
        return wordBankRepository.removeWordFromWordBank(word)
    }
}