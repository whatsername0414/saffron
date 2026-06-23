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
              di/         → AppModule (networkModule, coreDataModule, homeModule, detailModule, cookingModule, searchModule)
              ui/<feature>/ — each feature owns its Screen, ViewModel, and UiState:
                ui/home/      → HomeScreen, HomeViewModel, HomeUiState
                ui/detail/    → RecipeDetailScreen, RecipeDetailViewModel, RecipeDetailUiState
                ui/search/    → SearchScreen, SearchViewModel, SearchUiState
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
:app (ViewModels — inject via Koin)
    ↓
:app (Composable screens ← StateFlow/UiState)
```

**Completed:**
1. Koin DI — `networkModule`, `coreDataModule`, `homeModule`, `detailModule`, `cookingModule`, `searchModule` wired in `SaffronApplication`.
2. Home screen — `HomeViewModel` + `HomeScreen` (featured card, category chips, 2-column grid, async load from TheMealDB). `categoryJob` cancellation + structured concurrency in `loadData()`.
3. Recipe Detail screen — `RecipeDetailViewModel` + `RecipeDetailScreen` (hero, meta strip, ingredient list, "Start cooking" CTA). `isError` branch + `retry()` wired; 0dp button elevation; category label in sentence case.
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
   - `savedIds` managed in-memory in `SearchViewModel`; category filter applied client-side.

**Data layer notes:**
- `MealMapper.parseSteps` — paragraph-break detection first (`\r?\n\s*\r?\n`); falls back to single line splits if no paragraph breaks found.
- `MealDbRecipeRepository.getRecipes()` — `preferredCategoryIds` list (lowercase); fetches live category names from `getCategories()` at call time and resolves to exact API names (case-insensitive, falls back to `replaceFirstChar { uppercase }` if API fails).

**Next features:**
7. **Favorites screen** — implement `FavoritesScreen` with a 2-column grid of saved recipes. Requires shared saved-state persistence (DataStore or in-memory singleton) so saves in Detail/Search survive navigation.
8. **Profile screen** — implement `ProfileScreen` with stats (saved count, cooked count) and settings rows.

## Brand Rules (non-negotiable)

- **Color** — Saffron `#C8860A` is the only hero color. Apply sparingly (CTAs, active states, bookmark). Full palette in `:core:ui/Color.kt`.
- **Type** — Playfair Display for recipe titles, screen headers, category names (never below 18sp, never in paragraphs). Inter Light (300) for body copy. Inter Medium (500) max for labels/buttons — never Bold.
- **Flat UI** — zero elevation shadow on resting cards/buttons. Use `0.dp` `tonalElevation` on `NavigationBar`. Hairline borders (`0.5dp`) instead of shadows.
- **Voice** — sentence case everywhere. No emoji. No "Amazing!" — say "Saved." Metadata abbreviates ("35 min"); instructions spell out ("thirty-five minutes").
- **Dynamic color disabled** — `SaffronTheme` enforces the brand palette on all API levels.
- **Icons** — Material Icons Extended for now (outlined unselected, filled selected). Bookmark is the only filled icon in resting state (saved recipes).

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
- Each feature lives in `app/ui/<feature>/` — Screen, ViewModel, and UiState co-located. Shared UI components will move to `:core:ui` once there are two or more consumers.
- Data API is TheMealDB v1 (free, no key). `MealDbRecipeRepository` in `:core:data` is the live impl; `FakeRecipeRepository` is the in-memory fallback for tests — not yet wired into any test module.
