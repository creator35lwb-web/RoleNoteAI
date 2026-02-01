# Genesis Master Prompt Quick Reference

## Project Identity

**RoleNoteAI** is a privacy-first, role-based note-taking Android app that enables professionals to capture, organize, and execute ideas based on their specific job role.

**Tagline:** *"Capture ideas anywhere. Execute them everywhere. Let your role guide your focus."*

## Tech Stack

| Component | Technology |
|-----------|------------|
| Platform | Android (minSdk 26, targetSdk 34) |
| Language | Kotlin 1.9.x |
| UI | Jetpack Compose + Material3 |
| DI | Hilt |
| Database | Room + SQLCipher |
| AI (Planned) | Gemma 3 (on-device) |

## Key Differentiators

1. **Role-Based Intelligence** - 19 built-in role templates
2. **Privacy-First** - On-device AI, encrypted local storage
3. **BuJo-Inspired Execution** - Signifiers, migration, reviews
4. **Multi-Stakeholder Ready** - Same meeting, different perspectives

## Current Phase Priorities (3c)

1. Voice capture with transcription
2. Migration system (BuJo-style)
3. Weekly/Monthly review prompts
4. Gemma 3 integration preparation

## Repository Structure

```
android/app/src/main/java/com/rolenoteai/app/
├── core/           # Security, Database, Validation
├── data/           # Repository, DAO, Entities
├── di/             # Hilt modules
├── domain/         # Models
└── presentation/   # UI layer
```

## Full Document Location

`/home/ubuntu/RoleNoteAI/docs/GENESIS_MASTER_PROMPT_v1.0.md`
