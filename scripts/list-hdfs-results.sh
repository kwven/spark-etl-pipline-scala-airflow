#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
HDFS_PARQUET_RESULTS_DIR="${HDFS_PARQUET_RESULTS_DIR:-${HDFS_RESULTS_DIR:-/project-spark/curated/parquet}}"
HDFS_CSV_RESULTS_DIR="${HDFS_CSV_RESULTS_DIR:-/project-spark/curated/csv}"
HDFS_BIN="${HDFS_BIN:-/opt/hadoop-3.2.1/bin/hdfs}"

# shellcheck source=./lib/common.sh
source "$SCRIPT_DIR/lib/common.sh"

ensure_compose_env

list_hdfs_dir() {
  local label="$1"
  local path="$2"

  echo "== $label =="
  if compose_for_project exec -T namenode "$HDFS_BIN" dfs -test -e "$path"; then
    compose_for_project exec -T namenode "$HDFS_BIN" dfs -ls -R "$path"
  else
    echo "Missing HDFS directory: $path"
  fi
  echo
}

list_hdfs_dir "HDFS parquet results" "$HDFS_PARQUET_RESULTS_DIR"
list_hdfs_dir "HDFS csv results" "$HDFS_CSV_RESULTS_DIR"
