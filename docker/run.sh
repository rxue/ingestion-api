step1="STEP 1: Build the asynchronou Ingestion API project"
echo $step1
mvn -f ../java/async-api-endpoint/pom.xml clean package -Dquarkus.package.jar.type=legacy-jar
if [ $? -ne 0 ]; then echo "::$step1 Failed :(" && return 1; fi
step2="STEP 2: Start the asynchronous Ingestion API Docker container in the background"
echo $step2
docker compose up -d --build async-api-endpoint
step3="STEP 3: Start the Broker Docker container in the background"
echo $step3
docker compose up -d --build broker 

