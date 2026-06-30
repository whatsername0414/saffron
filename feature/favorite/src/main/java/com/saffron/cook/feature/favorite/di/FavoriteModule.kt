package com.saffron.cook.feature.favorite.di

import com.saffron.cook.feature.favorite.main.FavoriteViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val favoriteModule = module {
    viewModelOf(::FavoriteViewModel)
}
