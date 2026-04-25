# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
mvn spring-boot:run -pl bootstrap          # Démarrer l'application (port 8080)
mvn clean package                          # Builder tous les modules
mvn clean test                             # Lancer tous les tests
mvn test -pl bootstrap -am                 # Tests du module bootstrap (intégration)
mvn test -pl adapter-web -am               # Tests du module adapter-web (slice)
mvn test -pl domain -am                    # Tests du module domain (unitaires)
mvn test -Dtest=NomDuTest -pl <module> -am # Lancer un test unique
mvn clean package -DskipTests             # Builder sans tests
```

## Stack

- **Kotlin 2.1.21** / **Java 17** / **Spring Boot 3.4.4** / **Maven multi-module**
- **Spring Data JPA** + **H2** in-memory (`create-drop` — données perdues à l'arrêt)
- H2 Console disponible sur `http://localhost:8080/h2-console` (JDBC URL : `jdbc:h2:mem:tododb`)
- **JWT** (jjwt 0.12.6) + Spring Security pour l'authentification
- **Swagger UI** sur `http://localhost:8080/swagger-ui.html`
- **CI GitHub Actions** : tests lancés sur chaque push (`main`) et chaque PR

## Structure multi-module Maven

```
claude-todo-api/          → parent POM (packaging=pom)
├── domain/               → cœur métier, zéro dépendance Spring
├── adapter-web/          → REST + Spring Security (web layer)
├── adapter-persistence/  → JPA + H2
├── adapter-security/     → JWT (JwtService, TokenBlacklistService)
└── bootstrap/            → point d'entrée, câblage Spring, tests d'intégration
```

**Règle de dépendance des modules :**
- `domain` → aucun module interne
- `adapter-*` → `domain` uniquement
- `bootstrap` → tous les `adapter-*` + `domain`

## Architecture hexagonale

```
domain/
  annotation/   → @DomainService (annotation Kotlin pure, sans Spring)
  model/        → Todo, User (data classes, zéro dépendance framework)
  port/input/   → TodoUseCase, AuthUseCase (contrats entrants)
  port/output/  → TodoRepositoryPort, UserRepositoryPort, TokenPort, TokenBlacklistPort
  service/      → TodoService, AuthService (@DomainService, pas @Service Spring)

adapter-web/adapter/input/web/
  TodoController.kt           → REST, injecte TodoUseCase (jamais TodoService directement)
  AuthController.kt           → login / logout
  GlobalExceptionHandler.kt   → NoSuchElementException→404, IllegalArgumentException→400
  JwtAuthenticationFilter.kt  → valide le token via TokenPort et TokenBlacklistPort (ports)
  SecurityConfig.kt           → configuration Spring Security stateless
  UserDetailsServiceAdapter.kt→ implémente UserDetailsService via UserRepositoryPort
  dto/                        → CreateTodoRequest, UpdateTodoRequest, TodoResponse, AuthDto

adapter-persistence/adapter/output/persistence/
  TodoPersistenceAdapter.kt   → implémente TodoRepositoryPort
  UserPersistenceAdapter.kt   → implémente UserRepositoryPort
  TodoEntity.kt, UserEntity.kt→ entités JPA, indépendantes du domaine
  *JpaRepository.kt           → Spring Data JPA

adapter-security/adapter/output/security/
  JwtService.kt               → implémente TokenPort (génération/validation JWT)
  TokenBlacklistService.kt    → implémente TokenBlacklistPort (liste noire en mémoire)

bootstrap/
  TodoApplication.kt          → @SpringBootApplication
  config/DomainConfig.kt      → déclare TodoService et AuthService comme @Bean Spring
  config/DataInitializer.kt   → crée l'utilisateur admin au démarrage
  resources/application.properties
```

## Conventions de mapping

- **Domain → JSON** : `TodoResponse.from(todo)` (companion object dans `TodoResponse.kt`)
- **Domain ↔ JPA Entity** : fonctions d'extension privées dans les `*PersistenceAdapter` (`toDomain()` / `toEntity()`)
- Les controllers passent des paramètres scalaires aux méthodes du port (pas les DTOs bruts)

## Domaine sans Spring

Le domaine n'a **aucune annotation Spring**. Les services sont annotés `@DomainService` (annotation Kotlin pure définie dans `domain/annotation/`). C'est `bootstrap/config/DomainConfig.kt` qui les enregistre comme beans Spring via `@Bean` explicites.

Pour ajouter un service domaine :
1. Annoter la classe avec `@DomainService`
2. Ajouter un `@Bean` correspondant dans `DomainConfig`

## Ajouter un nouveau use case

1. Ajouter la méthode dans le port input (`TodoUseCase` ou `AuthUseCase`)
2. Implémenter dans le service domaine correspondant
3. Si besoin de persistence : ajouter dans le port output, puis dans l'adapter (`*PersistenceAdapter`)
4. Exposer dans le controller (`adapter-web`)
5. Si le use case est dans un nouveau service : ajouter un `@Bean` dans `DomainConfig`

## Stratégie de tests

| Module | Type | Annotation | Dépendances |
|--------|------|-----------|-------------|
| `domain` | Unitaire | `@Test` + Mockito | Ports mockés |
| `adapter-web` | Slice web | `@WebMvcTest` | Sécurité exclue, use cases mockés via `@MockitoBean` |
| `bootstrap` | Intégration | `@SpringBootTest` | Contexte complet, H2, JWT réel |
