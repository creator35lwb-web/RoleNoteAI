# RoleNote AI - ProGuard Rules
# CTO: RNA (Claude Code Opus 4.5)

# Keep Kotlin metadata
-keepattributes *Annotation*
-keep class kotlin.Metadata { *; }

# SQLCipher
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ComponentSupplier { *; }

# ONNX Runtime
-keep class ai.onnxruntime.** { *; }

# Compose
-keep class androidx.compose.** { *; }

# Keep data classes for JSON serialization
-keepclassmembers class com.rolenoteai.app.data.local.entity.** {
    <fields>;
    <init>(...);
}

-keepclassmembers class com.rolenoteai.app.domain.model.** {
    <fields>;
    <init>(...);
}
