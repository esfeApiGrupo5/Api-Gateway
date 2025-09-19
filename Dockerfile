# -- FASE 1: CONSTRUCCIÓN --
# Utiliza una imagen base de Maven con OpenJDK 21 para compilar el proyecto.
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Establece el directorio de trabajo dentro del contenedor.
WORKDIR /app

# Copia los archivos de configuración y el código fuente a la imagen de construcción.
COPY pom.xml .
COPY src ./src

# Compila la aplicación, saltando las pruebas para acelerar el proceso de construcción.
# Utiliza un caché de Maven para que las dependencias se descarguen una sola vez.
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests


# -- FASE 2: EJECUCIÓN --
# Utiliza una imagen ligera de OpenJDK 21 para la fase final, que solo contiene lo necesario para ejecutar la aplicación.
FROM openjdk:21-jdk-slim

# Establece el directorio de trabajo para el JAR final.
WORKDIR /app

# Copia el archivo JAR desde la fase de construcción a la imagen de ejecución.
# El nombre del JAR se basa en el 'artifactId' y la 'version' de tu pom.xml.
COPY --from=build /app/target/ApiGateway-0.0.1-SNAPSHOT.jar ApiGateway.jar

# Expone el puerto 8080, que es el puerto definido en tu application.properties.
EXPOSE 8080

# Comando para ejecutar la aplicación.
# Inicia el archivo JAR con la máquina virtual de Java.
CMD ["java", "-jar", "ApiGateway.jar"]