# Changelog

## [1.0.0] - 2026-07-16

### Added
- Complete Spring Boot 3 backend with Clean Architecture
- JWT authentication with access + refresh tokens
- Role-based access control (Admin/Manager/Employee)
- Project CRUD with member management
- Task CRUD with pagination, sorting, filtering, search
- Task workflows (TODO → IN_PROGRESS → IN_REVIEW → DONE)
- Comments on tasks with edit/delete own
- Notification system with Kafka event streaming
- Dashboard with stats and charts
- Redis caching (dashboard, projects, sessions)
- Redis rate limiting
- Kafka producers and idempotent consumers
- Flyway database migrations with seed data
- Swagger/OpenAPI documentation
- Actuator health checks
- React 19 frontend with TypeScript
- Redux Toolkit + RTK Query data layer
- Login and Register pages
- Dashboard with Recharts visualizations
- Project management pages
- Task management with filters
- Task detail with comments
- Notifications page
- Dark mode support
- Responsive design (mobile, tablet, desktop)
- Error boundaries and loading skeletons
- Toast notifications
- Protected routes with auth guards
- Lazy loading for all pages
- Python FastAPI AI backend
- AI provider abstraction (Ollama, OpenAI, Groq, OpenRouter)
- AI chat, task generation, priority classification
- AI project summaries and sprint planning
- Docker Compose setup (all services)
- Dockerfiles for backend, frontend, AI backend
- Kubernetes manifests (Deployments, Services, Ingress, HPA)
- ConfigMap and Secrets for K8s
- GitHub Actions CI/CD pipeline
- Nginx reverse proxy configuration
- Architecture documentation
- System design document
- Deployment guide
- Project knowledge base

### Changed
- N/A (initial release)

### Fixed
- N/A (initial release)

### Removed
- N/A (initial release)

---

## [1.0.1] - 2026-07-16

### Fixed
- AI-Backend `chat_stream` return type mismatch in `ollama.py` and `openai_compatible.py`
  - Changed from `async def` to non-async `def` with inner `async def _generate()` pattern
  - Base class and override now share identical signatures
- Added `pyrightconfig.json` for AI-Backend IDE import resolution

### Verified
- Frontend: `tsc --noEmit` passes with zero errors
- Frontend: `npm run build` (tsc + vite) produces production build successfully
- AI-Backend: Python module imports all resolve correctly
- Database seed data (V2) verified — 3 users, 1 project, 8 tasks, 3 comments
- Swagger/OpenAPI configuration verified for both Backend and AI-Backend

---

## [1.0.2] - 2026-07-16

### Fixed
- Backend Dockerfile: COPY paths prefixed with `Backend/` to match root build context
- Backend Dockerfile: Changed from `eclipse-temurin:21-jdk-jammy` to `gradle:8.9-jdk17` (includes Gradle)
- Backend Dockerfile: Added `curl` installation for HEALTHCHECK in runtime image
- Frontend Dockerfile: Removed `COPY nginx.conf` (handled by docker-compose volume mount)
- Frontend Dockerfile: Fixed `FROM` casing (`as` → `AS`) to eliminate BuildKit warning
- docker-compose.yml: Removed obsolete `version: '3.8'` attribute
- docker-compose.yml: Fixed database name from `taskmanagement` to `task_management` (matching application.yml)
- build.gradle: Changed Java sourceCompatibility/targetCompatibility from `21` to `17`

### Added
- `.dockerignore` files for root, Backend, Frontend, and AI-Backend
- `Backend/README.md` with architecture, API endpoints, and env vars
- `Frontend/README.md` with pages, features, and project structure
- `AI-Backend/README.md` with providers, endpoints, and env vars
- `AI-Backend/pyrightconfig.json` for IDE import resolution
