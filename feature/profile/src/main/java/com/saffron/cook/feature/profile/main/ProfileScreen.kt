package com.saffron.cook.feature.profile.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Person2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import coil.compose.AsyncImage
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import androidx.compose.material3.MaterialTheme
import com.saffron.cook.core.designsystem.theme.InterFamily
import com.saffron.cook.core.designsystem.theme.PlayfairDisplayFamily
import com.saffron.cook.core.designsystem.theme.Saffron20
import com.saffron.cook.core.designsystem.theme.SaffronTheme
import com.saffron.cook.core.designsystem.theme.saffronColors
import com.saffron.cook.feature.profile.R
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    serverClientId: String,
    onOpenNotes: () -> Unit = {},
    onOpenFavorites: () -> Unit = {},
    onOpenCooked: () -> Unit = {},
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)

    fun launchGoogleSignIn() {
        scope.launch {
            try {
                val option = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(serverClientId)
                    .setAutoSelectEnabled(true)
                    .build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(option)
                    .build()
                val result = credentialManager.getCredential(context, request)
                val token = GoogleIdTokenCredential.createFrom(result.credential.data)
                viewModel.handleGoogleIdToken(token.idToken)
            } catch (_: GetCredentialException) {
            }
        }
    }

    ProfileContent(
        state = state,
        onSignOut = { viewModel.signOut() },
        onOpenFavorites = onOpenFavorites,
        onOpenCooked = onOpenCooked,
        onOpenNotes = onOpenNotes,
        onAddAccount = ::launchGoogleSignIn,
    )
}

@Composable
private fun ProfileContent(
    state: ProfileUiState,
    onSignOut: () -> Unit,
    onOpenFavorites: () -> Unit,
    onOpenCooked: () -> Unit,
    onOpenNotes: () -> Unit,
    onAddAccount: () -> Unit,
) {
    val colors = MaterialTheme.saffronColors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(R.string.profile_title),
            style = TextStyle(
                fontFamily = PlayfairDisplayFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 26.sp,
                letterSpacing = (-0.3).sp,
            ),
            color = colors.textPrimary,
            modifier = Modifier.padding(top = 14.dp, start = 16.dp, end = 16.dp, bottom = 6.dp),
        )

        if (state.user == null) {
            SignedOutContent(
                state = state,
                onOpenFavorites = onOpenFavorites,
                onOpenCooked = onOpenCooked,
                onOpenNotes = onOpenNotes,
                onAddAccount = onAddAccount,
            )
        } else {
            SignedInContent(
                state = state,
                onOpenFavorites = onOpenFavorites,
                onOpenCooked = onOpenCooked,
                onOpenNotes = onOpenNotes,
                onSignOut = onSignOut,
            )
        }
    }
}

@Composable
private fun SignedOutContent(
    state: ProfileUiState,
    onOpenFavorites: () -> Unit,
    onOpenCooked: () -> Unit,
    onOpenNotes: () -> Unit,
    onAddAccount: () -> Unit,
) {
    val colors = MaterialTheme.saffronColors
    // Generic avatar row
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(colors.surfaceCream)
                .border(BorderStroke(0.5.dp, colors.borderTertiary), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                tint = colors.onCream,
                modifier = Modifier.size(28.dp),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = stringResource(R.string.profile_guest_name),
                style = TextStyle(
                    fontFamily = PlayfairDisplayFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp,
                    letterSpacing = (-0.2).sp,
                ),
                color = colors.textPrimary,
            )
            Text(
                text = stringResource(R.string.profile_guest_subtitle),
                style = TextStyle(
                    fontFamily = InterFamily,
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp,
                ),
                color = colors.textSecondary,
            )
        }
    }

    StatStrip(state = state, onOpenNotes = onOpenNotes, onOpenFavorites = onOpenFavorites, onOpenCooked = onOpenCooked)

    // Add an account card
    Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 8.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surfaceCream, RoundedCornerShape(10.dp))
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = stringResource(R.string.profile_sync_label).uppercase(),
                style = TextStyle(
                    fontFamily = InterFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    letterSpacing = 1.1.sp,
                ),
                color = colors.accent,
            )
            Text(
                text = stringResource(R.string.profile_add_account),
                style = TextStyle(
                    fontFamily = PlayfairDisplayFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp,
                    letterSpacing = (-0.2).sp,
                ),
                color = colors.textPrimary,
            )
            Text(
                text = stringResource(R.string.profile_add_account_body),
                style = TextStyle(
                    fontFamily = InterFamily,
                    fontWeight = FontWeight.Light,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                ),
                color = colors.textSecondary,
                modifier = Modifier.padding(bottom = 10.dp),
            )
            Button(
                onClick = onAddAccount,
                enabled = !state.isSigningIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                elevation = ButtonDefaults.buttonElevation(0.dp),
            ) {
                if (state.isSigningIn) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        text = stringResource(R.string.profile_add_account),
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

    SettingsSection(
        rows = listOf(
            SettingsEntry(stringResource(R.string.profile_setting_dietary)),
            SettingsEntry(stringResource(R.string.profile_setting_notifications)),
            SettingsEntry(stringResource(R.string.profile_setting_help)),
        ),
    )
}

