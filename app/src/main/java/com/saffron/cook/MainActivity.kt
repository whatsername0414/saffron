package com.saffron.cook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
import com.saffron.cook.core.designsystem.theme.Cinnamon
import com.saffron.cook.core.designsystem.theme.Saffron
import com.saffron.cook.core.designsystem.theme.SaffronTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SaffronTheme {
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

    val tabRoutes = BottomNavDestination.entries.map { it.screen.route }.toSet()
    val showBottomBar = currentDestination?.route in tabRoutes

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
                            ?.any { it.route == destination.screen.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (destination.screen.route == Screen.Home.route) {
                                    navController.popBackStack(
                                        route = Screen.Home.route,
                                        inclusive = false,
                                        saveState = true,
                                    )
                                } else {
                                    navController.navigate(destination.screen.route) {
                                        popUpTo(Screen.Home.route) {
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
                                    tint = if (selected) Saffron else Cinnamon
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
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                    onOpenRecipe = { id -> navController.navigate(Screen.RecipeDetail.createRoute(id)) },
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    onOpenRecipe = { id -> navController.navigate(Screen.RecipeDetail.createRoute(id)) },
                )
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    onOpenRecipe = { id -> navController.navigate(Screen.RecipeDetail.createRoute(id)) },
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    serverClientId = stringResource(R.string.default_web_client_id),
                    onOpenNotes = { navController.navigate(Screen.NotesList.route) },
                    onOpenFavorites = {
                        navController.navigate(Screen.Favorites.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onOpenCooked = { navController.navigate(Screen.CookedList.route) },
                )
            }
            composable(Screen.CookedList.route) {
                CookedListScreen(
                    onBack = { navController.popBackStack() },
                    onOpenRecipe = { id -> navController.navigate(Screen.RecipeDetail.createRoute(id)) },
                )
            }
            composable(
                route = Screen.RecipeDetail.route,
                arguments = listOf(navArgument("recipeId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getString("recipeId") ?: return@composable
                RecipeDetailScreen(
                    recipeId = recipeId,
                    onBack = { navController.popBackStack() },
                    onStartCooking = { id -> navController.navigate(Screen.CookingMode.createRoute(id)) },
                )
            }
            composable(
                route = Screen.CookingMode.route,
                arguments = listOf(navArgument("recipeId") { type = NavType.StringType }),
            ) {
                CookingModeScreen(
                    onBack = { navController.popBackStack() },
                    onAddNote = { recipeId ->
                        navController.navigate(Screen.NoteEditor.createRoute(recipeId))
                    },
                )
            }
            composable(Screen.NotesList.route) {
                NoteListScreen(
                    onBack = { navController.popBackStack() },
                    onOpenNote = { noteId -> navController.navigate(Screen.NoteDetail.createRoute(noteId)) },
                )
            }
            composable(
                route = Screen.NoteDetail.route,
                arguments = listOf(navArgument("noteId") { type = NavType.LongType }),
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getLong("noteId") ?: return@composable
                NoteDetailScreen(
                    onBack = { navController.popBackStack() },
                    onEdit = { recipeId ->
                        navController.navigate(Screen.NoteEditor.createEditRoute(recipeId, noteId))
                    },
                    onDeleted = { navController.popBackStack() },
                )
            }
            composable(
                route = Screen.NoteEditor.route,
                arguments = listOf(
                    navArgument("recipeId") { type = NavType.StringType },
                    navArgument("noteId") { type = NavType.LongType; defaultValue = 0L },
                ),
            ) { backStackEntry ->
                val isEditMode = (backStackEntry.arguments?.getLong("noteId") ?: 0L) != 0L
                NoteEditorScreen(
                    onCancel = { navController.popBackStack() },
                    onSaved = {
                        if (isEditMode) {
                            navController.popBackStack()
                        } else {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = false }
                            }
                        }
                    },
                )
            }
        }
    }
}
