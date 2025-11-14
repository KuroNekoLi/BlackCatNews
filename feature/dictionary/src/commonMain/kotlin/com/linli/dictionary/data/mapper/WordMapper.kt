package com.linli.dictionary.data.mapper

import com.linli.dictionary.data.model.WordResponseDto
import com.linli.dictionary.domain.model.Word

/**
 * 將 WordResponseDto 轉換為領域模型 Word。
 */
fun WordResponseDto.toDomain(): Word {
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
                        examples = definition.examples.map { it.exampleText }
                    )
                }
            )
        }
    )
}