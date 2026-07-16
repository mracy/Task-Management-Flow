SYSTEM_PROMPT = """You are an AI assistant for a Task Management Platform. You help users manage projects, tasks, and team productivity. You can:
- Create and break down tasks
- Estimate timelines
- Classify priorities
- Generate project summaries
- Answer questions about project status
- Suggest improvements

Always respond in a professional, concise manner. When generating tasks, include: title, description, priority (LOW/MEDIUM/HIGH/CRITICAL), and estimated time."""

TASK_GENERATION_PROMPT = """Based on the following project description, generate a comprehensive task breakdown.
For each task, include:
1. Title
2. Description
3. Priority (LOW/MEDIUM/HIGH/CRITICAL)
4. Category (Database/Backend/Frontend/Testing/Deployment)
5. Estimated hours

Project Description:
{description}

Generate at least 8-15 tasks covering all aspects of the project. Format as a numbered list."""

PRIORITY_PROMPT = """Analyze the following task and classify its priority as one of: LOW, MEDIUM, HIGH, or CRITICAL.
Consider factors like: deadline urgency, business impact, dependencies, and complexity.

Task Title: {title}
Task Description: {description}

Respond with only the priority level (LOW, MEDIUM, HIGH, or CRITICAL) and a brief justification."""

SUMMARY_PROMPT = """Provide a concise summary of the following text. Highlight key points, risks, and action items if applicable.

Text:
{text}

Summary:"""

SPRINT_PLANNING_PROMPT = """Based on the following tasks and team information, create a sprint plan.

Tasks:
{tasks}

Team:
{team}

Generate:
1. Recommended sprint duration
2. Task assignments based on team skills
3. Sprint backlog with story points
4. Identified risks and dependencies
5. Suggested milestones

Format as a structured plan."""

PROJECT_SUMMARY_PROMPT = """Generate a comprehensive project summary for:

Project: {project_name}
Description: {project_description}
Status: {project_status}
Tasks Completed: {completed_tasks}
Total Tasks: {total_tasks}
Team Size: {team_size}

Include:
1. Overall Progress
2. Key Achievements
3. Current Blockers
4. Risk Assessment
5. Next Steps
6. Recommendations"""

STANDUP_PROMPT = """Generate a daily standup report based on:

User: {user_name}
Recent Activities:
{activities}

Generate:
1. What was done yesterday
2. What's planned for today
3. Any blockers or concerns"""

RISK_ANALYSIS_PROMPT = """Analyze potential risks for the following project:

Project: {project_name}
Tasks with delays: {delayed_tasks}
Team workload: {workload}
Upcoming deadlines: {deadlines}

Provide:
1. Risk severity (Low/Medium/High/Critical)
2. Risk description
3. Mitigation strategy
4. Timeline impact"""
