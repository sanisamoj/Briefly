# Use a imagem base oficial do OpenJDK
FROM openjdk:22-jdk

# Defina o diretório de trabalho no contêiner
WORKDIR /app

# Copie o arquivo .env para o diretório de trabalho
COPY ../../../.env .

# Copie o arquivo .jar para o diretório de trabalho
COPY snapurl.jar .

# Define a variável de ambiente para o Java
#ENV JAVA_OPTS=""

# Comando para rodar a aplicação
CMD ["sh", "-c", "java -jar snapurl.jar"]
