# RoleNote AI Execution Framework

**Inspired by:** Bullet Journal (Ryder Carroll)
**Adapted for:** Digital AI-native note planning
**Version:** 1.0

---

## Philosophy

Bullet Journal succeeds because it creates **intentional friction** that forces reflection. RoleNote AI adapts this for digital while preserving the core insight: **unprocessed thoughts become forgotten thoughts**.

> "The act of migration forces you to decide: Is this still worth doing?"
> — Ryder Carroll

---

## Core Concepts

### 1. Signifiers (Rapid Logging)

Digital adaptation of BuJo bullets for quick capture:

| Signifier | Type | Keyboard | Voice Trigger | AI Behavior |
|-----------|------|----------|---------------|-------------|
| `•` | Task | `.` or `-` | "Task:" | Extract to task list, track completion |
| `○` | Event | `o` | "Event:" | Link to calendar, set reminder |
| `—` | Note | `--` | "Note:" | Pure information, no action needed |
| `!` | Priority | `!` | "Important:" | Boost in suggestions, prominent display |
| `?` | Explore | `?` | "Research:" | Queue for deeper investigation |
| `💡` | Idea | `*` | "Idea:" | Add to idea backlog, low urgency |

**Example Input:**
```
• Follow up with Sarah about Q2 budget
○ Team standup tomorrow 9am
— Market research shows 15% growth in segment
! Deadline for proposal is Friday
? What's the competitor's pricing strategy?
💡 Could we automate the onboarding flow?
```

**AI Processing:**
- Tasks (•) → Added to task list with role context
- Events (○) → Proposed calendar entry
- Notes (—) → Indexed for context matching only
- Priority (!) → Flagged for daily review
- Explore (?) → Queued for research time
- Ideas (💡) → Added to "Someday/Maybe" collection

---

### 2. Migration (The Anti-Graveyard System)

The core BuJo innovation: **forcing decisions on stale items**.

#### Automatic Migration Prompts

| Trigger | Prompt | Options |
|---------|--------|---------|
| Task > 3 days old | "Still relevant?" | Migrate / Schedule / Cancel |
| Task > 7 days old | "This keeps moving forward..." | Do Today / Defer to Project / Cancel |
| Task > 14 days old | "Time to decide" | Schedule Now / Move to Someday / Delete |

#### Migration States

```
• Task (open)
  ↓ complete
× Task (done)

• Task (open)
  ↓ migrate forward
> Task (migrated to future date)

• Task (open)
  ↓ migrate to project
< Task (scheduled in project)

• Task (open)
  ↓ cancel
~ Task (cancelled - logged for reflection)
```

#### Migration Log

All migrations are logged for reflection:
```json
{
  "original_date": "2026-01-28",
  "migration_date": "2026-01-31",
  "migration_count": 2,
  "action": "scheduled",
  "new_date": "2026-02-03",
  "reason": "Waiting on client response"
}
```

---

### 3. Collections (Themed Groupings)

BuJo collections map to RoleNote AI's Knowledge Graph:

| BuJo Collection | RoleNote AI Equivalent |
|-----------------|------------------------|
| Future Log | Calendar + Scheduled Tasks |
| Monthly Log | Monthly Review Collection |
| Daily Log | Today View |
| Custom Collections | Projects + Tags |

#### Smart Collections (AI-Generated)

RoleNote AI automatically creates collections:

- **Stalled Items** — Tasks migrated 2+ times
- **Quick Wins** — Tasks estimated < 15 min
- **Waiting On** — Items blocked by others
- **This Week's Priorities** — Role-based priority suggestions
- **Recurring Themes** — Patterns AI detects across notes

---

### 4. Reflection (Weekly & Monthly Reviews)

BuJo's power comes from intentional review. RoleNote AI prompts structured reflection.

#### Weekly Review (Every Sunday or Monday)

AI generates a review prompt:

```
📋 Weekly Review - Jan 27 - Feb 2

COMPLETED (12)
× Finalize Q1 budget proposal
× Team standup (x5)
× Review marketing deck
...

MIGRATED (3)
> Follow up with legal (→ next week)
> Research competitor pricing (→ Feb 5)
> Update documentation (→ someday)

CANCELLED (1)
~ Old vendor call (no longer relevant)

REFLECTION PROMPTS:
1. What got migrated repeatedly? Why?
2. What should I delegate?
3. What's the one thing for next week?

[Complete Review]
```

#### Monthly Review (First of month)

