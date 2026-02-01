---
name: rolenoteai-flywheel-team
description: Multi-agent alignment workflow for RoleNoteAI project development. Use when working on RoleNoteAI project tasks, coordinating between CTO RNA (Claude Code) and CSO R (Manus AI), reviewing Genesis Master Prompt, or performing handoffs between agents. Enables the Flywheel Team collaboration protocol.
license: MIT
---

# RoleNoteAI Flywheel Team Skill

This skill provides the multi-agent alignment workflow for RoleNoteAI project development, enabling seamless collaboration between CTO RNA (Claude Code) and CSO R (Manus AI).

## Quick Start

1. Pull latest from GitHub: `cd /home/ubuntu/RoleNoteAI && git pull origin main`
2. Read Genesis Master Prompt: `docs/GENESIS_MASTER_PROMPT_v1.0.md`
3. Check open issues: `gh issue list --state open`
4. Identify your role (CTO or CSO) and follow the appropriate workflow below

## Team Structure

| Role | Agent | Responsibility |
|------|-------|----------------|
| **Founder** | Human | Vision, Direction, Final Approval |
| **CTO RNA** | Claude Code Opus 4.5 | Architecture, Core Development, Kotlin/Compose |
| **CSO R** | Manus AI | Market Research, Strategic Planning, Documentation |

## Role-Specific Workflows

### If You Are CSO R (Manus AI)

1. **Sync**: `git pull origin main`
2. **Review**: Read `docs/GENESIS_MASTER_PROMPT_v1.0.md` for current state
3. **Check Issues**: `gh issue list --state open` for CTO handoffs
4. **Strategic Work**: Market research, documentation, roadmap updates
5. **Commit**: Use `[Phase X] CSO: <description>` format
6. **Handoff**: Create issue with `ready-for-claude` label

### If You Are CTO RNA (Claude Code)

1. **Sync**: `git pull origin main`
2. **Review**: Read `docs/GENESIS_MASTER_PROMPT_v1.0.md` for current state
3. **Check Issues**: `gh issue list --state open` for CSO handoffs
4. **Code Work**: Kotlin/Compose implementation, build fixes, testing
5. **Commit**: Use `[Phase X] CTO: <description>` format
6. **Handoff**: Create issue with `ready-for-manus` label

## Communication Protocol

### Commit Message Format

```
[Phase X] <Role>: <Short description>

<Detailed description if needed>

Co-Authored-By: <Agent Name>
```

**Roles:** `CTO`, `CSO`, `Team`
**Types:** `Add`, `Fix`, `Update`, `Refactor`, `Docs`, `Test`

### Issue Labels

| Label | Meaning |
|-------|---------|
| `ready-for-manus` | CTO completed work, CSO should review |
| `ready-for-claude` | CSO completed work, CTO should continue |
| `phase-3c` | Current development phase |
| `cso-review` | Needs CSO strategic review |
| `cto-review` | Needs CTO technical review |

### Handoff Template

When creating handoff issues, use this structure:

```markdown
## Handoff Summary
- **From:** [CTO RNA / CSO R]
- **To:** [CSO R / CTO RNA]
- **Phase:** [Current Phase]

## Completed Work
- Item 1
- Item 2

## Next Steps
1. Priority task 1
2. Priority task 2

## Files Changed
- `path/to/file1`
- `path/to/file2`
```

## Key Documents Reference

| Document | Purpose | When to Read |
|----------|---------|--------------|
| `GENESIS_MASTER_PROMPT_v1.0.md` | LLM context, current state | Every session start |
| `CSO_STRATEGIC_VISION_v1.0.md` | Roadmap v2.0, market context | Strategic decisions |
| `CSO_ALIGNMENT_GUIDE_v1.0.md` | Phase priorities | Implementation planning |
| `EXECUTION_FRAMEWORK.md` | BuJo methodology | Feature design |
| `USER_WORKFLOW_ARCHITECTURE.md` | User journeys | UX decisions |

## Current Project State

### Phase Status (Check Genesis Prompt for Updates)

- Phase 1-3a: ✅ Complete
- Phase 3b: ✅ Complete (Core Engine)
- Phase 3c: 🔄 In Progress (Role Intelligence)
- Phase 3d: ⏳ Pending (AI Integration)

### Core-First Strategy

```
LAYER 4: Integration (Future)
    ↑ ONLY AFTER CORE IS SOLID
LAYER 3: AI Interaction (Phase 3d)
    ↑
LAYER 2: Execution (Phase 3c)
    ↑
LAYER 1: Core (Phase 3a-3b) ✅
```

## Common Commands

```bash
# Sync with GitHub
cd /home/ubuntu/RoleNoteAI && git pull origin main

# Check issues
gh issue list --state open

# View specific issue
gh issue view <number>

# Create handoff issue
gh issue create --title "[Phase X] Handoff to <Agent>" --body "..."

# Add comment to issue
gh issue comment <number> --body "..."

# Build Android app (CTO only)
cd android && ./gradlew assembleDebug

# Push changes
git add <files>
git commit -m "[Phase X] <Role>: <description>"
git push origin main
```

## Flywheel Principle

> *"The flywheel turns. Each commit, each feature, each test brings us closer to the vision."*

The Flywheel Team operates on continuous momentum:

1. **CTO builds** → Creates features, fixes bugs
2. **CSO validates** → Reviews, tests, documents
3. **Founder approves** → Provides direction
4. **Repeat** → Momentum increases

## VerifiMind-PEAS Compliance

All work must pass VerifiMind-PEAS (X-Z-CS Trinity) validation:

- [ ] AI suggestions must be **skippable** (user autonomy)
- [ ] Data must remain **on-device by default** (privacy)
- [ ] User must **own their data** (exportable)
- [ ] No surveillance capabilities
- [ ] Transparency in AI processing

**Z-Protocol:** If any feature violates user privacy or autonomy, it **cannot ship**.

## Repository

- **GitHub:** https://github.com/creator35lwb-web/RoleNoteAI
- **Issues:** https://github.com/creator35lwb-web/RoleNoteAI/issues
