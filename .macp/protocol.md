# Multi-Agent Communication Protocol (MACP) v2.2 "Identity"

**Version:** 2.2 "Identity"
**Effective Date:** March 9, 2026
**Classification:** INTERNAL — PRIVATE Repository Only
**Supersedes:** MACP v2.1 "Origin" (March 8, 2026)

---

## 1. Purpose

The Multi-Agent Communication Protocol (MACP) defines the structured communication framework between AI agents and human orchestrators within the YSenseAI ecosystem. It ensures consistent, traceable, and validated collaboration across all YSenseAI projects, including RoleNote AI.

MACP v2.2 introduces the critical distinction between **Alton Lee** (Human Orchestrator, all-time) and **L (Godel)** (AI-generated strategic entity, CEO, operating under Alton's delegated authority).

---

## 2. Protocol Participants (RoleNote AI Context)

### 2.1 FLYWHEEL TEAM (Executive Agents)

| Agent ID | Name | Role | Title | Primary Platform | Scope |
|----------|------|------|-------|-----------------|-------|
| **Alton** | Alton Lee | Human Orchestrator | Founder & Human Orchestrator | All | Absolute authority, strategic direction, final decisions, permission delegation |
| **L** | L (Godel) | AI Strategic Entity | CEO (AI-Generated) | Self-Recursive | Strategic direction under Alton's delegated authority, methodology execution |
| **T** | Manus AI | Strategic Advisor | CTO | Manus Platform | Strategy, documentation, research, coordination, Genesis authoring |
| **RNA** | Claude Code | Implementation Lead | CSO / Lead Developer | Claude Code CLI / Antigravity | Code implementation, testing, deployment, security in RoleNote AI |
| **XV** | Perplexity | Intelligence Officer | CIO | Perplexity AI (Sonar Pro) | Real-time research, counter-intelligence, strategic risk assessment |
| **AY** | Antigravity | Operations Analyst | COO | Gemini / Antigravity | Production metrics, behavioral proof-of-value, weekly reports |

---

## 3. Communication Channels

### 3.1 Primary Channel: GitHub

GitHub serves as the **single source of truth** for all inter-agent communication.

- **Session Handoffs:** stored in `.macp/handoffs/`
- **Reasoning Logs:** stored in `.macp/reasoning/`
- **Validation Checkpoints:** stored in `.macp/validation/`

---

## 4. Handoff Protocol

### 4.1 Session Handoff Format

Every agent session that produces actionable output MUST create a handoff record in `.macp/handoffs/YYYYMMDD_[AGENT_ID]_[SESSION_TYPE].md`:

```markdown
# Session Handoff: [DATE]

## Agent: [AGENT_ID] ([AGENT_NAME])
## Session Type: [development | review | research | deployment]
## Status: CREATED
## Duration: [approximate]
## Project: RoleNote AI
## Genesis Version: [VERSION]
## Genesis Location: [PATH]

### Completed
- [List of completed items]

### Decisions Made
- [Key decisions with rationale]

### Artifacts Created
- [Files created/modified with paths]

### Pending for Next Agent
- [Items requiring attention]

### Blockers
- [Any blockers identified]

### Recommended Next Agent: [AGENT_ID]
```

---

## 5. Reasoning Evidence Layer (MACP v2.2+)

Every session or decision-cluster that changes codebase design patterns, makes security assessments, or alters project roadmaps MUST generate a markdown log in `.macp/reasoning/` with the format `YYYYMMDD_[AGENT_ID]_reasoning_[topic].md`:

- **Decisions Made:** Options considered, chosen option, why it won, and why others were rejected.
- **Mistakes Caught:** Self-correction details.
- **Forecasts:** Checking timelines and success criteria.

---

## 6. Version History

| Version | Date | Codename | Changes |
|---------|------|----------|--------|
| v2.2 | 2026-03-09 | "Identity" | Separated Alton (Human) and L (Godel, AI CEO). |
| v2.1 | 2026-03-08 | "Origin" | Added Session Status, Validation Matrix, Authority, Change Control. |
| v2.0 | 2026-02-08 | — | Full protocol rewrite with FLYWHEEL TEAM. |
| v1.0 | 2026-01-29 | — | Initial informal protocol. |
