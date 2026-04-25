# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
mvn spring-boot:run            # Démarrer l'application (port 8080)
mvn clean package              # Builder le JAR
mvn clean test                 # Lancer tous les tests
mvn test -Dtest=NomDuTest      # Lancer un test unique
mvn clean package -DskipTests  # Builder sans tests
```

## Stack

- **Kotlin 2.1.21** / **Java 17** / **Spring Boot 3.4.4** / **Maven**
- **Spring Data JPA** + **H2** in-memory (`create-drop` — données perdues à l'arrêt)
- H2 Console disponible sur `http://localhost:8080/h2-console` (JDBC URL : `jdbc:h2:mem:tododb`)

## Architecture hexagonale

```
domain/
  model/        → Todo (data class pure, zéro dépendance framework)
  port/input/   → TodoUseCase (interface : contrat entrant)
  port/output/  → TodoRepositoryPort (interface : contrat sortant)
  service/      → TodoService (@Service, implémente TodoUseCase, injecte TodoRepositoryPort)

adapter/input/web/
  TodoController.kt          → REST adapter, injecte TodoUseCase (jamais TodoService directement)
  GlobalExceptionHandler.kt  → NoSuchElementException→404, IllegalArgumentException→400
  dto/                       → CreateTodoRequest, UpdateTodoRequest, TodoResponse

adapter/output/persistence/
  TodoPersistenceAdapter.kt  → @Component, implémente TodoRepositoryPort
  TodoJpaRepository.kt       → Spring Data JPA (étend JpaRepository<TodoEntity, Long>)
  TodoEntity.kt              → entité JPA (@Entity), indépendante du domaine
```

**Règle de dépendance :** le domaine ne dépend de rien ; les adapters dépendent des ports.

## Conventions de mapping

- **Domain → JSON** : `TodoResponse.from(todo)` (companion object dans `TodoResponse.kt`)
- **Domain ↔ JPA Entity** : fonctions d'extension privées dans `TodoPersistenceAdapter` (`Todo.toEntity()` / `TodoEntity.toDomain()`)
- Les controllers passent des paramètres scalaires aux méthodes du port (pas les DTOs bruts)

## Ajouter un nouveau use case

1. Ajouter la méthode dans `TodoUseCase` (port input)
2. Implémenter dans `TodoService`
3. Si besoin de persistence : ajouter dans `TodoRepositoryPort` puis `TodoPersistenceAdapter`
4. Exposer dans `TodoController`
