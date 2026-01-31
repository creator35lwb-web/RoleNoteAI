# Template Import & Integration System

**Version:** 1.0
**Purpose:** Allow users to import existing templates from Excel, Google Sheets, Notion, and other tools they've used for years.

---

## Philosophy

> "The best productivity system is the one you'll actually use."

Users have invested years building workflows around their existing templates. RoleNote AI should **enhance** these workflows, not replace them. We meet users where they are.

---

## Supported Import Sources

### Phase 1 (MVP)
| Source | File Types | Status |
|--------|------------|--------|
| Microsoft Excel | `.xlsx`, `.xls`, `.csv` | Priority |
| Google Sheets | Direct API import | Priority |
| CSV/TSV | `.csv`, `.tsv` | Priority |

### Phase 2
| Source | File Types | Status |
|--------|------------|--------|
| Notion | Export + API | Planned |
| Airtable | API integration | Planned |
| Apple Numbers | `.numbers` | Planned |
| JSON/YAML | `.json`, `.yaml` | Planned |

### Phase 3
| Source | File Types | Status |
|--------|------------|--------|
| Obsidian | Markdown + frontmatter | Planned |
| Roam Research | JSON export | Planned |
| Evernote | `.enex` | Planned |
| OneNote | API | Planned |

---

## Import Workflow

### Step 1: Upload Template

```
┌─────────────────────────────────────────────────┐
│  Import Your Template                           │
│                                                 │
│  ┌─────────────────────────────────────────┐   │
│  │                                         │   │
│  │     Drag & drop your file here         │   │
│  │     or click to browse                  │   │
│  │                                         │   │
│  │     Supports: Excel, CSV, Google Sheets │   │
│  └─────────────────────────────────────────┘   │
│                                                 │
│  Or connect:                                    │
│  [Google Sheets]  [Notion]  [Airtable]         │
└─────────────────────────────────────────────────┘
```

### Step 2: AI Template Analysis

RoleNote AI analyzes the uploaded file:

```
┌─────────────────────────────────────────────────┐
│  Analyzing: "Monthly_Budget_Tracker.xlsx"       │
│                                                 │
│  ✓ Detected 12 columns                         │
│  ✓ Found 3 sheets                              │
│  ✓ Identified date patterns                    │
│  ✓ Recognized currency formatting              │
│                                                 │
│  Suggested Role Match: Accounting (87%)         │
│  Alternative: Financial Advisor (72%)           │
│                                                 │
│  [Continue with Mapping →]                      │
└─────────────────────────────────────────────────┘
```

### Step 3: Field Mapping

Map user's columns to RoleNote AI fields:

```
┌─────────────────────────────────────────────────┐
│  Map Your Fields                                │
│                                                 │
│  Your Column          →  RoleNote AI Field     │
│  ─────────────────────────────────────────────  │
│  "Transaction Date"   →  [Date Field ▼]        │
│  "Description"        →  [Note Content ▼]      │
│  "Amount"             →  [Custom: Amount ▼]    │
│  "Category"           →  [Tag ▼]               │
│  "Status"             →  [Task Status ▼]       │
│  "Notes"              →  [Note Content ▼]      │
│                                                 │
│  ○ AI Auto-Mapped (editable)                   │
│                                                 │
│  [+ Add Custom Field]                          │
│                                                 │
│  [← Back]                    [Preview →]       │
└─────────────────────────────────────────────────┘
```

### Step 4: Enhance with AI

Choose which RoleNote AI features to add:

```
┌─────────────────────────────────────────────────┐
│  Enhance Your Template                          │
│                                                 │
│  Keep your familiar structure, add intelligence │
│                                                 │
│  [✓] Automatic context matching                │
│      Link entries to related notes & projects   │
│                                                 │
│  [✓] Smart suggestions                         │
│      AI suggests next actions based on entries  │
│                                                 │
│  [✓] Signifiers & Migration                    │
│      Track task status with BuJo-style bullets  │
│                                                 │
│  [ ] Calendar integration                       │
│      Auto-create events from date fields        │
│                                                 │
│  [✓] Weekly/Monthly summaries                  │
│      AI-generated insights from your data       │
│                                                 │
│  [Create Template →]                           │
└─────────────────────────────────────────────────┘
```

