package com.saffron.cook.ui.notes

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.saffron.cook.R
import com.saffron.cook.ui.theme.Cinnamon
import com.saffron.cook.ui.theme.Cream
import com.saffron.cook.ui.theme.InterFamily
import com.saffron.cook.ui.theme.PlayfairDisplayFamily
import com.saffron.cook.ui.theme.Saffron
import com.saffron.cook.ui.theme.Saffron40
import com.saffron.cook.ui.theme.SaffronTheme
import com.saffron.cook.ui.theme.Truffle
import org.koin.androidx.compose.koinViewModel

private const val maxUpload = 4

@Composable
fun NoteEditorScreen(
    onCancel: () -> Unit,
    onSaved: () -> Unit,
    viewModel: NoteEditorViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    val imageOnly = remember { PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly) }

    val singlePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri?.let { viewModel.onAddPhoto(listOf(it.toString())) }
    }

    val multiPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxUpload),
    ) { uris ->
        if (uris.isNotEmpty()) viewModel.onAddPhoto(uris.map { it.toString() })
    }

    NoteEditorContent(
        state = state,
        onCancel = onCancel,
        onSave = { viewModel.onSave(onSaved) },
        onTitleChange = viewModel::onTitleChange,
        onBodyChange = viewModel::onBodyChange,
        onRatingChange = viewModel::onRatingChange,
        onToggleLabel = viewModel::onToggleLabel,
        onAddPhoto = {
            if (maxUpload - state.photos.size <= 1) singlePicker.launch(imageOnly)
            else multiPicker.launch(imageOnly)
        },
        onRemovePhoto = viewModel::onRemovePhoto,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NoteEditorContent(
    state: NoteEditorUiState,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onRatingChange: (Int) -> Unit,
    onToggleLabel: (String) -> Unit,
    onAddPhoto: () -> Unit,
    onRemovePhoto: (Int) -> Unit,
) {
    val quickLabels = listOf("Made it", "Would make again", "Tweak next time", "Halved it", "For guests")

    Surface(color = Color.White, modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp, top = 14.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onCancel) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = stringResource(R.string.cd_back),
                        tint = Truffle,
                    )
                }
                Text(
                    text = stringResource(R.string.note_editor_title),
                    style = MaterialTheme.typography.labelLarge,
                    color = Cinnamon,
                    modifier = Modifier.weight(1f),
                )
                TextButton(
                    onClick = onSave,
                    enabled = state.canSave,
                ) {
                    Text(
                        text = stringResource(R.string.action_save),
                        style = MaterialTheme.typography.labelLarge,
                        color = if (state.canSave) Saffron else Color(0xFFD3CFC8),
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
            ) {
                // Recipe context card
                if (state.recipeName.isNotEmpty()) {
                    RecipeContextCard(
                        name = state.recipeName,
                        imageUrl = state.recipeImage,
                    )
                    Spacer(Modifier.height(18.dp))
                }

                // Title — borderless Playfair input
                BasicTextField(
                    value = state.title,
                    onValueChange = onTitleChange,
                    textStyle = TextStyle(
                        fontFamily = PlayfairDisplayFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 26.sp,
                        letterSpacing = (-0.3).sp,
                        color = Truffle,
                    ),
                    cursorBrush = SolidColor(Saffron),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        Box {
                            if (state.title.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.note_title_hint),
                                    style = TextStyle(
                                        fontFamily = PlayfairDisplayFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 26.sp,
                                        letterSpacing = (-0.3).sp,
                                        color = Color(0xFFD3CFC8),
                                    ),
                                )
                            }
                            inner()
                        }
                    },
                )

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE4DFD5))
                Spacer(Modifier.height(18.dp))

                // Rating
                SectionLabel(stringResource(R.string.note_section_rating))
                Spacer(Modifier.height(8.dp))
                StarRating(
                    value = state.rating,
                    onRate = onRatingChange,
                )

                Spacer(Modifier.height(22.dp))

                // Labels
                SectionLabel(stringResource(R.string.note_section_labels))
                Spacer(Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    quickLabels.forEach { label ->
                        LabelChip(
                            text = label,
                            selected = label in state.labels,
                            onClick = { onToggleLabel(label) },
                        )
                    }
                }

                Spacer(Modifier.height(22.dp))

                // Notes body
                SectionLabel(stringResource(R.string.note_section_notes))
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = state.body,
                    onValueChange = onBodyChange,
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 5,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.note_body_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFD3CFC8),
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = Truffle),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Saffron,
                        unfocusedBorderColor = Color(0xFFE4DFD5),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = Saffron,
                    ),
                )

                Spacer(Modifier.height(22.dp))

                // Photos
                SectionLabel(stringResource(R.string.note_section_photos))
                Spacer(Modifier.height(10.dp))
                PhotoRow(
                    photos = state.photos,
                    onAddPhoto = onAddPhoto,
                    onRemovePhoto = onRemovePhoto,
                )

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun RecipeContextCard(name: String, imageUrl: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Cream)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White),
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Column {
            Text(
                text = stringResource(R.string.note_on_label).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = Saffron,
            )
            Text(
                text = name,
                style = TextStyle(
                    fontFamily = PlayfairDisplayFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = Truffle,
                ),
            )
        }
    }
}

