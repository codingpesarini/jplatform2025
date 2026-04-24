FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/jplatform-1.0.0.jar app.jar
VOLUME /app/uploads
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]