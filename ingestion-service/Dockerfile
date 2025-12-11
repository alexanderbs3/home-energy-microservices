FROM eclipse-temurin:21-jre
WORKDIR /app
# Copia o JAR já compilado no host
COPY target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]