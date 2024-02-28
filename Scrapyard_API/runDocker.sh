./mvnw clean package
./_buildDocker.sh
docker run -p 8080:8080 scrapyard:v1