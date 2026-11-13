# Add project specific ProGuard rules here.
# Kotlinx serialization and Retrofit/OkHttp keep rules are generally handled by
# their bundled consumer rules; add overrides here only if release builds show
# missing-class or reflection failures.

# androidx.security.crypto -> Tink references error-prone annotations that are
# compile-time only and not present at runtime; safe for R8 to ignore.
-dontwarn com.google.errorprone.annotations.**
