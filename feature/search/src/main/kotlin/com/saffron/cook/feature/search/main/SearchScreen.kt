package com.saffron.cook.feature.search.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.saffron.cook.feature.search.R
import com.saffron.cook.core.domain.model.Recipe
import com.saffron.cook.core.domain.model.RecipeFilter
import com.saffron.cook.core.designsystem.theme.InterFamily
import com.saffron.cook.core.designsystem.theme.PlayfairDisplayFamily
import com.saffron.cook.core.designsystem.theme.SaffronTheme
import com.saffron.cook.core.designsystem.theme.saffronColors
import org.koin.androidx.compose.koinViewModel

private val RecipeFilter.labelRes: Int
    get() = when (this) {
        RecipeFilter.All -> R.string.filter_all
        RecipeFilter.Breakfast -> R.string.filter_breakfast
        RecipeFilter.Lunch -> R.string.filter_lunch
        RecipeFilter.Dinner -> R.string.filter_dinner
        RecipeFilter.Baking -> R.string.filter_baking
    }

@Composable
fun SearchScreen(
    onOpenRecipe: (String) -> Unit,
    viewModel: SearchViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var activeFilter by remember { mutableStateOf(RecipeFilter.All) }
    val colors = MaterialTheme.saffronColors

    val baseRecipes = if (state.query.isBlank()) state.initialRecipes else state.results
    val displayedRecipes = if (activeFilter == RecipeFilter.All) baseRecipes
    else baseRecipes.filter { it.categoryId.equals(activeFilter.categoryId, ignoreCase = true) }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        Text(
            text = stringResource(R.string.nav_search),
            style = TextStyle(
                fontFamily = PlayfairDisplayFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 26.sp,
                letterSpacing = (-0.3).sp,
            ),
            color = colors.textPrimary,
            modifier = Modifier.padding(top = 14.dp, start = 16.dp, end = 16.dp, bottom = 6.dp),
        )

        Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 10.dp)) {
            SearchInput(
                query = state.query,
                onQueryChange = viewModel::onQueryChange,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 2.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            RecipeFilter.entries.forEach { filter ->
                FilterChip(
                    label = stringResource(filter.labelRes),
                    selected = filter == activeFilter,
                    onClick = { activeFilter = filter },
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = colors.accent, strokeWidth = 2.dp)
                }
                state.isError -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.search_error),
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.textSecondary,
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.onQueryChange(state.query) },
                        ) {
                            Text(stringResource(R.string.error_retry))
                        }
                    }
                }
                state.query.isNotBlank() && displayedRecipes.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 48.dp, start = 8.dp, end = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            tint = colors.textSecondary,
                            modifier = Modifier.size(32.dp),
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.search_no_results, state.query),
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.textSecondary,
                        )
                    }
                }
                displayedRecipes.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp),
                    ) {
                        itemsIndexed(displayedRecipes, key = { _, r -> r.id }) { _, recipe ->
                            ResultRow(
                                recipe = recipe,
                                isSaved = recipe.id in state.savedIds,
                                onToggleSave = { viewModel.onToggleSave(recipe.id) },
                                onClick = { onOpenRecipe(recipe.id) },
                            )
                        }
                    }
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun SearchInput(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.saffronColors
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val borderWidth = if (isFocused) 1.dp else 0.5.dp
    val borderColor = if (isFocused) colors.accent else colors.borderTertiary

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        singleLine = true,
        textStyle = TextStyle(
            fontFamily = InterFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            color = colors.textPrimary,
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        cursorBrush = SolidColor(colors.accent),
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(borderWidth, borderColor, RoundedCornerShape(10.dp))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = colors.textTertiary,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(8.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            text = stringResource(R.string.search_hint),
                            style = TextStyle(
                                fontFamily = InterFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 15.sp,
                                color = colors.textTertiary,
                            ),
                        )
                    }
                    innerTextField()
                }
            }
        },
    )
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.saffronColors
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) colors.accent else colors.surfaceCream)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        Text(
            text = label.uppercase(),
            style = TextStyle(
                fontFamily = InterFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                letterSpacing = 0.88.sp,
            ),
            color = if (selected) Color.White else colors.onCream,
        )
    }
}

@Composable
private fun ResultRow(
    recipe: Recipe,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.saffronColors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AsyncImage(
            model = recipe.imageUrl,
            contentDescription = recipe.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(92.dp)
                .height(70.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.surfaceCream),
        )
        Column(modifier = Modifier.weight(1f)) {
            if (recipe.categoryId.isNotBlank()) {
                Text(
                    text = recipe.categoryId.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.accent,
                )
            }
            Text(
                text = recipe.title,
                style = TextStyle(
                    fontFamily = PlayfairDisplayFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                ),
                color = colors.textPrimary,
                maxLines = 2,
                modifier = Modifier.padding(top = 2.dp, bottom = 4.dp),
            )
            val cookTimeStr = recipe.cookTimeMinutes?.let { stringResource(R.string.meta_duration_min, it) }
            val servingsStr = recipe.servings?.let { stringResource(R.string.meta_serves, it) }
            val hasMeta = cookTimeStr != null || servingsStr != null
            if (hasMeta) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    cookTimeStr?.let { MetaItem(icon = Icons.Outlined.Schedule, text = it) }
                    servingsStr?.let { MetaItem(icon = Icons.Outlined.People, text = it) }
                }
            }
        }
        IconButton(onClick = onToggleSave) {
            Icon(
                imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                contentDescription = if (isSaved) stringResource(R.string.action_saved) else stringResource(R.string.action_save),
                tint = if (isSaved) colors.accent else colors.textSecondary,
            )
        }
    }
}

@Composable
private fun MetaItem(icon: ImageVector, text: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.saffronColors
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.textTertiary,
            modifier = Modifier.size(15.dp),
        )
        Text(
            text = text,
            style = TextStyle(
                fontFamily = InterFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = colors.textTertiary,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenIdlePreview() {
    val fakeRecipes = listOf(
        Recipe(id = "1", title = "Teriyaki Chicken Casserole", description = "", imageUrl = "", categoryId = "chicken", ingredients = emptyList(), steps = emptyList(), servings = 5),
        Recipe(id = "2", title = "Chicken Alfredo", description = "", imageUrl = "", categoryId = "pasta", ingredients = emptyList(), steps = emptyList(), servings = 2),
    )
    SaffronTheme {
        Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
            Text(
                text = stringResource(R.string.nav_search),
                style = TextStyle(fontFamily = PlayfairDisplayFamily, fontWeight = FontWeight.Normal, fontSize = 26.sp, letterSpacing = (-0.3).sp),
                color = MaterialTheme.saffronColors.textPrimary,
                modifier = Modifier.padding(top = 14.dp, start = 16.dp, end = 16.dp, bottom = 6.dp),
            )
            Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 10.dp)) {
                SearchInput(query = "", onQueryChange = {}, modifier = Modifier.fillMaxWidth())
            }
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()).padding(start = 16.dp, end = 16.dp, top = 2.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                RecipeFilter.entries.forEach { FilterChip(label = stringResource(it.labelRes), selected = it == RecipeFilter.All, onClick = {}) }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                itemsIndexed(fakeRecipes) { _, r ->
                    ResultRow(recipe = r, isSaved = false, onToggleSave = {}, onClick = {})
                }
            }
        }
    }
}
