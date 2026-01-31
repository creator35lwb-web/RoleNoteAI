# RoleNote AI - Dual-Repo Development Protocol v1.0

## Team Structure

| Role | Agent | Responsibility | Primary Tools |
|------|-------|----------------|---------------|
| **CTO** | RNA (Claude Code Opus 4.5) | Architecture, Core Development, Code Review | Claude Code CLI |
| **CSO** | R (Manus AI) | Security Validation, Feature Development, Testing | Manus AI |
| **Founder** | Human | Vision, Direction, Final Approval | Both |

---

## Communication Bridge: GitHub

```
┌─────────────────┐         ┌─────────────────┐
│   Claude Code   │         │    Manus AI     │
│   (CTO: RNA)    │         │    (CSO: R)     │
└────────┬────────┘         └────────┬────────┘
         │                           │
         │    ┌─────────────┐        │
         └────►   GitHub    ◄────────┘
              │  Repository │
              └──────┬──────┘
                     │
              ┌──────▼──────┐
              │   Founder   │
              │   (Human)   │
              └─────────────┘
```

---

## Repository Structure

```
RoleNoteAI/
├── android/                    # Android App (Kotlin/Compose)
│   ├── app/src/main/
│   │   ├── java/com/rolenoteai/app/
│   │   │   ├── core/          # Security, Database, Validation
│   │   │   ├── data/          # Repository, DAO, Entities
│   │   │   ├── di/            # Hilt Dependency Injection
│   │   │   ├── domain/        # Models, Use Cases
│   │   │   └── presentation/  # UI, ViewModels, Screens
│   │   ├── assets/templates/  # Role JSON Templates
│   │   └── res/               # Resources
│   └── build.gradle.kts
├── docs/                       # Documentation
│   ├── security/              # Security specs
│   ├── ethics/                # Ethics guidelines
│   └── DUAL_REPO_PROTOCOL_v1.0.md  # THIS FILE
├── templates/                  # Template Registry
└── README.md
```

---

## Development Phases

### Phase 3a: Initial Setup ✅
- Android project structure
- Gradle configuration
- Basic dependencies

### Phase 3b: Core Engine ✅ (Current)
- Authentication (PIN/Biometric)
- Encrypted database (SQLCipher)
- Room DAOs and Entities
- Role Templates (19 built-in)
- Navigation and UI Screens

### Phase 3c: AI Integration (Next)
- Gemma 3 on-device AI
- Context-aware suggestions
- Template-based prompts
- Voice capture

### Phase 3d: Polish & Release
- UI/UX refinement
- Performance optimization
- Play Store release

---

## Git Workflow Protocol

### Branch Strategy
```
main ─────────────────────────────────► (stable releases)
  │
  ├── phase-3b/fixes ──────► (bug fixes, merged to main)
  │
  ├── phase-3c/ai-integration ──► (new features)
  │
  └── hotfix/* ──────────────────► (urgent fixes)
```

### Commit Message Format
```
[Phase X] <Type>: <Short description>

<Detailed description if needed>

Co-Authored-By: <Agent Name> <email>
```

**Types:**
- `Fix` - Bug fixes
- `Add` - New features
- `Update` - Enhancements
- `Refactor` - Code restructuring
- `Docs` - Documentation
- `Test` - Testing

### Example:
```
[Phase 3b] Fix: Resolve compilation errors for device testing

- Fixed EncryptedDatabaseFactory inline function issue
- Fixed SQLCipher openDatabase parameter types
- Fixed Mappers.kt missing parameters
- Fixed Screens.kt HorizontalDivider -> Divider
- Fixed RoleNoteApp.kt navigation stability
- Added error handling to template initialization

Co-Authored-By: Claude Code Opus 4.5 <noreply@anthropic.com>
```

---

## Handoff Protocol

### From Claude Code (CTO) to Manus AI (CSO)

1. **Push to GitHub** with detailed commit message
2. **Create Issue/PR** describing:
   - What was done
   - Current state
   - Next steps needed
   - Known issues
3. **Tag with labels**: `ready-for-manus`, `phase-X`

### From Manus AI (CSO) to Claude Code (CTO)

1. **Push to GitHub** with detailed commit message
2. **Create Issue/PR** describing:
   - What was implemented
   - Security considerations
   - Testing results
   - Questions for CTO
3. **Tag with labels**: `ready-for-claude`, `needs-review`

---

## Current Status (2026-02-01)

### Completed
- [x] Android project setup
- [x] Gradle build configuration
- [x] Core security layer (AuthenticationManager)
- [x] Encrypted database (SQLCipher + Room)
- [x] 19 Role templates loaded from assets
- [x] PIN authentication working
- [x] Basic navigation flow
- [x] Template selection screen

### Known Issues (For Manus AI to address)
1. **Role Switching**: No UI to switch between roles after initial selection
2. **Template Content**: Template pages show blank - need to implement template-specific UI
3. **Note Capture**: Basic text input only - no AI suggestions yet
4. **AI Integration**: Gemma 3 not yet integrated

### Next Steps (Phase 3c)
1. Implement role switching in Settings
2. Add template-specific capture prompts
3. Integrate Gemma 3 for on-device AI
4. Add voice capture functionality
5. Implement migration/scheduling features

---

## Testing Protocol

### Device Testing
- **Primary Device**: Redmi Pad SE 8.7 (Android 14)
- **Test Folder**: `C:\dev\RoleNoteAI-RedMiPadSE`
- **Workspace**: `C:\Users\weibi\OneDrive\Desktop\VerifiMind (Workspace)\RoleNote AI`

### Before Push Checklist
- [ ] Build succeeds (`./gradlew assembleDebug`)
- [ ] App installs on device
- [ ] PIN setup/unlock works
- [ ] Template selection displays all 19 roles
- [ ] No crashes on navigation
- [ ] Changes synced to workspace folder

---

## Security Guidelines

1. **Never commit** `.env`, credentials, or API keys
2. **Always use** SQLCipher for local storage
3. **Validate** all user inputs (InputValidator.kt)
4. **Log** security-relevant actions (AuditLogDao)
5. **Follow** docs/security/THREAT_MODEL.md

---

## Contact & Coordination

- **GitHub Repo**: https://github.com/creator35lwb-web/RoleNoteAI
- **Issues**: Use GitHub Issues for task tracking
- **PRs**: Use Pull Requests for code review

---

*Protocol Version: 1.0*
*Last Updated: 2026-02-01*
*Authors: CTO RNA (Claude Code), Founder (Human)*
