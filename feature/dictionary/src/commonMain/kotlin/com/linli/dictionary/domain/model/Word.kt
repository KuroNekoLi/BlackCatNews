package com.linli.dictionary.domain.model

import kotlinx.serialization.Serializable

/**
 * 表示詞典中單字的領域模型，包含其定義和發音。
 */
@Serializable
data class Word(
    val word: String,
    val pronunciations: Pronunciations,
    val entries: List<Entry>
) {
    @Serializable
    data class Pronunciations(
        val uk: String,
        val us: String
    )

    @Serializable
    data class Entry(
        val partOfSpeech: String,
        val definitions: List<Definition>
    )

    @Serializable
    data class Definition(
        val enDefinition: String,
        val zhDefinition: String,
        val examples: List<String>
    )
}