FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
COPY pom.xml /build/
WORKDIR /build/
RUN mvn dependency:go-offline
COPY src /build/src
RUN mvn package -DskipTests

#RUN STAGE
FROM eclipse-temurin:21-jre-alpine
ARG JAR_FILE=/build/target/*.jar
COPY --from=builder $JAR_FILE /opt/MyMessenger-Server/app.jar
ENTRYPOINT ["java","-jar","/opt/MyMessenger-Server/app.jar"]