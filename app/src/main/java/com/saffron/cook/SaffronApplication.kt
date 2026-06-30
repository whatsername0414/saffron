package com.saffron.cook

import android.app.Application
import com.saffron.cook.core.auth.di.authModule
import com.saffron.cook.core.database.di.databaseModule
import com.saffron.cook.di.coreDataModule
import com.saffron.cook.di.networkModule
import com.saffron.cook.feature.cooked.di.cookedModule
import com.saffron.cook.feature.cooking.di.cookingModule
import com.saffron.cook.feature.favorite.di.favoriteModule
import com.saffron.cook.feature.home.di.homeModule
import com.saffron.cook.feature.note.di.noteModule
import com.saffron.cook.feature.profile.di.profileModule
import com.saffron.cook.feature.recipe.di.recipeModule
import com.saffron.cook.feature.search.di.searchModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SaffronApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SaffronApplication)
            modules(authModule, networkModule, coreDataModule, databaseModule, noteModule, cookedModule, homeModule, recipeModule, cookingModule, searchModule, favoriteModule, profileModule)
        }
    }
}
