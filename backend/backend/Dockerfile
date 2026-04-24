FROM eclipse-temurin:17-jdk

WORKDIR /app

# copy everything from current folder (backend/backend)
COPY . .

# give permission
RUN chmod +x mvnw

# build jar
RUN ./mvnw clean package -DskipTests

# run app
CMD ["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]