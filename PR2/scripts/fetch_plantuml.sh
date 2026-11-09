#!/usr/bin/env bash
set -euo pipefail

project_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$project_dir"

version="${PLANTUML_VERSION:-1.2024.8}"
url="https://github.com/plantuml/plantuml/releases/download/v${version}/plantuml-${version}.jar"
mkdir -p tools
output="tools/plantuml.jar"

if [[ -f "$output" ]]; then
  echo "already present: $output"
  exit 0
fi

echo "downloading $url"
curl -fsSL -o "$output" "$url"
echo "saved $output"
