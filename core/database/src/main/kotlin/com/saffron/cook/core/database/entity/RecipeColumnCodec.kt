package com.saffron.cook.core.database.entity

import com.saffron.cook.core.domain.model.Ingredient
import com.saffron.cook.core.domain.model.Step

// U+001E (record separator) between items, U+001F (unit separator) between fields within an item.
// These characters never appear in recipe text from TheMealDB.
private const val ITEM_SEP = "\u001E"
private const val FIELD_SEP = "\u001F"

internal object RecipeColumnCodec {

    fun encodeIngredients(ingredients: List<Ingredient>): String =
        ingredients.joinToString(ITEM_SEP) { "${it.amount}$FIELD_SEP${it.name}" }

    fun decodeIngredients(encoded: String): List<Ingredient> =
        if (encoded.isEmpty()) emptyList()
        else encoded.split(ITEM_SEP).map {
            val parts = it.split(FIELD_SEP, limit = 2)
            Ingredient(parts.getOrElse(0) { "" }, parts.getOrElse(1) { "" })
        }

    fun encodeSteps(steps: List<Step>): String =
        steps.joinToString(ITEM_SEP) { "${it.title}$FIELD_SEP${it.instruction}" }

    fun decodeSteps(encoded: String): List<Step> =
        if (encoded.isEmpty()) emptyList()
        else encoded.split(ITEM_SEP).map {
            val parts = it.split(FIELD_SEP, limit = 2)
            Step(parts.getOrElse(0) { "" }, parts.getOrElse(1) { "" })
        }
}
