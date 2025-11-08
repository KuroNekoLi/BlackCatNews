package com.linli.dictionary.domain.usecase

import com.linli.dictionary.domain.repository.WordBankRepository

/**
 * 檢查單字是否已在單字庫中。
 *
 * 這個 UseCase 用來判斷特定單字是否已被使用者加入到單字庫中。
 * 通常用來更新 UI 狀態，例如顯示「加入單字庫」或「從單字庫移除」按鈕的外觀。
 */
class IsWordInWordBankUseCase(private val wordBankRepository: WordBankRepository) {

    /**
     * 執行檢查單字是否已在單字庫中。
     *
     * @param word 要檢查的單字。
     * @return 如果單字已在單字庫中則回傳 true，否則回傳 false。
     */
    suspend operator fun invoke(word: String): Boolean {
        return wordBankRepository.isWordInWordBank(word)
    }
}