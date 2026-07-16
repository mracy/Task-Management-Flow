from pydantic import BaseModel, Field
from typing import Optional


class ChatRequest(BaseModel):
    prompt: str = Field(
        ...,
        min_length=1,
        max_length=10000,
        description="The prompt to send to the AI",
        examples=["What are the best practices for structuring a Spring Boot project?"],
    )
    system_prompt: Optional[str] = Field(
        default=None,
        description="Optional system prompt to set AI behavior",
        examples=["You are a senior software engineer helping with project planning."],
    )

    class Config:
        json_schema_extra = {
            "example": {
                "prompt": "Break down the task of building a login module using JWT",
                "system_prompt": "You are a senior software engineer. Be concise.",
            }
        }


class ChatResponse(BaseModel):
    response: str = Field(description="AI generated response")
    provider: str = Field(description="AI provider used (ollama, openai, groq, etc.)")
    model: str = Field(description="Model name used for generation")


class TaskGenerationRequest(BaseModel):
    project_description: str = Field(
        ...,
        min_length=10,
        max_length=5000,
        description="Project description to generate tasks from",
        examples=["Build a real-time chat application with user authentication, group chats, file sharing, and message history."],
    )

    class Config:
        json_schema_extra = {
            "example": {
                "project_description": "Build a task management platform with user authentication, project CRUD, task assignment, real-time notifications, and analytics dashboard."
            }
        }


class TaskGenerationResponse(BaseModel):
    tasks: str = Field(description="AI-generated task breakdown")
    provider: str = Field(description="AI provider used")


class PriorityRequest(BaseModel):
    task_title: str = Field(
        ...,
        min_length=1,
        max_length=200,
        description="Task title to classify",
        examples=["Fix critical security vulnerability in payment processing"],
    )
    task_description: str = Field(
        ...,
        min_length=1,
        max_length=5000,
        description="Task description for context",
        examples=["There is a SQL injection vulnerability in the payment endpoint that needs immediate attention."],
    )

    class Config:
        json_schema_extra = {
            "example": {
                "task_title": "Fix SQL injection vulnerability in login endpoint",
                "task_description": "Security scan found a SQL injection vulnerability in the login endpoint. User input is not sanitized before being used in database queries."
            }
        }


class PriorityResponse(BaseModel):
    priority: str = Field(description="Classified priority level (LOW, MEDIUM, HIGH, CRITICAL)")
    justification: str = Field(description="AI reasoning for the priority classification")
    provider: str = Field(description="AI provider used")


class SummaryRequest(BaseModel):
    text: str = Field(
        ...,
        min_length=10,
        max_length=50000,
        description="Text to summarize",
        examples=["Sprint 12 Retrospective: The team completed 34 story points out of 40 planned. The CI/CD pipeline migration was completed ahead of schedule. Main blocker was the database migration script that caused 2 days of delay. Team velocity has increased 15% from last sprint. Action items: 1) Document the new deployment process, 2) Set up automated rollback procedures, 3) Schedule knowledge sharing session for the new monitoring tools."],
    )

    class Config:
        json_schema_extra = {
            "example": {
                "text": "During this sprint we completed 8 tasks, with 2 carrying over to next sprint. The main challenge was integrating the third-party payment API which had outdated documentation. The team's velocity was 42 story points, up from 35 last sprint. We deployed to production twice with zero downtime. Critical issue: the notification service had a memory leak that was caught in staging."
            }
        }


class SummaryResponse(BaseModel):
    summary: str = Field(description="AI-generated summary")
    provider: str = Field(description="AI provider used")


class SprintPlanningRequest(BaseModel):
    tasks: str = Field(
        ...,
        min_length=1,
        description="List of tasks to plan for the sprint",
        examples=["1. Implement user authentication (13 SP)\n2. Design dashboard UI (8 SP)\n3. Set up CI/CD pipeline (5 SP)\n4. Write API documentation (3 SP)"],
    )
    team: str = Field(
        ...,
        min_length=1,
        description="Team members and their skills",
        examples=["- Alice: Backend (Java, Spring Boot)\n- Bob: Frontend (React, TypeScript)\n- Charlie: DevOps (Docker, K8s)"],
    )

    class Config:
        json_schema_extra = {
            "example": {
                "tasks": "1. Implement JWT authentication (13 SP) - HIGH\n2. Build project dashboard (8 SP) - MEDIUM\n3. Create task CRUD API (5 SP) - HIGH\n4. Set up Kafka messaging (8 SP) - MEDIUM\n5. Write integration tests (5 SP) - LOW\n6. Deploy to staging (3 SP) - MEDIUM",
                "team": "- Alice (Senior Backend): Java, Spring Boot, PostgreSQL\n- Bob (Frontend): React, TypeScript, Material UI\n- Charlie (DevOps): Docker, Kubernetes, CI/CD\n- Diana (Full-stack): Spring Boot, React, Testing"
            }
        }


class ProjectSummaryRequest(BaseModel):
    project_name: str = Field(description="Project name", examples=["TaskFlow Platform"])
    project_description: str = Field(description="Project description", examples=["A full-stack task management platform"])
    project_status: str = Field(description="Current project status", examples=["IN_PROGRESS"])
    completed_tasks: int = Field(description="Number of completed tasks", examples=[12])
    total_tasks: int = Field(description="Total number of tasks", examples=[25])
    team_size: int = Field(description="Number of team members", examples=[5])


class StandupRequest(BaseModel):
    user_name: str = Field(description="Team member name", examples=["Alice Johnson"])
    activities: str = Field(
        description="Recent activities",
        examples=["Completed JWT auth module, reviewed 3 PRs, fixed 2 bugs in task service, attended sprint planning meeting."],
    )


class RiskAnalysisRequest(BaseModel):
    project_name: str = Field(description="Project name", examples=["TaskFlow Platform"])
    delayed_tasks: str = Field(description="Tasks that are delayed", examples=["Database migration (2 days late), Kafka setup (1 day late)"])
    workload: str = Field(description="Current team workload", examples=["Alice: 85%, Bob: 70%, Charlie: 90%, Diana: 60%"])
    deadlines: str = Field(description="Upcoming deadlines", examples=["Production deploy: 5 days, Client demo: 7 days, Security audit: 14 days"])


class HealthResponse(BaseModel):
    status: str = Field(description="Service health status", examples=["healthy"])
    provider: str = Field(description="Active AI provider", examples=["ollama"])
    model: str = Field(description="Active model name", examples=["llama3.1"])


class SearchRequest(BaseModel):
    query: str = Field(
        ...,
        min_length=1,
        max_length=500,
        description="Natural language search query",
        examples=["Show overdue backend tasks assigned to John"],
    )
    context: str = Field(
        default="",
        max_length=10000,
        description="Optional project context to enhance search",
        examples=["Project: TaskFlow - task management platform. Tech stack: Java 21, Spring Boot 3, React 19, PostgreSQL"],
    )
