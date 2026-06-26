package com.saffron.cook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.saffron.cook.core.data.model.Recipe
import com.saffron.cook.ui.theme.Cinnamon
import com.saffron.cook.ui.theme.Cream
import com.saffron.cook.ui.theme.PlayfairDisplayFamily
import com.saffron.cook.ui.theme.Saffron
import com.saffron.cook.ui.theme.Truffle

@Composable
internal fun RecipeCard(
    recipe: Recipe,
    isSaved: Boolean,
    onToggleSave: (String) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
                .size(width = 92.dp, height = 70.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Cream),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = recipe.categoryId.replaceFirstChar { it.uppercase() }.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = Saffron,
            )
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = PlayfairDisplayFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                ),
                color = Truffle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            val metaItems = buildList {
                recipe.cookTimeMinutes?.let { add(Icons.Outlined.Schedule to "$it min") }
                recipe.servings?.let { add(Icons.Outlined.People to "serves $it") }
            }
            if (metaItems.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
        IconButton(onClick = { onToggleSave(recipe.id) }) {
            Icon(
                imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                contentDescription = if (isSaved) "Saved" else "Save",
                tint = if (isSaved) Saffron else Cinnamon,
            )
        }
    }
}
