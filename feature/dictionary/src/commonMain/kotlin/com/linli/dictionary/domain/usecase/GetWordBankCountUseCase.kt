package com.linli.dictionary.domain.usecase

import com.linli.dictionary.domain.repository.WordBankRepository

/**
 * 獲取單字庫中單字的數量。
 *
 * 此用例提供一種簡單的方法來取得用戶單字庫中單字的總數。
 * 可用於顯示單字庫統計信息等場合。
 */
class GetWordBankCountUseCase(private val wordBankRepository: WordBankRepository) {

    /**
     * 執行查詢單字庫中單字的數量。
     *
     * @return 單字庫中的單字總數。
     */
    suspend operator fun invoke(): Int {
        return wordBankRepository.getWordBankCount()
    }
}