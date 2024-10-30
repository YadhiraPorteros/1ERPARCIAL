# Usa una imagen de Gradle para compilar el proyecto
FROM gradle:7.6.0-jdk17 AS build
WORKDIR /app
COPY . .

# Compila el proyecto y genera el JAR
RUN gradle clean build -x test

# Usa una imagen de Java para ejecutar el JAR
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copia el JAR generado desde la etapa de compilación
COPY --from=build /app/build/libs/*.jar app.jar

# Expone el puerto 2003
EXPOSE 2003

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]



