#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PIPELINES="${1:-all}"

mkdir -p "$PROJECT_ROOT/data/output/csv" "$PROJECT_ROOT/data/output/parquet"

cd "$PROJECT_ROOT"

exec sbt \
  -Dproject.inputDir="$PROJECT_ROOT/data/input" \
  -Dproject.outputDir="$PROJECT_ROOT/data/output" \
  -Dproject.pipelines="$PIPELINES" \
  run
