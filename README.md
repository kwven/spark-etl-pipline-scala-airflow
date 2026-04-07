# Spark Scala ETL with Airflow

This repository is a collaborative data engineering project by Ahmed Lamrani and Taha El Bekkali. We built it as a hands-on Spark ETL platform in Scala, using Airflow to orchestrate the workflow and Docker to package the supporting services.

## Project Overview

The project processes multiple datasets through a classic ETL flow:

- extract raw CSV files and transaction data from PostgreSQL
- transform and clean the data with Apache Spark
- run dataset-specific analysis jobs
- load curated results into Parquet and CSV outputs
- orchestrate the workflow through an Airflow-based pipeline

At a high level, the platform follows this path:

`Raw CSV files + PostgreSQL -> Spark ETL jobs -> HDFS/local outputs -> Airflow orchestration`

## Collaboration

This work was completed jointly by:

- Ahmed Lamrani
- Taha El Bekkali

The project was developed as a shared effort covering project structure, Docker environment setup, Spark data cleaning and analysis, HDFS-based export flow, and Airflow pipeline execution.

## Tech Stack

- Scala 2.12.19
- Apache Spark 3.5.1
- Apache Airflow 2.9.1
- Hadoop HDFS
- PostgreSQL 16
- Docker Compose
- ScalaTest

## Main Pipelines

### 1. Transactions Pipeline

Reads the `Transaction` table from PostgreSQL, cleans the records, produces filtered country datasets for USA, France, China, and Poland, and calculates the top 10 countries by client count. Selected outputs can also be written back to PostgreSQL.

### 2. Tweets Pipeline

Loads and combines tweet datasets from:

- `covid19_tweets.csv`
- `financial.csv`
- `GRAMMYs_tweets.csv`

The pipeline cleans the data, filters Trump-related tweets in the United States, and generates source and hashtag analysis outputs.

### 3. Forbes Pipeline

Loads `forbes_2022_billionaires.csv`, cleans the billionaire records, extracts billionaires under 50, counts records by country, and generates a top 10 self-made ranking.

### 4. Tweets User Pipeline

Loads `twitter_dataset.csv`, cleans tweet-user data, calculates word counts, user posting frequency, top liked posts, and top retweeted posts.

## Airflow Workflow

The project workflow is designed around an Airflow-driven ETL process with stages such as:

1. clean workspace
2. upload raw data to HDFS
3. run Spark transformation jobs
4. list HDFS results
5. export results locally

This matches the project execution flow demonstrated in the Airflow UI during the collaboration work.

## Repository Structure

- `src/main/scala/loaders` data ingestion logic
- `src/main/scala/cleaners` data cleaning logic
- `src/main/scala/analysers` analysis and filtering logic
- `src/main/scala/jobs` dataset-specific ETL jobs
- `src/main/scala/outputs` CSV and Parquet writers
- `infra/postgres` PostgreSQL image and seed data
- `infra/airflow` Airflow container image
- `airflow/` Airflow DAGs, logs, and config
- `scripts/` helper scripts for ETL and HDFS export
- `data/input` raw datasets
- `data/output` generated outputs

## Outputs

Curated results are stored under:

- `data/output/csv`
- `data/output/parquet`
- `data/output/hdfs-csv`
- `data/output/hdfs-parquet`

## Typical Commands

The following commands reflect the workflow used during development and demo runs:

```bash
docker compose up -d postgres namenode datanode airflow
./scripts/run-etl.sh all
./scripts/run-hdfs-etl.sh all
./scripts/list-hdfs-results.sh
./scripts/download-hdfs-results.sh
sbt test
```

Depending on the current snapshot of the repository, some orchestration entrypoints may still need small finishing updates before the entire flow runs end-to-end from source.

## Project Note

Kafka was part of the broader project direction discussed around the platform, but the code currently committed in this repository is centered on Spark, Airflow, HDFS, and PostgreSQL. In other words, the implemented stack in this repo is the Spark ETL workflow and orchestration layer rather than a completed Kafka integration.

## Summary

This project showcases a collaborative Spark ETL workflow built by Ahmed Lamrani and Taha El Bekkali. It combines Scala, Spark, Airflow, HDFS, PostgreSQL, and Docker to clean, transform, analyze, and export structured datasets in a reproducible data engineering environment.
