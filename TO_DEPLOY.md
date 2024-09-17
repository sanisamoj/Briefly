# Briefly para deploy

Sugere-se para a instalação do projeto em modo de produção instalar e rodar o mongodb e a aplicação dentro de uma rede Docker.

#### Primeiro crie uma rede com o seguinte comando:
    
    docker network create my_network

#### Após, execute o container do mongodb e mapeie-o para a rede criada anteriormente.

    docker run --name mongodb --network my_network -d mongo

Após, transfira um arquivo index.html para /files (pasta no mesmo diretório de src: diretório/files, diretório/src), para que uma página HTML seja entregue ao requisitar a rota principal.

Transfira também os bancos de dados para a pesquisa IPs, para /geo.

#### Construa a imagem do briefly:

    docker build -t briefly:latest .

#### E execute a imagem mapeando-a também para a rede.

    docker run --name briefly -p 9098:9098 --network  my_network briefly:latest

Para mapear as imagens, a página e o banco de dados de IP para fora do container, um exemplo:
docker run --rm -v /root/images/uploads:/app/uploads -v /root/geo:/app/briefly_resources/geo -v /root/files:/app/briefly_resources/files --network default_network -p 9098:9098 briefly:latest


## Para o backup, sugere-se:
#### Entrar no Docker

    docker exec -it <nome-ou-id-do-container> bash

#### Realizar o backup

    mongodump --out /backup --db <nome-do-banco>

#### E copiar o backup para fora do container:

    docker cp <nome-ou-id-do-container>:/backup /caminho/local

Sugere-se transferir o backup para outra máquina. 