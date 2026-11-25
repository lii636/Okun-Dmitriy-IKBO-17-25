#!/usr/bin/env bash
set -euo pipefail

root_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
cd "$root_dir"

./gradlew installDist --no-daemon --console=plain
exec ./build/install/uvm/bin/uvm "$@"
