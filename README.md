# StreamBox

StreamBox is an Android TV streaming application built with Kotlin and Jetpack Compose.  
The project is designed as a portfolio-quality Android TV application with a clean, modular architecture and support for live channel browsing and Media3-based playback.

## Project Status

**Current Phase:** Phase 7 — Favorites, History & Continue Watching

**Current Milestone:** Phase 7.1A — Favorites persistence foundation

### Completed

- Android TV project foundation
- TV-focused Compose UI
- Home screen
- Channel categories
- Live channel browsing
- Channel details screen
- Navigation between Home, Channels, Details and Player
- Remote/demo channel data source
- Remote channel data source using Retrofit
- Repository and use-case layers
- Media3 live playback
- HLS playback support
- Channel switching
- Custom player controls
- Volume controls
- Brightness controls
- Android Picture-in-Picture
- Home button → Picture-in-Picture behavior
- Normal Back navigation
- Room-based favorites persistence foundation

### Next

- Favorite / unfavorite button on Channel Details
- Favorites screen
- Continue Watching persistence
- Watch history
- Search and filtering
- EPG / program guide
- Settings
- Testing and performance hardening

---

## Features

### Home

The Home screen provides:

- StreamBox top bar
- Hero section
- Watch Now entry point
- Channel categories
- Live channel rows
- Loading state

### Live Channels

Users can:

- Browse channels
- Browse channels by category
- Open channel details
- Start playback

### Channel Details

The Channel Details screen provides:

- Channel name
- Live/offline state
- Channel description
- Stream availability
- Watch action
- Back navigation

### Media Player

Playback is implemented using **AndroidX Media3 / ExoPlayer**.

Current player functionality includes:

- HLS playback
- Live stream playback
- Previous channel
- Next channel
- Play / pause
- Volume decrease
- Volume increase
- Mute / unmute
- Brightness decrease
- Brightness increase
- Brightness percentage
- Picture-in-Picture
- Android TV D-pad support
- Mobile testing support

### Picture-in-Picture

StreamBox uses Android Picture-in-Picture while the player is active.

Current behavior:

- Pressing the Home button while watching opens PiP.
- Back does not open PiP.
- Back performs normal navigation.
- Returning from PiP keeps the player alive.
- Player resources are released when the player screen is actually disposed.

---

## Favorites

Favorites use Room for local persistence.

The current persistence layer stores:

- Channel ID
- Favorite creation timestamp

Channel metadata remains owned by the existing channel repository. Room is therefore used for user state rather than becoming a second source of truth for channel data.

Current architecture:

```text
UI
 ↓
Favorite ViewModel
 ↓
Favorite Use Cases
 ↓
Favorite Repository
 ↓
Room DAO
 ↓
SQLite
```

The next milestone connects this persistence layer to the Channel Details UI.

---

## Architecture

StreamBox follows a Clean Architecture-style structure:

```text
app
└── src
    └── main
        └── java
            └── com.allay.streambox
                ├── data
                │   ├── local
                │   ├── remote
                │   └── repository
                │
                ├── domain
                │   ├── model
                │   ├── repository
                │   └── usecase
                │
                ├── feature
                │   ├── channel_details
                │   ├── channels
                │   ├── components
                │   ├── home
                │   └── player
                │
                ├── navigation
                │
                └── ui
                    └── theme
```

### Data Layer

Responsible for:

- Remote API access
- Retrofit
- OkHttp
- Demo/local channel data
- Repository implementations
- Room persistence

### Domain Layer

Contains:

- Domain models
- Repository contracts
- Use cases

The domain layer does not depend on Android UI code.

### Feature Layer

Contains the Compose UI and ViewModels for:

- Home
- Channels
- Channel Details
- Player

### Navigation

Navigation Compose is used for:

```text
Home
  ↓
Channels
  ↓
Channel Details
  ↓
Player
```

---

## Technology Stack

| Technology | Version |
|---|---|
| Kotlin | 2.2.10 |
| Android Gradle Plugin | 9.2.1 |
| Gradle | 9.4.1 |
| Compile SDK | 37 |
| Target SDK | 36 |
| Minimum SDK | 24 |
| Jetpack Compose BOM | 2026.02.01 |
| Android TV Material | 1.0.0 |
| Navigation Compose | 2.9.6 |
| Media3 | 1.9.2 |
| Retrofit | 3.0.0 |
| OkHttp | 5.1.0 |
| Kotlin Serialization | 2.2.10 |
| Room | 2.8.5 |

> Minimum SDK is intentionally kept at API 24 and should not be increased without an explicit project decision.

---

## Project Configuration

### Application ID

```text
com.allay.streambox
```

### Package

```text
com.allay.streambox
```

### Android TV

The application is configured as an Android TV application using:

