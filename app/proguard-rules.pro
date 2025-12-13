# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep Firebase classes
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Keep Cloudinary classes
-keep class com.cloudinary.** { *; }

# Keep data models
-keep class com.example.twinzy_app.data.model.** { *; }

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel { *; }

# Keep Compose classes
-keep class androidx.compose.** { *; }

# Keep Kotlin coroutines
-keep class kotlinx.coroutines.** { *; }

# Keep Room database classes
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }

# Security: Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Keep BuildConfig for security configuration
-keep class com.example.twinzy_app.BuildConfig { *; }

# Obfuscate sensitive classes but keep public API
-keep public class com.example.twinzy_app.config.SecurityConfig {
    public *;
}

-keep public class com.example.twinzy_app.utils.InputValidator {
    public *;
}

-keep public class com.example.twinzy_app.utils.ErrorHandler {
    public *;
}

# Remove debug information
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable