# RoleNoteAI Genesis Master Prompt v1.0

## LLM Context Document for Continuous Development

**Version:** 1.0
**Date:** February 01, 2026
**Last Updated By:** CTO RNA (Claude Code Opus 4.5)

---

## 1. Project Identity

### 1.1 What is RoleNoteAI?

RoleNoteAI is a **privacy-first, role-based note-taking Android app** that enables professionals to capture, organize, and execute ideas based on their specific job role. Unlike generic note apps, RoleNoteAI understands that a **Project Manager** needs different capture prompts than a **Developer** or **CFO**.

### 1.2 Core Tagline

> *"Capture ideas anywhere. Execute them everywhere. Let your role guide your focus."*

### 1.3 The Problem We Solve

In complex organizations (government, enterprise, multi-stakeholder projects):
- Same meeting, different needs (PM needs deadlines, Dev needs specs, Exec needs strategy)
- Notes become graveyards of unexecuted ideas
- Current AI tools are role-agnostic and don't understand job context
- Privacy concerns with cloud-based AI assistants

### 1.4 Our Unique Value Proposition

| Differentiator | Description |
|----------------|-------------|
| **Role-Based Intelligence** | 19 built-in role templates with specific capture prompts |
| **Privacy-First** | On-device AI (Gemma 3), encrypted local storage (SQLCipher) |
| **BuJo-Inspired Execution** | Signifiers, migration system, weekly/monthly reviews |
| **Multi-Stakeholder Ready** | Same meeting, different perspectives captured correctly |

---

## 2. Team Structure

### 2.1 Core Team

| Role | Agent | Responsibility | Primary Tool |
|------|-------|----------------|--------------|
| **Founder** | Human | Vision, Direction, Final Approval | Both |
| **CTO** | RNA (Claude Code Opus 4.5) | Architecture, Core Development, Code Review | Claude Code CLI |
| **CSO** | R (Manus AI) | Security Validation, Feature Development, Testing, Market Research | Manus AI |

### 2.2 Communication Protocol

- **GitHub** is the communication bridge between agents
- **Issues** for task handoffs and reviews
- **Commits** use `[Phase X]` prefix
- **Labels**: `ready-for-manus`, `ready-for-claude`, `phase-3c`, `cso-review`

### 2.3 Agent Collaboration Rules

1. **CTO RNA** handles: Architecture decisions, Kotlin/Compose code, build fixes, database schema
2. **CSO R** handles: Security validation, market research, feature implementation, device testing
3. Both agents **push to GitHub** with detailed commit messages
4. Handoffs via **GitHub Issues** with clear next steps

---

## 3. Current State (As of February 01, 2026)

### 3.1 Phase Completion Status

| Phase | Status | Description |
|-------|--------|-------------|
| Phase 1 | ✅ Complete | Ideation & Planning |
| Phase 2 | ✅ Complete | Design & Architecture |
| Phase 3a | ✅ Complete | Android Project Setup |
| Phase 3b | ✅ Complete | Core Engine (Auth, DB, Templates) |
| Phase 3c | 🔄 In Progress | Role Intelligence UI |
| Phase 3d | ⏳ Pending | AI Integration (Gemma 3) |
| Phase 4 | ⏳ Pending | Polish & Release |

### 3.2 What's Working

- ✅ PIN authentication with biometric placeholder
- ✅ Encrypted database (SQLCipher + Room)
- ✅ 19 role templates loading from JSON assets
- ✅ Template selection on first launch
- ✅ Role switching in Settings
- ✅ Role color indicators in top bars
- ✅ Template-specific capture prompts
- ✅ Note CRUD operations
- ✅ Navigation flow (Auth → Setup → Overview → Notes)

### 3.3 What's Next (Phase 3c Continued)

Per CSO Alignment Guide priorities:
1. Voice capture with transcription
2. Migration system (BuJo-style)
3. Weekly/Monthly review prompts
4. Gemma 3 integration preparation

---

## 4. Technical Architecture

