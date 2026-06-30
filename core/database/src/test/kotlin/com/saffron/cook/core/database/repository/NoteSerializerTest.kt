package com.saffron.cook.core.database.repository

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NoteSerializerTest {

    @Test
    fun `labels round-trip through string`() {
        val labels = setOf("weeknight", "spicy")
        val encoded = RecipeNotesRepository.labelsToString(labels)
        assertThat(RecipeNotesRepository.labelsFromString(encoded)).isEqualTo(labels)
    }

    @Test
    fun `blank label string decodes to empty set`() {
        assertThat(RecipeNotesRepository.labelsFromString("")).isEmpty()
        assertThat(RecipeNotesRepository.labelsFromString("   ")).isEmpty()
    }

    @Test
    fun `photos round-trip preserves order`() {
        val photos = listOf("uri://a", "uri://b", "uri://c")
        val encoded = RecipeNotesRepository.photosToString(photos)
        assertThat(RecipeNotesRepository.photosFromString(encoded)).containsExactlyElementsIn(photos).inOrder()
    }

    @Test
    fun `blank photo string decodes to empty list`() {
        assertThat(RecipeNotesRepository.photosFromString("")).isEmpty()
    }

    @Test
    fun `empty collections encode to empty string`() {
        assertThat(RecipeNotesRepository.labelsToString(emptySet())).isEmpty()
        assertThat(RecipeNotesRepository.photosToString(emptyList())).isEmpty()
    }
}
