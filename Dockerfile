# syntax=docker/dockerfile:1.23

FROM eclipse-temurin:22-jdk AS build

WORKDIR /app
COPY . .

RUN ./gradlew clean build -x test

FROM eclipse-temurin:22-jre AS deploy

COPY --from=build /app/build/libs/stock.jar /opt/app/stock.jar

CMD ["java", "-jar", "/opt/app/stock.jar"]
