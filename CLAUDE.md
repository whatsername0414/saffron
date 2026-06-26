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
              auth/       → AuthRepository interface + FirebaseAuthRepository (Firebase Auth + Google Sign-In via Credential Manager)
              di/         → AppModule (networkModule, coreDataModule, savedRecipesModule, notesModule, authModule, homeModule, detailModule, cookingModule, searchModule, favoritesModule, loginModule, profileModule)
              data/       → SavedRecipesRepository (Room-backed singleton, shared across all ViewModels)
                            RecipeNotesRepository (Room-backed, Koin single in notesModule)
              data/local/ → SaffronDatabase v3, SavedRecipeDao, SavedRecipeEntity, RecipeNoteDao, RecipeNoteEntity, CookedRecipeDao, CookedRecipeEntity (Room)
              ui/components/ → RecipeCard (horizontal list row composable, shared by Home + Favorites; accepts `Recipe`)
              ui/<feature>/ — each feature owns its Screen, ViewModel, and UiState:
                ui/home/       → HomeScreen, HomeViewModel, HomeUiState
                ui/detail/     → RecipeDetailScreen, RecipeDetailViewModel, RecipeDetailUiState
                ui/search/     → SearchScreen, SearchViewModel, SearchUiState
                ui/favorites/  → FavoritesScreen, FavoritesViewModel, FavoritesUiState
                ui/profile/    → ProfileScreen, ProfileViewModel, ProfileUiState
                ui/login/      → LoginScreen, LoginViewModel, LoginUiState, LoginEvent
                ui/cooking/    → CookingModeScreen, CookingModeViewModel, CookingModeUiState
                ui/notes/      → NoteEditorScreen, NoteEditorViewModel, NoteEditorUiState
                ui/noteslist/  → NoteListScreen, NoteListViewModel, NoteListUiState
                ui/notedetail/ → NoteDetailScreen, NoteDetailViewModel, NoteDetailUiState
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
1. Koin DI — `networkModule`, `coreDataModule`, `savedRecipesModule`, `notesModule`, `authModule`, `homeModule`, `detailModule`, `cookingModule`, `searchModule`, `favoritesModule`, `loginModule`, `profileModule` wired in `SaffronApplication`.
2. Home screen — `HomeViewModel` + `HomeScreen` (featured card, category chips, vertical list rows, async load from TheMealDB). `categoryJob` cancellation + structured concurrency in `loadData()`.
   - Background: white (not Linen).
   - Category chips: pill shape (`RoundedCornerShape(percent=50)`), filled Saffron/white (selected) or Cream/Saffron160 (unselected), no border, uppercase labels.
   - Section labels ("FEATURED TONIGHT", "SAVED FOR THE WEEK") are `.uppercase()`.
   - "Saved for the week" section: vertical list of `RecipeCard` rows (`HomeUiState.recipes: List<Recipe>`), 16dp horizontal padding, 12dp gap between rows.
   - Bookmark: `Icons.Outlined.BookmarkBorder` (unsaved) / `Icons.Filled.Bookmark` (saved). Tint: Saffron when saved, Cinnamon when unsaved.
3. Recipe Detail screen — `RecipeDetailViewModel` + `RecipeDetailScreen` (hero, meta strip, ingredient list, "Start cooking" CTA). `isError` branch + `retry()` wired; 0dp button elevation; category label in sentence case.
   - Hero: 4:3 aspect ratio, Cream placeholder. Back (top-left) and bookmark (top-right) float on a 38dp circular pill (`rgba(255,255,255,0.92)`).
   - Bookmark tint: Saffron when saved, **Cinnamon when unsaved** (Detail screen differs from Home/Search).
   - Rating row (below title, above meta strip): 5 stars — filled = Saffron40 `#F5C76A`, empty = `#C9C2B6`; value + count in 12sp Inter Regular Cinnamon. Only rendered when `recipe.rating != null`.
   - Meta strip: 3-up cards (clock / users / flame), Cream bg, Saffron160 icon tint, `labelLarge` value, Cinnamon caption.
   - "Start cooking" CTA: full-width, 48dp, 10dp radius, Saffron fill, 0dp elevation, trailing `Icons.Filled.ChevronRight` (18dp, white).
