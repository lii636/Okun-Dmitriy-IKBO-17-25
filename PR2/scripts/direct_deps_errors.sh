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

run_case "missing version on PyPI" \
  --package requests --repo https://pypi.org/simple/ --version 0.0.0.0-not-exists

run_case "test-repo not supported yet" \
  --package A --repo fixtures/repos/sample.txt --version 1 --test-repo

run_case "bad host" \
  --package requests --repo https://no-such-host.invalid/simple/ --version 2.31.0
