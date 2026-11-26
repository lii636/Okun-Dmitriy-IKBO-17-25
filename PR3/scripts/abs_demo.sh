#!/usr/bin/env bash
set -euo pipefail

root_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root_dir"

mkdir -p /tmp/uvm-stage4
./run.sh assemble fixtures/asm/abs_demo.asm /tmp/uvm-stage4/abs.bin
./run.sh run /tmp/uvm-stage4/abs.bin /tmp/uvm-stage4/dump.xml 10-12
cat /tmp/uvm-stage4/dump.xml
