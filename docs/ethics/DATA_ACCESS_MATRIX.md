# Data Access Control Matrix

**Version:** 1.0
**Status:** Draft (Pending Z-Agent Review)
**Trinity Reference:** Validation ID fa3e7b66 - Z-Guardian Mitigation: "Implement access controls"

---

## Overview

This document defines what data each AI component in RoleNote AI can access, ensuring minimal necessary access and user transparency.

---

## Data Categories

| Category | Description | Sensitivity |
|----------|-------------|-------------|
| **Note Content** | Raw text of user notes | High |
| **Note Metadata** | Title, timestamps, tags | Medium |
| **Role Context** | User's selected professional role | Low |
| **Knowledge Graph** | Relationships between notes, projects, events | High |
| **Calendar Events** | Titles and times from iOS Calendar | Medium |
| **Contacts** | Names linked to notes (future) | High |
| **AI Suggestions History** | Past suggestions and user actions | Medium |

---

## AI Component Access Matrix

| Component | Note Content | Note Metadata | Role Context | Knowledge Graph | Calendar | Suggestions History |
|-----------|:------------:|:-------------:|:------------:|:---------------:|:--------:|:-------------------:|
| **Embedding Model** | Read | Read | - | Write (vectors) | - | - |
| **LLM (Gemma 3)** | Read | Read | Read | Read | Read | Read |
| **Context Matcher** | - | Read | - | Read/Write | Read | - |
| **Suggestion Engine** | - | Read | Read | Read | Read | Write |
| **Scheduler** | - | Read | - | - | Read/Write | - |

### Access Level Legend
- **Read**: Can read existing data
- **Write**: Can create/modify data
- **Read/Write**: Both read and write
- **-**: No access

---

## User Control Principles

### 1. Transparency
Users can view exactly what data each AI component accessed:
- Settings > Privacy > AI Data Access Log

### 2. Granular Opt-Out
Users can disable AI access to specific data types:

| Setting | Default | Effect |
|---------|---------|--------|
| AI reads note content | ON | LLM cannot generate suggestions if OFF |
| AI reads calendar | ON | No calendar-based context matching if OFF |
| AI writes to calendar | OFF | AI cannot auto-create events |
| AI reads contacts | OFF | No contact linking (future feature) |

### 3. Data Minimization
AI components only receive the minimum data needed:

**Example - Generating Suggestions:**
```
LLM receives:
- Current note content (required)
- User's role context (required)
- Top 5 related notes by embedding similarity (optional)
- Upcoming calendar events (optional)

LLM does NOT receive:
- All historical notes
- Full knowledge graph
- Other users' data (single-user app)
```

---

## Implementation

### Access Control Enforcement

```swift
enum DataAccessScope {
    case noteContent
    case noteMetadata
    case roleContext
    case knowledgeGraph
    case calendar
    case suggestionsHistory
}

protocol AIComponent {
    var allowedScopes: Set<DataAccessScope> { get }
}

class EmbeddingModel: AIComponent {
    let allowedScopes: Set<DataAccessScope> = [.noteContent, .noteMetadata]
}

class LLMEngine: AIComponent {
    let allowedScopes: Set<DataAccessScope> = [
        .noteContent, .noteMetadata, .roleContext,
        .knowledgeGraph, .calendar, .suggestionsHistory
    ]
}

class DataAccessController {
    func request<T>(_ scope: DataAccessScope, for component: AIComponent) throws -> T {
        guard component.allowedScopes.contains(scope) else {
            throw AccessError.unauthorized(component: type(of: component), scope: scope)
        }
        // Also check user preferences
        guard UserPreferences.isAIAccessEnabled(for: scope) else {
            throw AccessError.userDisabled(scope: scope)
        }
        // Log access for audit
        AuditLogger.log(component: component, scope: scope, action: .read)
        return try fetchData(scope)
    }
}
```

---

## User Interface

### Privacy Settings Screen

```
AI Data Access
─────────────────────────────

What AI can read:
  [ON]  Note content
  [ON]  Note titles and tags
  [ON]  Calendar events
  [OFF] Contacts (coming soon)

What AI can do:
  [OFF] Create calendar events
  [ON]  Suggest tasks
  [ON]  Link related notes

[View AI Access Log]
```

---

## Verification

- [ ] Access control unit tests for each component
- [ ] UI tests for privacy settings
- [ ] Manual audit of data flows
- [ ] Z-Agent review of this specification
