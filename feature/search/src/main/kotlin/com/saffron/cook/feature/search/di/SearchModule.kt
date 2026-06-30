package com.saffron.cook.feature.search.di

import com.saffron.cook.feature.search.main.SearchViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val searchModule = module {
    viewModelOf(::SearchViewModel)
}
