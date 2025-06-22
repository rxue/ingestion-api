restartDatabase() {
  docker compose down --volumes database 
  docker compose up -d --build database
  downloadURL=https://raw.githubusercontent.com/jberet/jsr352/refs/heads/main/jberet-core/src/main/resources/sql/jberet-postgresql.ddl
  ddlFileName=`basename $downloadURL`
  echo "Download ddl script ${ddlFileName} for generating status tracking tables used by quarkus-jberet JobRepository"
  curl -O $downloadURL
  sed -i '' 's/!!/;/g' $ddlFileName
  sleep 2
  echo "Run the ddl script"
  docker exec -i postgres psql -U postgres -d postgres < $ddlFileName
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
  docker compose down --volumes ingestion-manager 
  mvn -f ../java/ingestion-manager/pom.xml clean package -Dquarkus.package.jar.type=legacy-jar
  if [ $? -ne 0 ]; then
    echo "Project Build Failure :("
    return 1
  fi
  docker compose up -d --build ingestion-manager
}
