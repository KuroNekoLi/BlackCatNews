package com.linli.blackcatnews.data

object MockData {
    val mockResponse: String = """
        {
          "message": "success",
          "requestedCount": 2,
          "actualCount": 2,
          "totalAvailable": 2,
          "filters": {
            "source": "BBC",
            "section": "technology",
            "difficulty": "NORMAL",
            "from_language": "en",
            "to_language": "zh-TW"
          },
          "articles": [
            {
              "id": 1,
              "sourceName": "BBC",
              "section": "technology",
              "title": "Original title",
              "publishedAt": "2024-01-01T00:00:00Z",
              "originalUrl": "https://example.com/1",
              "contentHtml": "<p>Original content</p>",
              "titleFrom": "Original title",
              "titleTo": "原始標題",
              "title_zh": "原始標題(legacy)",
              "summaryFrom": "Summary EN",
              "summaryTo": "摘要中文",
              "summary_en": "Legacy summary en",
              "summary_zh": "Legacy summary zh",
              "requested_difficulty": "NORMAL",
              "from_language": "en",
              "to_language": "zh-TW",
              "content": {
                "difficulty": "NORMAL",
                "from_language": "en",
                "to_language": "zh-TW",
                "optimized_html_from": "<p>Optimized EN</p>",
                "optimized_html_to": "<p>優化中文</p>",
                "cleaned_text_from": "Optimized EN",
                "cleaned_text_to": "優化中文",
                "explanation": {
                  "vocabulary": [
                    {
                      "pos": "n.",
                      "word_from": "planet",
                      "word_to": "行星",
                      "definition_from": "A celestial body",
                      "definition_to": "天體",
                      "example_from": "Earth is a planet.",
                      "example_to": "地球是一顆行星。"
                    }
                  ],
                  "grammar": [
                    {
                      "rule_from": "Past tense",
                      "rule_to": "過去式",
                      "explanation_from": "Used for past actions",
                      "explanation_to": "用於表示過去的動作",
                      "example_from": "I walked.",
                      "example_to": "我走了。"
                    }
                  ],
                  "sentence_patterns": [
                    {
                      "pattern_from": "S + V + O",
                      "pattern_to": "主詞 + 動詞 + 受詞",
                      "explanation_from": "Basic sentence",
                      "explanation_to": "基本句型",
                      "example_from": "She eats apples.",
                      "example_to": "她吃蘋果。"
                    }
                  ],
                  "phrases_idioms": [
                    {
                      "phrase_from": "break a leg",
                      "phrase_to": "祝好運",
                      "explanation_from": "Good luck",
                      "explanation_to": "祝你成功",
                      "example_from": "Break a leg in your exam!",
                      "example_to": "考試加油！"
                    }
                  ],
                  "comprehension_mcq": [
                    {
                      "question_from": "What is Earth?",
                      "question_to": "地球是什麼？",
                      "options": [
                        {"label": "A", "text_from": "A star", "text_to": "恆星"},
                        {"label": "B", "text_from": "A planet", "text_to": "行星"}
                      ],
                      "answer": "B",
                      "explanation_from": "Earth is a planet.",
                      "explanation_to": "地球是一顆行星。"
                    }
                  ]
                }
              },
              "available_difficulties": ["EASY", "NORMAL", "HARD"],
              "available_views": [
                {"from_language": "en", "to_language": "zh-TW", "difficulty": "NORMAL"}
              ]
            },
            {
              "id": 2,
              "sourceName": "BBC",
              "section": "technology",
              "title": "Second title",
              "publishedAt": "2024-01-02T00:00:00Z",
              "originalUrl": "https://example.com/2",
              "contentHtml": "<p>Another article</p>",
              "titleFrom": "Second title",
              "titleTo": "第二標題",
              "summaryFrom": "Second summary",
              "summaryTo": "第二摘要",
              "from_language": "en",
              "to_language": "zh-TW",
              "content": {
                "difficulty": "NORMAL",
                "from_language": "en",
                "to_language": "zh-TW",
                "optimized_html_from": "<p>Optimized second EN</p>",
                "optimized_html_to": "<p>優化第二中文</p>",
                "cleaned_text_from": "Optimized second EN",
                "cleaned_text_to": "優化第二中文"
              },
              "available_difficulties": ["NORMAL"],
              "available_views": [
                {"from_language": "en", "to_language": "zh-TW", "difficulty": "NORMAL"}
              ]
            }
          ]
        }
    """.trimIndent()
}
