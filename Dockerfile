# ==========================================
# ETAPA 1: BUILD (Compilación y empaquetado)
# ==========================================
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# 1. Copiamos el Maven Wrapper y el pom.xml primero
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# 2. Damos permisos de ejecución y descargamos dependencias offline
RUN chmod +x mvnw && ./mvnw dependency:go-offline

# 3. Copiamos el código fuente y empaquetamos el JAR
COPY src ./src
RUN ./mvnw clean package -DskipTests

# ==========================================
# ETAPA 2: RUNTIME (Ejecución ligera)
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 4. Creamos un usuario sin privilegios de administrador (root)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 5. Copiamos ÚNICAMENTE el JAR generado desde la etapa 1
COPY --from=builder /app/target/app.jar app.jar

# 6. Exponemos el puerto y arrancamos la aplicación
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]