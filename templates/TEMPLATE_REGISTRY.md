# RoleNote AI Template Registry

**Total Templates:** 16
**Last Updated:** January 31, 2026

---

## Template Categories

### Functional Roles (9 templates)
Department-level roles for day-to-day professionals.

| ID | Name | Focus Areas |
|----|------|-------------|
| `project-manager` | Project Manager | Deadlines, decisions, blockers, action items |
| `developer` | Developer | Technical specs, code refs, implementation |
| `accounting` | Accounting | Transactions, compliance, reporting deadlines |
| `marketing` | Marketing | Campaigns, content, metrics, audience |
| `human-resources` | Human Resources | Recruitment, policies, employee relations |
| `business-administration` | Business Administration | Operations, vendors, processes |
| `technical-backend` | Technical - Backend | APIs, databases, server infrastructure |
| `technical-frontend` | Technical - Frontend | UI/UX, components, state management |
| `customer-services` | Customer Services | Tickets, resolutions, customer feedback |
| `financial-advisor` | Financial Advisor | Portfolios, market insights, compliance |
| `compliance-feedback` | Compliance & Feedback | Regulations, audits, policy adherence |

### C-Suite Roles (7 templates)
Executive leadership roles for strategic decision-makers.

| ID | Name | Focus Areas |
|----|------|-------------|
| `executive` | Executive (General) | Strategic alignment, high-level decisions |
| `ceo` | Chief Executive Officer | Vision, stakeholders, organizational leadership |
| `coo` | Chief Operations Officer | Operational excellence, cross-functional coordination |
| `cto` | Chief Technology Officer | Tech strategy, architecture, engineering leadership |
| `cfo` | Chief Financial Officer | Financial strategy, capital allocation, governance |
| `cino` | Chief Innovation Officer | Emerging tech, R&D, disruption opportunities |
| `cmo-monitor` | Chief Monitoring Officer | Risk oversight, compliance, control effectiveness |
| `cro` | Chief Research Officer | Research strategy, scientific discovery, knowledge |

---

## Template Structure

Each template includes:

```json
{
  "id": "unique-identifier",
  "name": "Display Name",
  "version": "1.0",
  "description": "Role description",
  "icon": "SF Symbol name",
  "color": "#HexColor",
  "category": "functional | c-suite",

  "capturePrompts": [
    {
      "field": "field_id",
      "label": "Display Label",
      "prompt": "Question to guide user",
      "placeholder": "Example input",
      "required": true | false
    }
  ],

  "suggestionRules": {
    "extractDates": true | false,
    "extractPeople": true | false,
    "extractTasks": true | false,
    "priorityKeywords": ["keyword1", "keyword2"],
    "calendarSuggestions": true | false,
    "taskSuggestions": true | false,
    "delegationSuggestions": true | false
  },

  "llmSystemPrompt": "Role-specific instructions for AI",

  "contextMatching": {
    "prioritizeProjects": true | false,
    "prioritizeMeetings": true | false,
    "lookbackDays": 7-90,
    "crossProjectVisibility": true | false
  },

  "execution": {
    "signifiersEnabled": true,
    "defaultSignifier": "task | note | event",
    "migrationPromptDays": [3, 7, 14],
    "weeklyReview": true | false,
    "monthlyReview": true | false,
    "autoThreading": true | false,
    "staleTaskThresholdDays": 3-14
  }
}
```

See `docs/EXECUTION_FRAMEWORK.md` for the BuJo-inspired execution system.

---

## Adding Custom Templates

### Option 1: Import Existing Template

Import your familiar templates from other tools:

1. **Excel/CSV:** Upload `.xlsx`, `.xls`, or `.csv` files
2. **Google Sheets:** Connect via OAuth and select sheet
3. **Notion/Airtable:** (v1.1) Import databases directly

The import wizard will:
- Auto-detect column types (dates, currency, text, etc.)
- Suggest matching RoleNote AI role templates
- Let you map fields and add AI enhancements

See `docs/TEMPLATE_IMPORT_SYSTEM.md` for full documentation.

### Option 2: Create from Scratch

1. Create a JSON file in `templates/custom/`
2. Follow the schema above
3. Use unique `id` (alphanumeric + hyphens)
4. Select an SF Symbol for `icon`
5. Choose a hex color for visual identification

---

## Template Hierarchy

```
templates/
├── functional/              (9 built-in role templates)
│   ├── project-manager.json
│   ├── developer.json
│   ├── accounting.json
│   ├── marketing.json
│   ├── human-resources.json
│   ├── business-administration.json
│   ├── technical-backend.json
│   ├── technical-frontend.json
│   ├── customer-services.json
│   ├── financial-advisor.json
│   └── compliance-feedback.json
├── c-suite/                 (7 built-in executive templates)
│   ├── executive.json
│   ├── ceo.json
│   ├── coo.json
│   ├── cto.json
│   ├── cfo.json
│   ├── cino.json
│   ├── cmo-monitor.json
│   └── cro.json
├── custom/                  (user-created from scratch)
│   └── [user templates].json
├── imported/                (imported from external sources)
│   ├── [excel-imports].json
│   ├── [sheets-imports].json
│   └── [notion-imports].json
└── TEMPLATE_REGISTRY.md
```
