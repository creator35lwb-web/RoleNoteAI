# RoleNote AI

**A Smart AI Note Planner with Role-Based Templates and Automatic Context Matching**

<div align="center">
  <img src="docs/assets/branding/RoleNoteAI-Icon.png" alt="RoleNote AI" width="200"/>
</div>

---

## 🌟 What is RoleNote AI?

RoleNote AI is a revolutionary open-source smart note planner that understands your professional role, captures your ideas anywhere and anytime, automatically connects them to your existing plans and schedules, and proactively suggests your next steps. It's designed to transform your scattered thoughts into executed outcomes.

### The Core Insight: Different Roles, Different Notes

The fundamental innovation of RoleNote AI is the understanding that **the same information is perceived differently by different roles**. In a single meeting, a Project Manager captures deadlines, a Developer notes technical specs, and an Executive focuses on strategic alignment. RoleNote AI is the first platform built around this crucial insight.

---

## 💡 Why RoleNote AI?

Traditional note-taking apps are passive digital notebooks. They store information but don't understand it. RoleNote AI is an active partner in your productivity, intelligently organizing your thoughts and connecting them to your workflow.

### The Problem

- **Note Graveyards:** Ideas are captured but never revisited or acted upon.
- **Context Collapse:** Notes are disconnected from the projects, meetings, and goals they relate to.
- **One-Size-Fits-All:** Generic tools don't adapt to the specific needs of your professional role.
- **Manual Overload:** You spend more time organizing your notes than acting on them.

### The Solution

RoleNote AI provides an intelligent, context-aware system that:

1.  **Understands Your Role:** Uses role-based templates to guide your focus and capture what matters most to you.
2.  **Captures Ideas Effortlessly:** Lets you save thoughts via text, voice, or photo, wherever you are.
3.  **Connects the Dots Automatically:** Links new notes to relevant projects, calendar events, and existing plans.
4.  **Suggests Your Next Move:** Proactively recommends next steps, turning ideas into action items.

---

## 🏗️ Architecture Overview

RoleNote AI is built on a modular, privacy-first architecture that combines on-device AI with powerful semantic matching and scheduling capabilities.

<p align="center">
  <img src="docs/assets/diagrams/RoleNoteAI-Architecture.png" alt="RoleNote AI Architecture" width="800"/>
</p>

### Key Components

1.  **Multi-Modal Input:** Capture ideas via text, voice, or images.
2.  **Role Template Engine:** Provides customizable templates based on job roles (e.g., PM, Dev, CEO).
3.  **On-Device AI Core:**
    -   **LLM (e.g., Gemma 3):** For natural language understanding and suggestion generation.
    -   **Embedding Model (e.g., all-MiniLM-L6-v2):** For powerful semantic search and context matching.
4.  **Context Matching Engine:** The core of the system, which links new notes to your existing knowledge graph of projects, events, and plans.
5.  **Smart Scheduling & Reminder System:** Automatically creates calendar events and triggers proactive reminders.
6.  **VerifiMind-PEAS Safety Layer:** Ensures all AI-driven suggestions and actions are ethical and secure.

---

## 🛡️ Security and Ethics: The VerifiMind-PEAS Guarantee

RoleNote AI is validated by the **VerifiMind-PEAS X-Z-CS RefleXion Trinity**, ensuring it is innovative, ethical, and secure. Our commitment is to build a tool that empowers users without compromising their privacy or autonomy.

- **X-Agent (Innovation):** Validated the role-based intelligence as a paradigm shift in productivity.
- **Z-Agent (Ethics):** Ensured the design respects user data ownership and autonomy.
- **CS-Agent (Security):** Confirmed that risks are manageable with standard security practices like end-to-end encryption.

---

## 🚀 Getting Started

*(RoleNote AI is currently in the architectural design phase. The following is a forward-looking statement of intent.)*

### Installation

```bash
# Download from the Google Play Store or App Store (when available)
pkg install rolenote-ai
```

### Example Workflow

1.  **Select Your Role:** `Project Manager`
2.  **Capture a Note:** "Meeting with the design team tomorrow at 10am to finalize the Q3 launch features. Need to confirm the marketing budget and get a decision on the new logo."
3.  **RoleNote AI Automatically:**
    -   Creates a calendar event: "Meeting: Q3 Launch Features" for tomorrow at 10am.
    -   Adds two tasks to your to-do list: "Confirm marketing budget" and "Get decision on new logo."
    -   Links this note to your existing "Q3 Product Launch" project plan.
    -   Reminds you of the unconfirmed marketing budget when you open the project plan later.

---

## 🤝 Contributing

RoleNote AI is an open-source project, and we welcome contributions from the community. Please read our [CONTRIBUTING.md](CONTRIBUTING.md) to learn how you can get involved.

---

## 📜 License

RoleNote AI is licensed under the **MIT License**.
