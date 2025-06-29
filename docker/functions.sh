restartDatabase() {
  docker compose down --volumes database 
  docker compose up -d --build database
}
restartJMS() {
  docker compose down --volumes broker
  docker compose up -d --build broker
}
restartAsyncAPIEndpoint() {
  mvn -f ../java/async-api-endpoint/pom.xml clean package -Dquarkus.package.jar.type=legacy-jar
  if [ $? -ne 0 ]; then
    echo "Project Build Failure :("
    return 1
  fi
  docker compose up -d --build async-api-endpoint
}
restartIngestionManager() {
  mvn -f ../java/ingestion-manager/pom.xml clean package -Dquarkus.package.jar.type=legacy-jar
  if [ $? -ne 0 ]; then
    echo "Project Build Failure :("
    return 1
  fi
  docker compose up -d --build ingestion-manager
}
