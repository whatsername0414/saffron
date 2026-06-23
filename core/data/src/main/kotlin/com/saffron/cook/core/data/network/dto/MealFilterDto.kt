package com.saffron.cook.core.data.network.dto

import com.google.gson.annotations.SerializedName

data class MealFilterItemDto(
    @SerializedName("idMeal") val id: String,
    @SerializedName("strMeal") val title: String,
    @SerializedName("strMealThumb") val thumb: String?,
)

data class MealFilterResponse(
    @SerializedName("meals") val meals: List<MealFilterItemDto>?,
)
