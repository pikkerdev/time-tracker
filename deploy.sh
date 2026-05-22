#!/bin/bash

mkdir -p logs
docker compose logs -t app 2>&1 | gzip > logs/app-"$(date +%Y-%m-%d_%H:%M:%S)".log.gz
docker compose -f compose.yml -f compose.server.yml --env-file .env --env-file .env.prod up -d --pull always --no-build
