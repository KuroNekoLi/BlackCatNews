package com.linli.dictionary.data.mock

import com.linli.dictionary.data.model.DefinitionDto
import com.linli.dictionary.data.model.EntryDto
import com.linli.dictionary.data.model.PronunciationsDto
import com.linli.dictionary.data.model.WordResponseDto
import com.linli.dictionary.domain.model.Word

/**
 * Mock data for testing dictionary functionality.
 */
object MockDictionaryData {

    /**
     * Returns a mock WordResponseDto for the given word.
     */
    fun getMockWordResponseDto(word: String = "adjust"): WordResponseDto {
        return WordResponseDto(
            word = word,
            pronunciations = PronunciationsDto(
                uk = "əˈdʒʌst",
                us = "əˈdʒʌst"
            ),
            entries = listOf(
                EntryDto(
                    partOfSpeech = "verb",
                    definitions = listOf(
                        DefinitionDto(
                            enDefinition = "to change something slightly, especially to make it more correct, effective, or suitable",
                            zhDefinition = "（尤指為了使某物更正確、有效或合適而）調整，調節",
                            examples = listOf(
                                "If the chair is too high you can adjust it to suit you. 如果椅子太高了，你可以把它調到適合你的高度。",
                                "As a teacher you have to adjust your methods to suit the needs of slower children. 作為一名老師，你必須調整教學方法來適應那些學得較慢的孩子的需要。"
                            )
                        ),
                        DefinitionDto(
                            enDefinition = "to arrange your clothing to make yourself look tidy",
                            zhDefinition = "整理（衣著）",
                            examples = listOf(
                                "She adjusted her skirt, took a deep breath, and walked into the room. 她整理了一下裙子，深吸一口氣，走進了房間。"
                            )
                        ),
                        DefinitionDto(
                            enDefinition = "to become more familiar with a new situation",
                            zhDefinition = "適應;習慣",
                            examples = listOf(
                                "I can't adjust to liv ing on my own. 我不習慣獨自生活。",
                                "Her eyes slowly adjusted to the dark. 她的眼睛慢慢適應了黑暗。",
                                "The lifestyle is so very different - it takes a while to adjust. 生活方式差別太大了，要過一段時間才能適應。"
                            )
                        )
                    )
                )
            )
        )
    }

    /**
     * Returns a mock domain Word model for the given word.
     */
    fun getMockWord(word: String = "adjust"): Word {
        return Word(
            word = word,
            pronunciations = Word.Pronunciations(
                uk = "əˈdʒʌst",
                us = "əˈdʒʌst"
            ),
            entries = listOf(
                Word.Entry(
                    partOfSpeech = "verb",
                    definitions = listOf(
                        Word.Definition(
                            enDefinition = "to change something slightly, especially to make it more correct, effective, or suitable",
                            zhDefinition = "（尤指為了使某物更正確、有效或合適而）調整，調節",
                            examples = listOf(
                                "If the chair is too high you can adjust it to suit you. 如果椅子太高了，你可以把它調到適合你的高度。",
                                "As a teacher you have to adjust your methods to suit the needs of slower children. 作為一名老師，你必須調整教學方法來適應那些學得較慢的孩子的需要。"
                            )
                        ),
                        Word.Definition(
                            enDefinition = "to arrange your clothing to make yourself look tidy",
                            zhDefinition = "整理（衣著）",
                            examples = listOf(
                                "She adjusted her skirt, took a deep breath, and walked into the room. 她整理了一下裙子，深吸一口氣，走進了房間。"
                            )
                        ),
                        Word.Definition(
                            enDefinition = "to become more familiar with a new situation",
                            zhDefinition = "適應;習慣",
                            examples = listOf(
                                "I can't adjust to liv ing on my own. 我不習慣獨自生活。",
                                "Her eyes slowly adjusted to the dark. 她的眼睛慢慢適應了黑暗。",
                                "The lifestyle is so very different - it takes a while to adjust. 生活方式差別太大了，要過一段時間才能適應。"
                            )
                        )
                    )
                )
            )
        )
    }

    /**
     * Returns a mock list of recent searches.
     */
    fun getMockRecentSearches(): List<String> {
        return listOf("adjust", "hello", "world", "dictionary", "kotlin")
    }
}