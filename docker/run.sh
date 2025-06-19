#!/bin/bash
. functions.sh
rm -fr ~/dockervolume/ingestion/*
mkdir ~/dockervolume/ingestion/input
docker compose up -d --build broker
restartAsyncAPIEndpoint
restartIngestionManager
