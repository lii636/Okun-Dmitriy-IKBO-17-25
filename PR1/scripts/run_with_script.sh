#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
bash ./run.sh --script fixtures/scripts/stage2_conf.txt
