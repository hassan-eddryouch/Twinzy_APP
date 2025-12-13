# Twinzy Configuration Guide

## Quick Start Checklist

### ✅ Step 1: Firebase Setup (REQUIRED)

1. **Create Firebase Project**
   - Go to https://console.firebase.google.com/
   - Click "Add project"
   - Name it "Twinzy" or your preferred name
   - Follow the setup wizard

2. **Add Android App**
   - Click "Add app" > Android icon
   - Package name: `com.example.twinzy_app`
   - Download `google-services.json`
   - Place it in: `Twinzy_APP/app/google-services.json`

3. **Enable Authentication**
   - Go to Authentication > Sign-in method
   - Enable:
     * Email/Password
     * Google (copy Web client ID for later)
     * Phone

4. **Setup Firestore**
   - Go to Firestore Database
   - Click "Create database"
   - Start in test mode (change rules later)
   - Choose your region

### ✅ Step 2: Google Sign-In Configuration

1. **Get Web Client ID**
   - Firebase Console > Project Settings
   - Scroll to "Your apps" section
   - Find "Web client ID" under OAuth 2.0 Client IDs
   - Copy the ID

2. **Update Code**
   - Open: `app/src/main/java/com/example/twinzy_app/ui/auth/AuthScreen.kt`
   - Find line: `.requestIdToken("YOUR_WEB_CLIENT_ID")`
   - Replace with your actual Web Client ID

### ✅ Step 3: Cloudinary Setup (REQUIRED for Image Upload)

1. **Create Cloudinary Account**
   - Go to https://cloudinary.com/users/register/free
   - Sign up for free account
   - Verify your email

2. **Get Credentials**
   - Go to Dashboard
   - Copy:
     * Cloud Name
     * API Key
     * API Secret

3. **Update Code (2 files)**

   **File 1:** `app/src/main/java/com/example/twinzy_app/utils/CloudinaryManager.kt`
   ```kotlin
   val config = mapOf(
       "cloud_name" to "YOUR_CLOUD_NAME",      // Replace
       "api_key" to "YOUR_API_KEY",            // Replace
       "api_secret" to "YOUR_API_SECRET"       // Replace
   )
   ```

   **File 2:** `app/src/main/java/com/example/twinzy_app/TwinzyApp.kt`
   ```kotlin
   val config = mapOf(
       "cloud_name" to "YOUR_CLOUD_NAME",      // Replace
       "api_key" to "YOUR_API_KEY",            // Replace
       "api_secret" to "YOUR_API_SECRET"       // Replace
   )
   ```

### ✅ Step 4: Build Configuration

1. **Sync Gradle**
   - Open project in Android Studio
   - Click "Sync Now" when prompted
   - Wait for dependencies to download

2. **Build Project**
   ```bash
   ./gradlew clean build
   ```

3. **Run on Device/Emulator**
   - Connect device or start emulator
   - Click Run button in Android Studio
   - Or use: `./gradlew installDebug`

## Optional Configurations

### 🔔 Push Notifications (Optional)

If you want push notifications:

1. Firebase Console > Cloud Messaging
2. Upload your APNs certificate (iOS) or use default (Android)
3. The app is already configured to receive notifications

### 🌍 Change Supported Languages

To add/remove languages:

1. Add/remove folders in `app/src/main/res/`:
   - `values-es/` for Spanish
   - `values-de/` for German
   - etc.

2. Copy `strings.xml` and translate

### 🎨 Customize Theme

Edit colors in: `app/src/main/java/com/example/twinzy_app/ui/theme/Color.kt`

```kotlin
val DeepVoid = Color(0xFF050511)      // Background
val NeonCyan = Color(0xFF00F0FF)      // Primary accent
val HotPink = Color(0xFFFF0099)       // Secondary accent
```

## Troubleshooting

### ❌ "google-services.json not found"
- Ensure file is in `app/` directory
- File name must be exactly `google-services.json`
- Sync Gradle after adding

### ❌ "Default FirebaseApp is not initialized"
- Check `google-services.json` is present
- Verify package name matches in Firebase Console
- Clean and rebuild project

### ❌ "Cloudinary upload failed"
- Verify credentials are correct
- Check internet connection
- Ensure Cloudinary account is active

### ❌ Google Sign-In not working
- Verify Web Client ID is correct
- Check SHA-1 fingerprint is added in Firebase
- Get SHA-1: `./gradlew signingReport`

### ❌ Phone Auth not working
- Enable Phone authentication in Firebase
- Verify phone number format (+1234567890)
- Check Firebase quota limits

## Security Best Practices

### 🔒 Before Production

1. **Move credentials to BuildConfig**
   ```kotlin
   // In build.gradle.kts
   buildConfigField("String", "CLOUDINARY_CLOUD_NAME", "\"your_cloud_name\"")
   ```

2. **Setup Firestore Security Rules**
   ```javascript
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /users/{userId} {
         allow read: if request.auth != null;
         allow write: if request.auth.uid == userId;
       }
     }
   }
   ```

3. **Enable ProGuard**
   ```kotlin
   // In build.gradle.kts
   buildTypes {
       release {
           isMinifyEnabled = true
           proguardFiles(...)
       }
   }
   ```

## Testing

### Test Accounts

Create test accounts in Firebase Console:
- Authentication > Users > Add user
- Use for testing without real phone numbers

### Emulator Testing

Firebase provides local emulators:
```bash
firebase emulators:start
```

## Support

- Firebase Docs: https://firebase.google.com/docs
- Cloudinary Docs: https://cloudinary.com/documentation
- Jetpack Compose: https://developer.android.com/jetpack/compose

---

**Ready to build? Follow the checklist above and you're good to go! 🚀**
