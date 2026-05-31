# Reasoning Log: 2026-05-19

## Agent: RNA (Claude Code / Antigravity)
## Topic: Clean Architecture Decoupling, Schema Validation, and MACP Protocol Upgrade

---

### 1. Trigger
The codebase had tight coupling between presentation ViewModels and concrete repository implementations, lacked deep verification for custom template JSON inputs, and required alignment with the YSenseAI MACP v2.2 "Identity" multi-agent coordination protocol.

---

### 2. Options Considered

#### Option A: Direct Implementation / Minimum Change
- Do not decouple ViewModels.
- Keep basic `try-catch` JSON syntax validation in `InputValidator`.
- Keep the old MACP JSON structure.

#### Option B: Clean Architecture and Git-Native MACP (Chosen)
- Define `INoteRepository` and `ITemplateRepository` interfaces in a new `domain.repository` package.
- Move concrete classes to `data.repository` and inject interfaces in ViewModels via Hilt DI.
- Upgrade `InputValidator.validateTemplateJson` to use deep schema validation checking exact field existence, primitive/array types, non-blank constraints, and boolean flags.
- Create directories `.macp/agents/`, `.macp/handoffs/`, `.macp/validation/`, `.macp/reasoning/`.
- Establish `protocol.md`, `flywheel-team.md`, `authority-manifest.md`, `genesis-registry.md` and migrate old logs to Git-native markdown formats.

---

### 3. Chosen Approach & Rationale

**Option B** won because:
1. **Separation of Concerns:** Clean Architecture ensures UI screens are decoupled from data storage logic, simplifying mock testing and future integrations (e.g., local ONNX search/FAISS).
2. **Security & Robustness:** Custom templates loaded from external files could inject malicious inputs or crash the app. The deep schema validator ensures structural integrity before parsing.
3. **Multi-Agent Alignment:** Adopting MACP v2.2 Git-native structures makes RoleNote AI compatible with the broader YSenseAI agent flywheel and establishes clear auditing via `.macp/reasoning/`.

---

### 4. Mistakes Caught Mid-Flight
During Room database and repository migration, we identified that `TemplateRepository` defined internal helper classes (such as `FullTemplateConfig`) which ViewModels were directly accessing. If we decoupled, we had to move `FullTemplateConfig` and its child data structures to the `ITemplateRepository` interface level so they are visible to both the presenter and the data layer without leaking concrete repository details. We resolved this by refactoring these structures into the interface file.

---

### 5. Forecast & Confidence

- **Forecast:** Phase 3c (Gemma 3/ONNX/FAISS integration) will proceed with zero DI compilation errors and high stability because the repository layers are isolated.
- **Verification Date:** 2026-05-26
- **Confidence Score:** 95%
