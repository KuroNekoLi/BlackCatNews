package com.linli.dictionary.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data transfer object representing the dictionary API response.
 */
@Serializable
data class WordResponseDto(
    @SerialName("word")
    val word: String,

    @SerialName("pronunciations")
    val pronunciations: PronunciationsDto,

    @SerialName("entries")
    val entries: List<EntryDto>
)

/**
 * Pronunciations data from the dictionary API.
 */
@Serializable
data class PronunciationsDto(
    @SerialName("uk")
    val uk: String,

    @SerialName("us")
    val us: String
)

/**
 * Dictionary entry containing part of speech and definitions.
 */
@Serializable
data class EntryDto(
    @SerialName("part_of_speech")
    val partOfSpeech: String,

    @SerialName("definitions")
    val definitions: List<DefinitionDto>
)

/**
 * Definition containing English and Chinese definitions with examples.
 */
@Serializable
data class DefinitionDto(
    @SerialName("en_definition")
    val enDefinition: String,

    @SerialName("zh_definition")
    val zhDefinition: String,

    @SerialName("examples")
    val examples: List<ExampleDto>
)

/**
 * Example text structure from the dictionary API.
 */
@Serializable
data class ExampleDto(
    @SerialName("example_text")
    val exampleText: String
)