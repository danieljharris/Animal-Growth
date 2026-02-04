#!/bin/bash
set -euo pipefail

# Forwards [AG_TEST:COMMAND:...] lines from server logs into the server command FIFO
COMMAND_FIFO="/tmp/hytale_commands.fifo"
LOG_DIR="./server/logs"

if [ ! -d "$LOG_DIR" ]; then
  echo "Log dir $LOG_DIR not found" >&2
  exit 1
fi

echo "Command forwarder starting, waiting for FIFO $COMMAND_FIFO"
while [ ! -p "$COMMAND_FIFO" ]; do
  sleep 0.5
done
echo "Found FIFO $COMMAND_FIFO, starting to follow logs"

# Wait for at least one log file to appear before starting tail.
echo "Waiting for log files in $LOG_DIR"
while ! ls "$LOG_DIR"/*.log* 1> /dev/null 2>&1; do
  sleep 0.5
done
echo "Found log files, starting to follow logs"

# Follow all log files (including .log.lck). tail -F will follow rotated files.
tail -n0 -F "$LOG_DIR"/*.log* 2>/dev/null | while IFS= read -r line; do
  if echo "$line" | grep -q "\[AG_TEST:COMMAND:"; then
    COMMAND=$(echo "$line" | sed -n 's/.*\[AG_TEST:COMMAND:\([^]]*\)\].*/\1/p')
    if [ -n "$COMMAND" ] && [ -p "$COMMAND_FIFO" ]; then
      echo "$COMMAND" > "$COMMAND_FIFO"
      echo "Forwarded command to server: $COMMAND"
    fi
  fi
done
