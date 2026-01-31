# Input Validation Specification

**Version:** 1.0
**Status:** Draft (Pending CS-Agent Review)
**Trinity Reference:** Validation ID fa3e7b66 - Vulnerability: "Input validation needed"

---

## Overview

RoleNote AI accepts multi-modal input (text, voice, images). All input must be sanitized before processing by the AI Core to prevent injection attacks and ensure data integrity.

---

## Input Types and Validation Rules

### 1. Text Input

| Field | Max Length | Allowed Characters | Validation |
|-------|------------|-------------------|------------|
| Note content | 50,000 chars | Unicode (sanitized) | Strip control chars, normalize whitespace |
| Note title | 200 chars | Unicode (sanitized) | Strip HTML, control chars |
| Project name | 100 chars | Alphanumeric + spaces | Reject special chars |
| Tags | 50 chars each | Alphanumeric + hyphen | Lowercase, trim |

**Sanitization Steps:**
1. Remove null bytes and control characters (except newline, tab)
2. Normalize Unicode (NFC normalization)
3. Strip HTML tags and entities
4. Limit consecutive whitespace
5. Truncate to max length

### 2. Voice Input (Deferred to v1.1)

Speech-to-text output treated as text input, same validation rules apply.

### 3. Image Input (Deferred to v1.1)

OCR output treated as text input, same validation rules apply.

---

## LLM Prompt Injection Defense

The on-device LLM (Gemma 3) is vulnerable to prompt injection via user notes.

### Mitigation Strategy

1. **Input Escaping:** Wrap user content in clear delimiters
   ```
   <user_note>
   {sanitized_user_input}
   </user_note>
   ```

2. **System Prompt Hardening:** Instruct LLM to treat content within `<user_note>` tags as data, not instructions

3. **Output Validation:** Verify LLM responses match expected format (JSON schema validation)

4. **Rate Limiting:** Limit AI suggestion generation to prevent abuse

### Blocked Patterns

Reject inputs containing obvious injection attempts:
- `ignore previous instructions`
- `you are now`
- `<system>` or `</system>` tags
- Excessive repetition of special characters

---

## Implementation

### Swift Example

```swift
struct InputValidator {
    static let maxNoteLength = 50_000
    static let maxTitleLength = 200

    static func sanitizeNote(_ input: String) -> String {
        var sanitized = input

        // Remove control characters
        sanitized = sanitized.filter { !$0.isASCII || !$0.asciiValue!.isControlCharacter || $0 == "\n" || $0 == "\t" }

        // Normalize unicode
        sanitized = sanitized.precomposedStringWithCanonicalMapping

        // Truncate
        if sanitized.count > maxNoteLength {
            sanitized = String(sanitized.prefix(maxNoteLength))
        }

        return sanitized
    }

    static func detectInjection(_ input: String) -> Bool {
        let patterns = [
            "ignore previous instructions",
            "you are now",
            "<system>",
            "</system>"
        ]
        let lowercased = input.lowercased()
        return patterns.contains { lowercased.contains($0) }
    }
}
```

---

## Verification

- [ ] Unit tests for all sanitization functions
- [ ] Fuzz testing with malformed inputs
- [ ] Prompt injection test suite against LLM
- [ ] CS-Agent review of this specification
