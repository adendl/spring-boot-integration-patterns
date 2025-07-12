#!/bin/bash

# Script to deploy Apache ActiveMQ Classic as a Docker container

# Configuration variables
ACTIVEMQ_VERSION="5.18.3"
IMAGE_NAME="apache/activemq-classic"
CONTAINER_NAME="activemq"
WEB_CONSOLE_PORT="8161"
AMQP_PORT="5672"
STOMP_PORT="61613"
OPENWIRE_PORT="61616"
DATA_DIR="$(pwd)/activemq-data"
LOG_DIR="$(pwd)/activemq-logs"

# Ensure Docker is installed
if ! command -v docker &> /dev/null; then
    echo "Error: Docker is not installed. Please install Docker and try again."
    exit 1
fi

# Create data and log directories if they don't exist
mkdir -p "${DATA_DIR}" "${LOG_DIR}"
if [ $? -ne 0 ]; then
    echo "Error: Failed to create directories ${DATA_DIR} and ${LOG_DIR}."
    exit 1
fi

# Check if container with same name is already running
if [ "$(docker ps -q -f name=${CONTAINER_NAME})" ]; then
    echo "Warning: A container named ${CONTAINER_NAME} is already running."
    read -p "Do you want to stop and remove it? (y/n): " answer
    if [ "$answer" = "y" ]; then
        docker stop "${CONTAINER_NAME}" && docker rm "${CONTAINER_NAME}"
        if [ $? -ne 0 ]; then
            echo "Error: Failed to stop or remove existing container."
            exit 1
        fi
    else
        echo "Exiting without deploying new container."
        exit 0
    fi
fi

# Pull the ActiveMQ image
echo "Pulling ActiveMQ image ${IMAGE_NAME}..."
docker pull "${IMAGE_NAME}"
if [ $? -ne 0 ]; then
    echo "Error: Failed to pull Docker image ${IMAGE_NAME}."
    exit 1
fi

# Run the ActiveMQ container
echo "Starting ActiveMQ container ${CONTAINER_NAME}..."
docker run -d \
    --name "${CONTAINER_NAME}" \
    -p "${WEB_CONSOLE_PORT}:8161" \
    -p "${AMQP_PORT}:5672" \
    -p "${STOMP_PORT}:61613" \
    -p "${OPENWIRE_PORT}:61616" \
    -v "${DATA_DIR}:/data/activemq" \
    -v "${LOG_DIR}:/var/log/activemq" \
    -e "ACTIVEMQ_ADMIN_LOGIN=admin" \
    -e "ACTIVEMQ_ADMIN_PASSWORD=admin" \
    "${IMAGE_NAME}"
if [ $? -ne 0 ]; then
    echo "Error: Failed to start ActiveMQ container."
    exit 1
fi

# Wait for ActiveMQ to start (up to 30 seconds)
echo "Waiting for ActiveMQ to start..."
for i in {1..30}; do
    if curl -s "http://localhost:${WEB_CONSOLE_PORT}/admin" > /dev/null; then
        echo "ActiveMQ is up and running!"
        echo "Web Console: http://localhost:${WEB_CONSOLE_PORT} (admin/admin)"
        echo "AMQP: localhost:${AMQP_PORT}"
        echo "STOMP: localhost:${STOMP_PORT}"
        echo "OpenWire: localhost:${OPENWIRE_PORT}"
        exit 0
    fi
    sleep 1
done

echo "Error: ActiveMQ failed to start within 30 seconds."
docker logs "${CONTAINER_NAME}"
exit 1