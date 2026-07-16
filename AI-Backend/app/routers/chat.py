from fastapi import APIRouter, HTTPException
from ..models.schemas import (
    ChatRequest, ChatResponse, HealthResponse, SearchRequest,
)
from ..providers import get_provider
from ..config import settings

router = APIRouter(prefix="/ai", tags=["AI"])


@router.get("/health", response_model=HealthResponse)
async def health_check():
    return HealthResponse(
        status="healthy",
        provider=settings.ai_provider,
        model=settings.ollama_model if settings.ai_provider == "ollama" else "cloud",
    )


@router.post("/chat", response_model=ChatResponse)
async def chat(request: ChatRequest):
    try:
        provider = get_provider()
        response = await provider.chat(request.prompt, request.system_prompt)
        return ChatResponse(
            response=response,
            provider=settings.ai_provider,
            model=settings.ollama_model if settings.ai_provider == "ollama" else "cloud",
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"AI provider error: {str(e)}")


@router.post("/search")
async def ai_search(request: SearchRequest):
    try:
        provider = get_provider()
        system = "You are a search assistant for a task management platform. Answer user queries using the provided context. If the context doesn't contain relevant information, say so."
        prompt = f"Context:\n{request.context}\n\nQuery: {request.query}"
        response = await provider.chat(prompt, system_prompt=system)
        return {"results": response, "provider": settings.ai_provider}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Search error: {str(e)}")
