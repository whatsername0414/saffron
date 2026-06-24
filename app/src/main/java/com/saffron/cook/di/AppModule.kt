package com.saffron.cook.di

import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.saffron.cook.BuildConfig
import com.saffron.cook.auth.AuthRepository
import com.saffron.cook.auth.FirebaseAuthRepository
import com.saffron.cook.core.data.network.TheMealDbService
import com.saffron.cook.core.data.repository.MealDbRecipeRepository
import com.saffron.cook.core.data.repository.RecipeRepository
import com.saffron.cook.data.SavedRecipesRepository
import com.saffron.cook.data.local.SaffronDatabase
import com.saffron.cook.ui.cooking.CookingModeViewModel
import com.saffron.cook.ui.detail.RecipeDetailViewModel
import com.saffron.cook.ui.favorites.FavoritesViewModel
import com.saffron.cook.ui.home.HomeViewModel
import com.saffron.cook.ui.login.LoginViewModel
import com.saffron.cook.ui.profile.ProfileViewModel
import com.saffron.cook.ui.search.SearchViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val authModule = module {
    single<FirebaseAuth> { FirebaseAuth.getInstance() }
    single<AuthRepository> { FirebaseAuthRepository(get()) }
}

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

val savedRecipesModule = module {
    single {
        Room.databaseBuilder(androidContext(), SaffronDatabase::class.java, "saffron_db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
    single { get<SaffronDatabase>().savedRecipeDao() }
    single { SavedRecipesRepository(get()) }
}

val homeModule = module {
    viewModelOf(::HomeViewModel)
}

val detailModule = module {
    viewModelOf(::RecipeDetailViewModel)
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

val loginModule = module {
    viewModelOf(::LoginViewModel)
}

val profileModule = module {
    viewModelOf(::ProfileViewModel)
}
