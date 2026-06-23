package com.saffron.cook.ui.home

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
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.People
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.saffron.cook.R
import com.saffron.cook.core.data.model.Category
import com.saffron.cook.core.data.model.Difficulty
import com.saffron.cook.core.data.model.Ingredient
import com.saffron.cook.core.data.model.Recipe
import com.saffron.cook.core.data.model.Step
import com.saffron.cook.ui.theme.Cinnamon
import com.saffron.cook.ui.theme.Cream
import com.saffron.cook.ui.theme.Linen
import com.saffron.cook.ui.theme.PlayfairDisplayFamily
import com.saffron.cook.ui.theme.Saffron
import com.saffron.cook.ui.theme.Saffron160
import com.saffron.cook.ui.theme.Saffron20
import com.saffron.cook.ui.theme.Saffron40
import com.saffron.cook.ui.theme.SaffronTheme
import com.saffron.cook.ui.theme.Truffle
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit = {},
    onOpenRecipe: (String) -> Unit = {},
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    HomeContent(
        state              = state,
        onToggleSave       = viewModel::onToggleSave,
        onSelectCategory   = viewModel::onSelectCategory,
        onNavigateToSearch = onNavigateToSearch,
        onOpenRecipe       = onOpenRecipe,
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
    val gridRows = state.gridRecipes.chunked(2)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Linen),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item { HomeHeader(greeting = state.greeting, dateLabel = state.dateLabel) }

        item { SearchBar(onClick = onNavigateToSearch) }

        if (state.categories.isNotEmpty()) {
            item {
                CategoryRow(
                    categories       = state.categories.map { it.id to it.name },
                    selectedId       = state.selectedCategoryId,
                    onSelectCategory = onSelectCategory,
                )
            }
        }

        state.featuredRecipe?.let { recipe ->
            item {
                FeaturedSection(
                    recipe       = recipe,
                    isSaved      = recipe.id in state.savedIds,
                    onToggleSave = onToggleSave,
                    onClick      = { onOpenRecipe(recipe.id) },
                )
            }
        }

        if (state.isLoading) {
            item {
                Box(
                    modifier         = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Saffron, strokeWidth = 2.dp)
                }
            }
        } else if (gridRows.isNotEmpty()) {
            item {
                Text(
                    text     = stringResource(R.string.saved_for_the_week),
                    style    = MaterialTheme.typography.labelMedium,
                    color    = Color(0xFF8A7A5C),
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 10.dp),
                )
            }
            items(gridRows) { row ->
                Row(
                    modifier              = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    row.forEach { recipe ->
                        RecipeCard(
                            recipe       = recipe,
                            isSaved      = recipe.id in state.savedIds,
                            onToggleSave = onToggleSave,
                            onClick      = { onOpenRecipe(recipe.id) },
                            modifier     = Modifier.weight(1f),
                        )
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

// ---- Header ----------------------------------------------------------------

@Composable
private fun HomeHeader(greeting: String, dateLabel: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text  = dateLabel.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF8A7A5C),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text  = greeting,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontFamily    = PlayfairDisplayFamily,
                    fontSize      = 28.sp,
                    letterSpacing = (-0.3).sp,
                ),
                color = Truffle,
            )
        }
        InitialsAvatar(initial = "M", size = 44)
    }
}

@Composable
private fun InitialsAvatar(initial: String, size: Int) {
    Box(
        modifier         = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(Saffron20),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text  = initial,
            style = MaterialTheme.typography.labelLarge,
            color = Saffron160,
        )
    }
}

// ---- Search bar ------------------------------------------------------------

@Composable
private fun SearchBar(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(0.5.dp, Color(0xFFD3CFC8), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector        = Icons.Outlined.Search,
            contentDescription = null,
            tint               = Color(0xFF8A7A5C),
            modifier           = Modifier.size(20.dp),
        )
        Text(
            text  = stringResource(R.string.search_hint),
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF8A7A5C),
        )
    }
}

// ---- Category chips --------------------------------------------------------

