cd scrapyard
mvn package
cd ../scrapyard-config-server
mvn package
cd ../scrapyard-service-discovery
mvn package
cd ..
docker-compose down
docker-compose build
docker-compose up -d