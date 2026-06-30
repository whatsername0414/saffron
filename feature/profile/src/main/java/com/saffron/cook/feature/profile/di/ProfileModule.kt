package com.saffron.cook.feature.profile.di

import com.saffron.cook.feature.profile.main.ProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val profileModule = module {
    viewModelOf(::ProfileViewModel)
}
