import httpx
from typing import Optional, AsyncGenerator
from .base import AIProvider
from ..prompts.templates import SYSTEM_PROMPT, TASK_GENERATION_PROMPT, PRIORITY_PROMPT, SUMMARY_PROMPT


class OpenAIProvider(AIProvider):
    """OpenAI-compatible provider (works with OpenAI, Groq, OpenRouter, vLLM, LM Studio)."""

    def __init__(self, api_key: str, base_url: str = "https://api.openai.com/v1", model: str = "gpt-4"):
        self.api_key = api_key
        self.base_url = base_url.rstrip("/")
        self.model = model

    def _headers(self) -> dict:
        return {"Authorization": f"Bearer {self.api_key}", "Content-Type": "application/json"}

    async def chat(self, prompt: str, system_prompt: Optional[str] = None) -> str:
        messages = []
        if system_prompt:
            messages.append({"role": "system", "content": system_prompt})
        messages.append({"role": "user", "content": prompt})

        async with httpx.AsyncClient(timeout=120.0) as client:
            response = await client.post(
                f"{self.base_url}/chat/completions",
                headers=self._headers(),
                json={"model": self.model, "messages": messages},
            )
            response.raise_for_status()
            data = response.json()
            return data["choices"][0]["message"]["content"]

    def chat_stream(self, prompt: str, system_prompt: Optional[str] = None) -> AsyncGenerator[str, None]:
        messages = []
        if system_prompt:
            messages.append({"role": "system", "content": system_prompt})
        messages.append({"role": "user", "content": prompt})

        async def _generate():
            async with httpx.AsyncClient(timeout=120.0) as client:
                async with client.stream(
                    "POST",
                    f"{self.base_url}/chat/completions",
                    headers=self._headers(),
                    json={"model": self.model, "messages": messages, "stream": True},
                ) as response:
                    async for line in response.aiter_lines():
                        if line.startswith("data: ") and line != "data: [DONE]":
                            import json
                            try:
                                data = json.loads(line[6:])
                                content = data["choices"][0]["delta"].get("content", "")
                                if content:
                                    yield content
                            except (json.JSONDecodeError, KeyError):
                                continue

        return _generate()

    async def summarize(self, text: str) -> str:
        return await self.chat(SUMMARY_PROMPT.format(text=text), system_prompt=SYSTEM_PROMPT)

    async def generate_tasks(self, project_description: str) -> str:
        return await self.chat(TASK_GENERATION_PROMPT.format(description=project_description), system_prompt=SYSTEM_PROMPT)

    async def classify_priority(self, task_title: str, task_description: str) -> str:
        return await self.chat(PRIORITY_PROMPT.format(title=task_title, description=task_description), system_prompt=SYSTEM_PROMPT)


class GroqProvider(OpenAIProvider):
    def __init__(self, api_key: str, model: str = "llama-3.1-70b-versatile"):
        super().__init__(api_key=api_key, base_url="https://api.groq.com/openai/v1", model=model)


class OpenRouterProvider(OpenAIProvider):
    def __init__(self, api_key: str, model: str = "meta-llama/llama-3.1-70b-instruct"):
        super().__init__(api_key=api_key, base_url="https://openrouter.ai/api/v1", model=model)
