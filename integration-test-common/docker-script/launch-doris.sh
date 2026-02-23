#!/bin/bash

# This script is used to launch the Doris cluster using Docker Compose

# Get the directory of this script
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Load environment variables
DORIS_ROOT_DIR="${GRAVITINO_ROOT_DIR:-$( cd "$SCRIPT_DIR/../.." && pwd )}"
DOCKER_COMPOSE_FILE="${SCRIPT_DIR}/docker-compose.doris.yaml"

# Stop any existing containers
"${SCRIPT_DIR}/shutdown-doris.sh"

# Start the containers
echo "Starting Doris cluster..."
if [ -z "$(which docker-compose)" ]; then
    docker compose -f "${DOCKER_COMPOSE_FILE}" up -d
else
    docker-compose -f "${DOCKER_COMPOSE_FILE}" up -d
fi

# Wait for containers to be healthy
echo "Waiting for Doris cluster to be healthy..."
if [ -z "$(which docker-compose)" ]; then
    while ! docker compose -f "${DOCKER_COMPOSE_FILE}" ps | grep -q "healthy"; do
        echo "Containers not healthy yet. Waiting..."
        sleep 10
    done
else
    while ! docker-compose -f "${DOCKER_COMPOSE_FILE}" ps | grep -q "healthy"; do
        echo "Containers not healthy yet. Waiting..."
        sleep 10
    done
fi

echo "All Docker Compose services are now available"
