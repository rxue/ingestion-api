downloadDataSet() {
  echo "Download dataset"
  local downloadAddress="https://www.cs.cmu.edu/~enron/enron_mail_20150507.tar.gz"
  originalFileName=$(basename ${downloadAddress})
  if [ ! -f ${originalFileName} ]; then curl -O ${downloadAddress}; fi
  echo "Extract to the Docker volume for the Spark to ingest"
  tar -xzf $originalFileName -C ~/dockervolume/spark/input/
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
