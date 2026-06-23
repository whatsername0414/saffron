# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Saffron** is an Android cooking/recipe app (package `com.saffron.cook`). The brand is editorial and warm — Saffron `#C8860A` hero color, Playfair Display headlines, Inter UI text, flat card design (hairline borders, no shadows on resting surfaces). The full design system lives at Claude Design project `e9c32af8-cb1e-4957-b736-17366c4eb9db`.

## Build & Run Commands

```bash
./gradlew assembleDebug        # build debug APK
./gradlew installDebug         # install on connected device
./gradlew test                 # unit tests
./gradlew connectedAndroidTest # instrumented tests (device/emulator required)
./gradlew lint
```

On Windows use `gradlew.bat` instead of `./gradlew`.

## Tech Stack

| | |
|---|---|
| Language | Kotlin 2.2.10 |
| UI | Jetpack Compose + Material3 |
| Compose BOM | 2026.02.01 |
| Navigation | Navigation Compose 2.8.0 |
| Min SDK | 24 / Target+Compile SDK 37 |
| AGP | 9.3.0-rc01 |
| Build system | Gradle 9.5 (Kotlin DSL), version catalog `gradle/libs.versions.toml` |

## Module Structure

```
:core:ui    — brand theme layer (android.library)
              Color.kt, Type.kt, Theme.kt + GMS font certs
              Exposes Compose/Material3 as api — feature modules need only depend on :core:ui

:core:data  — domain models + repository (kotlin.jvm, no Android framework)
              model/  → Recipe, Ingredient, Step, Difficulty, Category
              repository/ → RecipeRepository interface + MealDbRecipeRepository (TheMealDB)
              repository/fake/ → FakeRecipeRepository (in-memory, for tests)
              network/ → TheMealDbService (Retrofit), DTOs, MealMapper

:app        — shell: MainActivity, NavHost, BottomNav
              navigation/ → Screen (route definitions), BottomNavDestination (tab metadata)
              di/         → AppModule (networkModule, coreDataModule, homeModule, detailModule)
              ui/<feature>/ — each feature owns its Screen, ViewModel, and UiState:
                ui/home/      → HomeScreen, HomeViewModel, HomeUiState
                ui/detail/    → RecipeDetailScreen, RecipeDetailViewModel, RecipeDetailUiState
                ui/search/    → SearchScreen (stub)
                ui/favorites/ → FavoritesScreen (stub)
                ui/profile/   → ProfileScreen (stub)
                ui/cooking/   → CookingModeScreen, CookingModeViewModel, CookingModeUiState
```

When adding a new feature module use `android.library` for anything with Compose/resources, `kotlin.jvm` for pure logic. Declare the plugin `apply false` in the root `build.gradle.kts` first.

## Architecture

Standard MVI / unidirectional data flow:

```
:core:data  ←  repositories (interface + fake impl)
    ↓
:app (ViewModels — not yet added; inject via Koin)
    ↓
:app (Composable screens ← StateFlow/UiState)
```

**Completed:**
1. Koin DI — `networkModule`, `coreDataModule`, `homeModule`, `detailModule`, `cookingModule` wired in `SaffronApplication`.
2. Home screen — `HomeViewModel` + `HomeScreen` (featured card, category chips, 2-column grid, async load from TheMealDB).
3. Recipe Detail screen — `RecipeDetailViewModel` + `RecipeDetailScreen` (hero, meta strip, ingredient list, "Start cooking" CTA).
4. Cooking Mode — `CookingModeScreen` + `CookingModeViewModel` (step indicator pills, done-checkbox, back/next/finish footer).

**In progress — review fixes (half-applied, finish before new features):**

Already applied:
- `strings.xml` — section headers lowercased to sentence case; `error_load_failed` / `error_retry` strings added.
- `RecipeDetailUiState` + `CookingModeUiState` — `isError: Boolean = false` field added.
- `RecipeDetailViewModel` + `CookingModeViewModel` — `load()` wrapped in try/catch; `retry()` added.
- `MealDbRecipeRepository.getRecipeById` — `runCatching` removed; network errors now propagate so ViewModels can catch and set `isError`.

