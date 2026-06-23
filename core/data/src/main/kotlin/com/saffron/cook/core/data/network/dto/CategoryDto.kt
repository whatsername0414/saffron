package com.saffron.cook.core.data.network.dto

import com.google.gson.annotations.SerializedName

data class CategoryDto(
    @SerializedName("strCategory") val name: String,
)

data class CategoryResponse(
    @SerializedName("categories") val categories: List<CategoryDto>?,
)
