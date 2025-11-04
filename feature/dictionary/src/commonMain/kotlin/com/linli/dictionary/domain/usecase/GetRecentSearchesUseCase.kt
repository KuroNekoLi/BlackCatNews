package com.linli.dictionary.domain.usecase

import com.linli.dictionary.domain.repository.DictionaryRepository

/**
 * 取得最近搜尋的歷史紀錄。
 */
open class GetRecentSearchesUseCase(private val repository: DictionaryRepository) {
    /**
     * 取得最近搜尋的歷史紀錄。
     *
     * @return 最近搜尋的歷史紀錄列表。
     */
    open suspend operator fun invoke(): List<String> {
        return repository.getRecentSearches()
    }
}