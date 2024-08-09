docker compose down
cd scrapyard || exit
mvn clean install
cd ../auth-service || exit
mvn clean install
cd ../scrapyard-config-server || exit
mvn clean install
cd ../scrapyard-service-discovery || exit
mvn clean install
cd ../gateway || exit
mvn clean install
cd ..
docker compose up