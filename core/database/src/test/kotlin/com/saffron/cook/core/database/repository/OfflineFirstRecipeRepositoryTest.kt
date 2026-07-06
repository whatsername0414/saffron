package com.saffron.cook.core.database.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.saffron.cook.core.database.SaffronDatabase
import com.saffron.cook.core.database.entity.toCachedEntity
import com.saffron.cook.core.database.entity.toEntity
import com.saffron.cook.core.domain.model.Category
import com.saffron.cook.core.testing.Fixtures
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class OfflineFirstRecipeRepositoryTest {

    private lateinit var db: SaffronDatabase
    private lateinit var repository: OfflineFirstRecipeRepository
    private val remote = FakeRemoteRecipeRepository()

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SaffronDatabase::class.java,
        ).allowMainThreadQueries().build()
        repository = OfflineFirstRecipeRepository(remote, db.cachedRecipeDao(), db.savedRecipeDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun partialRecipe(id: String, title: String = "Recipe $id", categoryId: String = "italian") =
        Fixtures.recipe(id = id, title = title, categoryId = categoryId, ingredients = emptyList(), steps = emptyList())

    @Test
    fun `getRecipes success returns remote list and writes through to cache`() = runTest {
        remote.recipes = listOf(partialRecipe("1"), partialRecipe("2"))

        val online = repository.getRecipes()

        assertThat(online).hasSize(2)
        remote.shouldThrow = true
        val offline = repository.getRecipes()
        assertThat(offline.map { it.id }).containsExactly("1", "2")
    }

    @Test
    fun `getRecipes failure with empty cache returns empty list`() = runTest {
        remote.shouldThrow = true
        assertThat(repository.getRecipes()).isEmpty()
    }

    @Test
    fun `getRecipesByCategory failure serves cached rows for that category only`() = runTest {
        remote.recipes = listOf(partialRecipe("1", categoryId = "italian"), partialRecipe("2", categoryId = "baking"))
        repository.getRecipes()

        remote.shouldThrow = true
        val offline = repository.getRecipesByCategory("baking")

        assertThat(offline.map { it.id }).containsExactly("2")
    }

    @Test
    fun `partial list row does not clobber full cached row`() = runTest {
        remote.recipeById = Fixtures.recipe(id = "1")
        repository.getRecipeById("1")

        remote.recipes = listOf(partialRecipe("1"))
        repository.getRecipes()

        val cached = db.cachedRecipeDao().getById("1")
        assertThat(cached).isNotNull()
        assertThat(cached!!.isFullDetail).isTrue()
        assertThat(cached.steps).isNotEmpty()
    }

    @Test
    fun `getRecipeById prefers full cache over remote`() = runTest {
        remote.recipeById = Fixtures.recipe(id = "1", title = "Cached Full")
        repository.getRecipeById("1")

        remote.shouldThrow = true
        val recipe = repository.getRecipeById("1")

        assertThat(recipe?.title).isEqualTo("Cached Full")
        assertThat(recipe?.steps).isNotEmpty()
    }

    @Test
    fun `getRecipeById falls back to full saved row when not cached`() = runTest {
        db.savedRecipeDao().insert(Fixtures.recipe(id = "1", title = "Saved Full").toEntity())

        remote.shouldThrow = true
        val recipe = repository.getRecipeById("1")

        assertThat(recipe?.title).isEqualTo("Saved Full")
        assertThat(recipe?.steps).isNotEmpty()
    }

    @Test
    fun `getRecipeById rethrows when remote fails and no full local copy exists`() = runTest {
        remote.recipes = listOf(partialRecipe("1"))
        repository.getRecipes()

        remote.shouldThrow = true
        val result = runCatching { repository.getRecipeById("1") }

        assertThat(result.exceptionOrNull()).isInstanceOf(IOException::class.java)
    }

    @Test
    fun `searchRecipes failure falls back to cached title match`() = runTest {
        remote.recipes = listOf(Fixtures.recipe(id = "1", title = "Chicken Pie"), Fixtures.recipe(id = "2", title = "Beef Stew"))
        repository.searchRecipes("e")

        remote.shouldThrow = true
        val offline = repository.searchRecipes("chicken")

        assertThat(offline.map { it.id }).containsExactly("1")
    }

    @Test
    fun `getFeaturedRecipe failure falls back to most recent full cached recipe`() = runTest {
        remote.recipeById = Fixtures.recipe(id = "1", title = "Full Detail")
        repository.getRecipeById("1")

        remote.shouldThrow = true
        val featured = repository.getFeaturedRecipe()

        assertThat(featured?.title).isEqualTo("Full Detail")
    }

    @Test
    fun `getCategories failure falls back to cached categories`() = runTest {
        remote.categories = listOf(Category("italian", "Italian"), Category("baking", "Baking"))
        repository.getCategories()

        remote.shouldThrow = true
        val offline = repository.getCategories()

        assertThat(offline).containsExactly(Category("italian", "Italian"), Category("baking", "Baking"))
    }

    @Test
    fun `cache is capped and keeps the newest rows`() = runTest {
        val cappedRepository =
            OfflineFirstRecipeRepository(remote, db.cachedRecipeDao(), db.savedRecipeDao(), maxCachedRecipes = 2)
        remote.recipes = listOf(partialRecipe("1"), partialRecipe("2"), partialRecipe("3"))

        cappedRepository.getRecipes()

        assertThat(db.cachedRecipeDao().getRecent(10)).hasSize(2)
    }

    @Test
    fun `evictOldest deletes the oldest rows by cachedAt`() = runTest {
        val dao = db.cachedRecipeDao()
        dao.upsertFull(partialRecipe("1").toCachedEntity(cachedAt = 1L, isFullDetail = false))
        dao.upsertFull(partialRecipe("2").toCachedEntity(cachedAt = 2L, isFullDetail = false))
        dao.upsertFull(partialRecipe("3").toCachedEntity(cachedAt = 3L, isFullDetail = false))

        dao.evictOldest(keep = 2)

        assertThat(dao.getById("1")).isNull()
        assertThat(dao.getById("2")).isNotNull()
        assertThat(dao.getById("3")).isNotNull()
    }
}
