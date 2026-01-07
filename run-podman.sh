#!/bin/bash
set -e

# Check if podman-compose is installed
if command -v podman-compose &> /dev/null; then
    echo "Starting application with podman-compose..."
    podman-compose -f podman-compose.yml up -d
    echo "Application started. Access it at http://localhost:8080"
    echo "PostgreSQL is running on port 5432"
elif command -v podman &> /dev/null; then
    # Check if 'podman compose' is available (plugin)
    if podman compose version &> /dev/null; then
        echo "Starting application with podman compose..."
        podman compose -f podman-compose.yml up -d
        echo "Application started. Access it at http://localhost:8080"
    else
        echo "Error: 'podman-compose' command not found, and 'podman compose' not available."
        echo "Please install podman-compose or ensure podman-docker is set up."
        exit 1
    fi
else
    echo "Error: podman not found. Please install Podman."
    exit 1
fi
