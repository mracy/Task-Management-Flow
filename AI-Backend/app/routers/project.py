from fastapi import APIRouter, HTTPException
from ..models.schemas import (
    SummaryRequest, SummaryResponse,
    SprintPlanningRequest, ProjectSummaryRequest,
    StandupRequest, RiskAnalysisRequest,
)
from ..providers import get_provider
from ..prompts.templates import (
    SPRINT_PLANNING_PROMPT, PROJECT_SUMMARY_PROMPT,
    STANDUP_PROMPT, RISK_ANALYSIS_PROMPT,
)
from ..config import settings

router = APIRouter(prefix="/ai/project", tags=["AI Project"])


@router.post("/summarize", response_model=SummaryResponse)
async def summarize(request: SummaryRequest):
    try:
        provider = get_provider()
        summary = await provider.summarize(request.text)
        return SummaryResponse(summary=summary, provider=settings.ai_provider)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Summarization error: {str(e)}")


@router.post("/sprint-planning")
async def sprint_planning(request: SprintPlanningRequest):
    try:
        provider = get_provider()
        prompt = SPRINT_PLANNING_PROMPT.format(tasks=request.tasks, team=request.team)
        result = await provider.chat(prompt)
        return {"plan": result, "provider": settings.ai_provider}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Sprint planning error: {str(e)}")


@router.post("/summary")
async def project_summary(request: ProjectSummaryRequest):
    try:
        provider = get_provider()
        prompt = PROJECT_SUMMARY_PROMPT.format(
            project_name=request.project_name,
            project_description=request.project_description,
            project_status=request.project_status,
            completed_tasks=request.completed_tasks,
            total_tasks=request.total_tasks,
            team_size=request.team_size,
        )
        result = await provider.chat(prompt)
        return {"summary": result, "provider": settings.ai_provider}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Project summary error: {str(e)}")


@router.post("/standup")
async def standup(request: StandupRequest):
    try:
        provider = get_provider()
        prompt = STANDUP_PROMPT.format(user_name=request.user_name, activities=request.activities)
        result = await provider.chat(prompt)
        return {"standup": result, "provider": settings.ai_provider}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Standup error: {str(e)}")


@router.post("/risk-analysis")
async def risk_analysis(request: RiskAnalysisRequest):
    try:
        provider = get_provider()
        prompt = RISK_ANALYSIS_PROMPT.format(
            project_name=request.project_name,
            delayed_tasks=request.delayed_tasks,
            workload=request.workload,
            deadlines=request.deadlines,
        )
        result = await provider.chat(prompt)
        return {"risks": result, "provider": settings.ai_provider}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Risk analysis error: {str(e)}")
