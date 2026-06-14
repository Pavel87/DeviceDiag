# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew build                  # Full build (all variants)
./gradlew test                   # Run unit tests
./gradlew testDebugUnitTest      # Run unit tests for debug variant only
./gradlew lint                   # Run Android Lint checks
./gradlew lintDebug              # Lint for debug variant
```

**Build configuration:**
- Min SDK: 24, Target SDK: 37, Compile SDK: 37
- Java target: 17
- Kotlin: 2.2.20, AGP: 9.1.0, KSP: 2.3.6
- Single flavor: `free` (applicationId: `com.pacmac.devicediag.free`)
- ProGuard enabled for release builds

## Architecture

**MVVM + Repository Pattern with Jetpack Compose (single-module app)**

### Entry Points
- `DeviceInfoApplication` — Hilt `@HiltAndroidApp` application class
- `DeviceInfoActivity` — Single activity, hosts Compose navigation tree

### Layer Structure
1. **UI** — Pure Jetpack Compose; shared components in `ui/components/`, theming in `ui/theme/`
2. **ViewModels** — `@HiltViewModel`-annotated, one per feature module; use `mutableStateOf` / `StateFlow` for state
3. **Repository** — `AppRepository` / `AppRepositoryImpl` in `main/data/` handles permissions, version checks, export slots; `LocationRepository` for GPS; injected via Hilt singleton bindings in `AppModule`
4. **Utils/Info** — Per-feature static utility/info classes (e.g., `CPUInfoKt`, `DisplayInfoKt`) that read system APIs and return `UIObject` list items

### Feature Modules (all under `com.pacmac.devinfo/`)
Each feature has its own sub-package with a ViewModel, Compose screen(s), and utility/info class:

| Package | What it covers |
|---------|---------------|
| `battery/` | Battery status, health, temperature |
| `camera/` | Camera capabilities per lens |
| `cellular/` | SIM, radio, telephony info |
| `config/` | Build properties |
| `cpu/` | CPU type, cores, frequency, features |
| `display/` | Resolution, DPI, refresh rate |
| `export/` | Data export/share (`ExportViewModel`, `ExportTask`) |
| `gps/` | GNSS / location (`GPSViewModelKt`, `LocationRepository`) |
| `sensor/` | Sensor list + detail screen |
| `storage/` | Internal/external/RAM storage |
| `wifi/` | Wi-Fi and network info |
| `main/` | Navigation hub, dashboard, permission orchestration |

### Key Patterns
- **`UIObject`** — Generic key/value model used across all info modules to populate list screens
- **Permission flow** — Centralized in `AppRepository`; ViewModels observe `PermissionState` from there
- **DataStore Preferences** — Used for all persistent settings (migrated from SharedPreferences)
- **Ktor client** — Used only for lightweight API calls (e.g., version checking)
- **Ad mediation** — Google AdMob + AppLovin, Facebook, InMobi, Unity adapters; ad unit IDs differ between `debug` and `release` build types via `buildConfigField`

### Dependency Injection
Hilt is the DI framework. Module bindings are in `AppModule`. All repositories are provided as `@Singleton`. ViewModels are injected via `hiltViewModel()` in Compose.

## CI/CD
GitHub Actions (`.github/workflows/android.yml`) triggers on push to `master` and PRs, runs `./gradlew build`. **Note:** CI config currently uses JDK 11 but the build requires JDK 17 — CI will need updating.
