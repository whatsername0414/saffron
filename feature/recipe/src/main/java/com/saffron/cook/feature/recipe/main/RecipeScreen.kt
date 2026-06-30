package com.saffron.cook.feature.recipe.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.saffron.cook.core.domain.model.Difficulty
import com.saffron.cook.core.domain.model.Ingredient
import com.saffron.cook.core.domain.model.Recipe
import com.saffron.cook.core.domain.model.Step
import com.saffron.cook.core.designsystem.theme.Cinnamon
import com.saffron.cook.core.designsystem.theme.Cream
import com.saffron.cook.core.designsystem.theme.PlayfairDisplayFamily
import com.saffron.cook.core.designsystem.theme.Saffron
import com.saffron.cook.core.designsystem.theme.Saffron160
import com.saffron.cook.core.designsystem.theme.Saffron40
import com.saffron.cook.core.designsystem.theme.SaffronTheme
import com.saffron.cook.core.designsystem.theme.Truffle
import com.saffron.cook.feature.recipe.R
import kotlin.math.roundToInt
import org.koin.androidx.compose.koinViewModel

@Composable
fun RecipeDetailScreen(
    recipeId: String,
    onBack: () -> Unit,
    onStartCooking: (String) -> Unit,
    viewModel: RecipeViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    when {
        state.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator(color = Saffron, strokeWidth = 2.dp)
        }
        state.isError -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.error_load_failed),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Cinnamon,
                )
                Spacer(Modifier.height(12.dp))
                Button(onClick = viewModel::retry) {
                    Text(stringResource(R.string.error_retry))
                }
            }
        }
        state.recipe == null -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text(stringResource(R.string.recipe_not_found), style = MaterialTheme.typography.bodyLarge, color = Cinnamon)
        }
        else -> DetailContent(
            recipe = state.recipe!!,
            isSaved = state.isSaved,
            onBack = onBack,
            onToggleSave = viewModel::onToggleSave,
            onStartCooking = { onStartCooking(recipeId) },
        )
    }
}

@Composable
private fun DetailContent(
    recipe: Recipe,
    isSaved: Boolean,
    onBack: () -> Unit,
    onToggleSave: () -> Unit,
    onStartCooking: () -> Unit,
) {
    val cookTimeCaption = stringResource(R.string.meta_cook_time)
    val servingsCaption = stringResource(R.string.meta_servings)
    val difficultyCaption = stringResource(R.string.meta_difficulty)
    val cookTimeStr = recipe.cookTimeMinutes?.let { stringResource(R.string.meta_duration_min, it) }
    val servingsStr = recipe.servings?.let { stringResource(R.string.meta_serves, it) }

    Box(Modifier.fillMaxSize().background(Color.White)) {
        LazyColumn(contentPadding = PaddingValues(bottom = 96.dp)) {
            // Hero image
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                        .background(Cream),
                ) {
                    AsyncImage(
                        model = recipe.imageUrl,
                        contentDescription = recipe.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                    // Back
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = Truffle,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    // Bookmark
                    IconButton(
                        onClick = onToggleSave,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xEBFFFFFF)),
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (isSaved) stringResource(R.string.action_saved) else stringResource(R.string.action_save),
                            tint = if (isSaved) Saffron else Cinnamon,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }

            // Title block
            item {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 18.dp)) {
                    Text(
                        text = recipe.categoryId.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelMedium,
                        color = Saffron,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontFamily = PlayfairDisplayFamily,
                            fontSize = 32.sp,
                            lineHeight = (32 * 1.15).sp,
                            letterSpacing = (-0.3).sp,
                            fontWeight = FontWeight.Normal,
                        ),
                        color = Truffle,
                    )
                }
            }

            // Rating row (only if data is available)
            val rating = recipe.rating
            if (rating != null) {
                item {
                    Row(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            for (n in 1..5) {
                                val filled = n <= rating.roundToInt()
                                Icon(
                                    imageVector = if (filled) Icons.Filled.Star else Icons.Filled.StarBorder,
                                    contentDescription = null,
                                    tint = Saffron40,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }
                        val ratingText = buildString {
                            append("%.1f".format(rating))
                            if (recipe.ratingCount != null) append(" (${recipe.ratingCount})")
                        }
                        Text(
                            text = ratingText,
                            style = MaterialTheme.typography.bodySmall,
                            color = Cinnamon,
                        )
                    }
                }
            }

            // 3-up meta strip (only if at least one value available)
            val metaCards = buildList {
                cookTimeStr?.let { add(Triple(Icons.Outlined.Schedule, it, cookTimeCaption)) }
                servingsStr?.let { add(Triple(Icons.Outlined.People, it, servingsCaption)) }
                recipe.difficulty?.let { add(Triple(Icons.Outlined.LocalFireDepartment, it.name, difficultyCaption)) }
            }
            if (metaCards.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        metaCards.forEach { (icon, label, caption) ->
                            MetaCard(icon = icon, label = label, caption = caption, modifier = Modifier.weight(1f))
                        }
                    }
                }
            } else {
                item { Spacer(Modifier.height(14.dp)) }
            }

            // Description
            if (recipe.description.isNotBlank()) {
                item {
                    Text(
                        text = recipe.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Truffle,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }

            // Ingredients
            if (recipe.ingredients.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.ingredients),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = PlayfairDisplayFamily,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        color = Truffle,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 12.dp),
                    )
                }
                itemsIndexed(
                    items = recipe.ingredients,
                    key = { i, _ -> i },
                ) { index, ingredient ->
                    IngredientRow(
                        ingredient = ingredient,
                        showDivider = index < recipe.ingredients.lastIndex,
                    )
                }
            }
        }

        // Sticky "Start cooking" CTA
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.35f to Color.White,
                    )
                )
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp),
        ) {
            Button(
                onClick = onStartCooking,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Saffron),
                elevation = ButtonDefaults.buttonElevation(0.dp),
            ) {
                Text(
                    text = stringResource(R.string.start_cooking),
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp),
                    color = Color.White,
                )
                Spacer(Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.White,
                )
            }
        }
    }
}

