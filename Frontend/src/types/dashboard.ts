import { TaskStatus, Priority } from './task';

export interface DashboardStats {
  totalProjects: number;
  activeProjects: number;
  completedTasks: number;
  pendingTasks: number;
  overdueTasks: number;
  totalTasks: number;
  totalUsers: number;
}

export interface TaskStatusDistribution {
  status: string;
  count: number;
}

export interface PriorityDistribution {
  priority: string;
  count: number;
}

export interface RecentActivity {
  action: string;
  description: string;
  userName: string;
  timestamp: string;
}

export interface DashboardResponse extends DashboardStats {
  recentActivities: RecentActivity[];
  tasksByStatus: TaskStatusDistribution[];
  tasksByPriority: PriorityDistribution[];
}
