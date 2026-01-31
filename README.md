# RoleNote AI

**A Smart AI Note Planner with Role-Based Templates and Automatic Context Matching**

> *"Don't just take notes. Make connections."*

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](android/)
[![Validation](https://img.shields.io/badge/Validated-VerifiMind--PEAS-purple.svg)](docs/)
[![Phase](https://img.shields.io/badge/Phase-3a%20Complete-brightgreen.svg)](CHANGELOG.md)

---

## What is RoleNote AI?

RoleNote AI solves the "note graveyard" problem—where valuable ideas are captured but never acted upon.

**The Core Innovation:** Role-based intelligence. The same meeting produces different notes for a Project Manager (deadlines, blockers), a Developer (technical specs), and an Executive (strategic alignment). RoleNote AI understands your role and adapts.

### The 5 Pillars

| # | Pillar | Function | User Value |
|---|--------|----------|------------|
| 1 | **TEMPLATE** | Role-based context (16 built-in + custom) | AI understands MY job |
| 2 | **OVERVIEW** | Dashboard (Today/Week/Projects) | See everything at once |
| 3 | **AUTO REMINDER** | Smart scheduling + migration prompts | Never forget what matters |
| 4 | **AI CHAT-INTERACT** | Natural language assistant | Ask, don't search |
| 5 | **AUDIO FILE** | Voice capture + transcription | Ideas anytime, anywhere |

---

## VerifiMind-PEAS Trinity Validation

This project is validated using the **VerifiMind-PEAS X-Z-CS RefleXion Trinity** methodology:

| Agent | Role | Score | Status |
|-------|------|-------|--------|
| **X Intelligent** | Innovation & Strategy | 7.5/10 | Strong potential |
| **Z Guardian** | Ethics & Compliance | 7.5/10 | Safeguards required |
| **CS Security** | Security & Technical | 6.5/10 | Address before deploy |
| **Overall** | Trinity Synthesis | **7.3/10** | **Proceed with Caution** |

**Validation ID:** `fa3e7b66`
**Verdict:** Approved for Development

### Key Mitigations Implemented

| Concern | Solution | Status |
|---------|----------|--------|
| Prompt Injection | InputValidator with pattern blocking | ✅ Implemented |
| Data Privacy | SQLCipher encryption, on-device AI | ✅ Implemented |
| Authentication Gaps | Biometric + PIN with lockout | ✅ Implemented |
| AI Accountability | Audit logging for all AI actions | ✅ Designed |
| User Autonomy | All AI suggestions skippable | ✅ Designed |

---

## Current Status

### Phase 3a: Android Project Setup ✅ COMPLETE

**Test Device:** Redmi Pad SE 8.7

| Component | Status |
|-----------|--------|
| Project structure (Clean Architecture) | ✅ |
| Gradle + dependencies | ✅ |
| Input validation layer | ✅ |
| Authentication (Biometric + PIN) | ✅ |
| SQLCipher encryption | ✅ |
| Room database (7 entities) | ✅ |
| Hilt dependency injection | ✅ |
| Basic UI screens (8 screens) | ✅ |

### Next: Phase 3b - Core Engine

- [ ] Role Template Engine
- [ ] Note CRUD operations
- [ ] Signifier parsing
- [ ] Template loading

---

## Built-in Role Templates (16 Total)

### Functional Roles (9)
| Role | Focus |
|------|-------|
| Project Manager | Deadlines, decisions, blockers, action items |
| Developer | Technical specs, code refs, implementation |
| Accounting | Transactions, compliance, reporting |
| Marketing | Campaigns, content, metrics |
| Human Resources | Recruitment, policies, relations |
| Business Administration | Operations, vendors, processes |
| Technical - Backend | APIs, databases, infrastructure |
| Technical - Frontend | UI/UX, components, state |
| Customer Services | Tickets, resolutions, feedback |
| Financial Advisor | Portfolios, insights, compliance |
| Compliance & Feedback | Regulations, audits, adherence |

### C-Suite Roles (7)
| Role | Focus |
|------|-------|
| Executive (General) | Strategic alignment, decisions |
| CEO | Vision, stakeholders, leadership |
| COO | Operations, coordination |
| CTO | Tech strategy, architecture |
| CFO | Financial strategy, governance |
| Chief Innovation Officer | Emerging tech, R&D |
| Chief Monitoring Officer | Risk, compliance |
| Chief Research Officer | Research strategy |

---

## Technology Stack (Android MVP)

| Component | Technology |
|-----------|------------|
| **UI** | Jetpack Compose |
| **Language** | Kotlin |
| **Architecture** | Clean Architecture + MVVM |
| **DI** | Hilt |
| **Database** | Room + SQLCipher |
| **On-Device LLM** | Gemma 3 (Google AI Edge SDK) |
| **Embeddings** | all-MiniLM-L6-v2 (ONNX Runtime) |
| **Vector Search** | FAISS-Android |
| **Auth** | Biometric API + PIN |

---

## Project Structure

```
RoleNote AI/
├── README.md                           # This file
├── CHANGELOG.md                        # Version history
├── LICENSE                             # MIT License
├── RoleNote AI Genesis Master Prompt.md # Project specification
│
├── android/                            # Android MVP
│   ├── app/
│   │   └── src/main/java/com/rolenoteai/app/
│   │       ├── core/                   # Security layer
│   │       │   ├── validation/         # Input validation
│   │       │   ├── security/           # Authentication
│   │       │   └── database/           # Encryption
│   │       ├── data/                   # Room database
│   │       ├── domain/                 # Business logic
│   │       ├── presentation/           # Compose UI
│   │       └── di/                     # Hilt modules
│   └── build.gradle.kts
│
├── docs/
│   ├── security/
│   │   ├── INPUT_VALIDATION.md
│   │   ├── AUTHENTICATION.md
│   │   └── THREAT_MODEL.md
│   ├── ethics/
│   │   ├── DATA_ACCESS_MATRIX.md
│   │   └── AUDIT_LOGGING.md
│   ├── EXECUTION_FRAMEWORK.md          # BuJo-inspired system
│   ├── USER_WORKFLOW_ARCHITECTURE.md   # 5 Pillars detail
│   └── TEMPLATE_IMPORT_SYSTEM.md
│
├── templates/
│   ├── functional/                     # 9 functional role templates
│   ├── c-suite/                        # 7 c-suite templates
│   └── TEMPLATE_REGISTRY.md
│
└── ios/                                # (Phase 4 - After Android MVP)
```

---

## Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34
- Test device: Android 8.0+ (API 26+)

### Build

```bash
# Clone repository
git clone https://github.com/creator35lwb-web/RoleNoteAI.git
cd RoleNoteAI

# Open in Android Studio
# File > Open > Select android/ folder

# Build and run
# Select your device (e.g., Redmi Pad SE 8.7)
# Click Run
```

---

## Roadmap

| Phase | Status | Description |
|-------|--------|-------------|
| 1. Foundation | ✅ Complete | Concept, validation, architecture |
| 2. Specs | ✅ Complete | Security, ethics, templates |
| 3a. Android Setup | ✅ Complete | Project, security layer, database |
| 3b. Core Engine | 🔄 Current | Template engine, CRUD, signifiers |
| 3c. AI Integration | ⏳ Pending | Gemma 3, embeddings, suggestions |
| 3d. Android Release | ⏳ Pending | Testing, Play Store |
| 4. iOS | ⏳ After Android | SwiftUI port |
| 5. Cross-Platform | ⏳ Future | Cloud sync, marketplace |

---

## LLM Communication Protocol

This project uses GitHub as a **communication bridge for LLMs**. Future AI agents continuing this work should:

1. **Read `CHANGELOG.md`** - Understand current state and history
2. **Read `Genesis Master Prompt.md`** - Full specification and decisions
3. **Check CTO Notes** - Current focus and next steps
4. **Update documentation** - After significant changes
5. **Commit with context** - Reference phase and validation ID

### Current CTO Notes (RNA)

> **Focus:** Phase 3b - Core Engine Implementation
>
> Phase 3a is complete. Security layer is solid. Next priority is:
> - Role Template Engine integration
> - Load 16 templates from JSON files
> - Note CRUD operations
> - Signifier parsing and display
>
> **Test on:** Redmi Pad SE 8.7
>
> — RNA (Claude Code Opus 4.5), January 31, 2026

---

## Contributing

This is an open-source project (MIT License) from Day 1.

1. Fork the repository
2. Create a feature branch
3. Follow the existing code patterns
4. Update CHANGELOG.md
5. Submit a pull request

---

## License

MIT License - See [LICENSE](LICENSE)

---

## Credits

| Role | Entity |
|------|--------|
| **CTO** | RNA (Claude Code Opus 4.5) |
| **Concept** | Manus AI |
| **Validation** | VerifiMind-PEAS Trinity |
| **Methodology** | Bullet Journal (Ryder Carroll) |

---

## Links

- **GitHub:** https://github.com/creator35lwb-web/RoleNoteAI
- **VerifiMind:** Validation ID `fa3e7b66`
- **MarketPulse:** [Sister project](https://github.com/creator35lwb-web/MarketPulse) (also validated by VerifiMind)

---

*Built with VerifiMind-PEAS methodology. Validated before development.*
