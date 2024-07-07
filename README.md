# Snapurl
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
- Nível Gratuito:
 - Todos os links criados no nível gratuito terão uma validade de um ano a partir da data de criação.
 - Após um ano, os links expirarão automaticamente e não estarão mais acessíveis.


## Tecnologias e Ferramentas Utilizadas

- Backend: Ktor (Kotlin)
- Banco de Dados: MongoDB (para armazenamento de dados)
- Geolocalização: API de Geolocalização (API Não escolhida ainda.)
- Integrações: APIs para coleta de informações de IP (API Não escolhida ainda.)
