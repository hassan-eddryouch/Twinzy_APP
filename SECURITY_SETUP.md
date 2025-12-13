# 🔒 Twinzy Security Setup Guide

## Critical Security Configurations

### 1. Cloudinary Credentials Setup

**NEVER commit credentials to version control!**

#### For Development:
1. Create `local.properties` file in project root (already gitignored)
2. Add your Cloudinary credentials:
```properties
CLOUDINARY_CLOUD_NAME=your_cloud_name_here
CLOUDINARY_API_KEY=your_api_key_here
CLOUDINARY_API_SECRET=your_api_secret_here
```

#### For Production:
1. Set environment variables in your CI/CD pipeline:
   - `CLOUDINARY_CLOUD_NAME`
   - `CLOUDINARY_API_KEY`
   - `CLOUDINARY_API_SECRET`

2. Or use Gradle properties in `~/.gradle/gradle.properties`:
```properties
CLOUDINARY_CLOUD_NAME=your_cloud_name_here
CLOUDINARY_API_KEY=your_api_key_here
CLOUDINARY_API_SECRET=your_api_secret_here
```

### 2. Firebase Security Rules

Update your Firestore security rules:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can only read/write their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Matches are only accessible to involved users
    match /matches/{matchId} {
      allow read, write: if request.auth != null && 
        (request.auth.uid == resource.data.user1Id || 
         request.auth.uid == resource.data.user2Id);
    }
    
    // Swipes are private to each user
    match /swipes/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Messages are only accessible to match participants
    match /messages/{messageId} {
      allow read, write: if request.auth != null && 
        request.auth.uid in resource.data.participants;
    }
  }
}
```

### 3. Input Validation

All user inputs are now validated and sanitized:
- ✅ Email format validation
- ✅ Password strength requirements
- ✅ Name sanitization (removes special characters)
- ✅ Bio length and content validation
- ✅ Phone number format validation
- ✅ NoSQL injection prevention

### 4. Error Handling

Comprehensive error handling implemented:
- ✅ Network errors
- ✅ Authentication errors
- ✅ Validation errors
- ✅ Permission errors
- ✅ Server errors
- ✅ Safe error messages (no internal details exposed)

### 5. Network Security

- ✅ HTTPS-only communication
- ✅ Certificate pinning for Firebase/Cloudinary
- ✅ Cleartext traffic blocked
- ✅ Network security configuration

### 6. Data Protection

- ✅ No sensitive data in logs (removed in release builds)
- ✅ Secure image upload with validation
- ✅ User authorization checks
- ✅ Input sanitization
- ✅ SQL/NoSQL injection prevention

## Security Checklist

### Before Production Deployment:

- [ ] Remove all hardcoded credentials
- [ ] Set up proper environment variables
- [ ] Configure Firebase security rules
- [ ] Enable ProGuard/R8 obfuscation
- [ ] Test with release build configuration
- [ ] Verify network security configuration
- [ ] Review and update permissions
- [ ] Test input validation thoroughly
- [ ] Verify error handling doesn't leak sensitive info
- [ ] Enable Firebase App Check
- [ ] Set up proper backup rules
- [ ] Configure proper signing keys

### Monitoring & Maintenance:

- [ ] Set up Firebase Crashlytics
- [ ] Monitor authentication failures
- [ ] Regular security audits
- [ ] Keep dependencies updated
- [ ] Monitor for suspicious activity
- [ ] Regular penetration testing

## Common Security Mistakes to Avoid:

1. **Never** hardcode API keys or secrets
2. **Never** trust client-side validation alone
3. **Never** expose internal error details to users
4. **Never** allow unrestricted file uploads
5. **Never** skip input sanitization
6. **Never** use HTTP for sensitive data
7. **Never** store sensitive data in SharedPreferences
8. **Never** ignore certificate validation errors

## Emergency Response:

If credentials are compromised:
1. Immediately rotate all API keys
2. Update Firebase security rules
3. Force user re-authentication
4. Review access logs
5. Update app with new credentials
6. Monitor for suspicious activity

## Contact Security Team:

For security concerns or questions, contact: security@twinzy.app