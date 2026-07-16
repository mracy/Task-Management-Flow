# Architecture Document

## Overview

TaskFlow follows **Clean Architecture** (Hexagonal Architecture) principles with clear separation of concerns.

## Backend Architecture

```
┌─────────────────────────────────────────────────┐
│                  Controllers                     │
│  (REST API layer - HTTP request/response)        │
├─────────────────────────────────────────────────┤
│                   Services                       │
│  (Business logic - Use cases)                    │
├─────────────────────────────────────────────────┤
│                 Repositories                     │
│  (Data access - JPA Repositories)               │
├─────────────────────────────────────────────────┤
│                Infrastructure                    │
│  (PostgreSQL, Redis, Kafka)                      │
└─────────────────────────────────────────────────┘
```

### Request Flow
1. HTTP request hits Controller
2. Controller validates input (DTOs + Jakarta Validation)
3. Controller calls Service
4. Service implements business logic
5. Service calls Repository for data access
6. Service publishes Kafka events for async operations
7. Controller returns Response DTO

### Key Design Decisions

**1. DTO Pattern**: All API communication uses DTOs, never entities directly. This prevents over-fetching and decouples the API contract from the database schema.

**2. MapStruct Mappers**: Used for entity-DTO conversion. Compile-time generated mappers with zero reflection overhead.

**3. Service Layer**: All business logic resides in services. Controllers are thin - they only validate, call services, and return responses.

**4. Repository Pattern**: Spring Data JPA repositories abstract data access. Custom queries use @Query annotations.

## Database Design

### Entity Relationship

```
User ──── 1:N ──── Project (owner)
User ──── M:N ──── Project (members)
User ──── 1:N ──── Task (assignee)
User ──── 1:N ──── Task (reporter)
Project ── 1:N ──── Task
Task ──── 1:N ──── Comment
User ──── 1:N ──── Comment (author)
User ──── 1:N ──── Notification (recipient)
User ──── 1:N ──── RefreshToken
User ──── 1:N ──── AuditLog
```

### Key Features
- **Optimistic Locking** via `@Version` on User, Project, Task
- **Soft Delete** via `@SQLDelete` and `@Where` annotations
- **Audit Timestamps** via `@CreatedDate` and `@LastModifiedDate`
- **pgvector Extension** for AI semantic search

## Caching Strategy (Redis)

| Cache Key | TTL | Pattern | Eviction |
|-----------|-----|---------|----------|
| `dashboard:{userId}` | 5 min | Cache-Aside | TTL |
| `project:{id}` | 10 min | Cache-Aside | TTL |
| `user:session:{token}` | 15 min | Write-Through | TTL |
| `rate_limit:{ip}` | 1 min | Write-Through | Sliding Window |

**Cache Stampede Prevention**: Distributed lock using Redis SETNX with short TTL.

## Kafka Design

### Topics
| Topic | Partitions | Consumers | Purpose |
|-------|-----------|-----------|---------|
| `task-created` | 3 | notification-consumer | Task creation events |
| `task-updated` | 3 | notification-consumer | Task update events |
| `task-assigned` | 3 | notification-consumer | Task assignment events |
| `notification-events` | 3 | notification-consumer | Notification persistence |
| `audit-events` | 3 | audit-consumer | Audit log persistence |

### Outbox Pattern
Events are first saved to the `audit_log` table (outbox), then a polling publisher sends them to Kafka. This ensures at-least-once delivery.

### Idempotent Consumers
All consumers check the event ID before processing to prevent duplicate handling.

## Security

- **JWT Access Tokens**: 15-minute expiry
- **Refresh Tokens**: 7-day expiry, stored in DB
- **BCrypt**: Password hashing with strength 12
- **RBAC**: Role-based via `@PreAuthorize` annotations
- **CORS**: Configurable allowed origins
- **Rate Limiting**: Redis-based, 100 requests/minute per IP

## Frontend Architecture

```
App
├── Redux Store
│   ├── Auth State (tokens, user)
│   ├── UI State (sidebar, theme)
│   └── API Cache (RTK Query)
├── Router
│   ├── Public Routes (login, register)
│   └── Protected Routes (layout + pages)
└── Theme Provider
    ├── Light Theme
    └── Dark Theme
```

### Data Flow
1. Component dispatches RTK Query hook
2. RTK Query sends request with JWT interceptor
3. 401 triggers auto-refresh token flow
4. Response cached in Redux store
5. Component re-renders with data

## AI Architecture

```
Frontend → AI Backend (FastAPI) → AI Provider
                                  ├── Ollama (default, local)
                                  ├── OpenAI
                                  ├── Groq
                                  ├── OpenRouter
                                  └── LM Studio (local)
```

- **Provider Abstraction**: Switch AI providers via config only
- **RAG Pipeline**: Context from project data, tasks, comments
- **Prompt Templates**: Reusable templates for common tasks

## Scalability

- **Horizontal Scaling**: Kubernetes HPA auto-scales backend (2-10 pods)
- **Database**: PostgreSQL with connection pooling
- **Redis**: Cache layer reduces DB load
- **Kafka**: Async processing decouples services
- **CDN**: Frontend served via nginx with gzip

## Trade-offs

| Decision | Alternative | Reason |
|----------|------------|--------|
| JWT + Refresh | Session-based | Stateless, scalable across services |
| Cache-Aside | Write-Through | Better read performance for dashboard |
| Kafka | RabbitMQ | Better throughput for event streaming |
| MapStruct | ModelMapper | Compile-time safety, better performance |
| RTK Query | Axios directly | Built-in caching, deduplication, polling |
| pgvector | Pinecone | Self-hosted, no external dependency |
