# Session Logs

This directory contains detailed logs of all Claude Code development sessions for the Order Management System project.

## Purpose

Session logs provide:
- **Historical Context**: Track project evolution over time
- **Decision Documentation**: Record why certain architectural choices were made
- **Knowledge Transfer**: Help new developers understand the codebase
- **Audit Trail**: Document changes, issues, and resolutions

## Log Format

Each session log follows a standardized markdown template including:

1. **Session Metadata**
   - Date and time
   - Agent/developer
   - Session objective

2. **Planning Phase**
   - Requirements clarification
   - Design decisions

3. **Implementation**
   - Tasks completed
   - Files created/modified
   - Code examples

4. **Issues & Solutions**
   - Problems encountered
   - Resolution steps

5. **Results & Next Steps**
   - What was accomplished
   - Future work

## Current Sessions

- `../SESSION_LOG.md` - Initial application setup (2026-01-06)
  - Created Spring Boot + Flowable foundation
  - Implemented dual-schema PostgreSQL setup
  - Built 3 BPMN workflows
  - Completed core backend services

## Viewing Session Logs

**List all sessions:**
```bash
ls -lt
```

**View latest session:**
```bash
cat $(ls -t session_*.md | head -1)
```

**Search across sessions:**
```bash
grep -r "search term" .
```

**View specific topic:**
```bash
grep -r "BPMN" . | less
```

## Contributing

When adding manual session notes:

1. Copy the template:
   ```bash
   cp ../../.claude/session-template.md session_$(date +%Y%m%d_%H%M%S).md
   ```

2. Fill in all sections with relevant details

3. Commit the session log with the code changes:
   ```bash
   git add .
   git commit -m "Session: [Brief description of work]"
   ```

## File Naming Convention

```
session_YYYYMMDD_HHMMSS.md
```

Example: `session_20260106_143022.md`

## Integration with Project

Session logs complement:
- `README.md` - General project documentation
- Code comments - Implementation details
- Git history - Change tracking
- Issue tracker - Bug/feature tracking

## Automation

Session logging is automated via:
- `.claude/config.json` - Configuration
- `.claude/hooks/session-logger.sh` - Post-session hook
- `.claude/session-template.md` - Template file

Future sessions will automatically create logs in this directory.

---

**Note:** These logs are part of project documentation and should be committed to version control.
