package com.saffron.cook.di

import com.saffron.cook.BuildConfig
import com.saffron.cook.core.data.network.TheMealDbService
import com.saffron.cook.core.data.repository.MealDbRecipeRepository
import com.saffron.cook.core.data.repository.RecipeRepository
import com.saffron.cook.ui.cooking.CookingModeViewModel
import com.saffron.cook.ui.detail.RecipeDetailViewModel
import com.saffron.cook.ui.home.HomeViewModel
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

val homeModule = module {
    viewModelOf(::HomeViewModel)
}

val detailModule = module {
    viewModelOf(::RecipeDetailViewModel)
}

val cookingModule = module {
    viewModelOf(::CookingModeViewModel)
}
