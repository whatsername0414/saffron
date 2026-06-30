package com.saffron.cook.core.testing

import com.saffron.cook.core.database.repository.SavedRecipesRepository
import com.saffron.cook.core.domain.model.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeSavedRecipesRepository : SavedRecipesRepository {
    private val saved = MutableStateFlow<List<Recipe>>(emptyList())

    override val savedRecipesFlow: Flow<List<Recipe>> = saved.asStateFlow()
    override val savedIdsFlow: Flow<Set<String>> = saved.map { list -> list.map { it.id }.toSet() }

    fun seed(vararg recipes: Recipe) {
        saved.value = recipes.toList()
    }

    override suspend fun toggle(recipe: Recipe) {
        saved.update { list ->
            if (list.any { it.id == recipe.id }) list.filterNot { it.id == recipe.id }
            else list + recipe
        }
    }
}
