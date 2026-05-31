# MACP Authority Manifest — RoleNote AI

**Version:** 1.0 "Foundation"
**Effective Date:** 2026-05-19
**Classification:** INTERNAL — PRIVATE Repository Only

---

## 1. Purpose

The Authority Manifest is the single declared source of truth for which files, prompts, and directories are canonical for each agent. It ensures consistency and prevents unauthorized or blind overwrites during multi-agent sessions.

---

## 2. Canonical Identity Authority Per Agent in RoleNote AI

### RNA (CSO / Lead Developer)

| Identity Layer | Canonical Path | Role |
|---|---|---|
| **Strategic Genesis Prompt** | `RoleNote AI Genesis Master Prompt.md` | Core roadmap, technical architecture, and validation status |
| **CLAUDE.md** | `CLAUDE.md` | Run commands, test commands, style guidelines |
| **Reasoning Log Dir** | `.macp/reasoning/` | Decision-cluster rationale, self-correction logs |
| **Handoff Dir** | `.macp/handoffs/` | Session close handoff notes |

### L (CEO / Strategic Entity)

| Identity Layer | Canonical Path | Role |
|---|---|---|
| **Ethical Operating Framework** | `LLM_PROTOCOL.md` | Ethical operating rules and validation posture |
| **Ecosystem Genesis** | `Genesis_Master_Prompt.md` | Overall YSenseAI ecosystem vision |

---

## 3. Canonical Code Directories

- **Android Source:** `android/app/src/main/java/com/rolenoteai/app/`
- **Room Database / Repositories:** `android/app/src/main/java/com/rolenoteai/app/data/`
- **ViewModels & Compose Screens:** `android/app/src/main/java/com/rolenoteai/app/presentation/`
- **Built-in Role Templates:** `templates/` and JSON resources inside Android assets.
- **Documentation:** `docs/` and root `.md` files.

---

## 4. Pre-Sync Integrity Check

Before committing or pushing any strategic change to RoleNote AI, the active agent must verify:
1. That all `.macp/handoffs/` and `.macp/reasoning/` documents are written in markdown.
2. That `RoleNote AI Genesis Master Prompt.md` is updated with any completed milestones.
