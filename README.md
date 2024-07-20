# Caju Credit Card Authorizer
Solução para o desafio tecnico da Caju abordando a implementação de um sistema de autorização de cartão de crédito (de beneficios).

## Sobre o desafio
Ver requisitos do projeto nesse [link.](https://caju.notion.site/Desafio-T-cnico-para-fazer-em-casa-218d49808fe14a4189c3ca664857de72)

## Tecnologias
- Java 21 / Maven 3
- Spring Boot 3
- PostgreSQL
- Redis
- Flyway
- TestContainers
- Docker

## Como rodar projeto localmente
1. Clone o repositório `git clone https://github.com/cylonsam/caju-credit-card-authorizer.git`
2. Faça o build do projeto `mvn clean package`
3. Rode o docker-compose dentro de ./deploy/local `docker-compose -f ./deploy/local/docker-compose.yml up -d`
4. Acesse o projeto em http://localhost:8080

Obs. 1: O projeto utiliza as portas 8080 e 5432 para a aplicação e o PostgreSQL, respectivamente. 
Então é importante verificar se as portas não estão sendo utilizadas por outras aplicações.

Obs. 2: Rodar o docker-compose cria automaticamente um Account, que pode ser utilizada para testes.
Os detalhes dessa account podem ser vistos no arquivo init.sql dentro do diretório deploy/local.

## API

### POST /authorize
Endpoint para autorizar o uso do cartão de crédito de acordo com algum MCC.

#### Payload
```json
{
  "account": "string",
  "totalAmount": "number"
  "mcc": "string",
  "merchant": "string"
}
```

#### CURL da requisição
```bash
curl -X POST \
  http://localhost:8080/authorize \
  -H 'Content-Type: application/json' \
  -d '{
  "account": "1",
  "totalAmount": 100.00,
  "mcc": "5811",
  "merchant": "UBER TRIP                   SAO PAULO BR"
}
```
#### Response
O status code da resposta é sempre `200`.

- Autorização bem sucedida
```json
{
  "code": "00"
}
```

- Não autorizado devido a saldo insuficiente para o mcc e consequentemente a categoria de tranação associada (FOOD, MEAL ou CASH)
```json
{
  "code": "51"
}
```

- Não autorizado devido a um erro qualquer, podendo ser um erro do corpo da requisitação, conta não encontrada, etc.
```json
{
  "code": "07"
}
```

### POST /merchant

Endpoint para associar um merchant a um mcc. O valor associado terá precedência sobre o valor oriundo do payload de transação.

#### Payload
```json
{
  "mcc": "string",
  "merchant": "string"
}
```

#### CURL da requisição
```bash
curl -X POST \
  http://localhost:8080/merchant \
  -H 'Content-Type: application/json' \
  -d '{
  "mcc": "5811",
  "merchant": "UBER TRIP                   SAO PAULO BR"
}
```

#### Response

- Merchant associado com sucesso (status code 201)
```json
{
  "code": "00"
}
```

- Erro (status code 200)
```json
{
  "code": "07"
}