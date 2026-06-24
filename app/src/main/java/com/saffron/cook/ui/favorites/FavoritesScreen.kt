package com.saffron.cook.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.saffron.cook.data.local.SavedRecipeEntity
import com.saffron.cook.data.local.toRecipe
import com.saffron.cook.ui.components.RecipeCard
import com.saffron.cook.ui.theme.PlayfairDisplayFamily
import com.saffron.cook.ui.theme.SaffronTheme
import com.saffron.cook.ui.theme.Truffle
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

        if (state.savedRecipes.isEmpty()) {
            FavoritesEmptyState()
        } else {
            FavoritesGrid(
                savedRecipes = state.savedRecipes,
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
private fun FavoritesGrid(
    savedRecipes: List<SavedRecipeEntity>,
    onToggleSave: (String) -> Unit,
    onOpenRecipe: (String) -> Unit,
) {
    val rows = savedRecipes.chunked(2)
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 8.dp,
            bottom = 24.dp,
        ),
    ) {
        items(rows) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { entity ->
                    RecipeCard(
                        recipe = entity.toRecipe(),
                        isSaved = true,
                        onToggleSave = onToggleSave,
                        onClick = { onOpenRecipe(entity.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoritesScreenGridPreview() {
    SaffronTheme {
        FavoritesContent(
            state = FavoritesUiState(
                savedRecipes = listOf(
                    SavedRecipeEntity("1", "Saffron risotto", "", 35, 4, "Dinner"),
                    SavedRecipeEntity("2", "Shakshuka", "", 25, 2, "Breakfast"),
                    SavedRecipeEntity("3", "Rosemary focaccia", "", 60, 8, "Baking"),
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