@Composable
private fun CategoryRow(
    categories: List<Pair<String, String>>,
    selectedId: String?,
    onSelectCategory: (String) -> Unit,
) {
    Row(
        modifier              = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        categories.forEach { (id, name) ->
            CategoryChip(
                label    = name,
                selected = id == selectedId,
                onClick  = { onSelectCategory(id) },
            )
        }
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val bg     = if (selected) Saffron20 else Color.Transparent
    val border = if (selected) Color(0xFFE0A020) else Color(0xFFC9C2B6)
    val text   = if (selected) Saffron160 else Cinnamon

    Box(
        modifier         = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(0.5.dp, border, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text  = label,
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
) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 18.dp)) {
        Text(
            text     = stringResource(R.string.featured_tonight),
            style    = MaterialTheme.typography.labelMedium,
            color    = Saffron,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 10f)
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onClick),
        ) {
            AsyncImage(
                model              = recipe.imageUrl,
                contentDescription = recipe.title,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.45f to Color.Transparent,
                            1.0f  to Color(0xB81A1208),
                        )
                    )
            )
            IconButton(
                onClick  = { onToggleSave(recipe.id) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xEBFFFFFF)),
            ) {
                Icon(
                    imageVector        = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.Bookmark,
                    contentDescription = if (isSaved) stringResource(R.string.action_saved) else stringResource(R.string.action_save),
                    tint               = if (isSaved) Saffron else Cinnamon,
                    modifier           = Modifier.size(19.dp),
                )
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, end = 56.dp, bottom = 14.dp),
            ) {
                Text(
                    text  = recipe.categoryId.replaceFirstChar { it.uppercase() }.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = Saffron40,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text     = recipe.title,
                    style    = MaterialTheme.typography.displaySmall.copy(
                        fontFamily    = PlayfairDisplayFamily,
                        fontSize      = 26.sp,
                        lineHeight    = 30.sp,
                        letterSpacing = (-0.3).sp,
                    ),
                    color    = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                val metaItems = buildList {
                    recipe.cookTimeMinutes?.let { add(Icons.Outlined.Schedule to "$it min") }
                    recipe.difficulty?.let { add(Icons.Outlined.LocalFireDepartment to it.name) }
                }
                if (metaItems.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        metaItems.forEach { (icon, label) ->
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
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
    id             = "52772",
    title          = "Teriyaki Chicken Casserole",
    description    = "A hearty weeknight dinner the whole family will love.",
    imageUrl       = "",
    categoryId     = "chicken",
    ingredients    = listOf(Ingredient("3/4 cup", "soy sauce"), Ingredient("1/2 cup", "water")),
    steps          = listOf(Step("Marinate", "Mix soy sauce and water, pour over chicken.")),
    cookTimeMinutes = 35,
    servings       = 4,
    difficulty     = Difficulty.Medium,
)

@Preview(showBackground = true, backgroundColor = 0xFFF5F0E8)
@Composable
private fun HomeContentPreview() {
    SaffronTheme {
        HomeContent(
            state = HomeUiState(
                isLoading          = false,
                greeting           = "Good evening.",
                dateLabel          = "Monday, 23 June",
                categories         = listOf(
                    Category(id = "chicken", name = "Chicken"),
                    Category(id = "pasta",   name = "Pasta"),
                    Category(id = "seafood", name = "Seafood"),
                ),
                selectedCategoryId = "chicken",
                featuredRecipe     = previewRecipe,
                gridRecipes        = listOf(
                    previewRecipe,
                    previewRecipe.copy(id = "2", title = "Pasta Carbonara", categoryId = "pasta"),
                    previewRecipe.copy(id = "3", title = "Grilled Salmon",  categoryId = "seafood"),
                ),
            ),
            onToggleSave       = {},
            onSelectCategory   = {},
            onNavigateToSearch = {},
            onOpenRecipe       = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F0E8)
@Composable
private fun HomeLoadingPreview() {
    SaffronTheme {
        HomeContent(
            state              = HomeUiState(isLoading = true, greeting = "Good morning.", dateLabel = "Tuesday, 24 June"),
            onToggleSave       = {},
            onSelectCategory   = {},
            onNavigateToSearch = {},
            onOpenRecipe       = {},
        )
    }
}

// ---- Recipe card (2-column grid) -------------------------------------------

@Composable
private fun RecipeCard(
    recipe: Recipe,
    isSaved: Boolean,
    onToggleSave: (String) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(0.5.dp, Color(0xFFE4DFD5), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .background(Cream),
        ) {
            AsyncImage(
                model              = recipe.imageUrl,
                contentDescription = recipe.title,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize(),
            )
            IconButton(
                onClick  = { onToggleSave(recipe.id) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xEBFFFFFF)),
            ) {
                Icon(
                    imageVector        = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.Bookmark,
                    contentDescription = if (isSaved) stringResource(R.string.action_saved) else stringResource(R.string.action_save),
                    tint               = if (isSaved) Saffron else Cinnamon,
                    modifier           = Modifier.size(18.dp),
                )
            }
        }
        Column(
            modifier            = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text  = recipe.categoryId.replaceFirstChar { it.uppercase() }.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = Saffron,
            )
            Text(
                text     = recipe.title,
                style    = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = PlayfairDisplayFamily,
                    fontSize   = 18.sp,
                    lineHeight = 22.sp,
                ),
                color    = Truffle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            val metaItems = buildList {
                recipe.cookTimeMinutes?.let { add(Icons.Outlined.Schedule to "$it min") }
                recipe.servings?.let { add(Icons.Outlined.People to "$it") }
            }
            if (metaItems.isNotEmpty()) {
                Spacer(Modifier.height(2.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    metaItems.forEach { (icon, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                        ) {
                            Icon(icon, null, Modifier.size(13.dp), Color(0xFF8A7A5C))
                            Text(label, style = MaterialTheme.typography.bodySmall, color = Color(0xFF8A7A5C))
                        }
                    }
                }
            }
        }
    }
}
