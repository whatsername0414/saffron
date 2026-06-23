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
              repository/ → RecipeRepository interface
              repository/fake/ → FakeRecipeRepository (6 full recipes, in-memory)

:app        — shell: MainActivity, NavHost, BottomNav, placeholder screens
              navigation/ → Screen (route definitions), BottomNavDestination (tab metadata)
              ui/screen/  → HomeScreen, SearchScreen, FavoritesScreen, ProfileScreen (stubs)
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

**Next steps (in order):**
1. **Koin DI** — add `koin-android` + `koin-androidx-compose`, create a `di/` module in `:app`, bind `FakeRecipeRepository` as `RecipeRepository`. This unblocks ViewModels.
2. **Home screen** — category chips, featured editorial card (Playfair title + photo scrim), 2-column recipe grid. Needs a `HomeViewModel` backed by `RecipeRepository`.
3. **Recipe Detail screen** — hero image, title, rating, 3-up meta strip, ingredient list, "Start cooking" CTA.
4. **Cooking Mode** — full-screen step flow, `StepIndicator`, done checkbox.

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
- Screen-level composables live in `app/ui/screen/`; shared UI components will live in `:core:ui` once extracted.