Still to apply (do these first in the next session):
- `RecipeDetailScreen.kt` — add `state.isError` branch (show error + retry button); add `elevation = ButtonDefaults.buttonElevation(0.dp)` to "Start cooking" button; change `.replaceFirstChar { it.uppercase() }.uppercase()` → `.replaceFirstChar { it.uppercase() }` on categoryId (line 162).
- `CookingModeScreen.kt` — add `onRetry: () -> Unit` param to `CookingModeContent`; add `state.isError` branch; remove `.uppercase()` from step-progress label (line 312); pass `viewModel::retry` from `CookingModeScreen`.
- `HomeViewModel.kt` — (a) add `private var categoryJob: Job? = null`; cancel in `onSelectCategory` before launching; (b) replace `viewModelScope.async` in `loadData()` with `coroutineScope { async { } }` wrapped in `runCatching { }.onFailure { _uiState.update { it.copy(isLoading = false) } }`.
- `MainActivity.kt` — replace hardcoded `tabRoutes` setOf(...) with `BottomNavDestination.entries.map { it.screen.route }.toSet()`; add `backStackEntry.arguments?.getString("recipeId") ?: return@composable` guard to the CookingMode composable block.
- `MealMapper.kt` — fix `parseSteps`: split on paragraph breaks (`\r?\n\s*\r?\n`) first; fall back to single line breaks only if no paragraph breaks found. Removes the second-pass single-line split that over-fragments recipes.
- `MealDbRecipeRepository.kt` — replace hardcoded `gridCategories` list with `preferredCategoryIds` (lowercase); fetch live category names from `getCategories()` at the start of `getRecipes()` and resolve to exact API names (case-insensitive match, fall back to `replaceFirstChar { uppercase }` if API fetch fails).

**Next feature after fixes:**
5. **Search screen** — implement `SearchScreen` using `TheMealDbService.searchMeals(query)`. Full-text search by name/ingredient. `SearchViewModel` + `SearchUiState` in `ui/search/`. Debounce input, empty state, loading state.

## Brand Rules (non-negotiable)

- **Color** — Saffron `#C8860A` is the only hero color. Apply sparingly (CTAs, active states, bookmark). Full palette in `:core:ui/Color.kt`.
- **Type** — Playfair Display for recipe titles, screen headers, category names (never below 18sp, never in paragraphs). Inter Light (300) for body copy. Inter Medium (500) max for labels/buttons — never Bold.
- **Flat UI** — zero elevation shadow on resting cards/buttons. Use `0.dp` `tonalElevation` on `NavigationBar`. Hairline borders (`0.5dp`) instead of shadows.
- **Voice** — sentence case everywhere. No emoji. No "Amazing!" — say "Saved." Metadata abbreviates ("35 min"); instructions spell out ("thirty-five minutes").
- **Dynamic color disabled** — `SaffronTheme` enforces the brand palette on all API levels.
- **Icons** — Material Icons Extended for now (outlined unselected, filled selected). Bookmark is the only filled icon in resting state (saved recipes).

## Key Conventions

- All versions in `libs.versions.toml` — never hardcode in build files.
- All UI in Compose — no XML layouts.
- BOM-managed Compose deps have no version in the catalog (BOM provides it). Navigation Compose and Koin need explicit versions.
- `keepRules/rules.keep` holds ProGuard keep rules.
- Each feature lives in `app/ui/<feature>/` — Screen, ViewModel, and UiState co-located. Shared UI components will move to `:core:ui` once there are two or more consumers.
- Data API is TheMealDB v1 (free, no key). `MealDbRecipeRepository` in `:core:data` is the live impl; `FakeRecipeRepository` is the in-memory fallback for tests.
