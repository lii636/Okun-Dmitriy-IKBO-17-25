#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
printf 'ls\nconf-dump\nexit\n' | ./gradlew run --no-daemon --console=plain
