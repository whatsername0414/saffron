package com.saffron.cook.ui.notelist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
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
import com.saffron.cook.R
import com.saffron.cook.core.designsystem.theme.Cinnamon
import com.saffron.cook.core.designsystem.theme.Cream
import com.saffron.cook.core.designsystem.theme.PlayfairDisplayFamily
import com.saffron.cook.core.designsystem.theme.Saffron
import com.saffron.cook.core.designsystem.theme.Saffron40
import com.saffron.cook.core.designsystem.theme.SaffronTheme
import com.saffron.cook.core.designsystem.theme.Truffle
import org.koin.androidx.compose.koinViewModel

private val BorderTertiary = Color(0xFFE4DFD5)
private val TextTertiary = Color(0xFF8A7A5C)

@Composable
fun NoteListScreen(
    onBack: () -> Unit,
    onOpenNote: (Long) -> Unit,
    viewModel: NoteListViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    NoteListContent(state = state, onBack = onBack, onOpenNote = onOpenNote)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NoteListContent(
    state: NoteListUiState,
    onBack: () -> Unit,
    onOpenNote: (Long) -> Unit,
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
                    text = stringResource(R.string.notes_title),
                    style = TextStyle(
                        fontFamily = PlayfairDisplayFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 26.sp,
                        letterSpacing = (-0.3).sp,
                    ),
                    color = Truffle,
                )
            }

            if (state.notes.isEmpty()) {
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
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(32.dp),
                        )
                        Text(
                            text = stringResource(R.string.note_list_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Cinnamon,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.widthIn(max = 240.dp),
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 4.dp,
                        bottom = 24.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.notes, key = { it.id }) { note ->
                        NoteCard(note = note, onClick = { onOpenNote(note.id) })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NoteCard(note: NoteListItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(0.5.dp, BorderTertiary, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Cream),
            ) {
                AsyncImage(
                    model = note.recipeImage,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.recipeName.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = Saffron,
                )
                Text(
                    text = note.title,
                    style = TextStyle(
                        fontFamily = PlayfairDisplayFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                    ),
                    color = Truffle,
                )
            }
            Text(
                text = note.dateLabel,
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary,
            )
        }

        if (note.rating > 0) {
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                for (i in 1..5) {
                    Icon(
                        imageVector = if (i <= note.rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = null,
                        tint = if (i <= note.rating) Saffron40 else Color(0xFFC9C2B6),
                        modifier = Modifier.size(15.dp),
                    )
                }
            }
        }

        if (note.body.isNotBlank()) {
            Text(
                text = note.body,
                style = MaterialTheme.typography.bodySmall,
                color = Cinnamon,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        if (note.labels.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                note.labels.forEach { label ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .background(Cream)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge,
                            color = Cinnamon,
                        )
                    }
                }
            }
        }
    }
}

// ---- Previews ---------------------------------------------------------------

@Preview(showBackground = true, name = "Empty state")
@Composable
private fun NoteListEmptyPreview() {
    SaffronTheme {
        NoteListContent(state = NoteListUiState(), onBack = {}, onOpenNote = {})
    }
}

@Preview(showBackground = true, name = "With notes")
@Composable
private fun NoteListFilledPreview() {
    SaffronTheme {
        NoteListContent(
            state = NoteListUiState(
                notes = listOf(
                    NoteListItem(
                        id = 1L,
                        recipeName = "Saffron risotto",
                        recipeImage = "",
                        title = "Needed more stock than written",
                        body = "Took closer to forty minutes and a full extra ladle of stock.",
                        rating = 5,
                        labels = listOf("Made it", "Would make again"),
                        dateLabel = "18 Jun",
                    ),
                    NoteListItem(
                        id = 2L,
                        recipeName = "Rosemary focaccia",
                        recipeImage = "",
                        title = "Cold proof overnight",
                        body = "The 18-hour fridge rise gave a much better crumb.",
                        rating = 4,
                        labels = listOf("Tweak next time"),
                        dateLabel = "11 Jun",
                    ),
                ),
            ),
            onBack = {},
            onOpenNote = {},
        )
    }
}
