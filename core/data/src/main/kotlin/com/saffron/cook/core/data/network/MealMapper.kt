package com.saffron.cook.core.data.network

import com.saffron.cook.core.data.model.Category
import com.saffron.cook.core.data.model.Ingredient
import com.saffron.cook.core.data.model.Recipe
import com.saffron.cook.core.data.model.Step
import com.saffron.cook.core.data.network.dto.CategoryDto
import com.saffron.cook.core.data.network.dto.MealDto
import com.saffron.cook.core.data.network.dto.MealFilterItemDto

fun MealDto.toRecipe(isFeatured: Boolean = false): Recipe {
    val names =
        listOf(
            ingredient1,
            ingredient2,
            ingredient3,
            ingredient4,
            ingredient5,
            ingredient6,
            ingredient7,
            ingredient8,
            ingredient9,
            ingredient10,
            ingredient11,
            ingredient12,
            ingredient13,
            ingredient14,
            ingredient15,
            ingredient16,
            ingredient17,
            ingredient18,
            ingredient19,
            ingredient20,
        )
    val amounts =
        listOf(
            measure1,
            measure2,
            measure3,
            measure4,
            measure5,
            measure6,
            measure7,
            measure8,
            measure9,
            measure10,
            measure11,
            measure12,
            measure13,
            measure14,
            measure15,
            measure16,
            measure17,
            measure18,
            measure19,
            measure20,
        )
    val ingredients =
        names.zip(amounts).mapNotNull { (name, amount) ->
            if (!name.isNullOrBlank()) {
                Ingredient(amount = amount.orEmpty().trim(), name = name.trim())
            } else {
                null
            }
        }

    val steps = instructions?.parseSteps() ?: emptyList()
    val description =
        instructions
            ?.split("\r\n", "\n", "\r")
            ?.firstOrNull { it.isNotBlank() }
            ?.take(200)
            ?: ""

    return Recipe(
        id = id,
        title = title,
        description = description,
        imageUrl = thumb ?: "",
        categoryId = category?.lowercase() ?: "",
        ingredients = ingredients,
        steps = steps,
        isFeatured = isFeatured,
    )
}

fun MealFilterItemDto.toPartialRecipe(categoryId: String): Recipe =
    Recipe(
        id = id,
        title = title,
        description = "",
        imageUrl = thumb ?: "",
        categoryId = categoryId,
        ingredients = emptyList(),
        steps = emptyList(),
    )

fun CategoryDto.toCategory(): Category =
    Category(
        id = name.lowercase(),
        name = name,
    )

private val paragraphSplit = Regex("""\r?\n\s*\r?\n""")

private fun String.parseSteps(): List<Step> {
    val paragraphs = paragraphSplit.split(this)
        .map { it.trim() }
        .filter { it.isNotBlank() }

    val lines = if (paragraphs.size > 1) {
        paragraphs
    } else {
        split("\r\n", "\n", "\r")
            .map { it.trim() }
            .filter { it.isNotBlank() }
    }

    return lines.mapIndexed { index, text ->
        Step(title = "Step ${index + 1}", instruction = text)
    }
}
