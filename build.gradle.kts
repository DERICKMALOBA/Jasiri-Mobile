plugins {
    // Bumped from 8.6.1/2.0.21 — this machine's SDK/JDK (Android Studio-managed, JBR 25 /
    // system JDK 26) only works with tooling from roughly this ecosystem generation;
    // the original pinned versions can't run on the available JDKs. See dev-environment memory.
    // AGP 9.x removes the classic Kotlin Gradle plugin's extension APIs (breaks kotlin("android")
    // and KSP, see dev-environment memory) — staying on the latest 8.x line avoids that churn.
    id("com.android.application") version "8.13.2" apply false
    // Kotlin 2.0.21's bundled JavaVersion parser can't parse this machine's JDK version strings
    // (JBR 25.0.4 / JDK 26.0.1 both have non-standard build suffixes) — 2.2.21 fixes that.
    id("org.jetbrains.kotlin.android") version "2.2.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.21" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.21" apply false
    id("com.google.dagger.hilt.android") version "2.56.2" apply false
    id("com.google.devtools.ksp") version "2.2.21-2.0.5" apply false
}
