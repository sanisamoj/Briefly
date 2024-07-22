# Briefly
Este é um servidor de encurtador de links desenvolvido em Kotlin utilizando o Ktor. O servidor permite criar URLs curtas que redirecionam para URLs longas, 
oferecendo funcionalidades adicionais como contagem de cliques únicos e gerenciamento de links.

Será um projeto aberto, destinado a ser incluído em um portfólio e oferecerá um nível gratuito onde todos os links gerados estarão disponíveis por um ano.

## Informações Coletadas das Conexões

- IP: O endereço IP do usuário será registrado para fins de análise e segurança.
- Geolocalização: Com base no endereço IP, a localização geográfica do usuário será determinada.
- Horário: O timestamp de cada conexão será registrado.
- Dispositivo: Informações sobre o dispositivo do usuário, incluindo tipo (desktop, mobile), sistema operacional (Windows, iOS, Android, etc.) e navegador (Chrome, Firefox, Safari, etc.).
- URL de Referência: A URL da qual o usuário foi redirecionado para o link encurtado.

## Informações dos Links

- Cliques: Número total de cliques em cada link encurtado.
- Filtros de Análise:
  - Por Horário: Quantidade de cliques por hora, dia, semana e mês.
  - Por Região: Distribuição geográfica dos cliques.
  - Por Dispositivo: Análise dos cliques com base no tipo de dispositivo, sistema operacional e navegador.

## Funcionalidades dos Links

- Links Expiráveis: Possibilidade de criar links que expiram após um determinado período.
- QR Code: Possibilidade de compartilhar um QR Code que contém o link encurtado.
- Nível Gratuito:
  - Todos os links criados no nível gratuito terão uma validade de um ano a partir da data de criação.
  - Após um ano, os links expirarão automaticamente e não estarão mais acessíveis.

## Funcionalidades do Sistema

- Possibilidade de encurtar links (Estando ou não logado).
- Possibilidade de acompanhar informações coletadas de clicks dos links.
- Gerenciar links como atualizar status ou removê-lo.
- Há uma verificação pela madrugada de links expirados, e são desativados.
- Uma parte para um moderador gerenciar o estado da aplicação.

## Tecnologias e Ferramentas Utilizadas

- Backend: **Ktor (Kotlin)**
- Banco de Dados: 
    - **MongoDB** - Para armazenamento de dados sensíveis.
    - **Redis** - Para armazenamento de dados que precisam de acesso mais rápido.
- Geolocalização: API de Geolocalização (API Não escolhida ainda.)
- Integrações: APIs para coleta de informações de IP (API Não escolhida ainda.)

## Padrões de Design Utilizados

- Factory
     - O projeto utiliza a padrão factory, para auxiliar na criação dos objetos, para respostas de requisições ou para registro no banco de dados por exemplo.
- Injeção de Dependência
     - A Injeção de Dependência (DI) foi escolhida pois facilita os testes, podendo tanto trocar a implementações de registros, como também na expansão do projeto.

## Testes Utilizados

- Testes unitários na maior parte das funções dos Serviços, e nas rotas.

## Para instalação
Para instalar o projeto para testes, utilizaremos o Docker.

- Instale a última versão do **Docker** em sua máquina.
- Instale o **Mongodb** (Verifique na página oficial, ou monte uma imagem com o Docker).
- Instale o **Redis** na sua máquina (Verifique a página oficial, ou monte uma imagem com o Docker).
- Abra a pasta **"out"** do projeto, note que terá apenas 2 arquivos, o **Dockerfile**, e **briefly.jar**.
- Crie um arquivo **.env**, ou adicione um arquivo **.env** manualmente na construção da imagem docker.

```.env
#URL do banco de dados MONGODB
MONGODB_SERVER_URL=mongodb://host.docker.internal:27017
#Nome do banco de dados do MONGODB
NAME_DATABASE=Briefly
#URL do banco de dados do REDIS
REDIS_SERVER_URL=host.docker.internal
#Porta do banco de dados do REDIS
REDIS_SERVER_PORT=6379
#URl no qual a aplicação será instalada / Domain
SELF_URL=http://localhost:9098

#Audience do token, quem deve processar o token
JWT_AUDIENCE=
#Dominio do token, quem foi o emissor
JWT_DOMAIN=
#Secret Token do usuário
USER_SECRET=
#Secret Token do moderador
MODERATOR_SECRET=

#Configuração para envios de email
SMTP_HOST=smtp.gmail.com
SMTP_STARTTLS_ENABLE=true
SMTP_SSL_PROTOCOLS=TLSv1.2
SMTP_SOCKETFACTORY_PORT=465
SMTP_SOCKETFACTORY_CLASS=javax.net.ssl.SSLSocketFactory
SMTP_AUTH=true
SMTP_PORT=465
SMTP_SSL_TRUST=*

#Email que irá ser associado a aplicação para autenticação do serviço de email
EMAIL_SYSTEM=
#Senha do email para autenticação do serviço de email
EMAIL_PASSWORD=

#Link dos termos de serviço
TERMS_OF_SERVICE_LINK=

#IP-INFO | Token
IP_INFO_TOKEN=
```
> *MONGODB_SERVER_URL*=mongodb://host.docker.internal:27017 - Esta configuração serve para que a aplicação se conecte ao Mongodb localizado no localhost da máquina.

> *REDIS_SERVER_URL*=host.docker.internal - Esta configuração serve para que a aplicação se conecte ao Redis localizado no localhost da máquina.

> Nas configurações de Email, foi adicionado uma pré-configuração para utilizar os serviços do Gmail. Para a Senha do Email, é necessário gerar uma senha para aplicação em "Apps menos seguros" na sua conta do Gmail.

> Para coleta de dados como região dos IPs, estou utilizando a API do IP Info https://ipinfo.io/

#### Execute o comando a seguir para construir a imagem Docker.

    docker build -t snapurl:latest .

#### Execute o comando a seguir para executar a imagem criada com o Docker.

    docker run -p 9098:9098 snapurl:latest

> As portas pré-definidas podem ser alteradas no arquivo *"aplication.conf"*, e devem ser refletidas na construção da imagem com o Docker.


## Endpoints disponíveis
No momento apenas alguns endpoints estão disponíveis, e estão hospedados na página de endpoints do Postman.
[https://documenter.getpostman.com/view/29175154/2sA3e1BqN4](https://documenter.getpostman.com/view/29175154/2sA3e1BqN4)

## Protótipo
No presente momento estou dispondo de uma página muito humilde para testes.
https://www.sanisamojrepository.com/briefly/
