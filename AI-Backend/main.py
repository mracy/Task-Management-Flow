from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from contextlib import asynccontextmanager
import logging

from app.config import settings
from app.providers import get_provider
from app.routers import chat, tasks, project

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    logger.info(f"Starting AI Backend with provider: {settings.ai_provider}")
    try:
        provider = get_provider()
        logger.info(f"AI Provider initialized: {provider.__class__.__name__}")
    except Exception as e:
        logger.warning(f"AI Provider initialization failed: {e}. Will retry on request.")
    yield
    logger.info("Shutting down AI Backend")


app = FastAPI(
    title="TaskFlow AI Backend",
    description="""## TaskFlow AI Service

AI-powered features for the Task Management Platform.

### Providers
The AI backend supports multiple providers, switchable via configuration:

| Provider | API Key Required | Local | Description |
|----------|-----------------|-------|-------------|
| **Ollama** (default) | No | Yes | Local LLM inference, free |
| OpenAI | Yes | No | GPT-4, GPT-3.5 |
| Groq | Yes | No | Fast inference, free tier |
| OpenRouter | Yes | No | Multiple models, free tier |

### Features
- AI Chat assistant
- Task generation from project descriptions
- Priority classification
- Project summarization
- Sprint planning assistance
- Natural language search
- Risk analysis

### Usage
1. Set `AI_PROVIDER` in `.env` (default: `ollama`)
2. For cloud providers, set the corresponding API key
3. For Ollama, ensure it's running locally on port 11434
""",
    version="1.0.0",
    lifespan=lifespan,
    servers=[
        {"url": "http://localhost:8000", "description": "Local Development"},
    ],
    openapi_tags=[
        {"name": "AI", "description": "General AI chat and search endpoints"},
        {"name": "AI Tasks", "description": "AI-powered task generation and priority classification"},
        {"name": "AI Project", "description": "AI-powered project summaries, sprint planning, and analysis"},
    ],
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.cors_origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(chat.router)
app.include_router(tasks.router)
app.include_router(project.router)


@app.get("/health")
async def health():
    return {"status": "healthy", "service": "ai-backend"}


@app.get("/")
async def root():
    return {
        "service": "TaskFlow AI Backend",
        "version": "1.0.0",
        "provider": settings.ai_provider,
        "docs": "/docs",
    }
