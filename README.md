# Jasiri Field Officer App

Native Android (Kotlin, Jetpack Compose) app for Jasiri/ZiraHR field loan
officers. Calls the Spring Boot backend in the main `jasiri` repository
(`D:\projects\jasiri\springboot-backend`) — this is a client, not a separate
service.

Full requirements and rationale live in [`docs/`](./docs) — read
[`docs/REQUIREMENTS.md`](./docs/REQUIREMENTS.md) first.

## Getting started

1. Open this folder in **Android Studio** (Ladybug or newer).
2. Android Studio will detect there's no Gradle wrapper jar committed (see
   "Why no `gradlew`" below) and offer to generate one — accept that, or run
   `gradle wrapper --gradle-version 8.9` yourself if you have Gradle
   installed locally.
3. Let Gradle sync. It needs the Android SDK (API 34) and JDK 17+; Android
   Studio manages both for you if you don't already have them.
4. Run the `springboot-backend` locally (default port 8080). The debug build
   already points at `http://10.0.2.2:8080/` — the Android emulator's
   loopback address to your host machine — so no config is needed to test
   against a local backend from the emulator.
5. Run the `app` configuration on an emulator or device.

### Why no `gradlew`?

This project was scaffolded without an Android SDK or Gradle installed in
that environment, so the wrapper's binary `gradle-wrapper.jar` couldn't be
generated there. `gradle-wrapper.properties` (pointing at Gradle 8.9) is
already in place — Android Studio will offer to add the jar and scripts the
first time you open the project. This is a one-time step.

## Architecture

Single-module app, package-separated by layer (see the plan's §9 for the
full target structure):

```
app/src/main/java/com/jasiri/fieldofficer/
├── core/
│   ├── designsystem/   Compose theme — "Copper Harvest" palette, Material 3
│   ├── network/        Retrofit/OkHttp, auth interceptor, API interfaces, DTOs
│   ├── auth/           Token storage (EncryptedSharedPreferences), PIN gate, AuthRepository
│   ├── database/       Room — offline cache + outbox entities/DAOs
│   └── sync/           WorkManager outbox-draining worker + scheduler
├── feature/
│   ├── auth/            Login screen + ViewModel
│   ├── dashboard/        Home dashboard screen + ViewModel + repository
│   └── common/           Shared placeholder UI (ComingSoonScreen)
└── navigation/           NavHost + bottom nav wiring
```

Stack: Kotlin 2.0, Jetpack Compose + Material 3, Hilt, Retrofit +
kotlinx.serialization, Room, WorkManager, EncryptedSharedPreferences. Full
stack rationale (CameraX, Biometric, SQLCipher, FCM, etc.) is in the plan
doc §9.

## Build status

**Implemented (this scaffold):**
- Project skeleton: Gradle config, manifest, theme, DI wiring, navigation shell
- Login screen — calls `POST /api/auth/login`, stores tokens securely
- Home dashboard screen — today's goal card, 3 progress rings, quick actions
  grid; falls back to clearly-labeled placeholder data since the backend
  endpoint doesn't exist yet
- Bottom nav shell for Customers / Routes / Collections / Targets, each
  currently a "Coming soon" placeholder
- Room database + outbox entity/DAOs, WorkManager sync worker (scaffolded,
  not yet dispatching real requests)

**Not yet implemented** (see the plan's phased timeline, §11, for order):
- PIN/biometric re-entry after first login (Login screen currently only
  supports full credential login every time)
- Customer list/360, onboarding + KYC capture, loan application
- Collections (STK push flow, cash logging) — screen design is final
  (`docs/Field-Officer-App-Screen-Designs.pdf` screen 7), not yet built
- Daily routes, visit outcomes, GPS tracking
- Targets dashboard detail screen
- Outbox sync worker's actual dispatch logic (currently a no-op stub)
- SQLCipher encryption of the local Room database (flagged as a TODO at
  `core/database/DatabaseModule.kt`)

## Backend changes this app depends on

These don't exist on `springboot-backend` yet — each is called out with a
`NOTE:` comment at its call site in this project, and documented in full in
the plan's §4 and §10:

- Login/refresh must return the JWT in the JSON body for mobile clients
  (the session filter already accepts the resulting bearer token — only the
  response shape needs to change)
- `FIELD_OFFICER` role + permission set
- `GET /api/officer/dashboard`
- Idempotency-key support on customer/loan/visit/repayment write endpoints
- Device registration / revoke endpoints
- Cash-collection endpoint (STK push already exists and needs no backend work)

Coordinate these with whoever owns `D:\projects\jasiri\springboot-backend` —
they're small, additive changes, not a redesign.