@Composable
private fun SignedInContent(
    state: ProfileUiState,
    onOpenFavorites: () -> Unit,
    onOpenCooked: () -> Unit,
    onOpenNotes: () -> Unit,
    onSignOut: () -> Unit,
) {
    val user = state.user
    val colors = MaterialTheme.saffronColors

    // User avatar row
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        if (user?.photoUrl != null) {
            AsyncImage(
                model = user.photoUrl,
                contentDescription = user.displayName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceCream),
            )
        } else {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Saffron20),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = user?.displayName?.firstOrNull()?.uppercaseChar()?.toString() ?: "S",
                    style = TextStyle(
                        fontFamily = PlayfairDisplayFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 26.sp,
                    ),
                    color = colors.onCream,
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = user?.displayName ?: "",
                style = TextStyle(
                    fontFamily = PlayfairDisplayFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp,
                    letterSpacing = (-0.2).sp,
                ),
                color = colors.textPrimary,
            )
            Text(
                text = user?.email ?: "",
                style = TextStyle(
                    fontFamily = InterFamily,
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp,
                ),
                color = colors.textSecondary,
            )
        }
    }

    StatStrip(state = state, onOpenNotes = onOpenNotes, onOpenFavorites = onOpenFavorites, onOpenCooked = onOpenCooked)

    SettingsSection(
        rows = listOf(
            SettingsEntry(stringResource(R.string.profile_setting_account)),
            SettingsEntry(stringResource(R.string.profile_setting_dietary)),
            SettingsEntry(stringResource(R.string.profile_setting_notifications)),
            SettingsEntry(stringResource(R.string.profile_setting_help)),
            SettingsEntry(stringResource(R.string.profile_sign_out), labelColor = colors.textSecondary, onClick = onSignOut),
        ),
    )
}

@Composable
private fun StatStrip(state: ProfileUiState, onOpenNotes: () -> Unit, onOpenFavorites: () -> Unit, onOpenCooked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StatCard(
            label = stringResource(R.string.profile_stat_saved),
            value = state.savedCount,
            modifier = Modifier.weight(1f),
            onClick = onOpenFavorites,
        )
        StatCard(label = stringResource(R.string.cooked_title), value = state.cookedCount, modifier = Modifier.weight(1f), onClick = onOpenCooked)
        StatCard(
            label = stringResource(R.string.notes_title),
            value = state.notesCount,
            modifier = Modifier.weight(1f),
            onClick = onOpenNotes,
        )
    }
}

@Composable
private fun StatCard(
    label: String,
    value: Int,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit),
) {
    val colors = MaterialTheme.saffronColors
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surfaceCream, RoundedCornerShape(10.dp))
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = value.toString(),
                style = TextStyle(
                    fontFamily = PlayfairDisplayFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 28.sp,
                ),
                color = colors.onCream,
            )
            Text(
                text = label,
                style = TextStyle(
                    fontFamily = InterFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                ),
                color = colors.textSecondary,
            )
        }
    }
}

private data class SettingsEntry(
    val label: String,
    val labelColor: Color = Color.Unspecified,
    val icon: ImageVector = Icons.Filled.ChevronRight,
    val onClick: () -> Unit = {},
)

@Composable
private fun SettingsSection(rows: List<SettingsEntry>) {
    val colors = MaterialTheme.saffronColors
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp)) {
        rows.forEachIndexed { i, entry ->
            SettingsRow(entry = entry)
            if (i < rows.lastIndex) {
                HorizontalDivider(thickness = 0.5.dp, color = colors.borderTertiary)
            }
        }
    }
}

@Composable
private fun SettingsRow(entry: SettingsEntry) {
    val colors = MaterialTheme.saffronColors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = entry.onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = entry.label,
            style = TextStyle(
                fontFamily = InterFamily,
                fontWeight = FontWeight.Light,
                fontSize = 15.sp,
            ),
            color = if (entry.labelColor == Color.Unspecified) colors.textPrimary else entry.labelColor,
        )
        Icon(
            imageVector = entry.icon,
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileSignedOutPreview() {
    SaffronTheme {
        ProfileContent(
            state = ProfileUiState(savedCount = 5, notesCount = 2),
            onSignOut = {},
            onOpenFavorites = {},
            onOpenCooked = {},
            onOpenNotes = {},
            onAddAccount = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileSignedInPreview() {
    SaffronTheme {
        ProfileContent(
            state = ProfileUiState(savedCount = 12, cookedCount = 42, notesCount = 7),
            onSignOut = {},
            onOpenFavorites = {},
            onOpenCooked = {},
            onOpenNotes = {},
            onAddAccount = {},
        )
    }
}
