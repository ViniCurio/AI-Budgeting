# Badgeting

Badgeting é uma aplicação backend Java baseada em Spring Boot para registro de despesas financeiras, com funcionalidades de AI para transcrição de áudio, síntese de voz e chat usando OpenAI.

## Visão geral

Funcionalidades principais:
- Persistência de transações financeiras com categorias.
- Consulta de transações por categoria.
- Exclusão de transações.
- Transcrição de áudio em texto usando OpenAI Whisper.
- Geração de áudio a partir de texto usando OpenAI TTS.
- Rotas de chat para envio de prompts ao modelo OpenAI.
- Fluxo AI completo: áudio -> transcrição -> chat -> retorno em áudio.

## Tecnologias

- Java 26
- Spring Boot 4.1.0
- Spring AI (OpenAI chat, transcrição e TTS)
- Spring Web
- Spring Data JPA
- MySQL
- Lombok
- Gradle

## Requisitos

- JDK 26 instalado
- `OPENAI_API_KEY` configurado no ambiente
- MySQL disponível ou use o `docker compose` para subir o banco de dados

## Configuração

A configuração principal fica em `src/main/resources/application.properties`.

### Banco de dados

O projeto usa MySQL e já inclui um `compose.yml` para iniciar o banco.

O JPA está configurado como `spring.jpa.hibernate.ddl-auto=update`.

## Executando o projeto

No Windows, use o wrapper Gradle local:

```bash
cd "c:/Users/Flor/Desktop/Projeto final/badgeting"
./gradlew.bat bootRun
```

Ou para buildar:

```bash
./gradlew.bat clean build
```

Para rodar os testes:

```bash
./gradlew.bat test
```

## Endpoints

### AI e chat

- `POST /api/transcribe`
  - `multipart/form-data`
  - parâmetro: `file`
  - retorna texto transcrito.

- `POST /api/synthesize`
  - `application/json`
  - body: `{ "text": "..." }`
  - retorna `audio/mp3` com voz.

- `GET /api/chat`
  - query: `prompt`
  - retorna resposta de `ChatClient`.

- `GET /api/chat-model`
  - query: `prompt`
  - retorna resposta de `OpenAiChatModel`.

### Transações

- `POST /transactions`
  - body JSON:
 
  - `amount` representa centavos.

- `GET /transactions/{category}`
  - lista transações por categoria.
  - categorias válidas: `GROCERIES`, `PHARMA`, `AUTO`, `ENTERTAINMENT`.

- `DELETE /transactions/{id}`
  - exclui a transação pelo UUID.

- `POST /transactions/ai`
  - `multipart/form-data`
  - parâmetro: `file`
  - fluxo completo:
    1. transcreve áudio para texto
    2. envia texto ao chat AI
    3. converte resposta em áudio MP3

## Modelo de domínio

- `Transaction`: descrição, valor em centavos e categoria.
- `Category`: enum com valores `GROCERIES`, `PHARMA`, `AUTO`, `ENTERTAINMENT`.
- `TransactionRepository`: interface de repositório, implementada via JPA.

## Observações

- O arquivo `src/main/resources/prompts/system-message.st` contém a mensagem de sistema usada pelo `ChatClient` de transações.
- O endpoint `POST /transactions/ai` retorna diretamente um arquivo MP3.
- O projeto usa `spring.ai.tool` para expor `persist-transaction` e `list-transactions-by-category` como ferramentas no cliente.

## Estrutura de projeto

- `src/main/java/bootcamp/badgeting` - bootstrapping e controladores REST
- `src/main/java/bootcamp/badgeting/application` - casos de uso e DTOs
- `src/main/java/bootcamp/badgeting/domain` - modelos e repositórios de domínio
- `src/main/java/bootcamp/badgeting/infrastructore/persistence` - entidades e persistência JPA
- `src/main/resources` - configurações e prompts

## Contato

Para uso local, configure sua chave OpenAI e execute o aplicativo. Se quiser adicionar uma interface web, use estes endpoints como backend REST.
