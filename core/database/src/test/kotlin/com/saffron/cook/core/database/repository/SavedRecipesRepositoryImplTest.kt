package com.saffron.cook.core.database.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.saffron.cook.core.database.SaffronDatabase
import com.saffron.cook.core.database.entity.toRecipe
import com.saffron.cook.core.testing.Fixtures
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SavedRecipesRepositoryImplTest {

    private lateinit var db: SaffronDatabase
    private lateinit var repository: SavedRecipesRepositoryImpl
    private val remote = FakeRemoteRecipeRepository()

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SaffronDatabase::class.java,
        ).allowMainThreadQueries().build()
        repository = SavedRecipesRepositoryImpl(db.savedRecipeDao(), remote)
    }

    @After
    fun tearDown() {
        db.close()
    }

    private val partialRecipe =
        Fixtures.recipe(id = "1", title = "Partial", ingredients = emptyList(), steps = emptyList())

    @Test
    fun `toggling a partial recipe fetches and stores the full recipe`() = runTest {
        remote.recipeById = Fixtures.recipe(id = "1", title = "Full")

        repository.toggle(partialRecipe)

        val saved = db.savedRecipeDao().getById("1")?.toRecipe()
        assertThat(saved?.title).isEqualTo("Full")
        assertThat(saved?.steps).isNotEmpty()
    }

    @Test
    fun `offline toggle stores the partial recipe without crashing`() = runTest {
        remote.shouldThrow = true

        repository.toggle(partialRecipe)

        val saved = db.savedRecipeDao().getById("1")?.toRecipe()
        assertThat(saved?.title).isEqualTo("Partial")
        assertThat(saved?.steps).isEmpty()
    }

    @Test
    fun `toggling a full recipe stores it without hitting the network`() = runTest {
        repository.toggle(Fixtures.recipe(id = "1"))

        assertThat(remote.recipeByIdCalls).isEqualTo(0)
        assertThat(db.savedRecipeDao().getById("1")).isNotNull()
    }

    @Test
    fun `toggling a saved recipe removes it`() = runTest {
        repository.toggle(Fixtures.recipe(id = "1"))
        repository.toggle(Fixtures.recipe(id = "1"))

        assertThat(db.savedRecipeDao().getById("1")).isNull()
    }
}
