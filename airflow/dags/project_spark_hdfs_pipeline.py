from datetime import datetime

from airflow import DAG
from airflow.operators.bash import BashOperator


with DAG(
    dag_id="project_spark_hdfs_pipeline",
    description="Clean, upload, transform with Spark, and export HDFS parquet and CSV results.",
    start_date=datetime(2024, 1, 1),
    schedule="0 1 * * *",
    catchup=False,
    tags=["spark", "hdfs", "airflow", "etl"],
) as dag:
    clean_workspace = BashOperator(
        task_id="clean_workspace",
        bash_command="/bin/bash -lc '/opt/project/scripts/clean-hdfs-workspace.sh'",
    )

    upload_raw_to_hdfs = BashOperator(
        task_id="upload_raw_to_hdfs",
        bash_command="/bin/bash -lc '/opt/project/scripts/upload-hdfs-inputs.sh'",
    )

    run_spark_transform = BashOperator(
        task_id="run_spark_transform",
        bash_command="/bin/bash -lc '/opt/project/scripts/run-hdfs-etl.sh all'",
    )

    list_hdfs_results = BashOperator(
        task_id="list_hdfs_results",
        bash_command="/bin/bash -lc '/opt/project/scripts/list-hdfs-results.sh'",
    )

    export_results_locally = BashOperator(
        task_id="export_results_locally",
        bash_command="/bin/bash -lc '/opt/project/scripts/download-hdfs-results.sh'",
    )

    clean_workspace >> upload_raw_to_hdfs >> run_spark_transform >> list_hdfs_results >> export_results_locally