```
📊 January 2026 Review

BY THE NUMBERS:
- Tasks created: 87
- Tasks completed: 62 (71%)
- Tasks cancelled: 8 (9%)
- Average migration count: 1.4

TOP PROJECTS:
1. Q1 Planning (23 tasks)
2. Product Launch (18 tasks)
3. Team Hiring (12 tasks)

PATTERNS DETECTED:
⚠️ "Follow up" tasks often stall (avg 4 days)
💡 Most productive: Tuesday mornings
📉 Friday tasks rarely complete same-day

REFLECTION:
- What worked well?
- What should change?
- What's the focus for February?
```

---

### 5. Threading (Connecting Across Time)

BuJo uses page references. RoleNote AI uses intelligent linking.

#### Automatic Threading

When you create a note, AI suggests connections:

```
New Note: "Client feedback on prototype"

🔗 Related:
- "Prototype v2 specs" (3 days ago)
- "Client meeting notes" (1 week ago)
- Project: Product Launch

Thread these? [Yes] [No] [Select specific]
```

#### Thread View

See the full history of a topic:

```
Thread: Q2 Budget Discussion

Jan 15: "Initial budget request from finance"
Jan 18: "Meeting with CFO - revised numbers"
Jan 22: "Final approval pending"
Jan 28: "• Get CFO signature" (open task)
Jan 31: "× Budget approved" (done)

[5 notes, 3 tasks, 1 decision]
```

---

## Implementation in Role Templates

Each role template gains execution capabilities:

```json
{
  "id": "project-manager",
  "execution": {
    "signifiers_enabled": true,
    "default_signifier": "task",
    "migration_prompt_days": [3, 7, 14],
    "weekly_review": true,
    "monthly_review": true,
    "auto_threading": true,
    "stale_task_threshold_days": 5
  }
}
```

---

## UI Patterns

### Quick Capture Bar

```
┌─────────────────────────────────────────────┐
│ • | ○ | — | ! | ? | 💡 |  Type or speak... │
└─────────────────────────────────────────────┘
```

### Daily View (BuJo-Inspired)

```
Today - January 31, 2026
────────────────────────

TASKS
• Follow up with Sarah        [!] [>]
× Finalize budget doc         ✓
• Review PR #234              [ ]
> Research pricing (from Jan 28)

EVENTS
○ Team standup               9:00 AM
○ Client call               2:00 PM

NOTES
— Market report shows 15% growth
— Competitor launched new feature

────────────────────────
Migration queue: 2 items from yesterday
[Review Now]
```

### Migration Prompt

```
┌─────────────────────────────────────────────┐
│  📦 Migration Time                          │
│                                             │
│  "Follow up with legal" has been open       │
│  for 5 days. What would you like to do?     │
│                                             │
│  [Do Today]  [Schedule →]  [~ Cancel]       │
│                                             │
│  ○ Add reason (helps future reflection)     │
│  ┌─────────────────────────────────────┐    │
│  │ Waiting on their response           │    │
│  └─────────────────────────────────────┘    │
└─────────────────────────────────────────────┘
```

---

## Data Model Additions

### Note Schema Update

```swift
struct Note {
    let id: UUID
    let content: String
    let signifier: Signifier
    let createdAt: Date
    let roleContext: RoleTemplate

    // Execution fields (BuJo-inspired)
    var status: NoteStatus  // open, done, migrated, cancelled
    var migrationCount: Int
    var migrationHistory: [Migration]
    var threadId: UUID?     // Links related notes
    var scheduledFor: Date?
}

enum Signifier: String {
    case task = "•"
    case event = "○"
    case note = "—"
    case priority = "!"
    case explore = "?"
    case idea = "💡"
}

enum NoteStatus {
    case open
    case done       // ×
    case migrated   // >
    case scheduled  // <
    case cancelled  // ~
}
```

---

## Why This Works

| BuJo Principle | RoleNote AI Adaptation |
|----------------|------------------------|
| Analog friction forces intention | Migration prompts create digital friction |
| Page turning = review | Weekly/monthly AI-generated reviews |
| Index for finding | Knowledge Graph + semantic search |
| Rapid logging | Signifier shortcuts + voice |
| Collections | Smart collections + projects |
| Future log | Calendar integration |

**The key insight:** BuJo's power isn't the paper—it's the **system of intentional processing**. RoleNote AI preserves this while adding AI-powered context matching and role-based intelligence.

---

*"Productivity is not about doing more. It's about doing what matters."*
