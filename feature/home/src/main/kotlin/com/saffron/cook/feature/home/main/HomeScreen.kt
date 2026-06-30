package com.saffron.cook.feature.home.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.saffron.cook.feature.home.R
import com.saffron.cook.core.domain.model.Category
import com.saffron.cook.core.domain.model.Difficulty
import com.saffron.cook.core.domain.model.Ingredient
import com.saffron.cook.core.domain.model.Recipe
import com.saffron.cook.core.domain.model.Step
import com.saffron.cook.core.designsystem.theme.Cinnamon
import com.saffron.cook.core.designsystem.theme.Cream
import com.saffron.cook.core.designsystem.theme.PlayfairDisplayFamily
import com.saffron.cook.core.designsystem.theme.Saffron
import com.saffron.cook.core.designsystem.theme.Saffron160
import com.saffron.cook.core.designsystem.theme.Saffron20
import com.saffron.cook.core.designsystem.theme.Saffron40
import com.saffron.cook.core.presentation.RecipeCard
import com.saffron.cook.core.designsystem.theme.SaffronTheme
import com.saffron.cook.core.designsystem.theme.Truffle
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit = {},
    onOpenRecipe: (String) -> Unit = {},
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    HomeContent(
        state = state,
        onToggleSave = viewModel::onToggleSave,
        onSelectCategory = viewModel::onSelectCategory,
        onNavigateToSearch = onNavigateToSearch,
        onOpenRecipe = onOpenRecipe,
    )
}

@Composable
private fun HomeContent(
    state: HomeUiState,
    onToggleSave: (String) -> Unit,
    onSelectCategory: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onOpenRecipe: (String) -> Unit,
) {
    val categoryPairs = remember(state.categories) { state.categories.map { it.id to it.name } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item { HomeHeader(greeting = state.greeting, dateLabel = state.dateLabel) }

        item { SearchBar(onClick = onNavigateToSearch) }

        if (state.categories.isNotEmpty()) {
            item {
                CategoryRow(
                    categories = categoryPairs,
                    selectedId = state.selectedCategoryId,
                    onSelectCategory = onSelectCategory,
                )
            }
        }

        state.featuredRecipe?.let { recipe ->
            item {
                FeaturedSection(
                    recipe = recipe,
                    isSaved = recipe.id in state.savedIds,
                    onToggleSave = onToggleSave,
                    onClick = { onOpenRecipe(recipe.id) },
                )
            }
        }

        if (state.isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Saffron, strokeWidth = 2.dp)
                }
            }
        } else if (state.recipes.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.saved_for_the_week).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF8A7A5C),
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 10.dp),
                )
            }
            items(state.recipes, key = { it.id }) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    isSaved = recipe.id in state.savedIds,
                    onToggleSave = onToggleSave,
                    onClick = { onOpenRecipe(recipe.id) },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

// ---- Header ----------------------------------------------------------------

@Composable
private fun HomeHeader(greeting: String, dateLabel: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = dateLabel.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF8A7A5C),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = greeting,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = PlayfairDisplayFamily,
                    fontSize = 28.sp,
                    letterSpacing = (-0.3).sp,
                ),
                color = Truffle,
            )
        }
        InitialsAvatar(initial = "M", size = 44)
    }
}

@Composable
private fun InitialsAvatar(initial: String, size: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(Saffron20),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initial,
            style = MaterialTheme.typography.labelLarge,
            color = Saffron160,
        )
    }
}

// ---- Search bar ------------------------------------------------------------

@Composable
private fun SearchBar(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 6.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(0.5.dp, Color(0xFFD3CFC8), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            tint = Color(0xFF8A7A5C),
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = stringResource(R.string.search_hint),
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF8A7A5C),
        )
    }
}

// ---- Category chips --------------------------------------------------------

@Suppress("UnstableCollections")
@Composable
private fun CategoryRow(
    categories: List<Pair<String, String>>,
    selectedId: String?,
    onSelectCategory: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        categories.forEach { (id, name) ->
            CategoryChip(
                label = name,
                selected = id == selectedId,
                onClick = { onSelectCategory(id) },
            )
        }
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val bg = if (selected) Saffron else Cream
    val text = if (selected) Color.White else Saffron160

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = text,
        )
    }
}

// ---- Featured card ---------------------------------------------------------

@Composable
private fun FeaturedSection(
    recipe: Recipe,
    isSaved: Boolean,
    onToggleSave: (String) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(start = 16.dp, end = 16.dp, bottom = 18.dp)) {
        Text(
            text = stringResource(R.string.featured_tonight).uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = Saffron,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 10f)
                .clip(RoundedCornerShape(14.dp))
                .clickable(onClick = onClick),
        ) {
            AsyncImage(
                model = recipe.imageUrl,
                contentDescription = recipe.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.45f to Color.Transparent,
                            1.0f to Color(0xB81A1208),
                        )
                    )
            )
            IconButton(
                onClick = { onToggleSave(recipe.id) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .clip(CircleShape)
                    .background(Color(0xEBFFFFFF)),
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = if (isSaved) stringResource(R.string.action_saved) else stringResource(R.string.action_save),
                    tint = if (isSaved) Saffron else Cinnamon
                )
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, end = 16.dp, bottom = 14.dp),
            ) {
                Text(
                    text = recipe.categoryId.replaceFirstChar { it.uppercase() }.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = Saffron40,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontFamily = PlayfairDisplayFamily,
                        fontSize = 26.sp,
                        lineHeight = 30.sp,
                        letterSpacing = (-0.3).sp,
                    ),
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                val cookTimeStr = recipe.cookTimeMinutes?.let { stringResource(R.string.meta_duration_min, it) }
                val metaItems = buildList {
                    cookTimeStr?.let { add(Icons.Outlined.Schedule to it) }
                    recipe.difficulty?.let { add(Icons.Outlined.LocalFireDepartment to it.name) }
                }
                if (metaItems.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        metaItems.forEach { (icon, label) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Icon(icon, null, Modifier.size(15.dp), Color.White.copy(alpha = 0.85f))
                                Text(label, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.85f))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---- Previews --------------------------------------------------------------

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

@Preview(showBackground = true, backgroundColor = 0xFFF5F0E8)
@Composable
private fun HomeContentPreview() {
    SaffronTheme {
        HomeContent(
            state = HomeUiState(
                isLoading = false,
                greeting = "Good evening.",
                dateLabel = "Monday, 23 June",
                categories = listOf(
                    Category(id = "chicken", name = "Chicken"),
                    Category(id = "pasta", name = "Pasta"),
                    Category(id = "seafood", name = "Seafood"),
                ),
                selectedCategoryId = "chicken",
                featuredRecipe = previewRecipe,
                recipes = listOf(
                    previewRecipe,
                    previewRecipe.copy(id = "2", title = "Pasta Carbonara", categoryId = "pasta"),
                    previewRecipe.copy(id = "3", title = "Grilled Salmon", categoryId = "seafood"),
                ),
            ),
            onToggleSave = {},
            onSelectCategory = {},
            onNavigateToSearch = {},
            onOpenRecipe = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F0E8)
@Composable
private fun HomeLoadingPreview() {
    SaffronTheme {
        HomeContent(
            state = HomeUiState(isLoading = true, greeting = "Good morning.", dateLabel = "Tuesday, 24 June"),
            onToggleSave = {},
            onSelectCategory = {},
            onNavigateToSearch = {},
            onOpenRecipe = {},
        )
    }
}
