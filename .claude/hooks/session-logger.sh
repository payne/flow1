#!/bin/bash
# Claude Code Session Logger Hook
# This hook is triggered at the end of each Claude Code session
# to automatically create session documentation

SESSION_DIR="docs/sessions"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
SESSION_FILE="${SESSION_DIR}/session_${TIMESTAMP}.md"

# Create session directory if it doesn't exist
mkdir -p "$SESSION_DIR"

# Create session log from template
cat > "$SESSION_FILE" << 'EOF'
# Session Log

**Date:** $(date +"%Y-%m-%d %H:%M:%S")
**Project:** Order Management System with Flowable BPMN

---

## Session Summary

### Tasks Completed
- [ ] Task 1
- [ ] Task 2

### Prompts & Requests
1. **Initial Request:**
   ```
   [User's initial prompt]
   ```

2. **Follow-up Requests:**
   - Request 1
   - Request 2

### Files Created/Modified

**Created:**
- file1.java
- file2.sql

**Modified:**
- file3.xml
- file4.yml

### Key Decisions

1. **Decision 1:**
   - Context: ...
   - Decision: ...
   - Rationale: ...

### Issues Encountered

1. **Issue 1:**
   - Problem: ...
   - Solution: ...

### Next Steps

- [ ] Next task 1
- [ ] Next task 2

---

## Detailed Log

[Conversation details...]

---

## Commands Executed

```bash
# Command 1
command1

# Command 2
command2
```

---

## Notes

Additional observations or important information...

EOF

echo "Session log created: $SESSION_FILE"
