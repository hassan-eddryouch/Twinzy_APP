# 🚀 Quick Start Guide - Twinzy App

## Get Running in 5 Minutes!

### Step 1: Firebase Setup (2 minutes)

1. Go to https://console.firebase.google.com/
2. Click "Add project" → Name it "Twinzy"
3. Click "Add app" → Select Android
4. Package name: `com.example.twinzy_app`
5. Download `google-services.json`
6. **Place file here**: `Twinzy_APP/app/google-services.json`

### Step 2: Enable Authentication (1 minute)

In Firebase Console:
1. Go to **Authentication** → **Sign-in method**
2. Enable **Email/Password** ✅
3. Enable **Google** ✅ (copy the Web client ID)
4. Enable **Phone** ✅

### Step 3: Setup Firestore (30 seconds)

1. Go to **Firestore Database**
2. Click **Create database**
3. Select **Test mode**
4. Choose your region

### Step 4: Cloudinary Setup (1 minute)

1. Sign up at https://cloudinary.com/users/register/free
2. Go to Dashboard
3. Copy these 3 values:
   - Cloud Name
   - API Key
   - API Secret

### Step 5: Update Code (1 minute)

**File 1**: `app/src/main/java/com/example/twinzy_app/utils/CloudinaryManager.kt`

Find this:
```kotlin
val config = mapOf(
    "cloud_name" to "YOUR_CLOUD_NAME",
    "api_key" to "YOUR_API_KEY",
    "api_secret" to "YOUR_API_SECRET"
)
```

Replace with your Cloudinary credentials.

**File 2**: `app/src/main/java/com/example/twinzy_app/TwinzyApp.kt`

Same replacement as above.

**File 3**: `app/src/main/java/com/example/twinzy_app/ui/auth/AuthScreen.kt`

Find this:
```kotlin
.requestIdToken("YOUR_WEB_CLIENT_ID")
```

Replace with your Firebase Web Client ID from Step 2.

### Step 6: Build & Run! (30 seconds)

In Android Studio:
1. Click **Sync Now** (if prompted)
2. Click **Run** button (green play icon)
3. Select your device/emulator

**That's it! Your app should now be running! 🎉**

---

## 🎯 Test the App

### Test Flow:
1. **Splash Screen** → Auto-navigates after 2.5s
2. **Onboarding** → Swipe through 3 pages
3. **Sign Up** → Create account with email
4. **Profile Creation** → Upload photo, add details
5. **Discover** → Swipe on profiles
6. **Match** → See match dialog
7. **Chat** → View matches
8. **Profile** → See your profile

### Test Credentials:
Create a test account:
- Email: `test@twinzy.com`
- Password: `Test123!`

---

## ⚠️ Troubleshooting

### "google-services.json not found"
→ Make sure file is in `app/` folder, not `app/src/`

### "Default FirebaseApp is not initialized"
→ Clean project: `Build` → `Clean Project` → `Rebuild Project`

### Google Sign-In not working
→ Double-check Web Client ID is correct

### Cloudinary upload fails
→ Verify all 3 credentials are correct (cloud_name, api_key, api_secret)

---

## 📱 What You'll See

### Splash Screen
- Animated logo rotating and pulsing
- Particle effects in background
- Smooth transition to onboarding

### Onboarding
- 3 glass cards with smooth animations
- Animated page indicators
- Bouncy transitions

### Auth Screen
- Glassmorphic login card
- Neon glow on text field focus
- 3 sign-in options with icons

### Profile Creation
- Cyber scanning effect on photo upload
- Animated gender chips
- Glowing interest selection

### Swipe Screen
- Physics-based card swipes
- Rotating cards with drag
- Pulsing action buttons
- Match dialog with particles

### All Screens
- Dark cyberpunk theme
- Neon cyan and hot pink accents
- Smooth spring animations
- Particle backgrounds

---

## 🎨 Customization

### Change Colors
Edit: `app/src/main/java/com/example/twinzy_app/ui/theme/Color.kt`

```kotlin
val NeonCyan = Color(0xFF00F0FF)  // Change to your color
val HotPink = Color(0xFFFF0099)   // Change to your color
```

### Change App Name
Edit: `app/src/main/res/values/strings.xml`

```xml
<string name="app_name">Your App Name</string>
```

### Add More Languages
1. Create folder: `app/src/main/res/values-es/` (for Spanish)
2. Copy `strings.xml` from `values/`
3. Translate all strings

---

## 📚 Next Steps

1. ✅ **Test all features** - Try every screen and interaction
2. ✅ **Add real user data** - Create multiple test accounts
3. ✅ **Customize theme** - Make it your own
4. ✅ **Add more features** - Video chat, stories, etc.
5. ✅ **Deploy to Play Store** - Follow Android publishing guide

---

## 🆘 Need Help?

- Check `CONFIGURATION.md` for detailed setup
- Check `README.md` for feature documentation
- Check `IMPLEMENTATION_SUMMARY.md` for architecture details

---

**Enjoy building with Twinzy! 🚀💜**
