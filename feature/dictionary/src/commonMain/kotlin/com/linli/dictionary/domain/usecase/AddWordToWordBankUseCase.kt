package com.linli.dictionary.domain.usecase

import com.linli.dictionary.domain.repository.WordBankRepository

/**
 * 將單字添加到單字庫中。
 *
 * 使用時機：
 * 1. 若單字已存在於單字庫中，直接返回成功。
 * 2. 若單字不存在於單字庫中，則透過 API 查詢並新增到單字庫中。
 */
class AddWordToWordBankUseCase(private val wordBankRepository: WordBankRepository) {

    /**
     * 執行添加單字到單字庫的動作。
     *
     * @param word 要添加的單字。
     * @return 添加結果，成功返回 Unit，失敗返回 Exception。
     */
    suspend operator fun invoke(word: String): Result<Unit> {
        return wordBankRepository.addWordToWordBank(word)
    }
}