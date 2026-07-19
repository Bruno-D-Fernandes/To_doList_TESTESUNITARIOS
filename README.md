# TesteUnitarios-Mark-1 (jUnitEMosquito)

Projeto de estudo de testes unitários com JUnit 5 e Mockito sobre uma API de to-do list em Spring Boot (usuários, grupos, tarefas, tags e autenticação JWT). O objetivo é exercitar relacionamentos entre entidades e práticas de teste.

## Tecnologias

- Java 17 + Spring Boot
- Spring Web, Spring Data JPA, Spring Security
- MySQL
- JWT (java-jwt)
- JUnit 5 + Mockito


## Funcionalidades

- Cadastro e login de usuário (`/usuarios/register`, `/usuarios/login`)
- Gerenciamento de grupos (`/grupo/...`)
- Gerenciamento de tasks por grupo (criar, listar, atualizar, deletar)
- Tags por grupo (criar, listar, deletar)
- Controle de permissões por papel em usuário-grupo

## Exceções e tratamento

As exceções customizadas (grupo, usuário, task, tag, autorização) têm handlers dedicados em `src/main/java/edu/jUnitEMosquito/exception/advice/` — advices organizados por responsabilidade e um GlobalAdvice como fallback. As respostas usam `GenericExceptionResponseDTO`.

## Como rodar

1. Crie o banco com o script `dataBase/to_dos.sql`.
2. Ajuste usuário/senha/porta em `src/main/resources/application.properties` se necessário.
3. Rode a aplicação (Windows):
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```
4. Rode os testes (Windows):
   ```powershell
   .\mvnw.cmd test
   ```
(ou `./mvnw spring-boot:run` / `./mvnw test` em Unix)

## Testes

Os testes unitários estão em `src/test/java/edu/jUnitEMosquito/services/` e cobrem:
- TaskService (múltiplos cenários: sucesso, permissões, não encontrado)
- TagsService (get/create/delete + erros)
- UsuarioService (login e registro - validações)
