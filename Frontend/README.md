# Frontend - React 19 Dashboard

Modern, responsive task management dashboard built with React 19, TypeScript, and Material UI.

## Tech Stack

- **React 19** + **TypeScript 5.6**
- **Redux Toolkit** + **RTK Query** for state and data fetching
- **Material UI 5** for UI components
- **React Router 6** for routing
- **React Hook Form** + **Zod** for form validation
- **Recharts** for dashboard charts
- **react-hot-toast** for notifications

## Project Structure

```
src/
├── api/             # RTK Query API slices (apiSlice, projectApi, taskApi)
├── app/             # Redux store configuration
├── components/
│   ├── common/      # Reusable: ErrorBoundary, LoadingSkeleton, StatusBadge, Avatar
│   ├── dashboard/   # StatsCards, RecentActivity, charts
│   ├── tasks/       # TaskCard, TaskForm, TaskDetail
│   └── projects/    # ProjectCard, ProjectForm
├── hooks/           # useAuth, useTheme
├── pages/           # Route-level pages (lazy loaded)
├── store/           # Redux slices (authSlice, uiSlice)
├── theme/           # Light and dark themes
├── types/           # TypeScript interfaces and types
└── utils/           # Helpers, validators, constants
```

## Pages

| Page | Route | Description |
|------|-------|-------------|
| Login | `/login` | User authentication |
| Register | `/login` | New user registration |
| Dashboard | `/dashboard` | Stats, charts, recent activity |
| Projects | `/projects` | Project list with search/filter |
| Project Detail | `/projects/:id` | Project info + tasks + team |
| Tasks | `/tasks` | Task list with advanced filters |
| Task Detail | `/tasks/:id` | Task info + comments |
| Notifications | `/notifications` | User notifications |

## Key Features

- **Dark Mode**: Toggle persisted to localStorage
- **Responsive**: Mobile, tablet, desktop layouts
- **Error Boundaries**: Graceful error handling
- **Lazy Loading**: All pages code-split
- **Loading Skeletons**: Skeleton UI during data fetch
- **Toast Notifications**: Success/error feedback
- **Protected Routes**: Auth guard on all dashboard routes
- **Optimistic Updates**: Instant UI feedback for mutations
- **Form Validation**: Zod schemas + React Hook Form

## Running Locally

```bash
npm install
npm run dev        # Development (http://localhost:5173)
npm run build      # Production build
npm run preview    # Preview production build
```

## Build

```bash
npm run build      # tsc && vite build → dist/
```

## Environment

The frontend proxies API requests to the backend:
- `/api/*` → `http://localhost:8080` (via Vite proxy or nginx)
- `/api/v1/ai/*` → `http://localhost:8000` (AI backend)

## Default Users

| Email | Password | Role |
|-------|----------|------|
| admin@taskmanagement.com | admin123456 | Admin |
| manager@taskmanagement.com | manager123456 | Manager |
| employee@taskmanagement.com | employee123456 | Employee |
