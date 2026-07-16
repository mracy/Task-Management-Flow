from pydantic_settings import BaseSettings
from typing import Optional


class Settings(BaseSettings):
    ai_provider: str = "ollama"
    ollama_base_url: str = "http://localhost:11434"
    ollama_model: str = "llama3.1"
    openai_api_key: Optional[str] = None
    openai_model: str = "gpt-4"
    groq_api_key: Optional[str] = None
    groq_model: str = "llama-3.1-70b-versatile"
    openrouter_api_key: Optional[str] = None
    openrouter_model: str = "meta-llama/llama-3.1-70b-instruct"
    backend_api_url: str = "http://localhost:8080"
    cors_origins: list[str] = ["http://localhost:5173", "http://localhost:3000"]

    class Config:
        env_file = ".env"


settings = Settings()
