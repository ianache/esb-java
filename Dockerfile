# ==========================================================
# ETAPA 1: BUILDER (Compilación de la aplicación Java)
# ==========================================================
# Usamos una imagen de Maven que ya tiene el JDK y las herramientas de compilación
FROM maven:3.9.6-openjdk-17 AS builder

WORKDIR /app

# 1. Copiar el código fuente del proyecto Java
# Copia el pom.xml primero para aprovechar el cache de las dependencias
COPY pom.xml .
# Descarga las dependencias Maven. Esta capa solo se reconstruye si el pom.xml cambia.
RUN mvn dependency:go-offline

# Copia el resto del código fuente
COPY src/ ./src/

# 2. Compilar el proyecto y generar el JAR ejecutable
# El comando 'package' compila y empaqueta la aplicación
RUN mvn clean package -DskipTests

# ==========================================================
# ETAPA 2: RUNTIME (Entorno de Ejecución Final)
# ==========================================================
# Usar una imagen base ligera de Python (ya que incluye la librería C necesaria para Node.js)
FROM python:3.11-slim

WORKDIR /app

# 1. INSTALACIÓN DE NODE.JS Y DEPENDENCIAS
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        curl \
        # Instalar un JRE ligero para ejecutar el JAR (si no se instala el JDK)
        # openjdk-17-jre-headless es más ligero que el JDK completo
        openjdk-17-jre-headless \
    && rm -rf /var/lib/apt/lists/*

# 2. Instalar Node.js para JavaScript
RUN curl -fsSL https://deb.nodesource.com/setup_lts.x | bash - && \
    apt-get install -y nodejs

# 3. Copiar dependencias de Python/Node.js e instalarlas
# Estas dependencias NO se compilaron en la Etapa 1
COPY requirements.txt .
COPY package.json .
RUN pip install --no-cache-dir -r requirements.txt
RUN npm ci

# 4. Copiar el JAR compilado desde la etapa 'builder'
# La ruta 'target/...' es estándar para Maven
COPY --from=builder /app/target/*.jar /app/your-app.jar

# 5. Comando de Ejecución (inicia la aplicación Java)
CMD ["java", "-jar", "your-app.jar"]