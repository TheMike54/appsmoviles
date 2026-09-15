# Usar una imagen base ligera de Python
FROM python:3.9-slim

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar los requerimientos primero (para aprovechar la caché de Docker)
COPY requirements.txt .

# Instalar dependencias. --retries no sirve si la conexión se corta A MEDIA descarga
# (solo reintenta fallos al conectar), así que reintentamos el comando completo hasta
# 5 veces con una pausa entre cada uno: cada intento abre una conexión nueva.
RUN for i in 1 2 3 4 5; do \
      pip install --no-cache-dir --default-timeout=60 -r requirements.txt && exit 0; \
      echo "Intento $i fallo, reintentando en 5s..."; \
      sleep 5; \
    done; \
    echo "pip install fallo despues de 5 intentos" && exit 1

# Copiar el resto del código
COPY . .

# Exponer el puerto 5000
EXPOSE 5000

# Comando para ejecutar la app
CMD ["python", "app.py"]