- Leanback launcher support
- TV banner
- Non-touchscreen requirement
- TV Material components
- D-pad-friendly focusable UI

The project can also be tested on a mobile Android device/emulator during development.

---

## Data Flow

The channel flow is designed around repository and use-case boundaries:

```text
Remote / Demo Data Source
          ↓
Channel Repository
          ↓
Use Case
          ↓
Channel ViewModel
          ↓
Compose UI
```

Playback follows the selected channel from the domain/UI model into the Media3 player.

Favorites follow a separate user-state flow:

```text
Channel Details
      ↓
Favorite ViewModel
      ↓
Favorite Use Cases
      ↓
Favorite Repository
      ↓
Room
```

---

## Dependencies

Main libraries include:

- Jetpack Compose
- Android TV Material
- Navigation Compose
- AndroidX Media3
- Media3 ExoPlayer
- Media3 HLS
- Retrofit
- OkHttp
- Kotlin Serialization
- Room
- AndroidX Lifecycle

Dependency versions are maintained in:

```text
gradle/libs.versions.toml
```

---

## Building the Project

Open the project in Android Studio and allow Gradle to synchronize.

Then build the debug application:

```bash
./gradlew assembleDebug
```

On Windows:

```bat
gradlew.bat assembleDebug
```

The generated debug APK will normally be located under:

```text
app/build/outputs/apk/debug/
```

---

## Running

### Android TV

Run the application on:

- Android TV emulator
- Android TV device

Use the remote/D-pad to test focus and navigation.

### Mobile

The application can also be run on a mobile emulator/device for development and player testing.

Mobile testing is useful for validating:

- Playback
- Player controls
- Volume
- Brightness
- Picture-in-Picture
- Navigation

---

## Git Workflow

The project is developed incrementally.

Each completed milestone should be committed as a stable checkpoint.

Recommended commit style:

```text
feat: add favorites persistence foundation
feat: add favorite button to channel details
feat: add favorites screen
feat: add continue watching
feat: add watch history
```

Before starting a new milestone:

1. Build the project.
2. Run the application.
3. Verify existing functionality.
4. Commit the working state.
5. Start the next milestone.

This keeps the project easy to roll back if a new feature introduces a regression.

---

## Development Principles

The project follows these rules:

- Work incrementally.
- Preserve existing functionality.
- Avoid unnecessary redesign.
- Do not rename existing classes or packages without a strong reason.
- Do not increase the minimum SDK without an explicit decision.
- Keep player and navigation behavior stable.
- Prefer clean architecture boundaries.
- Keep UI independent from networking and persistence implementation details.
- Add logging for important navigation and playback operations.
- Complete and verify one milestone before starting the next.

---

## Roadmap

### Phase 1 — Foundation

- Project setup
- Android TV configuration
- Compose foundation

### Phase 2 — TV Design System & Navigation

- TV UI components
- Focus behavior
- Navigation structure

### Phase 3 — Channel Domain & Data Layer

- Channel models
- Repository
- Data sources
- Use cases

### Phase 4 — Free / Legal Stream Provider

- Remote provider
- Stream normalization
- HLS support

### Phase 5 — Home & Live Channel Browsing

- Home screen
- Categories
- Channel browsing
- Channel details

### Phase 6 — Media3 Player

- ExoPlayer
- HLS playback
- Player controls
- Channel switching
- Picture-in-Picture

### Phase 7 — Favorites, History & Continue Watching

- [x] Favorites Room persistence foundation
- [ ] Favorite button
- [ ] Favorites screen
- [ ] Watch history
- [ ] Continue Watching
- [ ] Last watched channel persistence

### Phase 8 — Search & Filters

- Search
- Category filtering
- Channel filtering

### Phase 9 — EPG / Program Guide

- Program data
- Current program
- Upcoming programs
- TV guide UI

### Phase 10 — Networking, Caching & Offline Resilience

- Better caching
- Retry behavior
- Offline handling
- Provider status

### Phase 11 — Settings & User Preferences

- Playback preferences
- Appearance
- Player preferences
- App settings

### Phase 12 — Testing

- Unit tests
- ViewModel tests
- Repository tests
- UI tests
- Playback testing

### Phase 13 — Performance & Stability

- Memory optimization
- Startup optimization
- Player stability
- Compose performance

### Phase 14 — Security & Production Hardening

- Network security
- Release configuration
- Error handling
- Production checks

### Phase 15 — CI/CD & GitHub

- GitHub Actions
- Automated builds
- Tests
- Release workflow

### Phase 16 — Portfolio Release

- Final UI polish
- Documentation
- Screenshots
- Demo video
- Release build

---

## License

This project is currently intended as a personal Android TV portfolio project.

Before distributing the application publicly, verify that all stream sources, logos, images, and other third-party content are legally permitted for the intended use.
