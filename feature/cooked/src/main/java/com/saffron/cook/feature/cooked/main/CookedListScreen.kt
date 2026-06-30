package com.saffron.cook.feature.cooked.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.saffron.cook.core.designsystem.theme.Cinnamon
import com.saffron.cook.core.designsystem.theme.Cream
import com.saffron.cook.core.designsystem.theme.PlayfairDisplayFamily
import com.saffron.cook.core.designsystem.theme.Saffron
import com.saffron.cook.core.designsystem.theme.Saffron160
import com.saffron.cook.core.designsystem.theme.SaffronTheme
import com.saffron.cook.core.designsystem.theme.Truffle
import com.saffron.cook.feature.cooked.R
import org.koin.androidx.compose.koinViewModel

private val TextTertiary = Color(0xFF8A7A5C)

@Composable
fun CookedListScreen(
    onBack: () -> Unit,
    onOpenRecipe: (String) -> Unit,
    onToggleSave: (String) -> Unit = {},
    viewModel: CookedListViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    CookedListContent(state = state, onBack = onBack, onOpenRecipe = onOpenRecipe, onToggleSave = onToggleSave)
}

@Composable
private fun CookedListContent(
    state: CookedListUiState,
    onBack: () -> Unit,
    onOpenRecipe: (String) -> Unit,
    onToggleSave: (String) -> Unit,
) {
    Surface(color = Color.White, modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 16.dp, top = 14.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.cd_back),
                        tint = Truffle,
                    )
                }
                Text(
                    text = stringResource(R.string.cooked_title),
                    style = TextStyle(
                        fontFamily = PlayfairDisplayFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 26.sp,
                        letterSpacing = (-0.3).sp,
                    ),
                    color = Truffle,
                )
            }

            if (state.items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(horizontal = 32.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocalFireDepartment,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(32.dp),
                        )
                        Text(
                            text = stringResource(R.string.cooked_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Cinnamon,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.widthIn(max = 240.dp),
                        )
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.cooked_summary, state.totalCooked, state.items.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 4.dp,
                        bottom = 24.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.items, key = { it.recipeId }) { item ->
                        CookedCard(
                            item = item,
                            onOpen = { onOpenRecipe(item.recipeId) },
                            onToggleSave = { onToggleSave(item.recipeId) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CookedCard(
    item: CookedListItem,
    onOpen: () -> Unit,
    onToggleSave: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .width(92.dp)
                .height(70.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Cream),
        ) {
            AsyncImage(
                model = item.recipeImage,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.recipeCategory.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = Saffron,
            )
            Text(
                text = item.recipeName,
                style = TextStyle(
                    fontFamily = PlayfairDisplayFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                ),
                color = Truffle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(vertical = 2.dp),
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MetaChip(
                    icon = { Icon(Icons.Outlined.LocalFireDepartment, contentDescription = null, tint = Saffron160, modifier = Modifier.size(15.dp)) },
                    label = item.timesLabel,
                )
                MetaChip(
                    icon = { Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Saffron160, modifier = Modifier.size(15.dp)) },
                    label = item.lastCookedLabel,
                )
            }
        }
        IconButton(onClick = onToggleSave) {
            Icon(
                imageVector = if (item.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                contentDescription = if (item.isSaved) stringResource(R.string.action_saved) else stringResource(R.string.action_save),
                tint = if (item.isSaved) Saffron else Cinnamon,
            )
        }
    }
}

@Composable
private fun MetaChip(icon: @Composable () -> Unit, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        icon()
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Cinnamon)
    }
}

@Preview(showBackground = true, name = "Empty state")
@Composable
private fun CookedListEmptyPreview() {
    SaffronTheme {
        CookedListContent(state = CookedListUiState(), onBack = {}, onOpenRecipe = {}, onToggleSave = {})
    }
}

@Preview(showBackground = true, name = "With items")
@Composable
private fun CookedListFilledPreview() {
    SaffronTheme {
        CookedListContent(
            state = CookedListUiState(
                totalCooked = 7,
                items = listOf(
                    CookedListItem(
                        recipeId = "1",
                        recipeName = "Saffron Risotto",
                        recipeImage = "",
                        recipeCategory = "Dinner",
                        timesLabel = "3 times",
                        lastCookedLabel = "last Jun 12",
                        isSaved = true,
                    ),
                    CookedListItem(
                        recipeId = "2",
                        recipeName = "Rosemary Focaccia",
                        recipeImage = "",
                        recipeCategory = "Baking",
                        timesLabel = "Once",
                        lastCookedLabel = "last Jun 8",
                        isSaved = false,
                    ),
                ),
            ),
            onBack = {},
            onOpenRecipe = {},
            onToggleSave = {},
        )
    }
}
