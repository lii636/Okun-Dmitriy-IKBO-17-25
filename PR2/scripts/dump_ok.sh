#!/usr/bin/env bash
set -euo pipefail

project_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$project_dir"

echo "== remote dump =="
./run.sh \
  --package requests \
  --repo https://pypi.org/simple/ \
  --version 2.31.0

echo
echo "== test-repo dump =="
./run.sh \
  --package A \
  --repo fixtures/repos/sample.txt \
  --version 1 \
  --test-repo \
  --ascii-tree
