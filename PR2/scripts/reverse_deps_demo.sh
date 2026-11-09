#!/usr/bin/env bash
set -euo pipefail

project_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$project_dir"

./gradlew installDist --no-daemon --console=plain >/dev/null
bin=./build/install/depgraph/bin/depgraph

run_case() {
  local title="$1"
  local package="$2"
  local file="$3"
  echo "== $title =="
  "$bin" --package "$package" --repo "$file" --version 1 --test-repo --reverse-deps
  echo
}

run_case "linear reverse from C" C fixtures/repos/linear.txt
run_case "linear reverse from A (empty)" A fixtures/repos/linear.txt
run_case "branch reverse from B" B fixtures/repos/branch.txt
run_case "diamond reverse from D" D fixtures/repos/diamond.txt
