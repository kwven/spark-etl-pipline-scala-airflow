#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
PIPELINES="${1:-all}"

# shellcheck source=./lib/common.sh
source "$SCRIPT_DIR/lib/common.sh"

ensure_compose_env

cd "$PROJECT_ROOT"
sbt assembly

compose_for_project run --rm spark-submit \
  "/opt/spark/bin/spark-submit \
    --class Main \
    --master local[*] \
    /opt/project/target/scala-2.12/project-spark-scala-etl-assembly-0.1.0-SNAPSHOT.jar \
    --pipelines=$PIPELINES \
    --use-hdfs=true \
    --hdfs-uri=hdfs://namenode:9000 \
    --hdfs-raw-dir=/project-spark/raw/input \
    --hdfs-curated-parquet-dir=/project-spark/curated/parquet \
    --hdfs-curated-csv-dir=/project-spark/curated/csv \
    --jdbc-url=jdbc:postgresql://postgres:5432/postgres \
    --jdbc-user=postgres \
    --jdbc-password=postgres"
