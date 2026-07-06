package com.saffron.cook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.saffron.cook.navigation.BottomNavDestination
import com.saffron.cook.navigation.Screen
import com.saffron.cook.feature.cooked.main.CookedListScreen
import com.saffron.cook.feature.cooking.main.CookingModeScreen
import com.saffron.cook.feature.favorite.main.FavoritesScreen
import com.saffron.cook.feature.home.main.HomeScreen
import com.saffron.cook.feature.note.detail.NoteDetailScreen
import com.saffron.cook.feature.note.editor.NoteEditorScreen
import com.saffron.cook.feature.note.main.NoteListScreen
import com.saffron.cook.feature.profile.main.ProfileScreen
import com.saffron.cook.feature.recipe.main.RecipeDetailScreen
import com.saffron.cook.feature.search.main.SearchScreen
import com.saffron.cook.feature.welcome.main.WelcomeScreen
import com.saffron.cook.core.database.repository.OnboardingRepository
import com.saffron.cook.core.database.repository.ThemeMode
import com.saffron.cook.core.database.repository.ThemeRepository
import com.saffron.cook.core.designsystem.theme.SaffronTheme
import com.saffron.cook.core.designsystem.theme.saffronColors
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeRepository = koinInject<ThemeRepository>()
            val themeMode by themeRepository.themeMode.collectAsState()
            val darkTheme = when (themeMode) {
                ThemeMode.Light -> false
                ThemeMode.Dark -> true
                ThemeMode.System -> isSystemInDarkTheme()
            }
            SaffronTheme(darkTheme = darkTheme) {
                SaffronApp()
            }
        }
    }
}

@Composable
fun SaffronApp() {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination

    val showBottomBar = BottomNavDestination.entries.any {
        currentDestination?.hasRoute(it.screen::class) == true
    }
    val colors = MaterialTheme.saffronColors

    val onboardingRepository = koinInject<OnboardingRepository>()
    val startDestination: Screen = if (onboardingRepository.hasCompletedOnboarding()) {
        Screen.Home
    } else {
        Screen.Welcome
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                ) {
                    BottomNavDestination.entries.forEach { destination ->
                        val selected = currentDestination?.hierarchy
                            ?.any { it.hasRoute(destination.screen::class) } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (destination.screen is Screen.Home) {
                                    navController.popBackStack(
                                        route = Screen.Home,
                                        inclusive = false,
                                        saveState = true,
                                    )
                                } else {
                                    navController.navigate(destination.screen) {
                                        popUpTo<Screen.Home> {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                    contentDescription = stringResource(destination.labelRes),
                                    tint = if (selected) colors.accent else colors.textSecondary
                                )
                            },
                            label = { Text(stringResource(destination.labelRes)) },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable<Screen.Welcome> {
                WelcomeScreen(
                    onGetStarted = {
                        navController.navigate(Screen.Home) {
                            popUpTo<Screen.Welcome> { inclusive = true }
                        }
                    },
                )
            }
            composable<Screen.Home> {
                HomeScreen(
                    onNavigateToSearch = { navController.navigate(Screen.Search) },
                    onOpenRecipe = { id -> navController.navigate(Screen.RecipeDetail(id)) },
                )
            }
            composable<Screen.Search> {
                SearchScreen(
                    onOpenRecipe = { id -> navController.navigate(Screen.RecipeDetail(id)) },
                )
            }
            composable<Screen.Favorites> {
                FavoritesScreen(
                    onOpenRecipe = { id -> navController.navigate(Screen.RecipeDetail(id)) },
                )
            }
            composable<Screen.Profile> {
                ProfileScreen(
                    serverClientId = stringResource(R.string.default_web_client_id),
                    onOpenNotes = { navController.navigate(Screen.NotesList) },
                    onOpenFavorites = {
                        navController.navigate(Screen.Favorites) {
                            popUpTo<Screen.Home> { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onOpenCooked = { navController.navigate(Screen.CookedList) },
                )
            }
            composable<Screen.CookedList> {
                CookedListScreen(
                    onBack = { navController.popBackStack() },
                    onOpenRecipe = { id -> navController.navigate(Screen.RecipeDetail(id)) },
                )
            }
            composable<Screen.RecipeDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<Screen.RecipeDetail>()
                RecipeDetailScreen(
                    recipeId = route.recipeId,
                    onBack = { navController.popBackStack() },
                    onStartCooking = { id -> navController.navigate(Screen.CookingMode(id)) },
                )
            }
            composable<Screen.CookingMode> {
                CookingModeScreen(
                    onBack = { navController.popBackStack() },
                    onAddNote = { recipeId ->
                        navController.navigate(Screen.NoteEditor(recipeId))
                    },
                )
            }
            composable<Screen.NotesList> {
                NoteListScreen(
                    onBack = { navController.popBackStack() },
                    onOpenNote = { noteId -> navController.navigate(Screen.NoteDetail(noteId)) },
                )
            }
            composable<Screen.NoteDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<Screen.NoteDetail>()
                NoteDetailScreen(
                    onBack = { navController.popBackStack() },
                    onEdit = { recipeId ->
                        navController.navigate(Screen.NoteEditor(recipeId, route.noteId))
                    },
                    onDeleted = { navController.popBackStack() },
                )
            }
            composable<Screen.NoteEditor> { backStackEntry ->
                val route = backStackEntry.toRoute<Screen.NoteEditor>()
                val isEditMode = route.noteId != 0L
                NoteEditorScreen(
                    onCancel = { navController.popBackStack() },
                    onSaved = {
                        if (isEditMode) {
                            navController.popBackStack()
                        } else {
                            navController.navigate(Screen.Home) {
                                popUpTo<Screen.Home> { inclusive = false }
                            }
                        }
                    },
                )
            }
        }
    }
}
