package com.linli.dictionary.domain.usecase

import com.linli.dictionary.domain.model.Word
import com.linli.dictionary.domain.repository.WordBankRepository

/**
 * 獲取用戶單字庫中儲存的所有單字。
 *
 * 該用例負責從 WordBankRepository 獲取用戶保存在單字庫中的所有單字。
 */
class GetSavedWordsUseCase(private val wordBankRepository: WordBankRepository) {

    /**
     * 執行用例，獲取單字庫中的所有單字。
     *
     * @return 單字庫中的單字列表。
     */
    suspend operator fun invoke(): List<Word> {
        return wordBankRepository.getSavedWords()
    }
}