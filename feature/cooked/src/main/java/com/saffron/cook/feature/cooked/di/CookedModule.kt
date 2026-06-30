package com.saffron.cook.feature.cooked.di

import com.saffron.cook.feature.cooked.main.CookedListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val cookedModule = module {
    viewModelOf(::CookedListViewModel)
}
