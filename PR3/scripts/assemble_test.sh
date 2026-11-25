#!/usr/bin/env bash
set -euo pipefail

root_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root_dir"

./run.sh assemble fixtures/asm/spec_tests.asm /tmp/uvm-stage5-spec.bin --test
