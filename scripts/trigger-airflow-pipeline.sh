#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DAG_ID="${1:-project_spark_hdfs_pipeline}"

# shellcheck source=./lib/common.sh
source "$SCRIPT_DIR/lib/common.sh"

ensure_compose_env

compose_for_project exec -T airflow airflow dags trigger "$DAG_ID"
