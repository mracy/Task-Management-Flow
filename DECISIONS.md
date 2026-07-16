# Decisions

## 1. Backend Framework: Spring Boot 3 + Java 21
- **Alternatives considered**: Spring Boot 2, Quarkus, Micronaut
- **Reason**: Latest LTS Java, best ecosystem, most mature
- **Impact**: Full compatibility with modern libraries

## 2. Architecture: Clean Architecture
- **Alternatives considered**: Layered Architecture, Hexagonal, Vertical Slice
- **Reason**: Best balance of separation and simplicity for team projects
- **Impact**: Clear boundaries, easy to test, maintainable

## 3. Database: PostgreSQL + pgvector
- **Alternatives considered**: MySQL, MongoDB, dedicated vector DB (Pinecone)
- **Reason**: pgvector eliminates need for separate vector DB; strong relational features
- **Impact**: Single database for all needs, reduced complexity

## 4. Caching: Redis Cache-Aside Pattern
- **Alternatives considered**: Write-through, Caffeine (in-memory), Hazelcast
- **Reason**: Redis provides distributed caching, rate limiting, and session storage
- **Impact**: Single technology for multiple concerns

## 5. Event Streaming: Apache Kafka
- **Alternatives considered**: RabbitMQ, AWS SNS/SQS, Redis Streams
- **Reason**: Better throughput, persistence, replay capability
- **Impact**: Reliable event delivery with outbox pattern

## 6. Frontend State: Redux Toolkit + RTK Query
- **Alternatives considered**: Zustand, Jotai, React Query
- **Reason**: Best TypeScript support, built-in caching, polling, optimistic updates
- **Impact**: Consistent data fetching pattern

## 7. UI Library: Material UI
- **Alternatives considered**: Ant Design, Chakra UI, Tailwind + Headless UI
- **Reason**: Most mature React component library, excellent theming
- **Impact**: Professional, consistent UI

## 8. AI Default: Ollama (Local)
- **Alternatives considered**: OpenAI default, cloud-only
- **Reason**: Free, no API key needed, works offline, reviewer-friendly
- **Impact**: Project works out of the box without paid services

## 9. AI Async Generator Pattern
- **Alternatives considered**: Making base class `async def`, using `AsyncIterator` return type
- **Reason**: Pyright type checker requires base and override `chat_stream` to have identical signatures. Base is `def` returning `AsyncGenerator[str, None]`, so overrides must also be `def`. Inner `async def _generate()` handles async operations.
- **Impact**: Clean type safety, works with all type checkers, no runtime changes
