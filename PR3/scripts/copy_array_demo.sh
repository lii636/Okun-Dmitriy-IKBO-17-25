#!/usr/bin/env bash
set -euo pipefail

project_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$project_dir"

mkdir -p /tmp/uvm-stage3
./run.sh assemble fixtures/asm/copy_array.asm /tmp/uvm-stage3/copy.bin
./run.sh run /tmp/uvm-stage3/copy.bin /tmp/uvm-stage3/dump.xml 100-102
cat /tmp/uvm-stage3/dump.xml
