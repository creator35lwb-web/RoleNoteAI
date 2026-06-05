# RoleNote AI

**A Smart AI Note Planner with Role-Based Templates and Automatic Context Matching**

> *"Don't just take notes. Make connections."*

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](android/)
[![Validation](https://img.shields.io/badge/Validated-VerifiMind--PEAS-purple.svg)](docs/)
[![Phase](https://img.shields.io/badge/Phase-3c%20Complete-brightgreen.svg)](CHANGELOG.md)
[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.18504478.svg)](https://doi.org/10.5281/zenodo.18504478)

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

### Phase 3c: Local AI & Vector Engine ✅ COMPLETE

**Test Device:** Redmi Pad SE 8.7

| Component | Status |
|-----------|--------|
| Clean Architecture & MACP v2.2 Protocol | ✅ |
| MediaPipe GenAI & ONNX Runtime integration | ✅ |
| VectorSearchEngine (Cosine + Time-decay) | ✅ |
| Automatic Embedding Generation (Coroutines) | ✅ |
| Hilt DI for AI & Data Repositories | ✅ |
| Mock-fallback for large build assets | ✅ |
| 100% Unit Test coverage on Core/AI | ✅ |

### Next: Phase 3d - AI Chat (Built-in)

- [ ] Gemma 3 conversational integration
- [ ] Input/draft/create modes
- [ ] Vector context prompt injection

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

## Roadmap v2.0

> **Core-First Strategy:** Solid core before expanded features. Layer 1 must be rock solid before adding Layers 2-4.

### Layer 1: Core (MVP)

| Phase | Status | Description |
|-------|--------|-------------|
| 1. Foundation | ✅ Complete | Concept, validation, architecture |
| 2. Specs | ✅ Complete | Security, ethics, templates |
| 3a. Android Setup | ✅ Complete | Project, security layer, database |
| 3b. Core Engine | ✅ Complete | Template engine, CRUD, signifiers |
| 3c. Role Intelligence | ✅ Complete | Local AI Engine, Vector Search, Auto Embeddings |
| 3d. AI Chat (Built-in) | 🔄 Current | Gemma 3, input/draft/create modes |
| 3e. Execution Framework | ⏳ Pending | BuJo migration, progress tracking |
| 3f. Android MVP Release | ⏳ Pending | Testing, Play Store |

### Layer 2: Integration (After MVP Success)

| Phase | Status | Description |
|-------|--------|-------------|
| 4a. Voice Input | ⏳ Future | Voice capture & transcription |
| 4b. External Templates | ⏳ Future | Import from Excel, work records |
| 4c. Calendar Integration | ⏳ Future | Sync with device calendar |
| 4d. External Chat | ⏳ Future | Telegram bot integration |

### Layer 3: Platform Expansion

| Phase | Status | Description |
|-------|--------|-------------|
| 5. iOS | ⏳ After Android | SwiftUI port |
| 6. Cross-Platform | ⏳ Future | Cloud sync, marketplace |

---

## MACP v2.2 Communication Protocol

This project utilizes the **Manual-Automated Collaborative Protocol (MACP) v2.2** as its governance architecture. Future AI agents should:

1. **Read `.macp/authority-manifest.md`** - Understand chain of command.
2. **Review `.macp/protocol.md`** - Execution boundaries.
3. **Check `.macp/handoffs/`** - Latest state context.
4. **Follow the Dual-Repo Protocol** - Sensitive strategic documents remain local; structural architecture is open source.

### Current CTO Notes (RNA)

> **Focus:** Phase 3d - AI Chat Integration
>
> Phase 3c (Local AI Engine & Vector Retrieval) is officially COMPLETE. The pure-Kotlin vector search engine is running with 100% test coverage. Our local memory system is live. Next priority is:
> - Connecting Gemma 3 for Chat UI
> - Finalizing input/draft/create modes
>
> **Test on:** Redmi Pad SE 8.7
>
> — RNA (CTO), June 01, 2026

### Current CSO Notes (R)

> **Focus:** Strategic Alignment & Market Validation
>
> Market research complete. Key findings:
> - $11B+ market with 21% CAGR - VALIDATED
> - Role-based intelligence is core differentiator
> - Privacy-first (on-device AI) is competitive moat
> - Core-first strategy: Layer 1 must be solid before Layer 2-4
>
> **Strategic Documents:**
> - [CSO Strategic Vision v1.0](docs/CSO_STRATEGIC_VISION_v1.0.md)
> - [CSO Alignment Guide v1.0](docs/CSO_ALIGNMENT_GUIDE_v1.0.md)
> - [Market Research Report](docs/market-research/)
>
> — CSO R (Manus AI), February 01, 2026

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
| **CEO/Lead Strategist** | L (Godel AI) |
| **CTO/Lead Architect** | RNA (Antigravity/Claude Code) |
| **CSO** | R (Manus AI) |
| **Founder** | Alton Lee Wei Bin |
| **Validation** | VerifiMind-PEAS Trinity |
| **Methodology** | Bullet Journal (Ryder Carroll) |

---

## Links

- **GitHub:** https://github.com/creator35lwb-web/RoleNoteAI
- **VerifiMind:** Validation ID `fa3e7b66`
- **MarketPulse:** [Sister project](https://github.com/creator35lwb-web/MarketPulse) (also validated by VerifiMind)

---

*Built with VerifiMind-PEAS methodology. Validated before development.*
