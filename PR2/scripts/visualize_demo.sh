#!/usr/bin/env bash
set -euo pipefail

project_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$project_dir"

if [[ ! -f tools/plantuml.jar && -z "${PLANTUML_JAR:-}" ]]; then
  ./scripts/fetch_plantuml.sh
fi

./gradlew installDist --no-daemon --console=plain >/dev/null
bin=./build/install/depgraph/bin/depgraph

run_case() {
  local title="$1"
  shift
  echo "== $title =="
  "$bin" "$@"
  echo
}

run_case "diamond package A" \
  --package A --repo fixtures/repos/diamond.txt --version 1 --test-repo

run_case "branch package A with ASCII" \
  --package A --repo fixtures/repos/branch.txt --version 1 --test-repo --ascii-tree

run_case "PyPI requests 2.31.0" \
  --package requests --repo https://pypi.org/simple/ --version 2.31.0 --ascii-tree