4. Cooking Mode — `CookingModeScreen` + `CookingModeViewModel` fully aligned with Claude Design spec:
   - Header: × exit icon (left), "Step N of M" centered, 48dp spacer (right). No divider.
   - Step pills: active = Saffron/white; done = Cream/Saffron160; pending = Cream/Cinnamon. Numbers only — no check icon.
   - Content: recipe name overline (Saffron) → `step.title` in Playfair 30sp → `step.instruction` Inter Light 17sp → plain checkbox "Mark this step done".
   - Footer: "Back" (natural width, secondary) | "Next step" / "Finish" (fills remaining space; Finish has leading CheckCircle icon). Gap 10dp, bottom padding 18dp.
   - Finish triggers `viewModel.onFinish()` which sets `isFinished = true` in `CookingModeUiState`. A `ModalBottomSheet` appears over the cooking layout showing the completion screen (check-circle, recipe name, "That's it — you're done.", "Add a note" primary + "Maybe later" ghost). Swipe dismiss and "Maybe later" both call `onBack`. "Add a note" navigates to `Screen.NoteEditor`.
   - **Step timers** — time references in step instructions (e.g. "bake for 25 minutes") are highlighted in Saffron160 with underline and are tappable via `LinkAnnotation.Clickable`. Each detected duration also renders a `TimerChip` below the step text ("Set timer · M:SS"). `parseTimedSpans()` uses `TIME_REGEX` to detect both range ("2-3 mins" → upper bound) and single-value patterns with min/sec/hour unit variants.
   - Timer sheet — `ModalBottomSheet` opened by `onShowTimer(seconds, stepTitle)`. Header overline: "Timer" / "Time's up" (Saffron). "From step: "X"" centered caption (Cinnamon). 220dp `CircularProgressIndicator`, 10dp stroke, `StrokeCap.Round`, Saffron arc (Saffron160 when done), Cream track. Inside ring: 48sp Playfair Display countdown + "Counting down" / "Paused" / "Done" status caption. Button row: `[↺ reset IconButton] [Pause/Resume/Done full-width Button] [+ add-minute IconButton]`. Timer auto-starts when sheet opens. Reset resets to `timerInitialSeconds` and auto-restarts. `+1 min` adds 60s to remaining and total without auto-restarting. Device vibrates (waveform `[0, 400, 100, 400]`) via `SharedFlow<Unit>` side-effect when countdown reaches zero. `VIBRATE` permission declared in manifest.
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
   - List: vertical `LazyColumn` of `RecipeCard` rows (`FavoritesUiState.recipes: List<Recipe>`), 16dp horizontal padding, 12dp gap, 8dp top / 24dp bottom — bookmark always filled/Saffron since all entries are saved.
   - Tapping a row navigates to RecipeDetail; unsaving removes it instantly via Room Flow.
   - `RecipeCard` in `ui/components/RecipeCard.kt` (`internal`) — shared by Home and Favorites. Accepts `recipe: Recipe`. Layout: 92×70dp thumbnail (10dp radius, Cream bg), category overline, Playfair Medium 16sp title, clock + people meta row, bookmark `IconButton`.
   - Shared saved state — `SavedRecipesRepository` (Room `saved_recipes` table) is a Koin `single`. All four ViewModels (Home, Search, Detail, Favorites) inject it. `savedIdsFlow: Flow<Set<String>>` keeps bookmark icons in sync across screens. `savedRecipesFlow: Flow<List<Recipe>>` maps entities to `Recipe` inside the repository (using `SavedRecipeEntity.toRecipe()`). Saves survive app restarts.
   - `RecipeDetailUiState` gained a `savedIds: Set<String>` field so `load()` can correctly set `isSaved` after the network call completes.
   - No auth gate — saves and favorites work without sign-in. `FavoritesUiState` has no `isSignedIn` field; `FavoritesViewModel` does not inject `AuthRepository`.
