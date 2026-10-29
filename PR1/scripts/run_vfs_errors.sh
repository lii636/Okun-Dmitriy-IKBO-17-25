#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

expect_failure() {
  if bash ./run.sh "$@"; then
    echo "expected emulator startup to fail" >&2
    exit 1
  fi
}

expect_failure --vfs fixtures/vfs/missing.xml
expect_failure --vfs fixtures/vfs/invalid.xml
