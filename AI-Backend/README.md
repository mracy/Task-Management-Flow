# AI Backend - Python FastAPI

AI-powered features for the Task Management Platform using open-source LLMs with a provider abstraction layer.

## Tech Stack

- **Python 3.12** + **FastAPI**
- **Pydantic** for data validation
- **httpx** for async HTTP client
- **uvicorn** for ASGI server

## Providers

Switch AI provider by changing `AI_PROVIDER` environment variable. No code changes needed.

| Provider | API Key Required | Local | Default Model |
|----------|-----------------|-------|---------------|
| **Ollama** (default) | No | Yes | llama3.1 |
| OpenAI | Yes | No | gpt-4 |
| Groq | Yes | No | llama-3.1-70b-versatile |
| OpenRouter | Yes | No | meta-llama/llama-3.1-70b-instruct |

## Project Structure

```
app/
├── config.py        # Settings (pydantic-settings, env vars)
├── providers/
│   ├── base.py              # Abstract AIProvider interface
│   ├── ollama.py            # Ollama (local, free)
│   └── openai_compatible.py # OpenAI, Groq, OpenRouter
├── models/
│   └── schemas.py    # Pydantic request/response models
├── prompts/
│   └── templates.py  # Reusable prompt templates
└── routers/
    ├── chat.py       # /ai/chat, /ai/search, /ai/health
    ├── tasks.py      # /ai/tasks/generate, /ai/tasks/classify-priority
    └── project.py    # /ai/project/summarize, sprint-planning, summary, standup, risk-analysis
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/health` | Health check |
| GET | `/ai/health` | AI provider health |
| POST | `/ai/chat` | General AI chat |
| POST | `/ai/search` | Natural language search |
| POST | `/ai/tasks/generate` | Generate tasks from description |
| POST | `/ai/tasks/classify-priority` | Classify task priority |
| POST | `/ai/project/summarize` | Summarize text |
| POST | `/ai/project/sprint-planning` | Sprint planning |
| POST | `/ai/project/summary` | Project summary |
| POST | `/ai/project/standup` | Daily standup |
| POST | `/ai/project/risk-analysis` | Risk analysis |

## Running Locally

```bash
# Install dependencies
pip install -r requirements.txt

# Run with Ollama (default, local, free)
uvicorn main:app --reload --port 8000

# Run with OpenAI
AI_PROVIDER=openai OPENAI_API_KEY=sk-... uvicorn main:app --reload --port 8000
```

## API Documentation

- **Swagger UI**: http://localhost:8000/docs
- **ReDoc**: http://localhost:8000/redoc
- **OpenAPI Spec**: http://localhost:8000/openapi.json

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `AI_PROVIDER` | Active provider | `ollama` |
| `OLLAMA_BASE_URL` | Ollama server URL | `http://localhost:11434` |
| `OLLAMA_MODEL` | Ollama model name | `llama3.1` |
| `OPENAI_API_KEY` | OpenAI API key | — |
| `OPENAI_MODEL` | OpenAI model | `gpt-4` |
| `GROQ_API_KEY` | Groq API key | — |
| `GROQ_MODEL` | Groq model | `llama-3.1-70b-versatile` |
| `OPENROUTER_API_KEY` | OpenRouter API key | — |
| `OPENROUTER_MODEL` | OpenRouter model | `meta-llama/llama-3.1-70b-instruct` |
| `BACKEND_API_URL` | Backend API URL | `http://localhost:8080` |

## Provider Architecture

```python
class AIProvider(ABC):
    async def chat(prompt, system_prompt) -> str
    def chat_stream(prompt, system_prompt) -> AsyncGenerator[str, None]
    async def summarize(text) -> str
    async def generate_tasks(project_description) -> str
    async def classify_priority(task_title, task_description) -> str
```

Adding a new provider:
1. Create a class extending `AIProvider`
2. Implement all abstract methods
3. Add configuration in `config.py`
4. Add factory case in `providers/__init__.py`
