package com.saffron.cook.feature.cooking.di

import com.saffron.cook.feature.cooking.main.CookingModeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val cookingModule = module {
    viewModelOf(::CookingModeViewModel)
}
