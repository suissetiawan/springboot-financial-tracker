# =========================
# BUILD STAGE
# =========================
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy maven config first (cache friendly)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src src
RUN ./mvnw clean package -DskipTests

# =========================
# RUNTIME STAGE
# =========================
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy jar only
COPY --from=build /app/target/*.jar app.jar

# JVM options for container
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]