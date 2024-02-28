cp -f ./target/Scrapyard_API-0.0.1-SNAPSHOT.jar ./src/main/docker/app.jar
cd src/main/docker

docker build -t scrapyard:v1 .