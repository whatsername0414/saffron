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
./gradlew ktlintCheck          # style + Compose rule check
./gradlew ktlintFormat         # auto-fix formatting violations
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
| Room | 2.7.1 (KSP 2.2.10-2.0.2) |
| Build system | Gradle 9.5 (Kotlin DSL), version catalog `gradle/libs.versions.toml` |

## Module Structure

```
:core:ui    — brand theme layer (android.library)
              Color.kt, Type.kt, Theme.kt
              res/font/ → bundled TTF: inter_{light,regular,medium}.ttf +
                          playfair_display_{regular,medium}.ttf
              Exposes Compose/Material3 as api — feature modules need only depend on :core:ui

:core:data  — domain models + repository (kotlin.jvm, no Android framework)
              model/  → Recipe, Ingredient, Step, Difficulty, Category
              repository/ → RecipeRepository interface + MealDbRecipeRepository (TheMealDB)
              repository/fake/ → FakeRecipeRepository (in-memory, for tests)
              network/ → TheMealDbService (Retrofit), DTOs, MealMapper

:app        — shell: MainActivity, NavHost, BottomNav
              navigation/ → Screen (route definitions), BottomNavDestination (tab metadata)
              di/         → AppModule (networkModule, coreDataModule, savedRecipesModule, homeModule, detailModule, cookingModule, searchModule, favoritesModule)
              data/       → SavedRecipesRepository (Room-backed singleton, shared across all ViewModels)
              data/local/ → SaffronDatabase, SavedRecipeDao, SavedRecipeEntity (Room)
              ui/components/ → RecipeCard (shared 2-column grid card, used by Home + Favorites)
              ui/<feature>/ — each feature owns its Screen, ViewModel, and UiState:
                ui/home/      → HomeScreen, HomeViewModel, HomeUiState
                ui/detail/    → RecipeDetailScreen, RecipeDetailViewModel, RecipeDetailUiState
                ui/search/    → SearchScreen, SearchViewModel, SearchUiState
                ui/favorites/ → FavoritesScreen, FavoritesViewModel, FavoritesUiState
                ui/profile/   → ProfileScreen (stub)
                ui/cooking/   → CookingModeScreen, CookingModeViewModel, CookingModeUiState
```

When adding a new feature module use `android.library` for anything with Compose/resources, `kotlin.jvm` for pure logic. Declare the plugin `apply false` in the root `build.gradle.kts` first.

## Architecture

Standard MVI / unidirectional data flow:

```
:core:data  ←  repositories (interface + fake impl)
    ↓
:app (ViewModels — inject via Koin)
    ↓
:app (Composable screens ← StateFlow/UiState)
```

**Completed:**
1. Koin DI — `networkModule`, `coreDataModule`, `savedRecipesModule`, `homeModule`, `detailModule`, `cookingModule`, `searchModule`, `favoritesModule` wired in `SaffronApplication`.
2. Home screen — `HomeViewModel` + `HomeScreen` (featured card, category chips, 2-column grid, async load from TheMealDB). `categoryJob` cancellation + structured concurrency in `loadData()`.
   - Background: white (not Linen).
   - Category chips: pill shape (`RoundedCornerShape(percent=50)`), filled Saffron/white (selected) or Cream/Saffron160 (unselected), no border, uppercase labels.
   - Section labels ("FEATURED TONIGHT", "SAVED FOR THE WEEK") are `.uppercase()`.
   - Bookmark: `Icons.Outlined.BookmarkBorder` (unsaved) / `Icons.Filled.Bookmark` (saved), always Saffron tint on Home cards.
   - RecipeCard title: 16sp / 20sp line height.
3. Recipe Detail screen — `RecipeDetailViewModel` + `RecipeDetailScreen` (hero, meta strip, ingredient list, "Start cooking" CTA). `isError` branch + `retry()` wired; 0dp button elevation; category label in sentence case.
   - Hero: 4:3 aspect ratio, Cream placeholder. Back (top-left) and bookmark (top-right) float on a 38dp circular pill (`rgba(255,255,255,0.92)`).
   - Bookmark tint: Saffron when saved, **Cinnamon when unsaved** (Detail screen differs from Home/Search).
   - Rating row (below title, above meta strip): 5 stars — filled = Saffron40 `#F5C76A`, empty = `#C9C2B6`; value + count in 12sp Inter Regular Cinnamon. Only rendered when `recipe.rating != null`.
   - Meta strip: 3-up cards (clock / users / flame), Cream bg, Saffron160 icon tint, `labelLarge` value, Cinnamon caption.
   - "Start cooking" CTA: full-width, 52dp, 10dp radius, Saffron fill, 0dp elevation, trailing `Icons.Filled.ChevronRight` (18dp, white).
