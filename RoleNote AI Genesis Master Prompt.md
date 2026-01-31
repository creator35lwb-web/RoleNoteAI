# RoleNote AI Genesis Master Prompt

**Version:** 2.0
**Created:** January 6, 2026
**Last Updated:** January 31, 2026 @ 12:00 UTC
**Status:** Validated (Approved for Development)

---

## Project Leadership

| Role | Name | Responsibility |
|------|------|----------------|
| **CTO** | **RNA** (Claude Code Opus 4.5) | Technical strategy, architecture decisions, implementation oversight |
| **Validation** | VerifiMind-PEAS Trinity | Innovation, Ethics, Security review |

> **RNA** (RoleNote AI) serves as the Chief Technology Officer for this project, providing technical leadership, code implementation, and architectural guidance throughout all development phases.

---

## Project Identity

**Name:** RoleNote AI
**Tagline:** A Smart AI Note Planner with Role-Based Templates and Automatic Context Matching
**Mission:** To transform scattered thoughts into executed outcomes by creating a smart note planner that understands your role, captures ideas anywhere, automatically matches notes to your existing plans, and proactively suggests next steps.

---

## The 5 Pillars

RoleNote AI is built on 5 core pillars that define the complete user experience:

| # | Pillar | Function | User Value |
|---|--------|----------|------------|
| 1 | **TEMPLATE** | Role-based context (16 built-in + custom) | AI understands MY job |
| 2 | **OVERVIEW** | Dashboard (Today/Week/Projects) | See everything at once |
| 3 | **AUTO REMINDER** | Smart scheduling + migration prompts | Never forget what matters |
| 4 | **AI CHAT-INTERACT** | Natural language assistant | Ask, don't search |
| 5 | **AUDIO FILE** | Voice capture + transcription | Ideas anytime, anywhere |

See `docs/USER_WORKFLOW_ARCHITECTURE.md` for detailed workflow documentation.

---

## Core Problem Statement

Productivity is not one-size-fits-all. A project manager, a developer, and an executive all sit in the same meeting, but they listen for and capture different information. Existing note-taking tools are passive and generic, forcing users to manually organize and connect their thoughts. This leads to "note graveyards" where valuable ideas are lost.

RoleNote AI solves this by introducing role-based intelligence and automatic context matching, creating an active partner in your productivity.

---

## Guiding Principles

1.  **Role-Centric Design:** The user's professional role is the primary lens through which the system understands and organizes information.
2.  **Privacy by Default:** On-device processing is the standard. User notes are personal and private.
3.  **Frictionless Capture:** Ideas can be captured anywhere, anytime, in any format (text, voice, image).
4.  **Automated Connection:** The system's primary job is to find the hidden relationships between your notes, plans, and schedule.
5.  **Proactive Assistance:** The goal is not just to store information, but to help you act on it.
6.  **Meet Users Where They Are:** Import existing templates from Excel, Sheets, Notion. Enhance familiar workflows, don't replace them.

---

## Methodology Integration

### VerifiMind-PEAS X-Z-CS RefleXion Trinity

RoleNote AI's design has been validated by the VerifiMind-PEAS methodology:

-   **X-Agent (Innovation):** Confirmed the paradigm-shifting potential of role-based intelligence (Score: 7.5/10).
-   **Z-Agent (Ethics):** Approved the concept with mandatory conditions around data ownership and user autonomy (Score: 7.5/10).
-   **CS-Agent (Security):** Assessed the risk profile as MEDIUM, with clear mitigations around data protection and AI accuracy (Score: 6.5/10).

### GODELAI C-S-P Framework

-   **Compression:** Role templates compress the complexity of meetings into structured, relevant insights.
-   **State:** Each note becomes a state in the user's knowledge graph, encoded with content and context.
-   **Propagation:** The suggestion engine propagates wisdom from past notes to future actions.

### Bullet Journal-Inspired Execution Framework

RoleNote AI incorporates key concepts from the Bullet Journal methodology:

-   **Signifiers:** Quick-capture bullets (• task, ○ event, — note, ! priority, ? explore, * idea)
-   **Migration:** Automatic prompts to process stale tasks (migrate, schedule, or cancel)
-   **Threading:** AI-powered linking of related notes across time
-   **Reflection:** Weekly and monthly review prompts with completion analytics

