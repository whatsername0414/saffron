package com.saffron.cook.di

import com.saffron.cook.BuildConfig
import com.saffron.cook.core.data.network.TheMealDbService
import com.saffron.cook.core.data.repository.MealDbRecipeRepository
import com.saffron.cook.core.domain.repository.RecipeRepository
import com.saffron.cook.ui.cooking.CookingModeViewModel
import com.saffron.cook.ui.cookedlist.CookedListViewModel
import com.saffron.cook.ui.detail.RecipeDetailViewModel
import com.saffron.cook.ui.favorites.FavoritesViewModel
import com.saffron.cook.ui.home.HomeViewModel
import com.saffron.cook.ui.notedetail.NoteDetailViewModel
import com.saffron.cook.ui.note.NoteEditorViewModel
import com.saffron.cook.ui.notelist.NoteListViewModel
import com.saffron.cook.ui.profile.ProfileViewModel
import com.saffron.cook.ui.search.SearchViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single {
        OkHttpClient.Builder()
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
                }
            }
            .build()
    }
    single {
        Retrofit.Builder()
            .baseUrl("https://www.themealdb.com/api/json/v1/1/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single { get<Retrofit>().create(TheMealDbService::class.java) }
}

val coreDataModule = module {
    single<RecipeRepository> { MealDbRecipeRepository(get()) }
}

val notesModule = module {
    viewModelOf(::NoteEditorViewModel)
    viewModelOf(::NoteListViewModel)
    viewModelOf(::NoteDetailViewModel)
}

val homeModule = module {
    viewModelOf(::HomeViewModel)
}

val detailModule = module {
    viewModelOf(::RecipeDetailViewModel)
}

val cookedRecipesModule = module {
    viewModelOf(::CookedListViewModel)
}

val cookingModule = module {
    viewModelOf(::CookingModeViewModel)
}

val searchModule = module {
    viewModelOf(::SearchViewModel)
}

val favoritesModule = module {
    viewModelOf(::FavoritesViewModel)
}

val profileModule = module {
    viewModelOf(::ProfileViewModel)
}
