FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn -q package -DskipTests

FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /build/target/ms-logistics.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar"]
