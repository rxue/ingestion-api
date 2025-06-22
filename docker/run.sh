#!/bin/bash
. functions.sh
mkdir ~/dockervolume/ingestion/input
restartDatabase
restartIngestionManager
