FROM openjdk:21-jdk-alpine

WORKDIR /app

COPY build/libs/caju-credit-card-authorizer-0.0.1-SNAPSHOT.jar /app/caju-credit-card-authorizer.jar

EXPOSE 8080

CMD ["java", "-jar", "caju-credit-card-authorizer.jar"]