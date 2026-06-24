package com.saffron.cook

import android.app.Application
import com.saffron.cook.di.authModule
import com.saffron.cook.di.cookingModule
import com.saffron.cook.di.coreDataModule
import com.saffron.cook.di.detailModule
import com.saffron.cook.di.favoritesModule
import com.saffron.cook.di.homeModule
import com.saffron.cook.di.loginModule
import com.saffron.cook.di.networkModule
import com.saffron.cook.di.notesModule
import com.saffron.cook.di.profileModule
import com.saffron.cook.di.savedRecipesModule
import com.saffron.cook.di.searchModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SaffronApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SaffronApplication)
            modules(authModule, networkModule, coreDataModule, savedRecipesModule, notesModule, homeModule, detailModule, cookingModule, searchModule, favoritesModule, loginModule, profileModule)
        }
    }
}
