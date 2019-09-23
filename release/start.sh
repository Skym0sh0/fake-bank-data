#!/bin/sh

set -e
set -x

echo "Starting Docker-Compose..."

docker-compose up -d

echo "Docker-Compose started"
