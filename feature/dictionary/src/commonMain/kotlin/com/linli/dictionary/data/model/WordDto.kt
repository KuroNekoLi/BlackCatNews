package com.linli.dictionary.data.model

import com.linli.dictionary.domain.model.Word
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data transfer object representing the dictionary API response.
 */
@Serializable
data class WordDto(
    val word: String,
    val pronunciations: PronunciationsDto,
    val entries: List<EntryDto>
) {
    @Serializable
    data class PronunciationsDto(
        val uk: String,
        val us: String
    )

    @Serializable
    data class EntryDto(
        @SerialName("part_of_speech")
        val partOfSpeech: String,
        val definitions: List<DefinitionDto>
    )

    @Serializable
    data class DefinitionDto(
        @SerialName("en_definition")
        val enDefinition: String,
        @SerialName("zh_definition")
        val zhDefinition: String,
        val examples: List<String>
    )
}

/**
 * Extension function to convert the DTO to a domain model.
 */
fun WordDto.toDomain(): Word {
    return Word(
        word = word,
        pronunciations = Word.Pronunciations(
            uk = pronunciations.uk,
            us = pronunciations.us
        ),
        entries = entries.map { entry ->
            Word.Entry(
                partOfSpeech = entry.partOfSpeech,
                definitions = entry.definitions.map { definition ->
                    Word.Definition(
                        enDefinition = definition.enDefinition,
                        zhDefinition = definition.zhDefinition,
                        examples = definition.examples
                    )
                }
            )
        }
    )
}