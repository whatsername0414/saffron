package com.saffron.cook.core.data.repository.fake

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FakeRecipeRepositoryTest {
    private val repo = FakeRecipeRepository()

    @Test
    fun `empty query returns all recipes`() {
        runTest {
            assertThat(repo.searchRecipes("")).isEqualTo(repo.getRecipes())
        }
    }

    @Test
    fun `search matches by title case-insensitively`() {
        runTest {
            val results = repo.searchRecipes("CACIO")
            assertThat(results.map { it.title }).contains("Cacio e Pepe")
        }
    }

    @Test
    fun `search matches by ingredient name`() {
        runTest {
            val results = repo.searchRecipes("miso")
            assertThat(results.map { it.title }).contains("Miso Glazed Salmon")
        }
    }

    @Test
    fun `no match returns empty`() {
        runTest {
            assertThat(repo.searchRecipes("zzzzz")).isEmpty()
        }
    }

    @Test
    fun `getRecipesByCategory filters by category id`() {
        runTest {
            val italian = repo.getRecipesByCategory("italian")
            assertThat(italian).isNotEmpty()
            assertThat(italian.all { it.categoryId == "italian" }).isTrue()
        }
    }

    @Test(expected = IllegalStateException::class)
    fun `shouldThrow makes calls fail`() {
        runTest {
            FakeRecipeRepository(shouldThrow = true).getRecipes()
        }
    }
}
