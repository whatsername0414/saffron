package com.saffron.cook.feature.note.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.saffron.cook.feature.note.R
import com.saffron.cook.core.designsystem.theme.InterFamily
import com.saffron.cook.core.designsystem.theme.PlayfairDisplayFamily
import com.saffron.cook.core.designsystem.theme.Saffron40
import com.saffron.cook.core.designsystem.theme.SaffronTheme
import com.saffron.cook.core.designsystem.theme.SemanticError
import com.saffron.cook.core.designsystem.theme.saffronColors
import org.koin.androidx.compose.koinViewModel

@Composable
fun NoteDetailScreen(
    onBack: () -> Unit,
    onEdit: (recipeId: String) -> Unit,
    onDeleted: () -> Unit,
    viewModel: NoteDetailViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    NoteDetailContent(
        state = state,
        onBack = onBack,
        onEdit = { state.note?.let { onEdit(it.recipeId) } },
        onShowDeleteConfirm = viewModel::onShowDeleteConfirm,
        onDismissDeleteConfirm = viewModel::onDismissDeleteConfirm,
        onDelete = { viewModel.onDelete(onDeleted) },
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun NoteDetailContent(
    state: NoteDetailUiState,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onShowDeleteConfirm: () -> Unit,
    onDismissDeleteConfirm: () -> Unit,
    onDelete: () -> Unit,
) {
    val colors = MaterialTheme.saffronColors
    Surface(color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp, top = 14.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.cd_back),
                        tint = colors.textPrimary,
                    )
                }
                Text(
                    text = stringResource(R.string.note_detail_header),
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.textSecondary,
                    modifier = Modifier.weight(1f),
                )
                TextButton(onClick = onShowDeleteConfirm) {
                    Text(
                        text = stringResource(R.string.action_delete),
                        style = MaterialTheme.typography.labelLarge,
                        color = SemanticError,
                    )
                }
            }

            val note = state.note
            if (note != null) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.surfaceCream)
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surface),
                            ) {
                                AsyncImage(
                                    model = note.recipeImage,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
                            Column {
                                Text(
                                    text = stringResource(R.string.note_on_label).uppercase(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = colors.accent,
                                )
                                Text(
                                    text = note.recipeName,
                                    style = TextStyle(
                                        fontFamily = PlayfairDisplayFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 15.sp,
                                    ),
                                    color = colors.textPrimary,
                                )
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        Text(
                            text = note.title,
                            style = TextStyle(
                                fontFamily = PlayfairDisplayFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 28.sp,
                                lineHeight = 34.sp,
                                letterSpacing = (-0.3).sp,
                            ),
                            color = colors.textPrimary,
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.note_added_date, note.dateLabel),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textTertiary,
                        )

                        Spacer(Modifier.height(16.dp))

                        if (note.rating > 0) {
                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                for (i in 1..5) {
                                    Icon(
                                        imageVector = if (i <= note.rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                        contentDescription = null,
                                        tint = if (i <= note.rating) Saffron40 else Color(0xFFC9C2B6),
                                        modifier = Modifier.size(22.dp),
                                    )
                                }
                            }
                            Spacer(Modifier.height(18.dp))
                        }

                        if (note.labels.isNotEmpty()) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                note.labels.forEach { label ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(percent = 50))
                                            .background(colors.surfaceCream)
                                            .padding(horizontal = 14.dp, vertical = 8.dp),
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = colors.textSecondary,
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(18.dp))
                        }

                        if (note.body.isNotBlank()) {
                            Text(
                                text = note.body,
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textPrimary,
                            )
                        }
                    }

                    if (note.photos.isNotEmpty()) {
                        Spacer(Modifier.height(18.dp))
                        Text(
                            text = stringResource(R.string.note_section_photos).uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.textTertiary,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                        Spacer(Modifier.height(10.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            itemsIndexed(note.photos) { _, uri ->
                                Box(
                                    modifier = Modifier
                                        .size(132.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(colors.surfaceCream),
                                ) {
                                    AsyncImage(
                                        model = uri,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                }

                HorizontalDivider(thickness = 0.5.dp, color = colors.borderTertiary)
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Button(
                        onClick = onEdit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                        elevation = ButtonDefaults.buttonElevation(0.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.action_update),
                            style = TextStyle(
                                fontFamily = InterFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp,
                            ),
                            color = Color.White,
                        )
                    }
                }
            }
        }
    }

    if (state.showDeleteConfirm) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = onDismissDeleteConfirm,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            dragHandle = null,
        ) {
            Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 20.dp)) {
                Text(
                    text = stringResource(R.string.note_delete_title),
                    style = TextStyle(
                        fontFamily = PlayfairDisplayFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                    ),
                    color = colors.textPrimary,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.note_delete_body),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                )
                Spacer(Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onDismissDeleteConfirm,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.surfaceCream, contentColor = colors.textPrimary),
                        elevation = ButtonDefaults.buttonElevation(0.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.note_keep),
                            style = TextStyle(fontFamily = InterFamily, fontWeight = FontWeight.Medium, fontSize = 15.sp),
                        )
                    }
                    Button(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SemanticError),
                        elevation = ButtonDefaults.buttonElevation(0.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.action_delete),
                            style = TextStyle(fontFamily = InterFamily, fontWeight = FontWeight.Medium, fontSize = 15.sp),
                            color = Color.White,
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

// ---- Previews ---------------------------------------------------------------

@Preview(showBackground = true, name = "Note with all fields")
@Composable
private fun NoteDetailPreview() {
    SaffronTheme {
        NoteDetailContent(
            state = NoteDetailUiState(
                note = NoteDetailItem(
                    id = 1L, recipeId = "risotto", recipeName = "Saffron risotto", recipeImage = "",
                    title = "Needed more stock than written",
                    body = "Took closer to forty minutes and a full extra ladle of stock.",
                    rating = 5, labels = listOf("Made it", "Would make again"), photos = emptyList(), dateLabel = "18 Jun",
                ),
            ),
            onBack = {}, onEdit = {}, onShowDeleteConfirm = {}, onDismissDeleteConfirm = {}, onDelete = {},
        )
    }
}
