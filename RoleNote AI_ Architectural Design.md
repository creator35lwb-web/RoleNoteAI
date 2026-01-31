# RoleNote AI: Architectural Design

**Version:** 1.0
**Date:** January 6, 2026
**Author:** Manus AI

---

## 1. Introduction

This document outlines the architectural design for **RoleNote AI**, a smart AI-powered note planner that understands user roles, automatically matches notes to schedules and plans, and provides proactive reminders and next-step suggestions. The architecture is designed to be privacy-preserving, extensible, and highly personalized.

### 1.1. Goals and Objectives

The primary goals of this architecture are to:

-   **Enable Role-Based Intelligence:** Build a system that understands and adapts to the user's professional context.
-   **Automate Contextual Linking:** Seamlessly connect new notes to the user's existing knowledge graph of projects, events, and plans.
-   **Ensure User Privacy:** Prioritize on-device processing and user-controlled data ownership.
-   **Provide Proactive Assistance:** Move beyond passive note storage to active, intelligent suggestion and reminder generation.
-   **Support Multi-Modal Input:** Allow users to capture ideas in the most natural way, whether through text, voice, or images.

### 1.2. Architectural Principles

The design of RoleNote AI is guided by the following principles:

-   **Privacy-First:** On-device processing is the default. Cloud sync is an optional, end-to-end encrypted feature.
-   **User in Control:** AI suggestions are aids, not directives. Users can always override, disable, or customize AI features.
-   **Modularity:** Components are designed to be independent and swappable, allowing for future upgrades (e.g., new AI models).
-   **High-Performance:** The system must be fast and responsive, especially for the core tasks of note capture and context matching.

---

## 2. High-Level Architecture

RoleNote AI is a multi-layered system that ingests user notes, enriches them with role-based context, processes them through an AI core for semantic understanding, and links them to a user-specific knowledge graph.

<p align="center">
  <img src="assets/diagrams/RoleNoteAI-Architecture.png" alt="RoleNote AI Architecture" width="800"/>
</p>

### 2.1. Component Overview

| Component | Description | Technology Stack |
|---|---|---|
| **Multi-Modal Input Layer** | Captures notes via text, voice (with on-device speech-to-text), and images (with OCR). | Android/iOS native UI, on-device speech recognition, OCR libraries |
| **Role Template Engine** | Manages and applies customizable templates based on user's selected role. | JSON or YAML for template definitions |
| **On-Device AI Core** | The brain of the system, responsible for understanding and processing notes. | - **LLM (e.g., Gemma 3):** For reasoning, summarization, and suggestion generation.<br>- **Embedding Model (e.g., all-MiniLM-L6-v2):** For semantic search and context matching. |
| **Context Matching Engine** | Links new notes to existing entities in the user's knowledge graph. | Vector database (e.g., FAISS, Weaviate embedded) for semantic search |
| **User Knowledge Graph** | A local database storing the user's notes, projects, events, contacts, and their relationships. | SQLite or a lightweight graph database |
| **Smart Scheduling & Reminder System** | Creates calendar events, tasks, and proactive reminders. | Integration with native calendar and reminder APIs |
| **VerifiMind-PEAS Safety Layer** | Ensures all AI-driven suggestions and actions are ethical and secure. | Custom validation rules and ethical guardrails |

---

## 3. Detailed Component Design

### 3.1. Multi-Modal Input Layer

This layer is responsible for capturing user input in various forms and converting it into a standardized text format. It must be fast and reliable to support the "capture anywhere, anytime" principle.

### 3.2. Role Template Engine

This engine manages a library of role-based templates. A template is a structured set of prompts and fields designed to guide a user's note-taking based on their professional context.

**Example Role Template (Project Manager):**

```json
{
  "role": "Project Manager",
  "template": [
    {"field": "Key Decisions", "prompt": "What were the key decisions made?"},
    {"field": "Action Items", "prompt": "What are the specific next steps?"},
    {"field": "Blockers", "prompt": "What is preventing progress?"},
    {"field": "Timeline Impact", "prompt": "How does this affect the project timeline?"}
  ]
}
```

When a user creates a note with this role active, the AI will use these prompts to structure the note and guide the user's attention.

### 3.3. On-Device AI Core

This is the heart of the system, consisting of two main AI models:

-   **Large Language Model (LLM):** The LLM is used for higher-level reasoning tasks, such as summarizing notes, generating next-step suggestions, and understanding complex user queries.
-   **Embedding Model:** This smaller, highly optimized model converts notes and other text into numerical vectors. These vectors are used by the Context Matching Engine to find semantically similar items.

### 3.4. Context Matching Engine

This engine is the core innovation of RoleNote AI. It performs the following steps:

1.  **Vectorize New Note:** The new note is converted into a vector using the embedding model.
2.  **Semantic Search:** The engine performs a search in the vector database to find the most similar existing notes, projects, and events.
3.  **Entity Extraction:** The LLM extracts key entities (people, dates, project names) from the new note.
4.  **Link Generation:** Based on semantic similarity and shared entities, the engine proposes links between the new note and existing items in the User Knowledge Graph.

### 3.5. User Knowledge Graph

This is a local database that stores all of the user's data in a structured way. It is designed to be a comprehensive model of the user's professional life, including:

-   **Notes:** The raw content of all captured ideas.
-   **Projects:** High-level initiatives with goals and deadlines.
-   **Events:** Calendar appointments and meetings.
-   **Contacts:** People and their roles.
-   **Relationships:** The links between all of these entities.

### 3.6. Smart Scheduling & Reminder System

This system acts on the output of the AI Core. When the LLM identifies a date, a task, or a commitment in a note, this system will:

-   **Propose Calendar Events:** Suggest creating a new event in the user's calendar.
-   **Create Tasks:** Add new items to the user's to-do list.
-   **Set Proactive Reminders:** If a new note is linked to an upcoming deadline, the system will create a reminder.

---

## 4. Data Flow and Interactions

The following diagram illustrates the data flow from note capture to action execution:

<p align="center">
  <img src="assets/diagrams/RoleNoteAI-Dataflow.png" alt="RoleNote AI Data Flow" width="800"/>
</p>

1.  The user captures a note via text, voice, or image.
2.  The **Role Template Engine** applies the user's current role context.
3.  The **On-Device AI Core** processes the note, generating a vector embedding and extracting entities.
4.  The **Context Matching Engine** searches the **User Knowledge Graph** for related items.
5.  The **LLM** generates suggestions (e.g., new tasks, calendar events, next steps).
6.  The **Smart Scheduling & Reminder System** acts on these suggestions, with user confirmation.

---

## 5. Technology Stack Summary

-   **Frontend:** Native Mobile (SwiftUI for iOS, Jetpack Compose for Android)
-   **On-Device AI:** Google AI Edge, MediaPipe, Gemma, Sentence Transformers
-   **Database:** SQLite with vector extension, or embedded Weaviate
-   **Sync (Optional):** End-to-end encrypted sync via a cloud backend (e.g., Firebase with user-controlled keys).

---

## 6. Future Work

-   **Multi-Perspective Meeting Synthesis:** When multiple users are in the same meeting, synthesize their role-based notes into a single, comprehensive summary.
-   **Team Knowledge Graph:** Extend the knowledge graph to support teams, allowing for shared context and collaboration.
-   **Predictive Scheduling:** Use historical data to predict how long tasks will take and proactively block time in the user's calendar.
