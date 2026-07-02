package com.saffron.cook.feature.welcome.di

import com.saffron.cook.feature.welcome.main.WelcomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val welcomeModule = module {
    viewModelOf(::WelcomeViewModel)
}
