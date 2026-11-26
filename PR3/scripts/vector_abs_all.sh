#!/usr/bin/env bash
set -euo pipefail

root_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root_dir"

out_dir="/tmp/uvm-stage5"
mkdir -p "$out_dir"

for name in vector_abs_ex1 vector_abs_ex2 vector_abs_ex3; do
  echo "=== $name ==="
  ./run.sh assemble "fixtures/asm/${name}.asm" "${out_dir}/${name}.bin"
  ./run.sh run "${out_dir}/${name}.bin" "${out_dir}/${name}.xml" 0-9
  diff -u \
    <(tr -d '\r' < "fixtures/expected/${name}_dump.xml") \
    <(tr -d '\r' < "${out_dir}/${name}.xml")
  cat "${out_dir}/${name}.xml"
  echo
done