### 4.1 Tech Stack

| Component | Technology |
|-----------|------------|
| Platform | Android (minSdk 26, targetSdk 34) |
| Language | Kotlin 1.9.x |
| UI | Jetpack Compose + Material3 |
| DI | Hilt |
| Database | Room + SQLCipher |
| AI (Planned) | Gemma 3 (on-device) |
| Build | Gradle Kotlin DSL |

### 4.2 Project Structure

```
android/app/src/main/
├── java/com/rolenoteai/app/
│   ├── core/           # Security, Database, Validation
│   │   ├── database/   # EncryptedDatabaseFactory
│   │   └── security/   # AuthenticationManager, InputValidator
│   ├── data/           # Repository, DAO, Entities, Mappers
│   │   ├── local/      # Room DAOs and Entities
│   │   ├── mapper/     # Entity ↔ Domain mappers
│   │   └── repository/ # TemplateRepository, NoteRepository
│   ├── di/             # Hilt modules (AppModule, DatabaseModule)
│   ├── domain/         # Models (Note, Reminder, RoleTemplate, Signifier)
│   └── presentation/   # UI layer
│       ├── ui/         # RoleNoteApp, Screens, Theme
│       └── viewmodel/  # AuthViewModel, NoteViewModel, TemplateViewModel
├── assets/templates/   # 19 role JSON templates
│   ├── functional/     # 11 functional roles
│   └── c-suite/        # 8 C-suite roles
└── res/                # Resources, drawables, values
```

### 4.3 Key Files Reference

| Purpose | File Path |
|---------|-----------|
| Main Entry | `presentation/ui/RoleNoteApp.kt` |
| All Screens | `presentation/ui/screens/Screens.kt` |
| Auth Logic | `core/security/AuthenticationManager.kt` |
| DB Factory | `core/database/EncryptedDatabaseFactory.kt` |
| Templates | `data/repository/TemplateRepository.kt` |
| Notes | `data/repository/NoteRepository.kt` |
| DI Setup | `di/AppModule.kt`, `di/DatabaseModule.kt` |

### 4.4 Template JSON Structure

```json
{
  "id": "project-manager",
  "name": "Project Manager",
  "version": "1.0",
  "description": "Track projects, milestones, and team coordination",
  "category": "functional",
  "icon": "assignment",
  "color": "#3B82F6",
  "capturePrompts": [
    { "field": "project", "prompt": "Which project is this for?", "required": false },
    { "field": "deadline", "prompt": "Is there a deadline?", "required": false },
    { "field": "assignee", "prompt": "Who is responsible?", "required": false }
  ],
  "suggestionRules": [
    { "trigger": "meeting", "action": "suggest_calendar_event", "priority": 1 }
  ],
  "execution": {
    "signifiers_enabled": true,
    "default_signifier": "task",
    "migration_prompt_days": [3, 7, 14],
    "weekly_review": true,
    "monthly_review": true
  }
}
```

---

## 5. Development Guidelines

### 5.1 Code Standards

- **Kotlin** with coroutines and Flow
- **Compose** for all UI (no XML layouts)
- **Material3** design system
- **Hilt** for all dependency injection
- **Room** for all database operations
- All sensitive data in **SQLCipher** encrypted database

### 5.2 Commit Message Format

```
[Phase X] <Type>: <Short description>

<Detailed description if needed>

Co-Authored-By: <Agent Name> <noreply@anthropic.com>
```

**Types:** `Add`, `Fix`, `Update`, `Refactor`, `Docs`, `Test`

### 5.3 Testing Protocol

- **Primary Device:** Redmi Pad SE 8.7 (Android 14)
- **Test Folder:** `C:\dev\RoleNoteAI-RedMiPadSE`
- **Workspace:** `C:\Users\weibi\OneDrive\Desktop\VerifiMind (Workspace)\RoleNote AI`
- Always run `./gradlew assembleDebug` before pushing

### 5.4 Before Push Checklist

