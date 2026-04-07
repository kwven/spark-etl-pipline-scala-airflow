FROM apache/airflow:2.9.1

USER root

RUN apt-get update \
  && apt-get install -y --no-install-recommends curl docker-compose-plugin openjdk-17-jdk \
  && curl -fsSL https://raw.githubusercontent.com/paulp/sbt-extras/master/sbt -o /usr/local/bin/sbt \
  && chmod +x /usr/local/bin/sbt \
  && apt-get clean \
  && rm -rf /var/lib/apt/lists/*

USER airflow