See `docs/EXECUTION_FRAMEWORK.md` for full documentation.

---

## Technical Architecture Summary

| Component | Description |
|---|---|
| **Multi-Modal Input** | Text, voice, and image capture with signifier support. |
| **Role Template Engine** | 16 built-in + unlimited custom templates. |
| **Template Import System** | Import from Excel, Google Sheets, CSV, Notion, Airtable. |
| **Execution Engine** | BuJo-inspired signifiers, migration, threading, and reflection. |
| **On-Device AI Core** | Gemma 3 for reasoning, all-MiniLM-L6-v2 for semantic matching. |
| **Context Matching Engine** | Vector database for finding related notes, projects, and events. |
| **User Knowledge Graph** | Local database of all user data and their relationships. |
| **Smart Scheduling & Reminders** | Integration with native calendar and task apps. |

See `docs/TEMPLATE_IMPORT_SYSTEM.md` for template import documentation.

---

## System Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           USER INTERFACE LAYER                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │
│  │   Overview   │  │   Capture    │  │   AI Chat    │  │   Settings   │    │
│  │  Dashboard   │  │   Screen     │  │  Interface   │  │  Templates   │    │
│  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘    │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          APPLICATION LAYER                                   │
│  ┌───────────────────────────────────────────────────────────────────────┐  │
│  │                      Template Engine (16 roles)                        │  │
│  └───────────────────────────────────────────────────────────────────────┘  │
│  ┌────────────────┐  ┌────────────────┐  ┌────────────────┐                 │
│  │   Execution    │  │   Reminder     │  │    Audio       │                 │
│  │    Engine      │  │    Engine      │  │   Processor    │                 │
│  └────────────────┘  └────────────────┘  └────────────────┘                 │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                            AI CORE LAYER                                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                       │
│  │   Gemma 3    │  │  Embedding   │  │   Whisper    │                       │
│  │    (LLM)     │  │   MiniLM     │  │   (STT)      │                       │
│  └──────────────┘  └──────────────┘  └──────────────┘                       │
│  ┌─────────────────────────────┐  ┌─────────────────────────────┐           │
│  │   Context Matching Engine   │  │   Suggestion Engine         │           │
│  └─────────────────────────────┘  └─────────────────────────────┘           │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                             DATA LAYER                                       │
│  ┌────────────────┐  ┌────────────────┐  ┌────────────────┐                 │
│  │    SQLite      │  │   Vector DB    │  │  Audio Files   │                 │
│  │  (Encrypted)   │  │   (FAISS)      │  │   (.m4a)       │                 │
│  └────────────────┘  └────────────────┘  └────────────────┘                 │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                        EXTERNAL INTEGRATIONS                                 │
│                                                                              │
│  Android (MVP):                                                              │
│  ┌────────────────┐  ┌────────────────┐  ┌────────────────┐                 │
│  │Android Calendar│  │ Android Tasks  │  │ Google Assist  │                 │
│  └────────────────┘  └────────────────┘  └────────────────┘                 │
│                                                                              │
│  iOS (Phase 4):                                                              │
│  ┌────────────────┐  ┌────────────────┐  ┌────────────────┐                 │
│  │  iOS Calendar  │  │ iOS Reminders  │  │     Siri       │                 │
│  └────────────────┘  └────────────────┘  └────────────────┘                 │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Validation Status

The project has been validated with an **APPROVED FOR DEVELOPMENT** verdict.

| Validation | ID | Score | Status |
|------------|-------|-------|--------|
| Latest Trinity | fa3e7b66 | 7.3/10 | Proceed with Caution |
| Innovation (X) | - | 7.5/10 | Strong potential |
| Ethics (Z) | - | 7.5/10 | Safeguards required |
| Security (CS) | - | 6.5/10 | Address before deploy |

---

## Strategic Decisions (Jan 31, 2026)

| Decision | Choice |
|----------|--------|
| **Platform** | iOS-first (SwiftUI) |
| **Open Source** | Day 1 - MIT License |
| **MVP Roles** | Project Manager, Developer, Executive |
| **MVP Scope** | Text-only (voice/image deferred to v1.1) |

