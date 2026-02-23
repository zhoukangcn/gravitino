#!/bin/bash

# This script is used to shutdown the Doris cluster using Docker Compose

# Get the directory of this script
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Load environment variables
DORIS_ROOT_DIR="${GRAVITINO_ROOT_DIR:-$( cd "$SCRIPT_DIR/../.." && pwd )}"
DOCKER_COMPOSE_FILE="${SCRIPT_DIR}/docker-compose.doris.yaml"

# Stop the containers
echo "Stopping Doris cluster..."
if [ -z "$(which docker-compose)" ]; then
    docker compose -f "${DOCKER_COMPOSE_FILE}" down -v
else
    docker-compose -f "${DOCKER_COMPOSE_FILE}" down -v
fi

echo "Doris cluster stopped successfully"
