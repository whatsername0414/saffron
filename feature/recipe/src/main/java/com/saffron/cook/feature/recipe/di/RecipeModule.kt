package com.saffron.cook.feature.recipe.di

import com.saffron.cook.feature.recipe.main.RecipeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val recipeModule = module {
    viewModelOf(::RecipeViewModel)
}