### Step 5: Preview & Confirm

```
┌─────────────────────────────────────────────────┐
│  Your Custom Template: "Monthly Budget"         │
│                                                 │
│  Based on: Accounting template                  │
│  Source: Monthly_Budget_Tracker.xlsx            │
│                                                 │
│  Fields:                                        │
│  ├─ Transaction Date (date)                    │
│  ├─ Description (text, primary)                │
│  ├─ Amount (currency)                          │
│  ├─ Category (tag)                             │
│  ├─ Status (signifier)                         │
│  └─ Notes (text)                               │
│                                                 │
│  AI Features: Context, Suggestions, Migration   │
│                                                 │
│  [← Edit]                    [Save Template]   │
└─────────────────────────────────────────────────┘
```

---

## Template Schema for Custom Imports

```json
{
  "id": "user-monthly-budget",
  "name": "Monthly Budget",
  "version": "1.0",
  "type": "imported",
  "source": {
    "type": "excel",
    "filename": "Monthly_Budget_Tracker.xlsx",
    "importedAt": "2026-01-31T10:00:00Z"
  },
  "baseTemplate": "accounting",
  "category": "custom",
  "icon": "dollarsign.circle",
  "color": "#059669",

  "fields": [
    {
      "id": "transaction_date",
      "label": "Transaction Date",
      "type": "date",
      "sourceColumn": "A",
      "required": true
    },
    {
      "id": "description",
      "label": "Description",
      "type": "text",
      "sourceColumn": "B",
      "isPrimaryContent": true,
      "required": true
    },
    {
      "id": "amount",
      "label": "Amount",
      "type": "currency",
      "sourceColumn": "C",
      "currencyCode": "USD",
      "required": true
    },
    {
      "id": "category",
      "label": "Category",
      "type": "tag",
      "sourceColumn": "D",
      "allowedValues": ["Income", "Expense", "Transfer"],
      "required": false
    },
    {
      "id": "status",
      "label": "Status",
      "type": "signifier",
      "sourceColumn": "E",
      "defaultValue": "task",
      "required": false
    },
    {
      "id": "notes",
      "label": "Notes",
      "type": "text",
      "sourceColumn": "F",
      "multiline": true,
      "required": false
    }
  ],

  "capturePrompts": [
    {
      "field": "description",
      "prompt": "What was this transaction?",
      "placeholder": "e.g., Office supplies from Staples"
    },
    {
      "field": "amount",
      "prompt": "How much?",
      "placeholder": "e.g., $125.00"
    },
    {
      "field": "category",
      "prompt": "Category?",
      "placeholder": "Income, Expense, or Transfer"
    }
  ],

  "aiEnhancements": {
    "contextMatching": true,
    "suggestions": true,
    "signifiers": true,
    "calendarIntegration": false,
    "weeklyReview": true,
    "monthlyReview": true
  },

  "suggestionRules": {
    "extractDates": true,
    "extractAmounts": true,
    "priorityKeywords": ["due", "overdue", "pending", "reconcile"],
    "taskSuggestions": true
  },

  "views": [
    {
      "id": "table",
      "name": "Table View",
      "type": "table",
      "default": true,
      "columns": ["transaction_date", "description", "amount", "category", "status"]
    },
    {
      "id": "timeline",
      "name": "Timeline",
      "type": "timeline",
      "dateField": "transaction_date"
    }
  ]
}
```

---

## Field Type Mappings

### Auto-Detection Rules

