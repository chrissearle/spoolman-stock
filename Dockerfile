# syntax=docker/dockerfile:1.26

FROM eclipse-temurin:25-jdk AS build

WORKDIR /app
COPY . .

RUN ./gradlew clean installDist \
    && mkdir -p /app/appjar \
    && mv /app/build/install/stock/lib/stock-*.jar /app/appjar/

FROM eclipse-temurin:25-jre AS deploy

COPY --from=build /app/build/install/stock/bin /opt/app/bin
COPY --from=build /app/build/install/stock/lib /opt/app/lib

COPY --from=build /app/appjar/ /opt/app/lib/

CMD ["/opt/app/bin/stock"]
