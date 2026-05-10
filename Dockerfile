# Bước 1: Build file JAR từ mã nguồn
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Bước 2: Chạy ứng dụng với JDK gọn nhẹ
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/cayduoclieu-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]