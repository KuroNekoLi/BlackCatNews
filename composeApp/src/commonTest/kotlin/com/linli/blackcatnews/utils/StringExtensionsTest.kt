package com.linli.blackcatnews.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class StringExtensionsTest {

    @Test
    fun testFromJsonHtml_removeEscapedSlashes() {
        val jsonHtml = "<h1>Title<\\/h1>"
        val expected = "<h1>Title</h1>"
        assertEquals(expected, jsonHtml.fromJsonHtml())
    }

    @Test
    fun testFromJsonHtml_removeEscapedQuotes() {
        val jsonHtml = "<p class=\\\"test\\\">Content</p>"
        val expected = "<p class=\"test\">Content</p>"
        assertEquals(expected, jsonHtml.fromJsonHtml())
    }

    @Test
    fun testFromJsonHtml_convertNewlines() {
        val jsonHtml = "<p>Line 1\\nLine 2</p>"
        val expected = "<p>Line 1\nLine 2</p>"
        assertEquals(expected, jsonHtml.fromJsonHtml())
    }

    @Test
    fun testFromJsonHtml_complexExample() {
        val jsonHtml =
            "<div class=\\\"container\\\"><h1>Title<\\/h1><p>Line 1\\nLine 2<\\/p><\\/div>"
        val expected = "<div class=\"container\"><h1>Title</h1><p>Line 1\nLine 2</p></div>"
        assertEquals(expected, jsonHtml.fromJsonHtml())
    }

    @Test
    fun testToJsonHtml_escapeSlashes() {
        val html = "<h1>Title</h1>"
        val expected = "<h1>Title<\\/h1>"
        assertEquals(expected, html.toJsonHtml())
    }

    @Test
    fun testToJsonHtml_escapeQuotes() {
        val html = "<p class=\"test\">Content</p>"
        val expected = "<p class=\\\"test\\\">Content<\\/p>"
        assertEquals(expected, html.toJsonHtml())
    }

    @Test
    fun testToJsonHtml_escapeNewlines() {
        val html = "<p>Line 1\nLine 2</p>"
        val expected = "<p>Line 1\\nLine 2<\\/p>"
        assertEquals(expected, html.toJsonHtml())
    }

    @Test
    fun testRoundTrip_fromJsonHtmlToJsonHtml() {
        val original = "<div class=\"test\"><h1>Title</h1><p>Content</p></div>"
        val jsonHtml = original.toJsonHtml()
        val result = jsonHtml.fromJsonHtml()
        assertEquals(original, result)
    }
}
