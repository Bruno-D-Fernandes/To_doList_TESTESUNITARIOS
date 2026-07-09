# TesteUnitarios-Mark-1 (jUnitEMosquito)

Projeto de estudo de **testes unitários com JUnit 5 e Mockito**, feito sobre 
uma API de to-do list em **Spring Boot** 
(usuários, grupos e tarefas, com login via JWT).
A ideia aqui é trazer relacionamentos complexos entre entidades para 
consolidar alguns conhecimentos e aprender e por prática de testes.

## Tecnologias

- Java 17 + Spring Boot
- Spring Web, Spring Data JPA, Spring Security
- MySQL
- JWT (java-jwt)
- JUnit 5 + Mockito git s

*A colocar:*

- OpenApi 
- Testes com SpringBootTest
- Valid nos DTO's

## Funcionalidades 
    (Presentes atualmente)

- Cadastro e login de usuário (`/usuarios/register`, `/usuarios/login`)
- Criar, listar e deletar grupos (`/grupo/...`)
- Tarefas organizadas por grupo, com tags e status
- Temos grupos de tasks, que mais de um usuário pode participar, muitos relacionamentos N:N etc

## Como rodar

1. Crie o banco com o script `dataBase/to_dos.sql`
2. Ajuste usuário/senha/porta em `src/main/resources/application.properties` se precisar
3. Rode a aplicação:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Rode os testes:
   ```bash
   ./mvnw test
   ```

## Testes

Os testes ficam em `src/test/java/...` e até o momento cobrem criação, listagem e remoção de grupos. Testes de `mergeGroup` e do `UsuarioService` ainda estão pendentes.

## Autor

[Bruno-D-Fernandes](https://github.com/Bruno-D-Fernandes)