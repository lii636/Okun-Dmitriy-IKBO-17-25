#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
printf 'ls\ncd home\nls\nexit\n' \
  | bash ./run.sh --vfs fixtures/vfs/minimal.xml
