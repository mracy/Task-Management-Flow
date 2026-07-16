#!/bin/bash
cd "/c/Internal/Pesonal project/Task Management Platform"
docker compose build --no-cache > /c/docker_build_log.txt 2>&1
echo "EXIT_CODE=$?" > /c/docker_build_exit.txt
