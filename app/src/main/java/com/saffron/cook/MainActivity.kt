package com.saffron.cook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.saffron.cook.navigation.BottomNavDestination
import com.saffron.cook.navigation.Screen
import com.saffron.cook.ui.cooking.CookingModeScreen
import com.saffron.cook.ui.detail.RecipeDetailScreen
import com.saffron.cook.ui.favorites.FavoritesScreen
import com.saffron.cook.ui.home.HomeScreen
import com.saffron.cook.ui.profile.ProfileScreen
import com.saffron.cook.ui.search.SearchScreen
import com.saffron.cook.ui.theme.SaffronTheme

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

    val tabRoutes = setOf(
        Screen.Home.route, Screen.Search.route, Screen.Favorites.route, Screen.Profile.route,
    )
    val showBottomBar = currentDestination?.route in tabRoutes

    Scaffold(
        modifier  = Modifier.fillMaxSize(),
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
                            onClick  = {
                                navController.navigate(destination.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState    = true
                                }
                            },
                            icon  = {
                                Icon(
                                    imageVector        = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                    contentDescription = stringResource(destination.labelRes),
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
            navController    = navController,
            startDestination = Screen.Home.route,
            modifier         = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                    onOpenRecipe       = { id -> navController.navigate(Screen.RecipeDetail.createRoute(id)) },
                )
            }
            composable(Screen.Search.route)    { SearchScreen() }
            composable(Screen.Favorites.route) { FavoritesScreen() }
            composable(Screen.Profile.route)   { ProfileScreen() }
            composable(
                route     = Screen.RecipeDetail.route,
                arguments = listOf(navArgument("recipeId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getString("recipeId") ?: return@composable
                RecipeDetailScreen(
                    recipeId       = recipeId,
                    onBack         = { navController.popBackStack() },
                    onStartCooking = { id -> navController.navigate(Screen.CookingMode.createRoute(id)) },
                )
            }
            composable(
                route     = Screen.CookingMode.route,
                arguments = listOf(navArgument("recipeId") { type = NavType.StringType }),
            ) {
                CookingModeScreen(
                    onBack   = { navController.popBackStack() },
                    onFinish = { navController.popBackStack() },
                )
            }
        }
    }
}
