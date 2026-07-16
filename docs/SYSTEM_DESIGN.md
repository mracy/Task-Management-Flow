# System Design Document

## Requirements

### Functional
- User registration/authentication with JWT
- Project management with team collaboration
- Task management with workflows
- Comments on tasks
- Notifications
- Dashboard with analytics
- AI-powered features

### Non-Functional
- Support 100+ concurrent users
- < 200ms API response time (p95)
- 99.9% availability
- Horizontal scalability
- Role-based access control

## High-Level Design

```
                    ┌──────────────┐
                    │   Browser    │
                    └──────┬───────┘
                           │
                    ┌──────▼───────┐
                    │    nginx     │
                    └──────┬───────┘
                           │
              ┌────────────┼────────────┐
              │            │            │
       ┌──────▼──────┐ ┌──▼──┐  ┌──────▼──────┐
       │  Backend    │ │ AI  │  │  Frontend   │
       │  (Java)     │ │(Py) │  │  (React)    │
       └──────┬──────┘ └──┬──┘  └─────────────┘
              │            │
    ┌─────────┼─────────┐  │
    │         │         │  │
┌───▼───┐ ┌──▼──┐ ┌────▼┐│
│  PG   │ │Redis│ │Kafka││
└───────┘ └─────┘ └─────┘│
                          │
                    ┌─────▼─────┐
                    │  Ollama   │
                    │  (Local)  │
                    └───────────┘
```

## Database Schema

### Users Table
| Column | Type | Constraint |
|--------|------|-----------|
| id | UUID | PK |
| email | VARCHAR(255) | UNIQUE, NOT NULL |
| password | VARCHAR(255) | NOT NULL |
| first_name | VARCHAR(100) | NOT NULL |
| last_name | VARCHAR(100) | NOT NULL |
| role | VARCHAR(20) | NOT NULL (ADMIN/MANAGER/EMPLOYEE) |
| enabled | BOOLEAN | DEFAULT TRUE |
| version | INTEGER | Optimistic locking |
| deleted | BOOLEAN | Soft delete |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### Projects Table
| Column | Type | Constraint |
|--------|------|-----------|
| id | UUID | PK |
| name | VARCHAR(100) | NOT NULL |
| description | TEXT | |
| status | VARCHAR(20) | NOT NULL |
| owner_id | UUID | FK → Users |
| version | INTEGER | Optimistic locking |
| deleted | BOOLEAN | Soft delete |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### Tasks Table
| Column | Type | Constraint |
|--------|------|-----------|
| id | UUID | PK |
| title | VARCHAR(200) | NOT NULL |
| description | TEXT | |
| priority | VARCHAR(10) | NOT NULL |
| status | VARCHAR(20) | NOT NULL |
| due_date | DATE | |
| assignee_id | UUID | FK → Users |
| reporter_id | UUID | FK → Users, NOT NULL |
| project_id | UUID | FK → Projects, NOT NULL |
| order_index | INTEGER | DEFAULT 0 |
| version | INTEGER | Optimistic locking |
| deleted | BOOLEAN | Soft delete |

### Indexes
- `idx_tasks_project_id` on Tasks(project_id)
- `idx_tasks_assignee_id` on Tasks(assignee_id)
- `idx_tasks_status` on Tasks(status)
- `idx_tasks_priority` on Tasks(priority)
- `idx_tasks_due_date` on Tasks(due_date)
- `idx_comments_task_id` on Comments(task_id)
- `idx_notifications_recipient` on Notifications(recipient_id)
- `idx_audit_log_entity` on AuditLog(entity_name, entity_id)

## Caching Strategy

### Dashboard Cache
- Key: `dashboard:{userId}`
- TTL: 5 minutes
- Pattern: Cache-Aside
- Invalidation: On any task/project mutation

### Project Cache
- Key: `project:{projectId}`
- TTL: 10 minutes
- Pattern: Cache-Aside
- Invalidation: On project update

### Rate Limiting
- Key: `rate_limit:{ip}:{endpoint}`
- Window: 1 minute
- Limit: 100 requests
- Algorithm: Sliding window counter

## Kafka Event Design

### Task Created Event
```json
{
  "eventId": "uuid",
  "eventType": "TASK_CREATED",
  "timestamp": "2024-01-01T00:00:00Z",
  "data": {
    "taskId": "uuid",
    "title": "...",
    "projectId": "uuid",
    "reporterId": "uuid"
  }
}
```

### Idempotency
- Each event has a unique `eventId`
- Consumers check if `eventId` already processed
- Stored in Redis with 24-hour TTL

## AI Provider Architecture

All providers implement the `AIProvider` interface:
```python
class AIProvider(ABC):
    async def chat(prompt, system_prompt) -> str
    async def chat_stream(prompt, system_prompt) -> AsyncGenerator[str]
    async def summarize(text) -> str
    async def generate_tasks(project_description) -> str
    async def classify_priority(title, description) -> str
```

Provider selection via `AI_PROVIDER` environment variable. No code changes needed.

## Failure Handling

| Failure | Strategy |
|---------|----------|
| Database down | Circuit breaker, return cached data |
| Redis down | Fallback to DB, rate limiting disabled |
| Kafka down | Events saved to outbox, retried later |
| AI Provider down | Return friendly error, retry with backoff |
| Auth token expired | Auto-refresh via refresh token |

## Consistency Model

- **Strong Consistency**: User auth, task CRUD (synchronous DB writes)
- **Eventual Consistency**: Notifications, audit logs (async Kafka events)
- **Read-after-Write**: Achieved via cache invalidation on writes
