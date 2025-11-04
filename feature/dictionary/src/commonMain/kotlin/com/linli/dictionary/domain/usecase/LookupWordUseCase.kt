package com.linli.dictionary.domain.usecase

import com.linli.dictionary.domain.model.Word
import com.linli.dictionary.domain.repository.DictionaryRepository

/**
 * 用於在詞典中查詢單字的用例。
 */
open class LookupWordUseCase(private val repository: DictionaryRepository) {
    /**
     * 在辭典中查詢單字。
     *
     * @param word 要查詢的單字。
     * @return 單字及其定義和發音。
     */
    open suspend operator fun invoke(word: String): Result<Word> {
        // 修剪並轉為小寫，確保一致的搜尋結果
        val formattedWord = word.trim().lowercase()

        // 如果單字為空，則返回錯誤
        if (formattedWord.isEmpty()) {
            return Result.failure(IllegalArgumentException("Word cannot be empty"))
        }

        // 將單字儲存到最近搜尋記錄中（無論成功或失敗）
        repository.saveRecentSearch(formattedWord)

        // 查詢單字
        return repository.lookupWord(formattedWord)
    }
}