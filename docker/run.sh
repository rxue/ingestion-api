soure functions.sh
docker compose up -d --build broker
restartAsyncAPIEndpoint
restartIngestionManager
