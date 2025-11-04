package com.linli.dictionary.domain.model

/**
 * 表示詞典中單字的領域模型，包含其定義和發音。
 */
data class Word(
    val word: String,
    val pronunciations: Pronunciations,
    val entries: List<Entry>
) {
    data class Pronunciations(
        val uk: String,
        val us: String
    )

    data class Entry(
        val partOfSpeech: String,
        val definitions: List<Definition>
    )

    data class Definition(
        val enDefinition: String,
        val zhDefinition: String,
        val examples: List<String>
    )
}