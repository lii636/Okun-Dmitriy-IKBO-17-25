#!/usr/bin/env bash
set -euo pipefail

project_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$project_dir"

./run.sh assemble fixtures/asm/spec_tests.asm /tmp/uvm-stage1-out.bin --test
