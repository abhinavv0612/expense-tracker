FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

WORKDIR /app/backend

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

CMD ["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]