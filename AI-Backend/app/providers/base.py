from abc import ABC, abstractmethod
from typing import Optional, AsyncGenerator


class AIProvider(ABC):
    """Abstract AI provider interface. Switch providers via configuration only."""

    @abstractmethod
    async def chat(self, prompt: str, system_prompt: Optional[str] = None) -> str:
        """Send a prompt and get a response."""
        pass

    @abstractmethod
    def chat_stream(self, prompt: str, system_prompt: Optional[str] = None) -> AsyncGenerator[str, None]:
        """Send a prompt and stream the response."""
        pass

    @abstractmethod
    async def summarize(self, text: str) -> str:
        """Summarize the given text."""
        pass

    @abstractmethod
    async def generate_tasks(self, project_description: str) -> str:
        """Generate tasks from a project description."""
        pass

    @abstractmethod
    async def classify_priority(self, task_title: str, task_description: str) -> str:
        """Classify the priority of a task."""
        pass
