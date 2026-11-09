#!/usr/bin/env bash
set -euo pipefail

project_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$project_dir"

./gradlew installDist --no-daemon --console=plain >/dev/null
bin=./build/install/depgraph/bin/depgraph

run_case() {
  local title="$1"
  local file="$2"
  echo "== $title =="
  "$bin" --package A --repo "$file" --version 1 --test-repo
  echo
}

run_case "linear" fixtures/repos/linear.txt
run_case "branch" fixtures/repos/branch.txt
run_case "cycle" fixtures/repos/cycle.txt
run_case "diamond" fixtures/repos/diamond.txt
