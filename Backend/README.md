# Backend - Spring Boot 3 API

Production-ready REST API built with Java 17, Spring Boot 3, and Clean Architecture.

## Tech Stack

- **Java 17** + **Spring Boot 3.3**
- **Spring Security** with JWT (access + refresh tokens)
- **Spring Data JPA** + **PostgreSQL 16** (with pgvector)
- **Redis 7** for caching and rate limiting
- **Apache Kafka** for event streaming
- **Flyway** for database migrations
- **MapStruct** for type-safe mapping
- **Lombok** for boilerplate reduction
- **SpringDoc OpenAPI** for Swagger documentation

## Architecture

```
src/main/java/com/taskmanagement/
├── config/          # Spring configuration (Security, Redis, Kafka, OpenAPI)
├── controller/      # REST controllers (@RestController)
├── dto/             # Request/Response DTOs with validation
├── entity/          # JPA entities with audit fields
├── exception/       # Global exception handling
├── kafka/           # Kafka producers and consumers
├── mapper/          # MapStruct mappers (entity ↔ DTO)
├── redis/           # Cache managers and rate limiting
├── repository/      # Spring Data JPA repositories
├── security/        # JWT provider, filters, user details
├── service/         # Business logic layer
└── util/            # Utility classes
```

## Key Features

- **Clean Architecture**: Controller → Service → Repository → Entity
- **API Versioning**: All endpoints under `/api/v1/`
- **Pagination**: `?page=0&size=10&sort=createdAt&direction=desc`
- **Soft Delete**: Entities marked deleted, not removed
- **Optimistic Locking**: `@Version` field on all entities
- **Global Exception Handling**: `@ControllerAdvice` with structured error responses
- **JWT Auth**: Access tokens (15min) + refresh tokens (7 days)
- **RBAC**: ADMIN, MANAGER, EMPLOYEE roles with `@PreAuthorize`
- **Redis Cache-Aside**: Dashboard, projects, frequently accessed data
- **Kafka Events**: task-created, task-updated, notification-events, audit-events

## Running Locally

```bash
# Requires Java 17+, PostgreSQL, Redis, Kafka running
./gradlew bootRun

# Or with Gradle wrapper (if available)
gradle bootRun
```

## API Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/v1/auth/register` | Register user | No |
| POST | `/api/v1/auth/login` | Login | No |
| POST | `/api/v1/auth/refresh` | Refresh token | No |
| GET | `/api/v1/projects` | List projects | Yes |
| POST | `/api/v1/projects` | Create project | MANAGER+ |
| GET | `/api/v1/tasks` | List tasks (filterable) | Yes |
| POST | `/api/v1/tasks` | Create task | MANAGER+ |
| PATCH | `/api/v1/tasks/{id}/status` | Change task status | Yes |
| POST | `/api/v1/tasks/{id}/comments` | Add comment | Yes |
| GET | `/api/v1/dashboard` | Dashboard stats | Yes |

## Swagger UI

- **URL**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs
- **Health Check**: http://localhost:8080/actuator/health

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | PostgreSQL URL | `jdbc:postgresql://localhost:5432/taskmanagement` |
| `SPRING_DATASOURCE_USERNAME` | DB username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | DB password | `postgres` |
| `SPRING_REDIS_HOST` | Redis host | `localhost` |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | Kafka brokers | `localhost:9092` |
| `JWT_SECRET` | JWT signing secret | — |
| `JWT_EXPIRY` | Access token TTL (ms) | `900000` (15 min) |
| `JWT_REFRESH_EXPIRY` | Refresh token TTL (ms) | `604800000` (7 days) |

## Database

- **Migrations**: `src/main/resources/db/migration/`
- V1: Schema creation (users, projects, tasks, comments, notifications, audit_log)
- V2: Seed data (3 users, 1 project, 8 tasks, 3 comments)
- Flyway runs automatically on startup