8. Firebase Auth — Google Sign-In, no forced gates:
   - `auth/AuthRepository.kt` — interface: `currentUser: Flow<FirebaseUser?>`, `currentUserSnapshot: FirebaseUser?`, `signInWithGoogle(idToken)`, `signOut()`.
   - `auth/FirebaseAuthRepository.kt` — implements via `callbackFlow` on `FirebaseAuth.AuthStateListener`; sign-in uses `GoogleAuthProvider` + `suspendCancellableCoroutine`.
   - `ui/login/` — `LoginScreen` (Saffron-branded, Credential Manager Google Sign-In), `LoginViewModel`, `LoginUiState`/`LoginEvent`. Standalone screen at `Screen.Login` route.
   - `ui/profile/` — Two-state layout. **Signed-out**: generic "Your kitchen" / "On this device" with bordered user-icon circle; 3 stat cards (Saved/Cooked/Notes); "Add an account" Cream card with Credential Manager Google Sign-In button (no navigation to LoginScreen). **Signed-in**: real photo/initials + name/email; "Synced just now" row (CheckCircle, Saffron160) below stats; settings rows Account/Dietary preferences/Notifications/Help/Sign out (all uniform `SettingsRow` with ChevronRight). `ProfileUiState` holds `savedCount`, `cookedCount`, `notesCount` (live from Room flows), `isSigningIn`. `ProfileViewModel` exposes `handleGoogleIdToken()` and `signOut()`. Stat card taps: "Saved" → Favorites tab (standard tab-switch nav); "Notes" → `NotesList`; "Cooked" → no-op.
   - Bookmarking is ungated: `HomeViewModel` and `SearchViewModel` do not check auth; `onToggleSave` always calls `SavedRecipesRepository.toggle()`.
   - Firebase project: `saffron-cook-2026`. `app/google-services.json` is present. Google Sign-In enabled, debug SHA-1 registered.
   - Firebase deps: `firebase-bom:33.15.0`, `firebase-auth`, `credentials:1.3.0`, `credentials-play-services-auth`, `googleid:1.1.1`, `google-services:4.4.3` plugin.

9. Recipe Notes — post-cook journaling with full browse/edit/delete flow:
   - `RecipeNoteEntity` / `RecipeNoteDao` — Room table `recipe_notes`; fields: id, recipeId, recipeName, recipeImage, title, body, rating (0–5), labels (comma-separated), photos (comma-separated URIs, max 4), createdAt. `SaffronDatabase` bumped to v2 with `MIGRATION_1_2`. DAO has `observeAll`, `observeById` (Flow), `observeCount`, `getById`, `insert`, `update`, `deleteById`.
   - `RecipeNotesRepository` — `allNotesFlow`, `noteCountFlow`, `observeNote(id)` (Flow), `upsert`, `getNote`, `delete`. `labelsToString/fromString` and `photosToString/fromString` helpers (comma-split).
   - `NoteEditorScreen` — full-screen, route `note_editor/{recipeId}?noteId={noteId}`. Create mode (noteId=0): fetches recipe info from API, saves and navigates to Home. Edit mode (noteId≠0): loads existing note from Room, preserves original `createdAt` on save, pops back to NoteDetail. Header shows "New note" / "Edit note". Layout: ×, label, "Save" ghost; recipe context card (Cream, 44×44); borderless Playfair 26sp title; hairline divider; star rating (32dp); label chips (FlowRow, pill, Saffron selected); OutlinedTextField body; photo LazyRow (100×100, max 4).
   - `NoteListScreen` — route `notes_list`. Header: "Notes" Playfair 26sp + back arrow. Empty state: plus icon + caption. List: `LazyColumn`, cards with 14dp radius, 0.5dp border; each card shows 40×40 recipe image, recipe name overline (Saffron), note title (Playfair Medium 16sp), date (right), star rating (15dp, if > 0), body preview (2-line clamp), label chips. Navigated to from Profile "Notes" stat card.
   - `NoteDetailScreen` — route `note_detail/{noteId}`. Subscribes to `observeNote` Flow so edits from NoteEditor auto-refresh. Header: back arrow, "Note" label, "Delete" ghost (error red). Content: recipe context card, Playfair 28sp title, "Added {date}" caption, 22dp stars, label chips, body, 132dp photo horizontal scroll. Footer: pinned "Update" primary button (48dp). Delete triggers `ModalBottomSheet` confirm ("Keep" secondary / "Delete" error primary).
   - Photo picker: two launchers registered unconditionally — `PickVisualMedia` (single, used when 1 slot remains) and `PickMultipleVisualMedia(4)` (used when 2+ slots remain). ViewModel caps with `.take(4)`. No `takePersistableUriPermission` needed (modern Photo Picker).
   - `canSave` — true when any of title / body / rating / labels / photos is non-empty.

