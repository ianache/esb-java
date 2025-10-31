# ==========================================================
# ETAPA 1: BUILDER (Compilación de la aplicación Java)
# ... (Sin cambios aquí)
# ==========================================================
FROM maven:3.8.7-openjdk-18 AS builder
WORKDIR /app
COPY pom.xml .
COPY api ./api
COPY esb_framework ./esb_framework
RUN mvn dependency:go-offline
RUN mvn clean install -DskipTests

# ==========================================================
# ETAPA 2: RUNTIME (Entorno de Ejecución Final)
# ==========================================================
FROM amazoncorretto:21-alpine3.19
#FROM amazoncorretto:21-jdk-alpine3.19

WORKDIR /app

# 1. DEFINIR VERSIÓN DE NODEJS Y FUSIONAR TODAS LAS INSTALACIONES Y LIMPIEZAS
ENV NODE_VERSION=20.10.0
ENV NODE_TMP_FILE="node-v${NODE_VERSION}-linux-x64.tar.xz"

RUN NODE_VERSION=${NODE_VERSION} && \
    apk update && \
    apk add --no-cache \
        python3 \
        py3-pip \
        nodejs-lts \
        npm \
        # Dependencias comunes de construcción para Python
        build-base \
        linux-headers \
        # Dependencias para descargar Node.js
        curl \
        tar \
        xz \
    && \
   # 3. LIMPIEZA FINAL: Eliminar herramientas de construcción
    # NO TOCAMOS curl, tar, o xz AQUÍ para evitar conflictos.
    apk del build-base linux-headers && \
    rm -rf /var/cache/apk/* /tmp/* /var/log/*

RUN apk add --no-cache nodejs-lts

ENV PATH="/usr/local/bin:$PATH"

# 4. PRUEBA DE INSTALACIÓN (Opcional, pero útil)
RUN java -version
RUN python3 --version
RUN node -v
RUN npm -v

# 5. Copiar dependencias de Python/Node.js e instalarlas
COPY requirements.txt .
# COPY package.json . # Asume que tienes un package.json
#RUN pip install --no-cache-dir -r requirements.txt
# RUN npm ci # Habilitar si usas package.json

# Crear un entorno virtual en un directorio temporal, instalar dependencias, y limpiarlo.
# Usamos /opt/venv como directorio persistente para el entorno virtual.
RUN python3 -m venv /opt/venv && \
    . /opt/venv/bin/activate && \
    pip install --no-cache-dir -r requirements.txt && \
    # Desactivar venv (opcional, pero buena práctica)
    deactivate

# 6. Asegurar que los binarios del venv estén en el PATH para la ejecución final
ENV PATH="/opt/venv/bin:$PATH"

# 6. Copiar el JAR compilado
# Revisa la ruta: /app/api/target/api.jar es correcto si tu proyecto es multi-módulo
COPY --from=builder /app/api/target/api-*jar /app/api.jar

# 7. Comando de Ejecución
CMD ["java", "-jar", "api.jar"]

