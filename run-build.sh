#!/bin/bash
cd "/c/Internal/Pesonal project/Task Management Platform"
echo "Starting backend build..."
docker compose build --no-cache backend > /tmp/backend-build2.log 2>&1
echo "Build finished with exit code: $?" >> /tmp/backend-build2.log
echo "DONE"
