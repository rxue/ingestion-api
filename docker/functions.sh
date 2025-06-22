restartDatabase() {
  docker compose down --volumes database 
  docker compose up -d --build database
}
restartIngestionBackend() {
  docker compose down --volumes ingestion-backend
  configIngestionManagerJobRepository
  mvn -f ${projectDir}/pom.xml clean package -Dquarkus.package.jar.type=legacy-jar
  if [ $? -ne 0 ]; then
    echo "Project Build Failure :("
    return 1
  fi
  docker compose up -d --build ingestion-backend
}
configIngestionManagerJobRepository() {
  local downloadURL=https://raw.githubusercontent.com/jberet/jsr352/refs/heads/main/jberet-core/src/main/resources/sql/jberet-postgresql.ddl
  local ddlFileName=`basename $downloadURL`
  if [ ! -e "${ddlFileName}" ]; then
    curl -O "$downloadURL"
    echo "Download $downloadURL completed"
  fi
  if [ ! -e "${ddlFileName}" ]; then
    echo "file does not exist yet!"
    return
  fi
  projectDir=../ingestion-backend
  projectResourcesDir=${projectDir}/src/main/resources
  mv $ddlFileName ${projectResourcesDir}
  quarkusDDLProperty=quarkus.jberet.repository.jdbc.ddl-file
  configFilePath=${projectResourcesDir}/application.properties
  if ! grep "$quarkusDDLProperty" ${configFilePath}; then
    echo "${quarkusDDLProperty}=${ddlFileName}" >> ${configFilePath}
  fi
}
