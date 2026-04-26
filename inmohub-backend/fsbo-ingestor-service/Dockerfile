FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn clean install -N

COPY fsbo-ingestor-service/pom.xml ./fsbo-ingestor-service/pom.xml
COPY fsbo-ingestor-service/src ./fsbo-ingestor-service/src
RUN mvn -f fsbo-ingestor-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/fsbo-ingestor-service/target/*.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]