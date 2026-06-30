package com.saffron.cook.feature.home.di

import com.saffron.cook.feature.home.main.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeModule = module {
    viewModelOf(::HomeViewModel)
}
