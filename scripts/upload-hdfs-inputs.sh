#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
HDFS_RAW_DIR="${HDFS_RAW_DIR:-/project-spark/raw/input}"
HDFS_BIN="${HDFS_BIN:-/opt/hadoop-3.2.1/bin/hdfs}"

# shellcheck source=./lib/common.sh
source "$SCRIPT_DIR/lib/common.sh"

ensure_compose_env

compose_for_project exec -T namenode "$HDFS_BIN" dfs -mkdir -p "$HDFS_RAW_DIR"
compose_for_project exec -T namenode /bin/bash -c "$HDFS_BIN dfs -put -f /project/input/*.csv $HDFS_RAW_DIR/"
compose_for_project exec -T namenode "$HDFS_BIN" dfs -ls "$HDFS_RAW_DIR"
