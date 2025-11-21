# Repository Guidelines

## Project Structure & Module Organization

- `composeApp/`: Compose Multiplatform UI shell; platform code in `androidMain` and `iosMain`,
  shared UI/state in `commonMain`, configs in `debug` and `release`.
- `shared/`: cross-platform domain/data logic with `commonMain` and `commonTest`.
- `core/`: authentication, login, and session modules following Clean Architecture (presentation →
  domain → data).
- `feature/`: feature-specific modules (e.g., `dictionary`) that plug into shared/core flows.
- `iosApp/`: SwiftUI runner; open `iosApp/iosApp.xcodeproj` for device/simulator builds. `server/`
  holds backend utilities; `docs/` contains architecture and platform guides.

## Build, Test, and Development Commands

- Android debug: `./gradlew :composeApp:assembleDebug` then `./gradlew :composeApp:installDebug` to
  deploy.
- Android release: `./gradlew :composeApp:assembleRelease`; use `./gradlew reinstallRelease` +
  `installAndRunRelease` when signatures change.
- iOS: run from Xcode (`open iosApp/iosApp.xcodeproj`, select scheme, Cmd+R); do not call
  `:composeApp:embedAndSignAppleFrameworkForXcode` directly.
- Static checks: `./gradlew :composeApp:lint` for Android lint and `./gradlew :composeApp:check` for
  aggregated verification.
- Unit tests: `./gradlew :shared:allTests` for KMP logic; `./gradlew :composeApp:commonTest` for
  shared UI logic.

## Coding Style & Naming Conventions

- Kotlin official formatting (4-space indent, trailing commas preferred in multiline). Use `val`
  /immutability and explicit visibility.
- Composables use `SomethingScreen`/`SomethingSection`; view models end with `ViewModel`; use
  `StateFlow`/`MutableStateFlow` for UI state.
- Packages stay lowercase dot-style (`com.linli.blackcatnews.*`); resources and routes keep
  lowercase with hyphens or underscores where applicable.
- Keep presentation lean: UI → ViewModel intents → UseCases → repositories/providers. Avoid platform
  calls in `commonMain`.

## Testing Guidelines

- Place shared tests in `composeApp/src/commonTest` or `shared/src/commonTest`; Android-specific
  tests live under `androidTest`/`androidDebug`.
- Name tests with behavior intent (`functionName_shouldDoX_whenY`). Prefer fakes over live
  Firebase/services; isolate Ktor/DB behind interfaces.
- Run fast KMP unit suites (`:shared:allTests`) before opening PRs; add regression tests when
  touching auth/session flows.

## Commit & Pull Request Guidelines

- Commit style mirrors history: `<type>: <concise summary>` (e.g., `feat: Android平台評分功能`);
  English or Traditional Chinese summaries are fine. Keep commits scoped and atomic.
- PRs include context, linked issues, and risk notes; attach screenshots or screen recordings for UI
  changes (Android/iOS). State how to reproduce and what tests were run.
- Ensure Gradle tasks you ran are listed in the PR description; keep Firebase keys and keystores out
  of commits.

## Security & Configuration Tips

- Firebase configs live at `composeApp/src/{debug,release}/google-services.json` and
  `iosApp/iosApp/GoogleService-Info-{Debug,Release}.plist`; do not rename paths.
- Avoid running CocoaPods; project uses SPM for iOS. Never commit local `local.properties`,
  keystores, or generated credentials. Follow `docs/` guides for platform-specific fixes before
  changing build scripts.
