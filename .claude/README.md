# Claude Code Configuration

This directory contains configuration and hooks for Claude Code sessions.

## Automatic Session Logging

This project is configured to automatically document all Claude Code sessions.

### Configuration

**File:** `.claude/config.json`

```json
{
  "sessionLogging": {
    "enabled": true,
    "directory": "docs/sessions",
    "format": "markdown"
  }
}
```

### Session Logs Location

All session logs are stored in:
```
docs/sessions/
```

Each session creates a new markdown file with:
- Timestamp
- Tasks completed
- Files created/modified
- Prompts and requests
- Key decisions made
- Issues and solutions
- Next steps

### Manual Session Logging

You can also manually create session logs using the template:

```bash
cp .claude/session-template.md docs/sessions/session_$(date +%Y%m%d_%H%M%S).md
```

### Hooks

**Session End Hook:** `.claude/hooks/session-logger.sh`
- Automatically triggered when Claude Code session ends
- Creates a timestamped session log file
- Captures session metadata

## Files Structure

```
.claude/
├── config.json              # Claude Code configuration
├── README.md               # This file
├── session-template.md     # Template for session logs
└── hooks/
    └── session-logger.sh   # Auto-logging hook
```

## Current Session Log

The current session is documented in:
- `SESSION_LOG.md` (comprehensive session documentation)

Future sessions will be logged in `docs/sessions/`.

## Usage

1. **View Current Session:**
   ```bash
   cat SESSION_LOG.md
   ```

2. **List All Sessions:**
   ```bash
   ls -lt docs/sessions/
   ```

3. **Search Session Logs:**
   ```bash
   grep -r "search term" docs/sessions/
   ```

## Customization

To customize session logging:

1. Edit `.claude/config.json` for logging preferences
2. Modify `.claude/session-template.md` for custom template
3. Update `.claude/hooks/session-logger.sh` for custom hook behavior

## Notes

- Session logs are markdown formatted for easy reading
- All logs are timestamped
- Logs include file changes, commands, and decisions
- Hook requires bash shell to execute
