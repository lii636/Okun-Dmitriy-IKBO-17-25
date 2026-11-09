#!/usr/bin/env bash
set -u

project_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$project_dir"

./gradlew installDist --no-daemon --console=plain >/dev/null
bin=./build/install/depgraph/bin/depgraph

run_case() {
  local title="$1"
  shift
  echo "== $title =="
  set +e
  "$bin" "$@"
  local code=$?
  set -e
  echo "exit=$code"
  echo
}

run_case "missing --version" \
  --package requests --repo https://pypi.org/simple/

run_case "empty package" \
  --package " " --repo https://pypi.org/simple/ --version 1.0

run_case "non-url without --test-repo" \
  --package requests --repo fixtures/repos/sample.txt --version 1.0

run_case "unknown option" \
  --package requests --repo https://pypi.org/simple/ --version 1.0 --foo

run_case "missing value for --repo" \
  --package requests --repo --version 1.0
