# TaskFlow - Task Management Platform

A production-ready full-stack Task Management Platform built with **Java 17 + Spring Boot 3**, **React 19 + TypeScript**, and **AI-powered features** using open-source LLMs.

## Features

### Core
- **Authentication**: JWT access + refresh tokens, role-based access (Admin/Manager/Employee)
- **Projects**: CRUD, member management, status tracking
- **Tasks**: CRUD, priorities, status workflows, assignment, search, filtering
- **Comments**: Threaded comments on tasks with edit/delete own
- **Notifications**: Real-time notifications via Kafka event streaming
- **Dashboard**: Stats cards, charts, recent activities

### AI-Powered
- **AI Chat Assistant**: ChatGPT-like assistant embedded in the application
- **Task Generation**: AI breaks down project descriptions into tasks
- **Priority Classification**: AI classifies task priorities
- **Project Summaries**: AI-generated summaries and reports
- **Sprint Planning**: AI-assisted sprint planning
- **Natural Language Search**: Query tasks in plain English

### Infrastructure
- Docker Compose for local development
- Kubernetes manifests for production
- GitHub Actions CI/CD pipeline
- Redis caching and rate limiting
- Kafka event streaming with outbox pattern

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3, Spring Security, Spring Data JPA, Flyway |
| Frontend | React 19, TypeScript, Redux Toolkit, RTK Query, Material UI |
| AI | Python FastAPI, Ollama (default), OpenAI-compatible APIs |
| Database | PostgreSQL 16 with pgvector |
| Cache | Redis 7 |
| Messaging | Apache Kafka |
| Container | Docker, Docker Compose |
| Orchestration | Kubernetes |
| CI/CD | GitHub Actions |

## Quick Start

### Prerequisites
- Docker and Docker Compose
- Java 17 (for local development)
- Node.js 20+ (for local development)
- Python 3.12+ (for AI backend)

### Start Everything

```bash
# Clone and start
docker compose up -d

# Verify
curl http://localhost:8080/actuator/health
curl http://localhost:8000/health
open http://localhost
```

### Default Users

| Email | Password | Role |
|-------|----------|------|
| admin@taskmanagement.com | admin123456 | Admin |
| manager@taskmanagement.com | manager123456 | Manager |
| employee@taskmanagement.com | employee123456 | Employee |

## API Documentation

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs
- **AI API Docs**: http://localhost:8000/docs

## Project Structure

```
Task Management Platform/
├── Backend/           # Spring Boot 3 backend
│   └── src/main/java/com/taskmanagement/
│       ├── controller/    # REST controllers
│       ├── service/       # Business logic
│       ├── repository/    # Data access
│       ├── entity/        # JPA entities
│       ├── dto/           # Data transfer objects
│       ├── mapper/        # MapStruct mappers
│       ├── config/        # Configuration
│       ├── security/      # JWT + Spring Security
│       ├── kafka/         # Kafka producers/consumers
│       ├── redis/         # Redis caching + rate limiting
│       └── exception/     # Global exception handling
├── Frontend/          # React 19 frontend
│   └── src/
│       ├── api/           # RTK Query API slices
│       ├── components/    # Reusable components
│       ├── pages/         # Page components
│       ├── store/         # Redux store
│       ├── hooks/         # Custom hooks
│       ├── types/         # TypeScript types
│       └── utils/         # Utilities
├── AI-Backend/        # Python FastAPI AI service
│   └── app/
│       ├── providers/     # AI provider implementations
│       ├── routers/       # API routes
│       ├── models/        # Pydantic models
│       └── prompts/       # Prompt templates
├── docker/            # Dockerfiles and nginx config
├── kubernetes/        # K8s manifests
├── docs/              # Documentation
└── docker-compose.yml
```

## Architecture

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for detailed architecture documentation.

## API Endpoints

### Auth
- `POST /api/v1/auth/register` - Register
- `POST /api/v1/auth/login` - Login
- `POST /api/v1/auth/refresh` - Refresh token
- `POST /api/v1/auth/logout` - Logout

### Projects
- `GET /api/v1/projects` - List (paginated, filterable)
- `POST /api/v1/projects` - Create
- `GET /api/v1/projects/{id}` - Get by ID
- `PUT /api/v1/projects/{id}` - Update
- `DELETE /api/v1/projects/{id}` - Delete
- `POST /api/v1/projects/{id}/members` - Add member
- `DELETE /api/v1/projects/{id}/members/{userId}` - Remove member

### Tasks
- `GET /api/v1/tasks` - List (paginated, filterable, sortable)
- `POST /api/v1/tasks` - Create
- `GET /api/v1/tasks/{id}` - Get by ID
- `PUT /api/v1/tasks/{id}` - Update
- `DELETE /api/v1/tasks/{id}` - Delete
- `PATCH /api/v1/tasks/{id}/status` - Change status
- `PATCH /api/v1/tasks/{id}/assign` - Assign task

### Comments
- `GET /api/v1/tasks/{taskId}/comments` - List comments
- `POST /api/v1/tasks/{taskId}/comments` - Add comment
- `PUT /api/v1/tasks/{taskId}/comments/{commentId}` - Edit comment
- `DELETE /api/v1/tasks/{taskId}/comments/{commentId}` - Delete comment

### Dashboard
- `GET /api/v1/dashboard` - Dashboard stats + charts

### AI
- `POST /api/v1/ai/chat` - AI chat
- `POST /api/v1/ai/tasks/generate` - Generate tasks
- `POST /api/v1/ai/tasks/classify-priority` - Classify priority
- `POST /api/v1/ai/project/summarize` - Summarize text
- `POST /api/v1/ai/project/sprint-planning` - Sprint planning

## License

MIT
