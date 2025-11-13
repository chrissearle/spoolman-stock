FROM eclipse-temurin:22-jre

RUN mkdir -p /opt/app
COPY build/libs/stock.jar /opt/app

CMD ["java", "-jar", "/opt/app/stock.jar"]
