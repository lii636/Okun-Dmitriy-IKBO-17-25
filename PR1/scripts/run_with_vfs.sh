#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
args="--vfs vfs/location"
printf 'ls\ncd home\nls\nexit\n' \
  | ./gradlew run --no-daemon --console=plain --args="$args"
