import httpx
from typing import Optional, AsyncGenerator
from .base import AIProvider
from ..prompts.templates import SYSTEM_PROMPT, TASK_GENERATION_PROMPT, PRIORITY_PROMPT, SUMMARY_PROMPT


class OllamaProvider(AIProvider):
    """Ollama provider for local LLM inference (default, free, no API key)."""

    def __init__(self, base_url: str = "http://localhost:11434", model: str = "llama3.1"):
        self.base_url = base_url.rstrip("/")
        self.model = model

    async def chat(self, prompt: str, system_prompt: Optional[str] = None) -> str:
        messages = []
        if system_prompt:
            messages.append({"role": "system", "content": system_prompt})
        messages.append({"role": "user", "content": prompt})

        async with httpx.AsyncClient(timeout=120.0) as client:
            response = await client.post(
                f"{self.base_url}/api/chat",
                json={"model": self.model, "messages": messages, "stream": False},
            )
            response.raise_for_status()
            data = response.json()
            return data.get("message", {}).get("content", "")

    def chat_stream(self, prompt: str, system_prompt: Optional[str] = None) -> AsyncGenerator[str, None]:
        messages = []
        if system_prompt:
            messages.append({"role": "system", "content": system_prompt})
        messages.append({"role": "user", "content": prompt})

        async def _generate():
            async with httpx.AsyncClient(timeout=120.0) as client:
                async with client.stream(
                    "POST",
                    f"{self.base_url}/api/chat",
                    json={"model": self.model, "messages": messages, "stream": True},
                ) as response:
                    async for line in response.aiter_lines():
                        if line.strip():
                            import json
                            try:
                                data = json.loads(line)
                                content = data.get("message", {}).get("content", "")
                                if content:
                                    yield content
                            except json.JSONDecodeError:
                                continue

        return _generate()

    async def summarize(self, text: str) -> str:
        return await self.chat(SUMMARY_PROMPT.format(text=text), system_prompt=SYSTEM_PROMPT)

    async def generate_tasks(self, project_description: str) -> str:
        return await self.chat(
            TASK_GENERATION_PROMPT.format(description=project_description),
            system_prompt=SYSTEM_PROMPT,
        )

    async def classify_priority(self, task_title: str, task_description: str) -> str:
        return await self.chat(
            PRIORITY_PROMPT.format(title=task_title, description=task_description),
            system_prompt=SYSTEM_PROMPT,
        )
