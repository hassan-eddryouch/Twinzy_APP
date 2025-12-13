# ✅ Twinzy Setup Checklist

Use this checklist to ensure everything is configured correctly.

## 🔥 Firebase Configuration

### Firebase Project Setup
- [ ] Created Firebase project at console.firebase.google.com
- [ ] Added Android app with package name: `com.example.twinzy_app`
- [ ] Downloaded `google-services.json`
- [ ] Placed `google-services.json` in `app/` directory (NOT in `app/src/`)
- [ ] Synced Gradle after adding the file

### Authentication Setup
- [ ] Enabled Email/Password authentication
- [ ] Enabled Google Sign-In authentication
- [ ] Copied Web Client ID from Google Sign-In settings
- [ ] Enabled Phone authentication
- [ ] (Optional) Added test phone numbers for development

### Firestore Database
- [ ] Created Firestore database
- [ ] Selected region
- [ ] Started in test mode (or configured security rules)

### (Optional) Cloud Messaging
- [ ] Enabled Firebase Cloud Messaging
- [ ] Configured notification settings

## ☁️ Cloudinary Configuration

### Account Setup
- [ ] Created Cloudinary account at cloudinary.com
- [ ] Verified email address
- [ ] Accessed Dashboard

### Credentials
- [ ] Copied Cloud Name from Dashboard
- [ ] Copied API Key from Dashboard
- [ ] Copied API Secret from Dashboard

### Code Updates
- [ ] Updated `CloudinaryManager.kt` with credentials
- [ ] Updated `TwinzyApp.kt` with credentials
- [ ] Verified credentials are correct (no typos)

## 🔐 Google Sign-In Configuration

### Web Client ID
- [ ] Copied Web Client ID from Firebase Console
- [ ] Updated `AuthScreen.kt` with Web Client ID
- [ ] Replaced `"YOUR_WEB_CLIENT_ID"` with actual ID

### SHA-1 Fingerprint (for production)
- [ ] Generated SHA-1: `./gradlew signingReport`
- [ ] Added SHA-1 to Firebase Console (Project Settings > Your apps)

## 📱 Android Studio Setup

### Project Setup
- [ ] Opened project in Android Studio
- [ ] Synced Gradle successfully
- [ ] No build errors shown
- [ ] All dependencies downloaded

### Build Configuration
- [ ] Verified `google-services.json` is recognized
- [ ] Checked package name matches in all files
- [ ] Verified minSdk is 26 or higher

## 🧪 Testing Checklist

### Build & Run
- [ ] Project builds successfully
- [ ] App installs on device/emulator
- [ ] No runtime crashes on launch

### Screen Flow Testing
- [ ] Splash screen displays and animates
- [ ] Onboarding pages swipe correctly
- [ ] Auth screen loads properly
- [ ] Email sign-up works
- [ ] Email sign-in works
- [ ] Google Sign-In works (if configured)
- [ ] Phone auth works (if configured)
- [ ] Profile creation screen loads
- [ ] Photo upload works (Cloudinary)
- [ ] Cyber scanning animation plays
- [ ] Profile saves successfully
- [ ] Discover screen loads
- [ ] Swipe gestures work
- [ ] Match dialog appears
- [ ] Chat list displays
- [ ] Profile screen shows user data
- [ ] Bottom navigation works

### Animation Testing
- [ ] Splash logo rotates and pulses
- [ ] Particles animate in background
- [ ] Onboarding cards animate
- [ ] Text fields glow on focus
- [ ] Buttons animate on press
- [ ] Cards swipe with physics
- [ ] Match dialog animates in
- [ ] Chat items stagger in
- [ ] Profile parallax scrolls

### Localization Testing
- [ ] English strings display correctly
- [ ] French strings work (change device language)
- [ ] Arabic strings work (change device language)
- [ ] RTL layout works for Arabic

## 🎨 Customization (Optional)

### Branding
- [ ] Changed app name in `strings.xml`
- [ ] Updated app icon in `res/mipmap-*/`
- [ ] Customized color scheme in `Color.kt`

### Features
- [ ] Added custom interests
- [ ] Modified age range
- [ ] Adjusted swipe distance threshold
- [ ] Customized match algorithm

## 🚀 Pre-Production Checklist

### Security
- [ ] Moved credentials to BuildConfig or local.properties
- [ ] Configured Firestore security rules
- [ ] Enabled ProGuard for release builds
- [ ] Removed all test/debug code

### Performance
- [ ] Tested on low-end devices
- [ ] Optimized image sizes
- [ ] Reduced APK size
- [ ] Tested offline functionality

### Legal
- [ ] Added Privacy Policy
- [ ] Added Terms of Service
- [ ] Configured data retention policies
- [ ] Added age verification (18+)

## 📊 Status Summary

**Total Items**: 70+
**Required for Basic Functionality**: 25
**Optional/Advanced**: 45+

---

## 🎯 Minimum Required to Run

These are the ABSOLUTE MINIMUM items needed:

1. ✅ Firebase project created
2. ✅ `google-services.json` in `app/` folder
3. ✅ Email/Password auth enabled in Firebase
4. ✅ Firestore database created
5. ✅ Cloudinary credentials in code (2 files)
6. ✅ Project builds without errors

**With just these 6 items, the app will run!**

---

## 📝 Notes

- Use this checklist during setup
- Check off items as you complete them
- Keep this file for reference
- Update as you add features

---

**Last Updated**: [Current Date]
**App Version**: 1.0.0
**Status**: Development

---

## 🆘 If Something Doesn't Work

1. Check this checklist - did you miss something?
2. Read `QUICKSTART.md` for common issues
3. Check `CONFIGURATION.md` for detailed steps
4. Clean and rebuild project
5. Invalidate caches and restart Android Studio

---

**Good luck! You've got this! 💪**
