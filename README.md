# StreamBox

StreamBox is an Android TV streaming application built with Kotlin and Jetpack Compose.

The project is designed as a portfolio-quality Android TV application with a clean architecture, TV-focused UI, live channel browsing, Media3-based playback, local user-state persistence, watch history, and Continue Watching support.

---

## Project Status

**Current Phase:** Phase 7 — Favorites, History & Continue Watching

**Current Milestone:** Phase 7.6 — Continue Watching

### Completed

- Android TV project foundation
- TV-focused Compose UI
- Home screen
- StreamBox top bar
- Hero section
- Channel categories
- Live channel browsing
- Channel details screen
- Navigation between Home, Channels, Details, Player, Favorites and History
- Remote/demo channel data source
- Remote channel data source using Retrofit
- Repository and use-case layers
- Media3 live playback
- HLS playback support
- Channel switching
- Custom player controls
- Play / pause
- Previous / next channel
- Volume controls
- Mute / unmute
- Brightness controls
- Android Picture-in-Picture
- Home button → Picture-in-Picture behavior
- Normal Back navigation
- Android TV D-pad support
- Mobile testing support
- Room database
- Favorites persistence
- Favorites screen
- Favorite removal and synchronization
- Watch history persistence
- Watch History screen
- Clear watch history
- Continue Watching persistence
- Playback progress saving
- Playback resume from saved position
- Continue Watching section
- Playback progress UI
- Automatic progress updates
- Continue Watching synchronization with the player
- Live channels excluded from Continue Watching

### Next

- Phase 7 integration testing
- Search and filtering
- EPG / program guide
- Settings
- Networking and caching improvements
- Testing and performance hardening
- CI/CD
- Portfolio release

---

## Features

### Home

The Home screen provides:

- StreamBox top bar
- Hero section
- Watch Now entry point
- Favorites navigation
- Watch History navigation
- Continue Watching
- Channel categories
- Live channel rows
- Loading state

---

### Live Channels

Users can:

- Browse channels
- Browse channels by category
- Open channel details
- Start playback
- Switch between channels
- Control playback volume
- Control brightness
- Enter Picture-in-Picture mode

---

### Channel Details

The Channel Details screen provides:

- Channel name
- Live/offline state
- Channel description
- Stream availability
- Watch action
- Favorite functionality
- Back navigation

---

## Media Player

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
- Playback state handling
- Buffering state
- Playback error handling

---

## Picture-in-Picture

StreamBox uses Android Picture-in-Picture while the player is active.

Current behavior:

- Pressing the Home button while watching opens PiP.
- Back does not open PiP.
- Back performs normal navigation.
- Returning from PiP keeps the player alive.
- Player resources are released when the player screen is actually disposed.
- PiP displays the video without the normal Compose player controls.

---

## Favorites

Favorites use Room for local persistence.

The persistence layer stores:

- Channel ID
- Favorite creation timestamp

Channel metadata remains owned by the existing channel repository.

Room is therefore used for user state rather than becoming a second source of truth for channel data.

### Architecture

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