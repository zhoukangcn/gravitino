#!/bin/bash

# This script is used to inspect the Doris cluster and get service URIs

# Get the directory of this script
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Load environment variables
DORIS_ROOT_DIR="${GRAVITINO_ROOT_DIR:-$( cd "$SCRIPT_DIR/../.." && pwd )}"
DOCKER_COMPOSE_FILE="${SCRIPT_DIR}/docker-compose.doris.yaml"

# Get service IP addresses
FE_IP=""
BE_IP=""
BROKER_IP=""

if [ -z "$(which docker-compose)" ]; then
    FE_IP=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' doris-ci-fe)
    BE_IP=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' doris-ci-be)
    BROKER_IP=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' doris-ci-broker)
else
    FE_IP=$(docker-compose -f "${DOCKER_COMPOSE_FILE}" exec -T fe sh -c 'hostname -i')
    BE_IP=$(docker-compose -f "${DOCKER_COMPOSE_FILE}" exec -T be sh -c 'hostname -i')
    BROKER_IP=$(docker-compose -f "${DOCKER_COMPOSE_FILE}" exec -T broker sh -c 'hostname -i')
fi

# Print service URIs
echo "fe:${FE_IP}"
echo "be:${BE_IP}"
echo "broker:${BROKER_IP}"
