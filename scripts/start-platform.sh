#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
HDFS_BIN="${HDFS_BIN:-/opt/hadoop-3.2.1/bin/hdfs}"

# shellcheck source=./lib/common.sh
source "$SCRIPT_DIR/lib/common.sh"

ensure_compose_env

mkdir -p \
  "$PROJECT_ROOT/data/output/hdfs-parquet" \
  "$PROJECT_ROOT/data/output/hdfs-csv" \
  "$PROJECT_ROOT/airflow/logs" \
  "$PROJECT_ROOT/airflow/plugins" \
  "$PROJECT_ROOT/airflow/config"

compose_for_project up -d --build postgres namenode datanode airflow

until compose_for_project exec -T namenode /bin/bash -c "$HDFS_BIN dfsadmin -report | grep -q 'Live datanodes'" >/dev/null 2>&1; do
  sleep 3
done

until compose_for_project exec -T airflow /bin/bash -c "airflow dags list >/dev/null 2>&1" >/dev/null 2>&1; do
  sleep 3
done

compose_for_project ps