4. Cooking Mode — `CookingModeScreen` + `CookingModeViewModel` fully aligned with Claude Design spec:
   - Header: × exit icon (left), "Step N of M" centered, 48dp spacer (right). No divider.
   - Step pills: active = Saffron/white; done = Cream/Saffron160; pending = Cream/Cinnamon. Numbers only — no check icon.
   - Content: recipe name overline (Saffron) → `step.title` in Playfair 30sp → `step.instruction` Inter Light 17sp → plain checkbox "Mark this step done".
   - Footer: "Back" (natural width, secondary) | "Next step" / "Finish" (fills remaining space; Finish has leading CheckCircle icon). Gap 10dp, bottom padding 18dp.
5. Brand fonts bundled — Playfair Display (400/500) + Inter (300/400/500) as TTF files in `core/ui/src/main/res/font/`. `Type.kt` uses `Font(R.font.*)` — renders in Compose Previews, works offline, no GMS dependency.
6. Search screen — `SearchViewModel` + `SearchScreen` aligned with Claude Design spec:
   - Header: "Search" in Playfair 26sp.
   - Input: 48dp tall, 10dp radius, white bg, Saffron focus ring (1dp border on focus).
   - Filter chips: horizontal scroll row — All / Breakfast / Lunch / Dinner / Baking. Pill shape, Saffron selected.
   - Results: 92×70dp thumbnail, category overline, Playfair Medium 16sp title, clock + people meta row, bookmark toggle. 12dp gap between rows (no dividers).
   - Empty state: 32dp search icon + "No results for "X". Try a different ingredient or dish."
   - Pre-loads initial recipes on open via `getRecipes()`; debounced full-text search (300ms) via `searchMeals(query)`.
   - `savedIds` sourced from shared `SavedRecipesRepository` (Room-backed); category filter applied client-side.
   - Bookmark: `Icons.Outlined.BookmarkBorder` (unsaved) / `Icons.Filled.Bookmark` (saved). Tint: Saffron when saved, Cinnamon when unsaved (consistent with Home/Favorites RecipeCard).
7. Favorites screen — `FavoritesViewModel` + `FavoritesScreen` aligned with Claude Design spec:
   - Header: "Favorites" in Playfair 26sp (same pattern as Search header).
   - Empty state: 32dp `Icons.Outlined.BookmarkBorder` + "Your saved recipes will live here." — both `Color(0xFF8A7A5C)`, centered with `fillMaxSize`.
   - Grid: 2-column, 12dp column gap, 16dp row gap, 16dp horizontal padding, 24dp bottom — same `RecipeCard` as Home (bookmark always filled/Saffron since all cards are saved).
   - Tapping a card navigates to RecipeDetail; unsaving a card removes it from the grid instantly via Room Flow.
   - `RecipeCard` extracted from `HomeScreen.kt` to `ui/components/RecipeCard.kt` (`internal`) — shared by Home and Favorites.
   - Shared saved state — `SavedRecipesRepository` (Room `saved_recipes` table) is a Koin `single`. All four ViewModels (Home, Search, Detail, Favorites) inject it. `savedIdsFlow: Flow<Set<String>>` keeps bookmark icons in sync across screens. Saves survive app restarts.
   - `RecipeDetailUiState` gained a `savedIds: Set<String>` field so `load()` can correctly set `isSaved` after the network call completes.

**Navigation notes:**
- Bottom nav Home tab uses `navController.popBackStack(Screen.Home.route, inclusive = false)` instead of `navigate()` to avoid re-creating the Home screen when it is already the top destination. All other tabs use the standard `navigate { popUpTo / launchSingleTop / restoreState }` pattern.

