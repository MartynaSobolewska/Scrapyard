FROM arm64v8/openjdk:17-jdk-buster AS builder
COPY ./app.jar app.jar
EXPOSE 80
ENTRYPOINT ["java","-jar","/app.jar"]