package com.saffron.cook.core.database.di

import androidx.room.Room
import com.saffron.cook.core.database.SaffronDatabase
import com.saffron.cook.core.database.repository.CookedRecipesRepository
import com.saffron.cook.core.database.repository.CookedRecipesRepositoryImpl
import com.saffron.cook.core.database.repository.OnboardingRepository
import com.saffron.cook.core.database.repository.OnboardingRepositoryImpl
import com.saffron.cook.core.database.repository.RecipeNotesRepository
import com.saffron.cook.core.database.repository.RecipeNotesRepositoryImpl
import com.saffron.cook.core.database.repository.SavedRecipesRepository
import com.saffron.cook.core.database.repository.SavedRecipesRepositoryImpl
import com.saffron.cook.core.database.repository.ThemeRepository
import com.saffron.cook.core.database.repository.ThemeRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(androidContext(), SaffronDatabase::class.java, "saffron_db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
    single { get<SaffronDatabase>().savedRecipeDao() }
    single { get<SaffronDatabase>().recipeNoteDao() }
    single { get<SaffronDatabase>().cookedRecipeDao() }
    single<SavedRecipesRepository> { SavedRecipesRepositoryImpl(get()) }
    single<RecipeNotesRepository> { RecipeNotesRepositoryImpl(get()) }
    single<CookedRecipesRepository> { CookedRecipesRepositoryImpl(get()) }
    single<OnboardingRepository> { OnboardingRepositoryImpl(androidContext()) }
    single<ThemeRepository> { ThemeRepositoryImpl(androidContext()) }
}
