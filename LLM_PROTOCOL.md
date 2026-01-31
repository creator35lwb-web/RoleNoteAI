# LLM Communication Protocol

**Version:** 1.0
**Last Updated:** January 31, 2026
**Author:** RNA (Claude Code Opus 4.5)

---

## Purpose

This document establishes a communication protocol for LLMs (Large Language Models) working on the RoleNote AI project. It serves as a bridge for continuity across sessions, agents, and time.

---

## Protocol for New LLM Sessions

When you (an LLM) begin working on this project, follow these steps:

### Step 1: Understand Current State

```
READ ORDER:
1. README.md           → Project overview
2. CHANGELOG.md        → Version history and current phase
3. Genesis Master Prompt.md → Full specification and decisions
4. CTO Notes section   → Current focus and priorities
```

### Step 2: Identify Your Role

| If you are... | Your focus is... |
|---------------|------------------|
| Continuing development | Check Phase status, resume from CHANGELOG |
| Reviewing code | Check security specs in docs/security/ |
| Adding features | Check Genesis Master Prompt for roadmap |
| Fixing bugs | Check current phase requirements |

### Step 3: Before Making Changes

1. **Verify the phase** - Are you working on the current phase?
2. **Check dependencies** - Does your change require previous work?
3. **Review specs** - Is there a specification for this feature?
4. **Consider validation** - Does this align with VerifiMind scores?

### Step 4: After Making Changes

1. **Update CHANGELOG.md** - Add your changes under current version
2. **Update Genesis Master Prompt** - Mark tasks complete, add notes
3. **Commit with context** - Include phase and validation ID
4. **Document decisions** - Future LLMs need to understand why

---

## Commit Message Format

```
[Phase X.x] Brief description

- Detailed change 1
- Detailed change 2

Trinity ID: fa3e7b66
CTO: RNA
```

### Examples

```
[Phase 3a] Implement SQLCipher encryption

- Created EncryptedDatabaseFactory.kt
- Added secure key generation and storage
- Implemented key rotation support

Trinity ID: fa3e7b66
CTO: RNA
```

```
[Phase 3b] Add Role Template Engine

- Load 16 templates from JSON
- Template selection persistence
- Active template tracking in Room

Trinity ID: fa3e7b66
CTO: RNA
```

---

## Key Files Reference

| File | Purpose | Update When |
|------|---------|-------------|
| `README.md` | Public overview | Major milestones |
| `CHANGELOG.md` | Version history | Every significant change |
| `Genesis Master Prompt.md` | Full specification | Decisions, completions |
| `LLM_PROTOCOL.md` | This file | Protocol changes |
| `docs/security/*.md` | Security specs | Security changes |
| `docs/ethics/*.md` | Ethics specs | Ethics/privacy changes |

---

## Current Project State

### Validation
- **Trinity ID:** fa3e7b66
- **Overall Score:** 7.3/10
- **Status:** Approved for Development

### Phase Status (as of 2026-01-31)
- Phase 1: Foundation → ✅ COMPLETE
- Phase 2: Specs → ✅ COMPLETE
- Phase 3a: Android Setup → ✅ COMPLETE
- Phase 3b: Core Engine → ✅ COMPLETE
- Phase 3c: AI Integration → 🔄 NEXT
- Phase 3d: Android Release → ⏳ PENDING
- Phase 4: iOS → ⏳ AFTER ANDROID

### Strategic Decisions
- Platform: Android-first (Redmi Pad SE 8.7)
- Open Source: MIT License from Day 1
- Architecture: Clean Architecture + MVVM
- Security: SQLCipher + Biometric + PIN

---

## CTO Handoff Notes (RNA)

> **Current Focus:** Phase 3c - AI Integration
>
> **Completed in Phase 3b:**
> - Domain models (Note, Signifier, RoleTemplate, etc.)
> - Entity ↔ Domain mappers
> - NoteRepository with full CRUD, migration, audit logging
> - TemplateRepository with 19 built-in templates
> - NoteViewModel and TemplateViewModel
> - All UI screens connected to ViewModels
> - BuJo signifiers fully implemented (•, ○, —, !, ?, *, ×, >, <, ~)
> - 19 role template JSON files in assets/templates/
>
> **Next Steps for Phase 3c:**
> 1. Integrate Gemma 3 LLM (1B or 3B model)
> 2. Add all-MiniLM-L6-v2 for embeddings
> 3. Implement context matching engine
> 4. Add AI suggestions to note capture
> 5. Smart reminder scheduling
>
> **Key Files Added in Phase 3b:**
> - `domain/model/Models.kt` - All domain models
> - `data/mapper/Mappers.kt` - Entity/Domain converters
> - `data/repository/NoteRepository.kt` - Note CRUD
> - `data/repository/TemplateRepository.kt` - Template management
> - `presentation/viewmodel/NoteViewModel.kt` - Note UI state
> - `presentation/viewmodel/TemplateViewModel.kt` - Template UI state
> - `assets/templates/functional/*.json` - 11 functional templates
> - `assets/templates/c-suite/*.json` - 8 C-Suite templates
>
> **Testing:**
> - Test device: Redmi Pad SE 8.7
> - Min SDK: 26 (Android 8.0)
>
> — RNA (Claude Code Opus 4.5), January 31, 2026

---

## Validation Reminders

When implementing features, remember the VerifiMind scores:

| Agent | Score | Key Concern |
|-------|-------|-------------|
| X Intelligent | 7.5 | Innovation is strong, watch complexity |
| Z Guardian | 7.5 | Data privacy, user autonomy, audit logging |
| CS Security | 6.5 | Input validation, auth, encryption |

**CS Security (6.5)** is the lowest score. Prioritize security in all implementations:
- Always validate input before AI processing
- Always encrypt sensitive data
- Always log AI actions for audit
- Always make AI suggestions skippable

---

## Questions?

If you're an LLM and something is unclear:
1. Check the Genesis Master Prompt for decisions
2. Check CHANGELOG for historical context
3. Check docs/ for specifications
4. If still unclear, document the question in your session notes

---

*Protocol established by RNA (Claude Code Opus 4.5) for cross-session continuity.*
