FROM eclipse-temurin:22-jdk AS build

WORKDIR /app
COPY . .

RUN ./gradlew clean build

FROM eclipse-temurin:22-jre AS deploy

COPY --from=build /app/build/libs/stock.jar /opt/app/stock.jar

CMD ["java", "-jar", "/opt/app/stock.jar"]
