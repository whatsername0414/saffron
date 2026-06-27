package com.saffron.cook.core.auth.di

import com.google.firebase.auth.FirebaseAuth
import com.saffron.cook.core.auth.AuthRepository
import com.saffron.cook.core.auth.FirebaseAuthRepository
import org.koin.dsl.module

val authModule = module {
    single<FirebaseAuth> { FirebaseAuth.getInstance() }
    single<AuthRepository> { FirebaseAuthRepository(get()) }
}
