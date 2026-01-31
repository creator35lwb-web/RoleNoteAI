# Authentication Specification

**Version:** 1.0
**Status:** Draft (Pending CS-Agent Review)
**Trinity Reference:** Validation ID fa3e7b66 - Vulnerability: "Authentication gaps"

---

## Overview

RoleNote AI stores sensitive professional notes locally. Authentication protects against unauthorized access if the device is compromised or shared.

---

## Authentication Methods

### Primary: Biometric (FaceID / TouchID)

| Platform | API | Fallback |
|----------|-----|----------|
| iOS | LocalAuthentication framework | Device passcode |

**Implementation:**
```swift
import LocalAuthentication

class AuthManager {
    private let context = LAContext()

    func authenticate() async throws -> Bool {
        var error: NSError?

        guard context.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: &error) else {
            // Fall back to device passcode
            return try await authenticateWithPasscode()
        }

        return try await context.evaluatePolicy(
            .deviceOwnerAuthenticationWithBiometrics,
            localizedReason: "Unlock RoleNote AI"
        )
    }
}
```

### Secondary: App-Specific PIN (Optional)

Users can optionally set a 6-digit PIN as an additional layer:
- Stored as salted hash in iOS Keychain
- 5 failed attempts = 30 second lockout
- 10 failed attempts = app data wipe (optional, user-configurable)

---

## Session Management

| Setting | Value | Rationale |
|---------|-------|-----------|
| Auto-lock timeout | 5 minutes (configurable) | Balance security/convenience |
| Background lock | Immediate | Prevent screenshot in app switcher |
| Re-auth for sensitive ops | Always | Export, delete all, disable encryption |

### Sensitive Operations Requiring Re-authentication

1. Export notes
2. Delete all data
3. Disable encryption
4. Change PIN
5. View audit logs

---

## Data Protection

### SQLite Encryption

Use iOS Data Protection with Keychain-stored encryption key:

```swift
// Database encryption key stored in Keychain
class DatabaseKeyManager {
    private static let keychainKey = "com.rolenoteai.dbkey"

    static func getOrCreateKey() throws -> Data {
        if let existing = try? KeychainManager.get(keychainKey) {
            return existing
        }

        // Generate new 256-bit key
        var key = Data(count: 32)
        let result = key.withUnsafeMutableBytes {
            SecRandomCopyBytes(kSecRandomDefault, 32, $0.baseAddress!)
        }
        guard result == errSecSuccess else {
            throw AuthError.keyGenerationFailed
        }

        try KeychainManager.set(key, forKey: keychainKey)
        return key
    }
}
```

### File Protection

All app files use iOS file protection:
```swift
try data.write(to: url, options: .completeFileProtection)
```

---

## Authentication Flow

```
App Launch
    │
    ▼
┌─────────────────┐
│ Check Biometric │
│   Available?    │
└────────┬────────┘
         │
    ┌────┴────┐
    │         │
   Yes        No
    │         │
    ▼         ▼
┌────────┐  ┌──────────┐
│ FaceID │  │ Passcode │
│ Prompt │  │  Prompt  │
└────┬───┘  └────┬─────┘
     │           │
     └─────┬─────┘
           │
      ┌────┴────┐
      │         │
   Success    Failure
      │         │
      ▼         ▼
┌──────────┐  ┌──────────┐
│ Unlock   │  │ Retry or │
│ App      │  │ Lockout  │
└──────────┘  └──────────┘
```

---

## Verification

- [ ] Biometric auth tested on device
- [ ] PIN lockout mechanism tested
- [ ] Keychain key storage verified
- [ ] Background screenshot protection verified
- [ ] CS-Agent review of this specification
