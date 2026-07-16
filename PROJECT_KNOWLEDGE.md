# Project Knowledge

## Architecture
- Clean Architecture with clear separation: controller → service → repository → entity
- DTOs used for all API communication, never entities
- MapStruct for compile-time safe entity-DTO mapping
- Soft delete pattern with @SQLDelete and @Where
- Optimistic locking with @Version

## Backend Conventions
- API versioning: `/api/v1/`
- Pagination: Pageable with PaginatedResponse wrapper
- Error format: { timestamp, status, error, message, path }
- Authentication: Bearer JWT in Authorization header
- RBAC: @PreAuthorize annotations on controllers

## Frontend Conventions
- RTK Query for ALL data fetching (no direct axios calls)
- React Hook Form + Zod for form validation
- Material UI components exclusively
- Toast notifications via react-hot-toast
- Dark mode persisted to localStorage
- Lazy loading for all page components

## Database
- PostgreSQL with pgvector extension for AI semantic search
- Snake_case for DB columns, camelCase for Java/TS
- Foreign keys with proper cascade/restrict actions
- Indexes on frequently queried columns

## Kafka
- Events published asynchronously after DB commit
- Outbox pattern for reliability
- Idempotent consumers with event ID tracking

## AI Integration
- Provider pattern: switch via AI_PROVIDER env var only
- Ollama as default (no API key needed)
- Prompt templates for consistent AI outputs
- Streaming support via SSE

## Build & Type Safety
- Frontend: `npm run build` = `tsc --noEmit` + `vite build`
- MUI v5 (not v6): use `InputProps`/`InputLabelProps`, NOT `slotProps`
- Store imports: `src/app/store.ts` imports from `../store/authSlice` (NOT `./authSlice`)
- Page imports: files in `src/pages/` use `../` prefix for all sibling imports
- AI-Backend: non-async `chat_stream` with inner `async def _generate()` for type safety
- AI-Backend: use absolute imports in `main.py` (`from app.config`), relative imports in packages

## Database Seed Data
- 3 users: admin/manager/employee (all passwords use same BCrypt hash)
- 1 project: "Task Management Platform" with all users as members
- 8 tasks with varied statuses, priorities, and assignees
- 3 comments on different tasks
- Auto-applied by Flyway V2 migration on backend startup

## Swagger/OpenAPI
- Backend: `http://localhost:8080/swagger-ui.html` (SpringDoc OpenAPI)
- Backend: Bearer JWT security scheme with test account table
- AI-Backend: `http://localhost:8000/docs` (FastAPI auto-generated)
- AI-Backend: Rich schema examples on all request/response models
- All 13 request DTOs have `@Schema` annotations with examples
