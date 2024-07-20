FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/caju-credit-card-authorizer-0.0.1-SNAPSHOT.jar /app/caju-credit-card-authorizer.jar

EXPOSE 8080

CMD ["java", "-jar", "caju-credit-card-authorizer.jar"]