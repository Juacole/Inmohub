# 1.Construcción
# Usamos una imagen con Maven y JDK completo
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos el pom padre y descargamos dependencias
COPY pom.xml .
RUN mvn clean install -N

# Copiamos el código fuente y el pom específico del servicio
COPY lead-service/pom.xml ./lead-service/pom.xml
COPY lead-service/src ./lead-service/src

# Empaquetamos el jar saltando los tests para agilizar la construcción en Docker
RUN mvn -f lead-service/pom.xml clean package -DskipTests

# 2. Ejecución
# Usamos una imagen JRE ligera
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiamos solo el artefacto compilado (JAR) de la etapa anterior
COPY --from=build /app/lead-service/target/*.jar app.jar

# Exponemos el puerto estándar de Eureka
EXPOSE 8083

# Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]