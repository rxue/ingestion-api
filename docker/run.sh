mvn -f ../java/async-api-endpoint/pom.xml package -Dquarkus.package.jar.type=legacy-jar
if [ $? -ne 0 ]; then return 1; fi
docker compose up async-api-endpoint