- [ ] Build succeeds
- [ ] App installs on device
- [ ] PIN setup/unlock works
- [ ] Template selection displays all 19 roles
- [ ] No crashes on navigation
- [ ] Changes synced between workspace and test folder

---

## 6. VerifiMind-PEAS Compliance

### 6.1 Ethical Requirements

RoleNoteAI must pass VerifiMind-PEAS (X-Z-CS Trinity) validation:

- [ ] All AI suggestions must be **skippable** (user autonomy)
- [ ] Data must remain **on-device by default** (privacy)
- [ ] User must **own their data** (exportable)
- [ ] No surveillance capabilities in enterprise deployments
- [ ] Transparency in AI processing ("show your work" feature)

### 6.2 Z-Protocol (Ethics Guardian)

If any feature violates user privacy or autonomy, it **cannot ship**. Z Guardian has veto power.

---

## 7. Roadmap v2.0 (Core-First Strategy)

### 7.1 The Principle

> **"Solid core before expanded features."**

### 7.2 Layer Model

```
LAYER 4: Integration (Future) - External templates, calendar, chat, voice
    ↑ ONLY AFTER CORE IS SOLID
LAYER 3: AI Interaction (Phase 3c-3d) - Gemma 3, suggestions, auto-tagging
    ↑
LAYER 2: Execution (Phase 3b-3c) - Migration, signifiers, reviews
    ↑
LAYER 1: Core (Phase 3a-3b) - Templates, CRUD, switching, encryption
```

### 7.3 Current Priority (Phase 3c)

1. **P1:** Role switching UI ✅ DONE
2. **P2:** Template-specific prompts ✅ DONE
3. **P3:** Role indicators ✅ DONE
4. **P4:** Voice capture (next)
5. **P5:** Migration system (next)

---

## 8. Quick Start for New LLM Sessions

### 8.1 If You're CTO RNA (Claude Code)

1. Read this Genesis Prompt
2. Check GitHub for latest issues/PRs
3. Pull latest from `main` branch
4. Review `docs/CSO_ALIGNMENT_GUIDE_v1.0.md` for priorities
5. Make changes in workspace folder
6. Copy to test folder, build, verify
7. Commit with `[Phase X]` prefix
8. Create GitHub issue for CSO handoff

### 8.2 If You're CSO R (Manus AI)

1. Read this Genesis Prompt
2. Check GitHub for latest issues/PRs from CTO
3. Review `docs/CSO_STRATEGIC_VISION_v1.0.md` for market context
4. Test on Redmi Pad SE device
5. Implement features per alignment guide priorities
6. Push with detailed commit messages
7. Create GitHub issue with `ready-for-claude` label

### 8.3 Key Commands

```bash
# Build the app
cd android && ./gradlew assembleDebug

# Check git status
git status

# Push to GitHub
git add <files>
git commit -m "[Phase 3c] ..."
git push origin main

# Create GitHub issue
gh issue create --title "..." --body "..."
```

---

## 9. Repository Links

- **GitHub:** https://github.com/creator35lwb-web/RoleNoteAI
- **Latest Issue:** https://github.com/creator35lwb-web/RoleNoteAI/issues/2

---

## 10. Document References

| Document | Purpose |
|----------|---------|
| `GENESIS_MASTER_PROMPT_v1.0.md` | This file - LLM context |
| `DUAL_REPO_PROTOCOL_v1.0.md` | Team collaboration rules |
| `CSO_ALIGNMENT_GUIDE_v1.0.md` | Phase 3c priorities from CSO |
| `CSO_STRATEGIC_VISION_v1.0.md` | Roadmap v2.0 and vision |
| `EXECUTION_FRAMEWORK.md` | BuJo execution methodology |
| `USER_WORKFLOW_ARCHITECTURE.md` | User journey and flows |

---

## 11. Version History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | 2026-02-01 | CTO RNA | Initial Genesis Master Prompt |

---

*"The flywheel turns. Each commit, each feature, each test brings us closer to the vision."*

**Team RoleNoteAI**
