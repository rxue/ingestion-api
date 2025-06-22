#!/bin/bash
. functions.sh
rm -fr ~/dockervolume/ingestion/*
mkdir ~/dockervolume/ingestion/input
restartDatabase
restartIngestionManager