@Composable
private fun MetaCard(
    icon: ImageVector,
    label: String,
    caption: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Cream)
            .border(0.5.dp, Color(0xFFE4DFD5), RoundedCornerShape(14.dp))
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(icon, contentDescription = null, tint = Saffron160, modifier = Modifier.size(20.dp))
        Text(label, style = MaterialTheme.typography.labelLarge, color = Truffle)
        Text(caption, style = MaterialTheme.typography.bodySmall, color = Cinnamon)
    }
}

// ---- Preview ---------------------------------------------------------------

private val previewDetailRecipe = Recipe(
    id = "52772",
    title = "Teriyaki Chicken Casserole",
    description = "Soy sauce, brown sugar, and sesame oil come together in a sticky glaze that coats tender chicken and vibrant vegetables.",
    imageUrl = "",
    categoryId = "chicken",
    ingredients = listOf(
        Ingredient("3/4 cup", "soy sauce"),
        Ingredient("1/2 cup", "water"),
        Ingredient("1/4 cup", "brown sugar"),
        Ingredient("1 tbsp", "sesame oil"),
    ),
    steps = listOf(
        Step("Marinate", "Whisk together soy sauce, water, brown sugar, and sesame oil."),
        Step("Cook", "Pour over chicken in a baking dish. Bake at 190 °C for 35 minutes."),
    ),
    cookTimeMinutes = 35,
    servings = 4,
    difficulty = Difficulty.Medium,
    isFeatured = false,
)

@Preview(showBackground = true)
@Composable
private fun RecipeDetailScreenPreview() {
    SaffronTheme {
        DetailContent(
            recipe = previewDetailRecipe,
            isSaved = false,
            onBack = {},
            onToggleSave = {},
            onStartCooking = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RecipeDetailSavedPreview() {
    SaffronTheme {
        DetailContent(
            recipe = previewDetailRecipe,
            isSaved = true,
            onBack = {},
            onToggleSave = {},
            onStartCooking = {},
        )
    }
}

@Composable
private fun IngredientRow(ingredient: Ingredient, showDivider: Boolean, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.padding(vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Saffron),
            )
            Spacer(Modifier.width(12.dp))
            val text = buildString {
                if (ingredient.amount.isNotBlank()) { append(ingredient.amount); append(" ") }
                append(ingredient.name)
            }
            Text(text, style = MaterialTheme.typography.bodyLarge, color = Truffle)
        }
        if (showDivider) {
            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE4DFD5))
        }
    }
}
