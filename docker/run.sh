#!/bin/bash
. functions.sh
docker compose down ingestion-manager
rm -fr ~/dockervolume/ingestion/*
mkdir ~/dockervolume/ingestion/input
docker compose up -d --build broker
restartAsyncAPIEndpoint
restartIngestionManager
