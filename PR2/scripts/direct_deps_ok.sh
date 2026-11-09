#!/usr/bin/env bash
set -euo pipefail

project_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$project_dir"

echo "== BFS from requests 2.31.0 (needs network) =="
./run.sh \
  --package requests \
  --repo https://pypi.org/simple/ \
  --version 2.31.0
