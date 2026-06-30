package com.saffron.cook.feature.cooking.main

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ParseTimedSpansTest {

    @Test
    fun `single minute value parses to seconds`() {
        val spans = parseTimedSpans("bake for 25 minutes")
        assertThat(spans).hasSize(1)
        assertThat(spans[0].seconds).isEqualTo(25 * 60)
    }

    @Test
    fun `range takes the upper bound`() {
        val spans = parseTimedSpans("simmer for 2-3 mins")
        assertThat(spans).hasSize(1)
        assertThat(spans[0].seconds).isEqualTo(3 * 60)
    }

    @Test
    fun `seconds unit stays as seconds`() {
        val spans = parseTimedSpans("rest for 90 seconds")
        assertThat(spans[0].seconds).isEqualTo(90)
    }

    @Test
    fun `hours unit converts to seconds`() {
        val spans = parseTimedSpans("chill for 1 hour")
        assertThat(spans[0].seconds).isEqualTo(3600)
    }

    @Test
    fun `multiple durations are all detected`() {
        val spans = parseTimedSpans("toast 2 minutes then bake 25 minutes")
        assertThat(spans).hasSize(2)
        assertThat(spans.map { it.seconds }).containsExactly(120, 1500).inOrder()
    }

    @Test
    fun `span offsets point at the matched text`() {
        val text = "wait 5 mins"
        val span = parseTimedSpans(text).single()
        assertThat(text.substring(span.start, span.end)).isEqualTo("5 mins")
    }

    @Test
    fun `text with no duration yields empty list`() {
        assertThat(parseTimedSpans("stir until smooth")).isEmpty()
    }
}
