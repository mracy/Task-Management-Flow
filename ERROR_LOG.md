# Error Log

## Errors Encountered During Development

### Sub-agent Connection Failure
- **Date**: 2026-07-16
- **Error**: Sub-agent for frontend build failed with connection error
- **Root Cause**: Network connectivity issue with external API
- **Affected Files**: Frontend directory
- **Solution**: Built frontend files directly instead of via sub-agent
- **Prevention**: Continue with direct file creation as fallback
- **Status**: Resolved

### useTheme Hook Import Order
- **Date**: 2026-07-16
- **Error**: Import statement placed at bottom of file in useTheme.ts
- **Root Cause**: Copy/paste ordering mistake
- **Affected Files**: `Frontend/src/hooks/useTheme.ts`
- **Solution**: Rewrote file with correct import order
- **Prevention**: Always place imports at top of file
- **Status**: Resolved

### PriorityChart Import Order
- **Date**: 2026-07-16
- **Error**: Cell import from recharts placed at bottom of file
- **Root Cause**: Copy/paste ordering mistake
- **Affected Files**: `Frontend/src/components/dashboard/PriorityChart.tsx`
- **Solution**: Rewrote file with imports at top
- **Prevention**: Always verify imports are at top
- **Status**: Resolved

### AI-Backend chat_stream Type Mismatch (Pyright)
- **Date**: 2026-07-16
- **Error**: `Method "chat_stream" overrides class "AIProvider" in an incompatible manner — Return type mismatch`
- **Root Cause**: Base class declares non-async `def chat_stream() -> AsyncGenerator[str, None]`, but provider implementations used `async def` which pyright types as returning `Coroutine[..., AsyncGenerator[str, None]]` instead of `AsyncGenerator[str, None]`
- **Affected Files**: `AI-Backend/app/providers/ollama.py`, `AI-Backend/app/providers/openai_compatible.py`
- **Solution**: Changed both overrides from `async def chat_stream` to non-async `def chat_stream` with inner `async def _generate()` pattern. The inner async function uses `yield` to create an async generator, and the outer non-async function returns it.
- **Prevention**: When overriding abstract methods that return `AsyncGenerator`, ensure the override has the same `async`/non-async signature as the base. If the implementation needs async operations, use an inner async function.
- **Status**: Resolved

### AI-Backend IDE Import Resolution
- **Date**: 2026-07-16
- **Error**: `Import '.config' could not be resolved`, `Import '.providers' could not be resolved`, `Import '.routers' could not be resolved`
- **Root Cause**: Pyright/Pylance cannot resolve relative imports within the `app` package because no `pyrightconfig.json` exists to set the Python path
- **Affected Files**: `AI-Backend/app/providers/__init__.py`
- **Solution**: Created `AI-Backend/pyrightconfig.json` with `executionEnvironments` pointing to the project root, and set `reportMissingImports: false` and `reportMissingTypeStubs: false`
- **Prevention**: Always include a `pyrightconfig.json` for Python projects to help IDEs resolve imports
- **Status**: Resolved

### Docker Build Failed — COPY paths and Missing Gradle
- **Date**: 2026-07-16
- **Error**: `failed to calculate checksum of ref ... "/settings.gradle": not found`, `"/gradle": not found`, `"/src": not found`
- **Root Cause**: `docker-compose.yml` sets build context to `.` (project root), but `Dockerfile.backend` COPY paths didn't include `Backend/` prefix. Also no Gradle wrapper existed, and `eclipse-temurin` base image doesn't include Gradle.
- **Affected Files**: `docker/Dockerfile.backend`, `docker-compose.yml`, `Backend/build.gradle`
- **Solution**: (1) Added `Backend/` prefix to all COPY paths in Dockerfile.backend. (2) Changed builder base from `eclipse-temurin:21-jdk-jammy` to `gradle:8.9-jdk17`. (3) Changed runtime base to `eclipse-temurin:17-jre-jammy`. (4) Added `apt-get install curl` for HEALTHCHECK. (5) Changed Java version from 21 to 17 in build.gradle.
- **Prevention**: Always verify Dockerfile COPY paths match the actual build context directory structure.
- **Status**: Resolved

### Frontend Dockerfile — COPY nginx.conf from Wrong Context
- **Date**: 2026-07-16
- **Error**: Would fail when building standalone (nginx.conf not in Frontend context)
- **Root Cause**: Dockerfile.frontend had `COPY nginx.conf` but context is `./Frontend` where nginx.conf doesn't exist.
- **Affected Files**: `docker/Dockerfile.frontend`
- **Solution**: Removed the COPY line. nginx.conf is mounted via docker-compose volume.
- **Prevention**: For files mounted via docker-compose volumes, don't COPY them in Dockerfile.
- **Status**: Resolved

### Database Name Mismatch
- **Date**: 2026-07-16
- **Error**: Backend would fail to connect to database in Docker
- **Root Cause**: `application.yml` uses `task_management` but `docker-compose.yml` used `taskmanagement`.
- **Affected Files**: `docker-compose.yml`
- **Solution**: Changed docker-compose to use `task_management` to match application.yml.
- **Prevention**: Always ensure database names are consistent across all config files.
- **Status**: Resolved