@Composable
private fun StarRating(value: Int, onRate: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= value) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = null,
                tint = if (i <= value) Saffron40 else Color(0xFFC9C2B6),
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onRate(i) },
            )
        }
    }
}

@Composable
private fun LabelChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(if (selected) Saffron else Cream)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) Color.White else Cinnamon,
        )
    }
}

@Composable
private fun PhotoRow(
    photos: List<String>,
    onAddPhoto: () -> Unit,
    onRemovePhoto: (Int) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        itemsIndexed(photos) { index, uri ->
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Cream)
                    .border(0.5.dp, Color(0xFFE4DFD5), RoundedCornerShape(8.dp)),
            ) {
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable { onRemovePhoto(index) },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = stringResource(R.string.note_remove_photo),
                        tint = Color.White,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }

        if (photos.size < 4) {
            item {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(0.5.dp, Color(0xFFD3CFC8), RoundedCornerShape(8.dp))
                        .clickable(onClick = onAddPhoto),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            tint = Saffron,
                            modifier = Modifier.size(22.dp),
                        )
                        Text(
                            text = if (photos.isEmpty()) {
                                stringResource(R.string.note_add_photo)
                            } else {
                                stringResource(R.string.note_add_more_photos)
                            },
                            style = TextStyle(
                                fontFamily = InterFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                color = Cinnamon,
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = Color(0xFF8A7A5C),
    )
}

// ---- Previews --------------------------------------------------------------

@Preview(showBackground = true, name = "Empty state")
@Composable
private fun NoteEditorEmptyPreview() {
    SaffronTheme {
        NoteEditorContent(
            state = NoteEditorUiState(
                recipeName = "Saffron risotto",
                recipeImage = "",
            ),
            onCancel = {},
            onSave = {},
            onTitleChange = {},
            onBodyChange = {},
            onRatingChange = {},
            onToggleLabel = {},
            onAddPhoto = {},
            onRemovePhoto = {},
        )
    }
}

@Preview(showBackground = true, name = "Filled — can save")
@Composable
private fun NoteEditorFilledPreview() {
    SaffronTheme {
        NoteEditorContent(
            state = NoteEditorUiState(
                recipeName = "Saffron risotto",
                recipeImage = "",
                title = "Needed more stock than written",
                body = "Took closer to forty minutes and a full extra ladle of stock. Toasting the saffron first made a real difference to the colour.",
                rating = 5,
                labels = setOf("Made it", "Would make again"),
                photos = listOf("", ""),
            ),
            onCancel = {},
            onSave = {},
            onTitleChange = {},
            onBodyChange = {},
            onRatingChange = {},
            onToggleLabel = {},
            onAddPhoto = {},
            onRemovePhoto = {},
        )
    }
}
