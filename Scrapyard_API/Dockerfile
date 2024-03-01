#FROM arm64v8/openjdk:17-jdk-buster AS builder
#COPY src/main/docker/app.jar app.jar
#EXPOSE 80
#ENTRYPOINT ["java","-jar","/app.jar"]

FROM openjdk:17-jdk-alpine
COPY target/ app.jar
ENTRYPOINT ["java","-jar","/app.jar"]