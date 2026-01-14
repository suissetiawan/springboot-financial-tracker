# =========================
# BUILD STAGE
# =========================
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy maven files first (cache friendly)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# copy source code
COPY src src
RUN ./mvnw clean package -DskipTests

# =========================
# RUNTIME STAGE
# =========================
FROM eclipse-temurin:21-jre
WORKDIR /app

# copy the jar file from build stage
COPY --from=build /app/target/*.jar /app/app.jar

# JVM options (optional but recommended)
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"

# Expose the port
EXPOSE 8080

# Run the application
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