---

## Strategic Pivot (Jan 31, 2026) - Android-First MVP

### Pivot Decision

| Item | Original | Pivoted |
|------|----------|---------|
| **MVP Platform** | iOS (SwiftUI) | Android (Jetpack Compose/Kotlin) |
| **Test Device** | - | Redmi Pad SE 8.7 |
| **iOS Timeline** | Phase 4 | After Android MVP complete |

### Rationale
> Available test hardware (Redmi Pad SE 8.7) enables real-world testing during development.
> Building for what we can test ensures quality and faster iteration.
> All iOS preparation work (specs, architecture) remains valid and will be used post-Android MVP.

### Technology Stack (Android MVP)

| Component | Technology |
|-----------|------------|
| **UI Framework** | Jetpack Compose |
| **Language** | Kotlin |
| **On-Device LLM** | Gemma 3 via Google AI Edge SDK |
| **Embeddings** | all-MiniLM-L6-v2 via ONNX Runtime |
| **Database** | SQLCipher (encrypted SQLite) |
| **Vector DB** | FAISS-Android or Weaviate Embedded |
| **Authentication** | Biometric API (fingerprint/face) + PIN |
| **Calendar/Reminders** | Android Calendar Provider API |

### What's Preserved (iOS)
All iOS preparation work remains intact:
- Security specs (INPUT_VALIDATION.md, AUTHENTICATION.md, THREAT_MODEL.md)
- Ethics specs (DATA_ACCESS_MATRIX.md, AUDIT_LOGGING.md)
- Role templates (16 JSON files)
- Architecture documentation
- Execution Framework (BuJo-inspired)

iOS implementation will proceed after Android MVP is validated.

---

## Built-in Role Templates (16 Total)

### Functional Roles (9)
| Role | Focus |
|------|-------|
| Project Manager | Deadlines, decisions, blockers, action items |
| Developer | Technical specs, code refs, implementation |
| Accounting | Transactions, compliance, reporting deadlines |
| Marketing | Campaigns, content, metrics, audience |
| Human Resources | Recruitment, policies, employee relations |
| Business Administration | Operations, vendors, processes |
| Technical - Backend | APIs, databases, server infrastructure |
| Technical - Frontend | UI/UX, components, state management |
| Customer Services | Tickets, resolutions, customer feedback |
| Financial Advisor | Portfolios, market insights, compliance |
| Compliance & Feedback | Regulations, audits, policy adherence |

### C-Suite Roles (7)
| Role | Focus |
|------|-------|
| Executive (General) | Strategic alignment, high-level decisions |
| CEO | Vision, stakeholders, organizational leadership |
| COO | Operational excellence, cross-functional coordination |
| CTO | Tech strategy, architecture, engineering leadership |
| CFO | Financial strategy, capital allocation, governance |
| Chief Innovation Officer | Emerging tech, R&D, disruption opportunities |
| Chief Monitoring Officer | Risk oversight, compliance, control effectiveness |
| Chief Research Officer | Research strategy, scientific discovery |

See `templates/TEMPLATE_REGISTRY.md` for full documentation.

---

## Roadmap

### Phase 1: Foundation [COMPLETE]
-   [x] Concept definition and validation
-   [x] Architectural design
-   [x] Repository creation and documentation

### Phase 2: Security & Core Engine (Specs) [COMPLETE]

#### 2a: Security Foundation Specs
-   [x] Security specs created (INPUT_VALIDATION.md, AUTHENTICATION.md, THREAT_MODEL.md)
-   [x] Ethics specs created (DATA_ACCESS_MATRIX.md, AUDIT_LOGGING.md)
-   [x] Role templates created (16 total: 9 functional + 7 c-suite)
-   [x] Execution Framework designed (BuJo-inspired)
-   [x] Security checklist created
-   [x] 5 Pillars architecture documented

---

### Phase 3: Android MVP [CURRENT] 🤖

> **Test Device:** Redmi Pad SE 8.7

