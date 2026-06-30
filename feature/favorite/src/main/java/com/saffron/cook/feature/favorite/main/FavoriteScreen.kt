package com.saffron.cook.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saffron.cook.R
import com.saffron.cook.core.domain.model.Difficulty
import com.saffron.cook.core.domain.model.Ingredient
import com.saffron.cook.core.domain.model.Recipe
import com.saffron.cook.core.domain.model.Step
import com.saffron.cook.core.presentation.RecipeCard
import com.saffron.cook.core.designsystem.theme.PlayfairDisplayFamily
import com.saffron.cook.core.designsystem.theme.SaffronTheme
import com.saffron.cook.core.designsystem.theme.Truffle
import com.saffron.cook.feature.favorite.main.FavoritesViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoritesScreen(
    onOpenRecipe: (String) -> Unit,
    viewModel: FavoritesViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    FavoritesContent(
        state = state,
        onToggleSave = viewModel::onToggleSave,
        onOpenRecipe = onOpenRecipe,
    )
}

@Composable
private fun FavoritesContent(
    state: FavoritesUiState,
    onToggleSave: (String) -> Unit,
    onOpenRecipe: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Text(
            text = stringResource(R.string.favorites_title),
            style = TextStyle(
                fontFamily = PlayfairDisplayFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 26.sp,
                letterSpacing = (-0.3).sp,
            ),
            color = Truffle,
            modifier = Modifier.padding(top = 14.dp, start = 16.dp, end = 16.dp, bottom = 6.dp),
        )

        if (state.recipes.isEmpty()) {
            FavoritesEmptyState()
        } else {
            FavoritesList(
                recipes = state.recipes,
                onToggleSave = onToggleSave,
                onOpenRecipe = onOpenRecipe,
            )
        }
    }
}

@Composable
private fun FavoritesEmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 64.dp, start = 32.dp, end = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.BookmarkBorder,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color(0xFF8A7A5C),
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.favorites_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF8A7A5C),
            )
        }
    }
}

@Composable
private fun FavoritesList(
    recipes: List<Recipe>,
    onToggleSave: (String) -> Unit,
    onOpenRecipe: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(recipes, key = { it.id }) { recipe ->
            RecipeCard(
                recipe = recipe,
                isSaved = true,
                onToggleSave = onToggleSave,
                onClick = { onOpenRecipe(recipe.id) },
            )
        }
    }
}

private val previewRecipe = Recipe(
    id = "52772",
    title = "Teriyaki Chicken Casserole",
    description = "A hearty weeknight dinner the whole family will love.",
    imageUrl = "",
    categoryId = "chicken",
    ingredients = listOf(Ingredient("3/4 cup", "soy sauce"), Ingredient("1/2 cup", "water")),
    steps = listOf(Step("Marinate", "Mix soy sauce and water, pour over chicken.")),
    cookTimeMinutes = 35,
    servings = 4,
    difficulty = Difficulty.Medium,
)

@Preview(showBackground = true)
@Composable
private fun FavoritesScreenGridPreview() {
    SaffronTheme {
        FavoritesContent(
            state = FavoritesUiState(
                recipes = listOf(
                    previewRecipe,
                    previewRecipe.copy(id = "2", title = "Pasta Carbonara", categoryId = "pasta"),
                    previewRecipe.copy(id = "3", title = "Grilled Salmon", categoryId = "seafood"),
                ),
            ),
            onToggleSave = {},
            onOpenRecipe = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoritesScreenEmptyPreview() {
    SaffronTheme {
        FavoritesContent(
            state = FavoritesUiState(),
            onToggleSave = {},
            onOpenRecipe = {},
        )
    }
}
