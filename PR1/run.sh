#!/usr/bin/env bash
set -euo pipefail

if [ "$#" -eq 0 ]; then
  exec ./gradlew run --no-daemon --console=plain
fi

args=
for argument in "$@"; do
  args="$args '$argument'"
done

exec ./gradlew run --no-daemon --console=plain --args="${args# }"