| Excel/Sheet Pattern | Detected Type | RoleNote AI Type |
|---------------------|---------------|------------------|
| Date formats | Date | `date` |
| Currency symbols ($, €, ¥) | Currency | `currency` |
| Checkbox/Yes/No | Boolean | `checkbox` |
| Dropdown/Validation | Enum | `tag` |
| Long text (>100 chars) | Text | `text` (multiline) |
| Short text | Text | `text` |
| Numbers | Number | `number` |
| Email pattern | Email | `email` |
| URL pattern | URL | `url` |
| Phone pattern | Phone | `phone` |

### Custom Field Types

Users can define custom field types:

```json
{
  "id": "invoice_number",
  "label": "Invoice #",
  "type": "custom",
  "pattern": "INV-[0-9]{4}-[0-9]{4}",
  "placeholder": "INV-2026-0001",
  "autoIncrement": true
}
```

---

## Example Use Cases

### 1. Accountant: Monthly Expense Tracker

**Original Excel Template:**
| Date | Vendor | Amount | Category | Approved | Notes |
|------|--------|--------|----------|----------|-------|
| 1/15 | Amazon | $125 | Supplies | Yes | Office supplies |

**After Import:**
- All columns preserved
- AI links expenses to related projects
- Migration prompts for unapproved items
- Monthly summary with spending insights

### 2. Financial Advisor: Client Portfolio Tracker

**Original Excel Template:**
| Client | Holdings | Value | Last Review | Action Needed |
|--------|----------|-------|-------------|---------------|
| J. Smith | AAPL, MSFT | $50K | 12/15/25 | Rebalance |

**After Import:**
- Client field linked to contact matching
- "Last Review" triggers calendar suggestions
- "Action Needed" becomes task with signifier
- Weekly review surfaces clients needing attention

### 3. HR Manager: Recruitment Pipeline

**Original Google Sheet:**
| Candidate | Position | Stage | Interview Date | Feedback |
|-----------|----------|-------|----------------|----------|
| Jane Doe | Engineer | Phone Screen | 2/1/26 | Positive |

**After Import:**
- Stages become visual pipeline
- Interview dates sync to calendar
- Feedback links to interview notes
- Migration prompts for stalled candidates

---

## Data Sync Options

### One-Time Import
- Import template structure
- Import existing data (optional)
- No ongoing sync

### Live Sync (Phase 2)
- Connect to Google Sheets/Airtable
- Bidirectional sync
- RoleNote AI as enhanced view layer

### Hybrid Mode
- Template in RoleNote AI
- Export back to Excel/Sheets on demand
- Best for users who share spreadsheets with others

---

## Privacy & Data Handling

| Concern | Mitigation |
|---------|------------|
| Sensitive financial data | All processing on-device, no cloud upload |
| Client PII | Fields can be marked "sensitive" (excluded from AI) |
| Proprietary templates | Templates stored locally, never shared |
| Export capability | Users can always export back to original format |

---

## Implementation Phases

### Phase 1: Basic Import
- [ ] Excel/CSV parser
- [ ] Column type detection
- [ ] Field mapping UI
- [ ] Custom template creation
- [ ] Basic views (table, list)

### Phase 2: Smart Import
- [ ] AI-powered field suggestions
- [ ] Automatic role matching
- [ ] Google Sheets integration
- [ ] Live sync capability
- [ ] Formula/calculation support

### Phase 3: Advanced Integration
- [ ] Notion/Airtable import
- [ ] Template marketplace (share templates)
- [ ] Template versioning
- [ ] Team template sharing

---

## UI Components Needed

### Import Wizard
- File upload component
- Google/Notion OAuth connectors
- Column mapping interface
- Preview component

### Custom Template Editor
- Field type selector
- Drag-and-drop field ordering
- Capture prompt editor
- AI enhancement toggles

### Hybrid Views
- Table view (spreadsheet-familiar)
- Card view (RoleNote AI native)
- Timeline view
- Kanban view (for status fields)

---

*"Your workflow, supercharged with intelligence."*
