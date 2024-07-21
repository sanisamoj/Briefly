# Use a imagem base oficial do OpenJDK
FROM openjdk:22-jdk

# Defina o diretório de trabalho no contêiner
WORKDIR /app

# Copie o arquivo .env para o diretório de trabalho
COPY ../../../.env .

# Copie o arquivo .jar para o diretório de trabalho
COPY briefly.jar .

EXPOSE 9098

# Comando para rodar a aplicação
CMD ["sh", "-c", "java -jar briefly.jar"]
