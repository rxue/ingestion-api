step1_1="STEP 1.1: Build the asynchronou Ingestion API project"
echo $step1_1
mvn -f ../java/async-api-endpoint/pom.xml clean package -Dquarkus.package.jar.type=legacy-jar
if [ $? -ne 0 ]; then echo "::$step1_1 Failed :(" && return 1; fi
step1_2="STEP 1.2: Start the asynchronous Ingestion API Docker container in the background"
echo $step1_2
docker compose up -d --build async-api-endpoint
step2="STEP 2: Start the Broker Docker container in the background"
echo $step2
docker compose up -d --build broker
step3_1="STEP 3.1: Build the Consumer project - ingestion-manager"
echo $step3_1
mvn -f ../java/ingestion-manager/pom.xml clean package -Dquarkus.package.jar.type=legacy-jar
if [ $? -ne 0 ]; then echo "::$step3_1 Failed :(" && return 1; fi
step3="STEP 3: Start the Consumer service -ingestion-manager"
echo $step3
docker compose up -d --build ingestion-manager

