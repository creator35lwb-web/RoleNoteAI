# Audit Logging Design

**Version:** 1.0
**Status:** Draft (Pending Z-Agent Review)
**Trinity Reference:** Validation ID fa3e7b66 - Z-Guardian Mitigation: "Add audit logging"

---

## Overview

RoleNote AI logs all AI-driven actions to ensure accountability and user awareness. Users can review what the AI suggested, what they accepted, and what data was accessed.

---

## What Gets Logged

### 1. AI Suggestions

| Event | Data Logged |
|-------|-------------|
| Suggestion generated | Timestamp, suggestion type, source note ID, suggestion content |
| Suggestion accepted | Timestamp, suggestion ID, resulting action |
| Suggestion dismissed | Timestamp, suggestion ID |
| Suggestion auto-expired | Timestamp, suggestion ID |

### 2. AI Data Access

| Event | Data Logged |
|-------|-------------|
| Component accessed data | Timestamp, component name, data scope, note IDs accessed |
| Access denied (user pref) | Timestamp, component name, data scope |

### 3. AI Actions (with user confirmation)

| Event | Data Logged |
|-------|-------------|
| Calendar event created | Timestamp, event title, linked note ID |
| Task created | Timestamp, task title, linked note ID |
| Notes linked | Timestamp, source note ID, target note ID, confidence score |

---

## Log Schema

```swift
struct AuditLogEntry: Codable {
    let id: UUID
    let timestamp: Date
    let eventType: AuditEventType
    let componentName: String
    let details: [String: String]

    // Not logged: actual note content, only IDs
}

enum AuditEventType: String, Codable {
    case suggestionGenerated
    case suggestionAccepted
    case suggestionDismissed
    case dataAccessed
    case accessDenied
    case calendarEventCreated
    case taskCreated
    case notesLinked
}
```

### Example Log Entries

```json
{
  "id": "a1b2c3d4-...",
  "timestamp": "2026-01-31T10:30:00Z",
  "eventType": "suggestionGenerated",
  "componentName": "LLMEngine",
  "details": {
    "suggestionType": "nextStep",
    "sourceNoteId": "note-123",
    "suggestionPreview": "Schedule follow-up meeting..."
  }
}
```

```json
{
  "id": "e5f6g7h8-...",
  "timestamp": "2026-01-31T10:30:05Z",
  "eventType": "suggestionAccepted",
  "componentName": "LLMEngine",
  "details": {
    "suggestionId": "a1b2c3d4-...",
    "action": "taskCreated",
    "taskId": "task-456"
  }
}
```

---

## Storage

| Property | Value |
|----------|-------|
| Storage location | Local SQLite (encrypted with app DB) |
| Retention period | 30 days (configurable: 7, 30, 90 days, forever) |
| Max log size | 10,000 entries (oldest auto-deleted) |
| Export format | JSON |

---

## User Interface

### Audit Log Viewer

```
AI Activity Log
─────────────────────────────

Today
  10:30 AM  Suggestion: "Schedule follow-up meeting"
            Source: Meeting notes (Jan 30)
            [Accepted] → Created task

  10:15 AM  Linked notes
            "Q1 Planning" ↔ "Budget Review"
            Confidence: 87%

  09:45 AM  Data accessed
            LLM read: 3 notes, calendar events

Yesterday
  ...

[Export Log]  [Clear Log]

Settings:
  Log retention: [30 days ▼]
  Log AI data access: [ON]
```

### Suggestion Dismissal with Reason (Optional)

When user dismisses a suggestion:
```
Why are you dismissing this?
  ○ Not relevant
  ○ Already done
  ○ Will do later
  ○ Don't ask again for similar
  ○ No reason
```

This feedback improves future suggestions and is logged.

---

## Implementation

```swift
class AuditLogger {
    private static let maxEntries = 10_000
    private static let db = DatabaseManager.shared

    static func log(
        eventType: AuditEventType,
        component: String,
        details: [String: String]
    ) {
        let entry = AuditLogEntry(
            id: UUID(),
            timestamp: Date(),
            eventType: eventType,
            componentName: component,
            details: details
        )

        // Async write to avoid blocking UI
        Task {
            try await db.insertAuditEntry(entry)
            try await pruneOldEntries()
        }
    }

    static func export() async throws -> Data {
        let entries = try await db.getAllAuditEntries()
        return try JSONEncoder().encode(entries)
    }

    private static func pruneOldEntries() async throws {
        let retentionDays = UserPreferences.auditRetentionDays
        let cutoff = Date().addingTimeInterval(-Double(retentionDays * 24 * 60 * 60))
        try await db.deleteAuditEntriesBefore(cutoff)

        // Also enforce max entries
        let count = try await db.auditEntryCount()
        if count > maxEntries {
            try await db.deleteOldestAuditEntries(count: count - maxEntries)
        }
    }
}
```

---

## Privacy Considerations

### What is NOT Logged

- Full note content (only note IDs)
- Exact calendar event details (only titles)
- User's raw voice input
- Biometric data

### User Rights

| Right | Implementation |
|-------|----------------|
| View logs | Audit Log Viewer in app |
| Export logs | JSON export button |
| Delete logs | Clear Log button (immediate, permanent) |
| Disable logging | Settings toggle (not recommended) |

---

## Verification

- [ ] Log viewer UI implemented and tested
- [ ] Export function verified
- [ ] Retention policy enforced
- [ ] No PII in log content (only IDs)
- [ ] Z-Agent review of this specification
