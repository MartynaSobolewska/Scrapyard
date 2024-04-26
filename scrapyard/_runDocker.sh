docker compose down
cd scrapyard
mvn clean install
cd ../scrapyard-config-server
mvn clean install
cd ../scrapyard-service-discovery
mvn clean install
cd ../gateway
mvn clean install
cd ..
docker compose up