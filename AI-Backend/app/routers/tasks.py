from fastapi import APIRouter, HTTPException
from ..models.schemas import (
    TaskGenerationRequest, TaskGenerationResponse,
    PriorityRequest, PriorityResponse,
)
from ..providers import get_provider
from ..config import settings

router = APIRouter(prefix="/ai/tasks", tags=["AI Tasks"])


@router.post("/generate", response_model=TaskGenerationResponse)
async def generate_tasks(request: TaskGenerationRequest):
    try:
        provider = get_provider()
        tasks = await provider.generate_tasks(request.project_description)
        return TaskGenerationResponse(tasks=tasks, provider=settings.ai_provider)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Task generation error: {str(e)}")


@router.post("/classify-priority", response_model=PriorityResponse)
async def classify_priority(request: PriorityRequest):
    try:
        provider = get_provider()
        result = await provider.classify_priority(request.task_title, request.task_description)
        lines = result.strip().split("\n")
        priority_line = lines[0].strip() if lines else result
        justification = "\n".join(lines[1:]).strip() if len(lines) > 1 else ""

        for p in ["LOW", "MEDIUM", "HIGH", "CRITICAL"]:
            if p in priority_line.upper():
                return PriorityResponse(priority=p, justification=justification or priority_line, provider=settings.ai_provider)

        return PriorityResponse(priority="MEDIUM", justification=result, provider=settings.ai_provider)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Priority classification error: {str(e)}")
