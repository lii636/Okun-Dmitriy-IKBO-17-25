#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
args="--script fixtures/scripts/stage2_conf.txt"
./gradlew run --no-daemon --console=plain --args="$args"