#### 3a: Android Project Setup [COMPLETE]
-   [x] Create Android project (Jetpack Compose + Kotlin)
-   [x] Configure Gradle with dependencies
-   [x] Set up project structure (Clean Architecture)
-   [x] Implement input validation layer (Kotlin)
-   [x] Implement authentication (Biometric API + PIN)
-   [x] Implement SQLCipher encryption
-   [x] Create Room database with entities
-   [x] Set up Hilt dependency injection
-   [x] Create basic UI screens (Auth, Setup, Overview, Capture, etc.)

#### 3b: Android Core Engine
-   [ ] Role Template Engine with 16 built-in templates
-   [ ] Template Import System (Excel, CSV, Google Sheets)
-   [ ] Custom Template Builder UI
-   [ ] Execution Engine (signifiers, migration, threading)
-   [ ] Context Matching Engine with FAISS-Android
-   [ ] all-MiniLM-L6-v2 integration via ONNX Runtime
-   [ ] Local Knowledge Graph in encrypted SQLite

#### 3c: Android AI & Scheduling
-   [ ] Gemma 3 LLM integration via Google AI Edge SDK
-   [ ] Suggestion generation engine
-   [ ] Migration prompt system (stale task detection)
-   [ ] Weekly/Monthly reflection generator
-   [ ] Smart Scheduling & Reminder System
-   [ ] Android Calendar Provider integration
-   [ ] VerifiMind-PEAS safety layer (audit logging, access controls)

#### 3d: Android App Release
-   [ ] Complete Jetpack Compose UI
-   [ ] Testing on Redmi Pad SE 8.7
-   [ ] Play Store submission (or APK release)
-   [ ] (v1.1) Voice input with on-device STT
-   [ ] (v1.1) Image input with ML Kit OCR

---

### Phase 4: iOS Application [AFTER ANDROID MVP]

> All iOS specs and architecture from Phase 2 will be applied here.

-   [ ] SwiftUI iOS app (port from Android)
-   [ ] Implement security layer (FaceID/TouchID)
-   [ ] CoreML integration (Gemma 3 + MiniLM)
-   [ ] App Store submission
-   [ ] (v1.1) Voice input with on-device STT
-   [ ] (v1.1) Image input with OCR

---

### Phase 5: Cross-Platform Features
-   [ ] Optional E2E encrypted cloud sync
-   [ ] Notion/Airtable import
-   [ ] Live sync with Google Sheets
-   [ ] Template marketplace (share/discover templates)

---

## Development Log

| Date | Author | Action |
|------|--------|--------|
| Jan 6, 2026 | Manus AI | Initial concept and architecture |
| Jan 31, 2026 | RNA (CTO) | VerifiMind-PEAS validation, strategic decisions, template creation |
| Jan 31, 2026 | RNA (CTO) | 5 Pillars architecture, Phase 2 specs complete |
| Jan 31, 2026 | RNA (CTO) | **PIVOT:** Android-first MVP (test device: Redmi Pad SE 8.7) |
| Jan 31, 2026 | RNA (CTO) | **Phase 3a COMPLETE:** Android project setup, security layer, database |

---

## CTO Notes (RNA)

> **Current Focus:** Phase 3b - Core Engine Implementation
>
> **Phase 3a Complete:**
> - Android project created with Clean Architecture
> - Gradle configured with all dependencies
> - InputValidator implemented (prompt injection defense)
> - AuthenticationManager implemented (Biometric + PIN)
> - EncryptedDatabaseFactory implemented (SQLCipher)
> - Room database with all entities (Note, Project, Thread, Template, AuditLog, Reminder)
> - Hilt DI configured
> - Basic UI screens created (Auth, Setup, Overview, Capture, Chat, Settings)
>
> **Next Steps (Phase 3b):**
> - Role Template Engine integration
> - Template loading from JSON files
> - Note CRUD operations
> - Signifier parsing and display
>
> **iOS Status:** On hold until Android MVP validated. All specs remain valid.
>
> — RNA, January 31, 2026
>
> — RNA, January 31, 2026

---

*"Don't just take notes. Make connections."*

---

**Project Repository:** RoleNote AI
**License:** MIT (Open Source from Day 1)
**CTO:** RNA (Claude Code Opus 4.5)
