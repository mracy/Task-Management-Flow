from .base import AIProvider
from .ollama import OllamaProvider
from .openai_compatible import OpenAIProvider, GroqProvider, OpenRouterProvider
from ..config import settings


def get_provider() -> AIProvider:
    """Factory function to get AI provider based on configuration."""
    provider = settings.ai_provider.lower()

    if provider == "ollama":
        return OllamaProvider(
            base_url=settings.ollama_base_url,
            model=settings.ollama_model,
        )
    elif provider == "openai":
        if not settings.openai_api_key:
            raise ValueError("OPENAI_API_KEY is required for OpenAI provider")
        return OpenAIProvider(
            api_key=settings.openai_api_key,
            model=settings.openai_model,
        )
    elif provider == "groq":
        if not settings.groq_api_key:
            raise ValueError("GROQ_API_KEY is required for Groq provider")
        return GroqProvider(api_key=settings.groq_api_key, model=settings.groq_model)
    elif provider == "openrouter":
        if not settings.openrouter_api_key:
            raise ValueError("OPENROUTER_API_KEY is required for OpenRouter provider")
        return OpenRouterProvider(api_key=settings.openrouter_api_key, model=settings.openrouter_model)
    else:
        raise ValueError(f"Unknown AI provider: {provider}")