10. Cooked list — tracks every recipe completed in cooking mode, with per-recipe cook counts and last-cooked dates:
   - `CookedRecipeEntity` / `CookedRecipeDao` — Room table `cooked_recipes`; PK `recipeId` (TEXT); fields: recipeName, recipeImage, recipeCategory, times (INT, default 1), lastCookedAt (epoch millis). `SaffronDatabase` bumped to v3 with `MIGRATION_2_3`. DAO has `observeAll` (Flow, ordered by lastCookedAt DESC), `observeTotalCount` (Flow, SUM of times), insert-ignore + `incrementAndTouch`.
   - `CookedRecipesRepository` — `allCookedFlow`, `totalCountFlow`, `recordCooked(recipeId, recipeName, recipeImage, recipeCategory)` (upsert: insert-ignore returns -1 → increment).
   - `CookingModeViewModel.onFinish()` — sets `isFinished = true` AND calls `cookedRepository.recordCooked(...)` with the loaded recipe's id/title/imageUrl/categoryId.
   - `ProfileViewModel` — subscribes to `cookedRepository.totalCountFlow` to populate `cookedCount` in state (same pattern as `notesCount`).
   - `ui/cookedlist/` — `CookedListScreen`, `CookedListViewModel`, `CookedListUiState`/`CookedListItem`.
   - Screen layout: header "Cooked" Playfair 26sp + back arrow; empty state: 32dp `LocalFireDepartment` icon + "Recipes you finish in cooking mode will gather here."; non-empty: caption "{total} dishes cooked across {N} recipes" (bodySmall, TextTertiary), then `LazyColumn` of rows — 92×70dp thumbnail, category overline (Saffron uppercase), Playfair Medium 16sp title, meta row (flame icon + "Once"/"N times" | check-circle icon + "last {d MMM}"), bookmark toggle.
   - Profile "Cooked" stat card taps → `Screen.CookedList` (`"cooked_list"` route); tapping a cooked row navigates to RecipeDetail.

**Navigation notes:**
- Bottom nav Home tab uses `navController.popBackStack(Screen.Home.route, inclusive = false)` instead of `navigate()` to avoid re-creating the Home screen when it is already the top destination. All other tabs use the standard `navigate { popUpTo / launchSingleTop / restoreState }` pattern.
- `Screen.Login` exists as a standalone route but is not currently reachable from any bottom-nav tab.
- Profile stat card flows: "Saved" → Favorites tab (uses `popUpTo(Home) / launchSingleTop / restoreState`, same as bottom nav tap); "Notes" → `NotesList` → `NoteDetail` → (Update) → `NoteEditor` (edit mode, pops back to NoteDetail on save) / (Delete) → pops back to `NotesList`; "Cooked" → `CookedList` (simple push, `popBackStack` to return).
- `Screen.NoteEditor` — route `note_editor/{recipeId}?noteId={noteId}`. Create (from CookingMode): noteId defaults to 0, saves → Home. Edit (from NoteDetail): noteId set, saves → popBackStack.

**Data layer notes:**
- `MealMapper.parseSteps` — paragraph-break detection first (`\r?\n\s*\r?\n`); falls back to single line splits if no paragraph breaks found.
- `MealDbRecipeRepository.getRecipes()` — `preferredCategoryIds` list (lowercase); fetches live category names from `getCategories()` at call time and resolves to exact API names (case-insensitive, falls back to `replaceFirstChar { uppercase }` if API fails).

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

## Key Conventions

- All versions in `libs.versions.toml` — never hardcode in build files. Exception: ktlint engine version is hardcoded as `version.set("1.4.1")` in `build.gradle.kts` because it overrides the plugin's bundled engine (1.0.1) and the version catalog accessor doesn't resolve inside `subprojects {}`.
- Named arguments use single-space `=` — no alignment padding. This is the enforced ktlint style. Use Android Studio `Code → Reformat Code` to keep new code consistent.
- All UI in Compose — no XML layouts.
- BOM-managed Compose deps have no version in the catalog (BOM provides it). Navigation Compose and Koin need explicit versions.
- `keepRules/rules.keep` holds ProGuard keep rules.
- Each feature lives in `app/ui/<feature>/` — Screen, ViewModel, and UiState co-located. Shared UI components that need `:core:data` types (e.g. `RecipeCard`) live in `app/ui/components/` (internal to `:app`). Pure design-system components with no data-model dependency move to `:core:ui`.
- Data API is TheMealDB v1 (free, no key). `MealDbRecipeRepository` in `:core:data` is the live impl; `FakeRecipeRepository` is the in-memory fallback for tests — not yet wired into any test module.
