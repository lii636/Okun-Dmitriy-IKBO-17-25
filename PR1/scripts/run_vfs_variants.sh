#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

printf 'ls\ncd home\nls\nexit\n' \
  | bash ./run.sh --vfs fixtures/vfs/minimal.xml
printf 'ls\ncd docs\nls\ncd ..\nls bin.dat\nexit\n' \
  | bash ./run.sh --vfs fixtures/vfs/multi-file.xml
bash ./run.sh \
  --vfs fixtures/vfs/deep-3levels.xml \
  --script fixtures/scripts/stage3_all.txt
