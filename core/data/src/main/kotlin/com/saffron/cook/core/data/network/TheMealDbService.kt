package com.saffron.cook.core.data.network

import com.saffron.cook.core.data.network.dto.CategoryResponse
import com.saffron.cook.core.data.network.dto.MealFilterResponse
import com.saffron.cook.core.data.network.dto.MealSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TheMealDbService {
    @GET("search.php")
    suspend fun searchMeals(@Query("s") query: String): MealSearchResponse

    @GET("lookup.php")
    suspend fun lookupMeal(@Query("i") id: String): MealSearchResponse

    @GET("filter.php")
    suspend fun filterByCategory(@Query("c") category: String): MealFilterResponse

    @GET("categories.php")
    suspend fun getCategories(): CategoryResponse

    @GET("random.php")
    suspend fun getRandomMeal(): MealSearchResponse
}
