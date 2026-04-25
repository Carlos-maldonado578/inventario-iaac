# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot 3.5.14 inventory management application (`com.tienda.inventario`) using Java 17, JPA/Hibernate with an H2 in-memory database, and Maven as the build tool.

## Commands

```bash
# Run the application
./mvnw spring-boot:run

# Build JAR
./mvnw clean package

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=InventarioApplicationTests
```

## Runtime Access Points

- Application: `http://localhost:8080`
- H2 Console (browser DB browser): `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:inventariodb`, user: `sa`, no password

## Architecture

This is a scaffold — no controllers, services, or entities exist yet. The intended 3-tier structure follows the base package `com.tienda.inventario`:

| Layer | Package suffix | Responsibility |
|-------|---------------|----------------|
| Presentation | `.controller` | REST endpoints |
| Business | `.service` | Business logic |
| Data | `.entity` + `.repository` | JPA models and Spring Data repositories |

**Database:** H2 in-memory, schema recreated on each startup (`ddl-auto=create-drop`). SQL logs are enabled (`show-sql=true`). For persistence across restarts, switch to a file-based H2 URL or an external DB and change `ddl-auto` to `update` or use Flyway/Liquibase migrations.
