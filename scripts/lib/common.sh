#!/usr/bin/env bash

project_root() {
  if [ -n "${PROJECT_HOST_ROOT:-}" ] && [ -d "${PROJECT_HOST_ROOT}" ]; then
    echo "${PROJECT_HOST_ROOT}"
    return
  fi

  cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd
}

compose_project_name() {
  echo "${PROJECT_COMPOSE_NAME:-project-spark-scala-etl-master}"
}

ensure_compose_env() {
  export PROJECT_HOST_ROOT="${PROJECT_HOST_ROOT:-$(project_root)}"
  export AIRFLOW_UID="${AIRFLOW_UID:-$(id -u)}"

  if [ -S /var/run/docker.sock ]; then
    export DOCKER_GID="${DOCKER_GID:-$(stat -c '%g' /var/run/docker.sock)}"
  fi
}

docker_compose() {
  if docker compose version >/dev/null 2>&1; then
    docker compose "$@"
  elif command -v docker-compose >/dev/null 2>&1; then
    docker-compose "$@"
  else
    echo "Docker Compose is not installed." >&2
    return 1
  fi
}

compose_for_project() {
  local root
  root="$(project_root)"
  docker_compose -p "$(compose_project_name)" -f "$root/docker-compose.yml" "$@"
}
