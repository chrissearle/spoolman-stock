# syntax=docker/dockerfile:1.7

FROM --platform=$BUILDPLATFORM eclipse-temurin:25.0.1_8-jdk AS build

WORKDIR /app
COPY . .

RUN ./gradlew clean build

FROM eclipse-temurin:25.0.1_8-jre AS deploy

COPY --from=build /app/build/libs/stock.jar /opt/app/stock.jar

CMD ["java", "-jar", "/opt/app/stock.jar"]
