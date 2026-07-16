import { UserSummary } from './auth';

export type ProjectStatus = 'PLANNING' | 'IN_PROGRESS' | 'ON_HOLD' | 'COMPLETED' | 'ARCHIVED';

export interface Project {
  id: string;
  name: string;
  description: string;
  status: ProjectStatus;
  owner: UserSummary;
  members: UserSummary[];
  createdAt: string;
  updatedAt: string;
}

export interface ProjectSummary {
  id: string;
  name: string;
  description: string;
  status: ProjectStatus;
  owner: UserSummary;
  memberCount: number;
  createdAt: string;
}

export interface CreateProjectRequest {
  name: string;
  description: string;
  status: ProjectStatus;
}

export interface UpdateProjectRequest {
  name?: string;
  description?: string;
  status?: ProjectStatus;
}

export interface AddMemberRequest {
  userId: string;
}
