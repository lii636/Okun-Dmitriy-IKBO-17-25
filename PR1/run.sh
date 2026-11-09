#!/usr/bin/env bash
set -euo pipefail

project_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
cd "$project_dir"

./gradlew installDist --no-daemon --console=plain
exec ./build/install/shell-emulator/bin/shell-emulator "$@"
