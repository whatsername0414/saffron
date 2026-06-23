# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Saffron** is an Android cooking app (package `com.saffron.cook`). It is in early scaffolding — currently only a `MainActivity` with a placeholder `Greeting` composable.

## Build & Run Commands

All commands run from the repo root via Gradle wrapper.

```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Run a single unit test class
./gradlew test --tests "com.saffron.cook.ExampleUnitTest"

# Lint
./gradlew lint

# Install on connected device
./gradlew installDebug
```

On Windows use `gradlew.bat` instead of `./gradlew`.

## Tech Stack

| Layer | Library |
|---|---|
| Language | Kotlin 2.2.10 |
| UI | Jetpack Compose + Material3 |
| Compose BOM | 2026.02.01 |
| Min SDK | 24 (Android 7.0) |
| Target/Compile SDK | 36 |
| AGP | 9.3.0-rc01 |
| Build system | Gradle 9.5 (Kotlin DSL) |

Dependencies are version-catalogued in `gradle/libs.versions.toml`.

## Architecture

The app targets a standard Android unidirectional-data-flow architecture with Jetpack Compose:

- **UI layer** — Composables in `app/src/main/java/com/saffron/cook/`. Screens and components live here. The theme is `SaffronTheme` (defined in `ui/theme/`).
- **Presentation layer** — ViewModels (not yet added) should be placed alongside their screens or in a `viewmodel/` sub-package.
- **Data layer** — Repositories and data sources (not yet added) belong in a `data/` package.

`SaffronTheme` supports dynamic color (Android 12+) and falls back to static `DarkColorScheme`/`LightColorScheme`. Custom brand colors go in `ui/theme/Color.kt`.

## Key Conventions

- Single module (`:app`) for now — extract feature modules only if the codebase grows large enough to warrant it.
- All UI must use Compose; no XML layouts.
- Version catalog (`libs.versions.toml`) is the single source of truth for dependency versions — never hardcode versions in build files.
- `keepRules/rules.keep` holds ProGuard keep rules for the release build.
