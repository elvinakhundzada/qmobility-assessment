# QMobility – Product Catalog (KMP)

A product catalog built with **Kotlin Multiplatform**, **Clean Architecture** and **MVI**.
All business and presentation logic lives in the shared `:kmp` module; the Android app is a
thin Jetpack Compose layer that renders state and forwards user intents.

Data comes from the [DummyJSON Products API](https://dummyjson.com/docs/products).

| Products (search + paging) | Details (favorite) | Favorites (offline) |
|---|---|---|
| Browse, debounced search, infinite scroll | Gallery, price with discount, heart toggle | Saved in Room, survives restarts |

## Setup

Requirements: Android Studio with AGP 9.2 support, JDK 17+, Android SDK platform 37.

```bash
./gradlew :app:installDebug              # build and install the app
./gradlew :kmp:testAndroidHostTest       # run the shared unit tests (JVM)
./gradlew :kmp:compileKotlinIosSimulatorArm64   # prove the shared code compiles for iOS
```

## Tech stack

| Concern | Choice |
|---|---|
| Shared code | Kotlin 2.4 Multiplatform: `android`, `iosArm64`, `iosSimulatorArm64` |
| UI (Android) | Jetpack Compose, Material 3, type-safe Navigation Compose, Coil 3 |
| Networking | Ktor 3 (OkHttp engine on Android, Darwin on iOS) + kotlinx.serialization |
| Persistence | Room KMP with the bundled SQLite driver |
| DI | Koin 4 (`koin-core` + `koin-core-viewmodel` in shared, `koin-compose-viewmodel` on Android) |
| ViewModels | AndroidX `lifecycle-viewmodel` (multiplatform) |
| Tests | `kotlin.test`, `kotlinx-coroutines-test`, Ktor `MockEngine`, hand-written fakes |

## Architecture

```
┌──────────────────────── :app (Android only) ────────────────────────┐
│  Compose screens ── collect StateFlow<State> / Flow<Effect>         │
│                  └─ send Intent ──────────────┐                     │
└───────────────────────────────────────────────┼─────────────────────┘
┌──────────────────────── :kmp (commonMain) ────▼─────────────────────┐
│ presentation   MviViewModel · Contract (State/Intent/Effect)        │
│                Reducer (pure) · UI models + mappers                 │
│ domain         Product · repository interfaces · use cases          │
│ data           Ktor data source · Room DAO · mappers · repo impls   │
│ di             Koin modules (+ expect/actual platformModule)        │
└─────────────────────────────────────────────────────────────────────┘
```

Dependencies point inwards: `presentation → domain ← data`. The domain layer has no
framework dependencies. Repository interfaces live in `domain`, implementations in `data`
(dependency inversion). DTOs and Room entities are `internal` to `:kmp` and never leave the
data layer.

```
kmp/src/
├── commonMain/kotlin/ae/qmobility/kmp/
│   ├── core/            AppResult, AppError
│   ├── domain/          model/, repository/, usecase/
│   ├── data/            remote/ (Ktor + DTOs), local/ (Room), mapper/, repository/
│   ├── presentation/    mvi/, model/, products/, details/, favorites/
│   └── di/              Koin modules, initKoin()
├── androidMain/         OkHttp engine, Room builder with Context
├── iosMain/             Darwin engine, Room builder
└── commonTest/          all unit tests (run on every target)
```

### MVI

Each screen has a **Contract** (`State`, `Intent`, `Effect`), a **Reducer** and a
**ViewModel** extending `MviViewModel<Intent, State, Transition, Effect>`:

```
View ──Intent──▶ ViewModel ──use case──▶ Transition ──Reducer──▶ State ──▶ View
                     └────────────────▶ Effect (one-shot) ──────────────▶ View
```

- **Intent**: what the user did (`QueryChanged`, `LoadNextPage`, `ToggleFavorite`…). `onIntent()` is the ViewModel's only input.
- **Transition**: how the state moves on as a result (`FirstPageLoading`, `NextPageLoaded`…).
- **Reducer**: a pure `(State, Transition) -> State` function. It's the only place state changes and is unit-tested without coroutines.
- **State**: one immutable data class per screen, exposed as `StateFlow<State>`. It holds display-ready UI models (`ProductUi` with formatted price, rating, badge), so every platform renders the same thing without re-implementing formatting.
- **Effect**: one-shot events such as navigation or a snackbar, delivered through a `Channel` so each is handled exactly once and buffered during configuration changes.

Why MVI over MVVM:
- A single state object means the screen can't reach an inconsistent combination of flags.
- Unidirectional flow makes every state change traceable to an intent.
- Pure reducers are trivial to test.
- A single `StateFlow` plus a single `onIntent` function is the smallest possible API surface for iOS to bind to.

### Notable decisions

- **Errors as values.** Repositories return `AppResult<T>` (`Success` / `Failure(AppError)`) instead of throwing. Failures are part of the signature, and Kotlin exceptions don't become Swift errors unless every function is annotated with `@Throws`. `CancellationException` is always rethrown.
- **No user-facing strings in shared code.** Errors reach the UI as a `UiError` enum; each platform maps it to its own localized text.
- **Pagination by hand, not Paging 3.** Paging state (`nextSkip`, `endReached`, `isLoadingMore`, `loadMoreError`) is part of the MVI state, is testable, and doesn't tie the iOS side to an Android-first library.
- **Search.** The query flow is trimmed, debounced (300 ms; clearing is immediate), and deduplicated after the debounce. Starting a new first page cancels any in-flight request, so a slow response for an old query can't overwrite newer results. The search endpoint pages the same way the list does.
- **Favorites.** The whole product is stored, not just its id, so the Favorites screen works offline. Room `Flow` queries are the single source of truth: the details heart and the Favorites list update automatically whenever a favorite changes anywhere.
- **Main-safety.** Ktor and Room (via `setQueryCoroutineContext(Dispatchers.IO)`) are main-safe, so ViewModels and use cases don't switch dispatchers.

## Tests

34 unit tests in `kmp/src/commonTest`, run with `./gradlew :kmp:testAndroidHostTest`:

| Area | What's covered |
|---|---|
| Mappers | DTO → domain, blank brand, page `hasMore`, product → entity and entity → product, price/rating/discount/category formatting, original price |
| Use cases | blank query → list endpoint, trimmed query → search, toggle add/remove |
| Repository | real Ktor client over `MockEngine`: request URL and parameters, JSON parsing, 404 → `NotFound`, 5xx → `Server`, I/O → `Network`, bad JSON → `Unknown` |
| Reducer | new query resets paging, next page appends without duplicate keys |
| ViewModels | first load, paging to the end, debounced search, error + retry, next-page error keeps items, favorite toggle + effect, favorites follow DB changes, navigation effects |

Fakes are written by hand rather than with a mocking library: mocking frameworks such as MockK are JVM-only, and hand-written fakes keep the tests runnable on every KMP target.

## iOS considerations

The brief doesn't require an iOS app, so there is none. The shared module is still built
for iOS: the `iosArm64` and `iosSimulatorArm64` targets compile, and `:kmp` produces a
`Shared` framework that an Xcode project can link.

What iOS already gets from `:kmp`:
- The same ViewModels, use cases, repositories and Room database as Android.
- iOS implementations in `iosMain`: the Darwin Ktor engine, and a Room database file in the app's Documents directory.

How an iOS app would use it:
1. **Startup.** Call `initKoin(...)` once when the SwiftUI `App` starts, passing `CommonConfiguration(enableNetworkLogs: true)` in debug builds. Swift doesn't see Kotlin default arguments, so a small Swift-facing wrapper in `iosMain` would keep that call short.
2. **ViewModel lifecycle.** Each screen owns a `ViewModelStore`, gets its ViewModel from Koin through it, and calls `store.clear()` when the screen goes away. That cancels `viewModelScope`, just like popping the Android back stack. iOS has no configuration changes, so this is the only lifecycle event to handle.
3. **Observing state.** A SwiftUI `ObservableObject` wraps each ViewModel, collects `state` into a `@Published` property, and forwards user actions to `onIntent`. I'd add [SKIE](https://skie.touchlab.co) for this: it exposes `StateFlow` as a typed Swift `AsyncSequence` (`for await state in viewModel.state`) and the sealed `Intent`/`Effect` types as exhaustive Swift enums. Without it, generics are lost on the way to Swift and every value needs a cast.
4. **Effects.** Collect `effects` the same way, and use them to drive `NavigationStack` or show an alert.
5. **Strings.** The iOS app maps `UiError` to its own `Localizable.strings`. The shared code contains no user-facing text.

## Possible next steps

- Offline fallback for the details screen from the favorites table.
- Locale-aware currency formatting through an `expect`/`actual` formatter.
- Compose UI tests for the stateless `*Content` composables, and a Koin module verification test.