**Data layer notes:**
- `MealMapper.parseSteps` — paragraph-break detection first (`\r?\n\s*\r?\n`); falls back to single line splits if no paragraph breaks found.
- `MealDbRecipeRepository.getRecipes()` — `preferredCategoryIds` list (lowercase); fetches live category names from `getCategories()` at call time and resolves to exact API names (case-insensitive, falls back to `replaceFirstChar { uppercase }` if API fails).

**Next features:**
8. **Profile screen** — implement `ProfileScreen` with stats (saved count, cooked count) and settings rows.

## Brand Rules (non-negotiable)

- **Color** — Saffron `#C8860A` is the only hero color. Apply sparingly (CTAs, active states, bookmark). Full palette in `:core:ui/Color.kt`.
- **Type** — Playfair Display for recipe titles, screen headers, category names (never below 18sp, never in paragraphs). Inter Light (300) for body copy. Inter Medium (500) max for labels/buttons — never Bold.
- **Flat UI** — zero elevation shadow on resting cards/buttons. Use `0.dp` `tonalElevation` on `NavigationBar`. Hairline borders (`0.5dp`) instead of shadows.
- **Voice** — sentence case everywhere. No emoji. No "Amazing!" — say "Saved." Metadata abbreviates ("35 min"); instructions spell out ("thirty-five minutes").
- **Dynamic color disabled** — `SaffronTheme` enforces the brand palette on all API levels.
- **Icons** — Material Icons Extended for now (outlined unselected, filled selected). Bookmark icon: `Icons.Outlined.BookmarkBorder` (unsaved) / `Icons.Filled.Bookmark` (saved). Tint: Saffron when saved, Cinnamon when unsaved — applies on all screens (Home, Search, Favorites, Detail).

## Linting

ktlint **1.4.1** + `io.nlopez.compose.rules:ktlint:0.4.22`. Config in `.editorconfig` at project root.

**Coverage limitation:** ktlint-gradle cannot detect Android source sets under AGP 9.x + Kotlin 2.x (adding `kotlin-android` explicitly conflicts with `kotlin-compose`). As a result:
- `:core:data` (Kotlin JVM) — fully covered; `ktlintFormat` auto-fixes on every run.
- `:app`, `:core:ui` (Android) — only `.kts` build scripts are checked. Kotlin source files must be formatted manually via Android Studio (`Code → Reformat Code`) or by hand.

The plugin is applied per-module via `plugins { alias(libs.plugins.ktlint) }` and configured in root `build.gradle.kts` using `subprojects { plugins.withId("org.jlleitschuh.gradle.ktlint") { ... } }`.

Active `.editorconfig` suppressions:
- `max_line_length = off` — long string literals in fake data and modifier chains make a hard limit impractical.
- `ktlint_function_naming_ignore_when_annotated_with = Composable` — allows PascalCase `@Composable` function names.

Known open compose-rule violations (require manual fixes):
- `ModifierMissing` — `HomeHeader`, `InitialsAvatar`, `SearchBar`, `CategoryChip`, `FeaturedSection`, `IngredientRow`, `StepContent`, `Footer`
- `RememberMissing` — `chunked(2)` and `.map { }` in `HomeContent`
- `UnstableCollections` — `CategoryRow(categories: List<Pair<...>>)`

## Key Conventions

- All versions in `libs.versions.toml` — never hardcode in build files. Exception: ktlint engine version is hardcoded as `version.set("1.4.1")` in `build.gradle.kts` because it overrides the plugin's bundled engine (1.0.1) and the version catalog accessor doesn't resolve inside `subprojects {}`.
- Named arguments use single-space `=` — no alignment padding. This is the enforced ktlint style. Use Android Studio `Code → Reformat Code` to keep new code consistent.
- All UI in Compose — no XML layouts.
- BOM-managed Compose deps have no version in the catalog (BOM provides it). Navigation Compose and Koin need explicit versions.
- `keepRules/rules.keep` holds ProGuard keep rules.
- Each feature lives in `app/ui/<feature>/` — Screen, ViewModel, and UiState co-located. Shared UI components that need `:core:data` types (e.g. `RecipeCard`) live in `app/ui/components/` (internal to `:app`). Pure design-system components with no data-model dependency move to `:core:ui`.
- Data API is TheMealDB v1 (free, no key). `MealDbRecipeRepository` in `:core:data` is the live impl; `FakeRecipeRepository` is the in-memory fallback for tests — not yet wired into any test module.
