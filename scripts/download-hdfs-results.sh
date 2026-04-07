#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
HDFS_PARQUET_RESULTS_DIR="${HDFS_PARQUET_RESULTS_DIR:-${HDFS_RESULTS_DIR:-/project-spark/curated/parquet}}"
HDFS_CSV_RESULTS_DIR="${HDFS_CSV_RESULTS_DIR:-/project-spark/curated/csv}"
HDFS_BIN="${HDFS_BIN:-/opt/hadoop-3.2.1/bin/hdfs}"

# shellcheck source=./lib/common.sh
source "$SCRIPT_DIR/lib/common.sh"

ensure_compose_env

mkdir -p "$PROJECT_ROOT/data/output/hdfs-parquet" "$PROJECT_ROOT/data/output/hdfs-csv"

compose_for_project exec -T namenode /bin/bash -c "\
  rm -rf /project/export-parquet/* /project/export-csv/* && \
  $HDFS_BIN dfs -get -f $HDFS_PARQUET_RESULTS_DIR/* /project/export-parquet/ && \
  $HDFS_BIN dfs -get -f $HDFS_CSV_RESULTS_DIR/* /project/export-csv/"

find "$PROJECT_ROOT/data/output/hdfs-parquet" -maxdepth 3 -type d | sort
find "$PROJECT_ROOT/data/output/hdfs-csv" -maxdepth 3 -type d | sort
