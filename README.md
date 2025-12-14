# Twinzy - Futuristic Cyberpunk Dating App

A production-ready dating application built with Jetpack Compose featuring a stunning cyberpunk aesthetic, smooth animations, and robust backend integration.

## 🎨 Design Features

- **Dark Mode Only** with Deep Void background (#050511)
- **Neon Accents**: Cyan (#00F0FF) & Hot Pink (#FF0099)
- **Glassmorphism** UI with blurred transparent surfaces
- **Extreme Smoothness**: Spring physics animations throughout
- **Particle Effects** and glowing orbs for immersive experience

## 🏗️ Architecture

- **MVVM + Clean Architecture** (Data, Domain, UI layers)
- **Dependency Injection**: Hilt
- **Reactive Programming**: Kotlin Coroutines & Flow
- **Navigation**: Jetpack Navigation Compose

## 🔧 Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Authentication**: Firebase Auth (Email, Google, Phone/OTP)
- **Database**: Firebase Firestore
- **Image Upload**: Cloudinary Android SDK
- **Image Loading**: Coil
- **Localization**: English, French, Arabic

## 📱 Features Implemented

### Phase A: Onboarding
- ✅ Animated splash screen with pulsing logo
- ✅ 3-page onboarding with glass cards
- ✅ Smooth page indicators

### Phase B: Authentication
- ✅ Email/Password authentication
- ✅ Google One-Tap Sign-In
- ✅ Phone/OTP verification
- ✅ Unified glass login sheet

### Phase C: Profile Creation
- ✅ Animated floating label text fields
- ✅ Cloudinary image upload with cyber scanning effect
- ✅ Gender selection with animated chips
- ✅ Interest selection with glow effects

### Phase D: Core Discovery
- ✅ Physics-based swipe gestures
- ✅ Tinder-style card stack
- ✅ Animated action buttons (Like, Dislike, Super Like)
- ✅ Match dialog with particle effects

### Phase E: Social Features
- ✅ Chat list with staggered entrance animations
- ✅ Profile screen with parallax scrolling
- ✅ Bottom navigation with animated tabs

## 🎯 Key Components

### UI Components
- `TwinzyTextField`: Glassmorphic text field with neon glow on focus
- `TwinzyButton`: Gradient button with spring animations
- `ParticleBackground`: Animated particle system
- `GlowingOrb`: Rotating orb with pulsing effect
- `SwipeCard`: Physics-based swipeable card
- `CyberScanningEffect`: Radar scan animation for image upload

### Screens
- `SplashScreen`: Animated logo with rotation and scale
- `OnboardingScreen`: Pager with glass cards
- `AuthScreen`: Unified authentication with 3 methods
- `ProfileCreationScreen`: Multi-step profile wizard
- `DiscoverScreen`: Swipe deck with match detection
- `ChatListScreen`: Message list with animations
- `ProfileScreen`: User profile with parallax header

### ViewModels
- `AuthViewModel`: Handles authentication state
- `ProfileViewModel`: Manages profile creation and image upload
- `DiscoverViewModel`: Controls swipe logic and profile loading

## 🌍 Localization

The app supports 3 languages:
- English (en) - Default
- French (fr)
- Arabic (ar)

## 🎨 Theme System

### Colors
- **DeepVoid**: #050511 (Background)
- **NeonCyan**: #00F0FF (Primary accent)
- **HotPink**: #FF0099 (Secondary accent)
- **GlassSurface**: #1A1A2E (Glass surfaces)

### Animations
All animations use Spring physics with:
- `dampingRatio = Spring.DampingRatioMediumBouncy`
- `stiffness = Spring.StiffnessLow`


## 🚀 Performance Optimizations

- Lazy loading of images with Coil
- Efficient Compose recomposition
- Cloudinary auto-optimization (quality: auto:good)
- Firebase offline persistence
- Coroutine-based async operations

## 🔐 Security Features

- Firebase Authentication with secure tokens
- Firestore security rules (configure in Firebase Console)
- No hardcoded credentials (use BuildConfig or local.properties)
- HTTPS-only image URLs from Cloudinary

## 📄 License

This project is for educational purposes. Customize as needed for production use.

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

## 📧 Support

For issues and questions, please open an issue on GitHub.

---

**Built with ❤️ using Jetpack Compose**
