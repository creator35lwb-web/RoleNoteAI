# Claude Code Instructions - RoleNote AI

**Project:** RoleNote AI (Smart AI Note Planner)
**Repository:** creator35lwb-web/RoleNoteAI (PUBLIC)
**Command Central Hub:** creator35lwb-web/verifimind-genesis-mcp

---

## MACP Integration

This project is coordinated via Command Central Hub (verifimind-genesis-mcp).

### Session Start: Check MACP Inbox

At the start of every session, check for pending tasks:

Use the `macp_read_messages` MCP tool with:
- repository: `creator35lwb-web/verifimind-genesis-mcp`
- filters.to: `RNA`
- limit: 5

Or run `/macp-inbox`.

### Session End: Create Handoff

Use the `macp_create_handoff` MCP tool with:
- repository: `creator35lwb-web/verifimind-genesis-mcp`
- agent: `RNA`
- session_type: `development`
- All required fields (completed, decisions, artifacts, pending, blockers, next_agent)

---

## Session Start Checklist

When starting a new session, ALWAYS:

1. [ ] Read this CLAUDE.md file
2. [ ] **Check MACP inbox** for pending tasks
3. [ ] Check README.md for project overview
4. [ ] Check CHANGELOG.md for recent changes
5. [ ] Review recent git log for latest state

---

## Project Overview

RoleNote AI is a Smart AI Note Planner with Role-Based Templates and Automatic Context Matching. It solves the "note graveyard" problem with role-based intelligence.

### The 5 Pillars

1. **TEMPLATE** - Role-based context (16 built-in + custom)
2. **OVERVIEW** - Dashboard (Today/Week/Projects)
3. **AUTO REMINDER** - Smart scheduling + migration prompts
4. **AI CHAT-INTERACT** - Natural language assistant
5. **AUDIO FILE** - Voice capture + transcription

### Key Technologies

- Android (Kotlin/Java)
- VerifiMind-PEAS validation (Trinity scores: X=7.5, Z=7.5, CS=6.5)

### Key Directories

| Directory | Purpose |
|-----------|---------|
| `android/` | Android application source |
| `templates/` | Role-based note templates |
| `docs/` | Documentation and specs |
| `iteration/` | Development iteration records |

---

## Development Workflow

```
1. Check MACP inbox for tasks
2. Implement changes locally
3. Test on Android device/emulator
4. Commit with descriptive message
5. Push to origin/main
6. Create handoff record via macp_create_handoff
```

---

## Important Notes

- This is a PUBLIC repository
- Never commit API keys, tokens, or credentials
- Validated by VerifiMind-PEAS Trinity
- Coordinate with VerifiMind-PEAS for validation updates
- Genesis Master Prompt v3.0 covers this project

---

**Protocol:** MACP v2.0 | FLYWHEEL Level 2
