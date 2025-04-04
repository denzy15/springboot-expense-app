# Используем официальный образ Maven для сборки
FROM maven:3.8.4-openjdk-17-slim AS build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файл pom.xml и загружаем зависимости
COPY pom.xml .
RUN mvn dependency:go-offline

# Копируем исходный код проекта
COPY src /app/src

# Сборка приложения
RUN mvn clean package -DskipTests

# Используем официальный образ OpenJDK 17 для запуска приложения
FROM openjdk:17-jdk-slim

# Устанавливаем рабочую директорию для приложения
WORKDIR /app

# Копируем собранный JAR файл из предыдущего этапа
COPY --from=build /app/target/finance-service.jar /app/finance-service.jar

# Открываем порт для приложения
EXPOSE 8080

# Команда для запуска приложения
CMD ["java", "-jar", "finance-service.jar"]
