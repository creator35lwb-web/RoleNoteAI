# Reasoning Log: 2026-05-20

## Agents: L (Godel - CEO) & RNA (CTO)
## Topic: Porting GodelAI-Lite Memory and Drift Governance to RoleNote AI Android Stack

---

### 1. Trigger
The user requested an analysis of the `godelai-lite` repository (https://github.com/creator35lwb-web/godelai-lite) to evaluate how its core paradigms—**MemPalace-Lite v2** (episodic memory), **MACP-Lite** (reasoning continuity), and **GIFP-Lite v2** (identity drift governance)—can enhance RoleNote AI.

---

### 2. Strategic Vision (L / CEO)
The core insight of GodelAI-Lite is that **"Memory is a protocol, not a model property."**
This aligns perfectly with RoleNote AI's product mission:
1. **Memory Portability:** A user's role-play notes and character interactions should exist independently of the underlying LLM weights. If a user switches from local Gemma 3 (mobile) to a cloud API (e.g. Gemini 2.5), the character's memory context should remain intact via a standardized state.
2. **SLM Mitigation:** Mobile devices run highly constrained Small Language Models (SLMs) like Gemma 3 (2B/8B). These models naturally suffer from rapid context window saturation, forgetting, and out-of-character drift. Using inference-time memory and governance is the only way to achieve high-fidelity role-play on consumer mobile hardware.

---

### 3. Technical Mapping (RNA / CTO)
We map the three pillars of GodelAI-Lite to RoleNote AI's upcoming Phase 3c (Android AI + FAISS + ONNX) architecture:

```
                  ┌──────────────────────────────────────────┐
                  │          ROLENOTE AI ENGINE (Phase 3c)   │
                  └────────────────────┬─────────────────────┘
                                       │
                                       ▼
      1. MemPalace-Android ────────────┼──► ONNX Embeddings + FAISS Search
                                       │    (Semantic Similarity × Temporal Decay)
                                       ▼
      2. Prompt Compiler ──────────────┼──► MACP-Lite Kotlin Envelope
                                       │    [CONTEXT] [ROLE] [TASK] [REASONING]
                                       ▼
      3. Persona Monitor ──────────────┼──► Local Cosine Similarity Check
                                       │    (Trigger Gemma Rewrite on >0.35 drift)
                                       ▼
                                [App UI Output]
```

#### A. MemPalace-Lite v2 ──► MemPalace-Android
- **Lite Version:** Used TF-IDF and JSON files.
- **Android Port:** We will store episodic memories and extracted facts in the local Room SQLite database (with metadata like `timestamp`, `session_id`, and `use_count`). For retrieval, we use **ONNX (MiniLM)** to generate query embeddings and **FAISS-Android** to perform semantic similarity searches.
- **Scoring Function:** We will implement the temporal decay function in Kotlin:
  $$\text{score}(m) = \text{semantic\_similarity}(m, q) \times \exp(-\lambda \times \text{age\_steps})$$

#### B. MACP-Lite ──► RoleNote Prompt Compiler
- **Lite Version:** Used markdown envelopes.
- **Android Port:** The Kotlin prompt building pipeline will format context retrieved from FAISS, the character card description (`ROLE`), the user's input (`TASK`), and the conversation history into a structured envelope matching MACP-Lite guidelines. This prevents Gemma 3 from getting confused by multi-turn noise.

#### C. GIFP-Lite v2 ──► Persona Drift Monitor
- **Lite Version:** TF-IDF cosine drift checks + HuggingFace model pass.
- **Android Port:** Mobile SLMs (e.g. Gemma 3 2B) frequently slip out of character. We will implement a `PersonaDriftMonitor` class:
  - Generate the embedding of the AI's generated response using the local MiniLM ONNX model.
  - Calculate the cosine distance against the embedding of the character's core persona definition.
  - If drift exceeds a threshold (e.g., $> 0.35$), the engine rejects the raw output and runs a fast, low-temperature refinement pass (e.g., prompt Gemma: *"The following response is out-of-character. Rewrite it to match [Persona]."*), returning only the refined output.

---

### 4. Chosen Integration Strategy
We will officially incorporate these concepts into the **Phase 3c Execution Plan**. Instead of a naive vector DB wrapper, we will design RoleNote AI's local engine around a unified **Inference-Time Memory and Governance Pipeline (MemPalace + GIFP)** optimized for Android.

---

### 5. Confidence & Verification
- **Confidence Score:** 90% (We have native ONNX and FAISS bindings available for Android, making local vector operations and similarity comparisons highly performant).
- **Verification Plan:** Build a mock JUnit test validating the Kotlin implementation of the scoring decay and the drift detection logic using a mock embedding matrix.
