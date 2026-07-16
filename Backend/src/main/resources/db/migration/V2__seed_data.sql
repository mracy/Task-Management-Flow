-- V2: Seed data - admin user and sample project

-- Insert admin user (password: admin123456)
INSERT INTO users (email, password, first_name, last_name, role, enabled, deleted, created_at, updated_at, version)
VALUES (
    'admin@taskmanagement.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'Admin',
    'User',
    'ADMIN',
    TRUE,
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0
);

-- Insert sample manager user (password: manager123456)
INSERT INTO users (email, password, first_name, last_name, role, enabled, deleted, created_at, updated_at, version)
VALUES (
    'manager@taskmanagement.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'Project',
    'Manager',
    'MANAGER',
    TRUE,
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0
);

-- Insert sample employee user (password: employee123456)
INSERT INTO users (email, password, first_name, last_name, role, enabled, deleted, created_at, updated_at, version)
VALUES (
    'employee@taskmanagement.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'John',
    'Doe',
    'EMPLOYEE',
    TRUE,
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0
);

-- Insert sample project
INSERT INTO projects (name, description, status, owner_id, created_at, updated_at, version, deleted)
VALUES (
    'Task Management Platform',
    'A comprehensive task management platform similar to Jira/Trello with full project and task tracking capabilities.',
    'IN_PROGRESS',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    FALSE
);

-- Add members to the project
INSERT INTO project_members (project_id, user_id) VALUES (1, 1);
INSERT INTO project_members (project_id, user_id) VALUES (1, 2);
INSERT INTO project_members (project_id, user_id) VALUES (1, 3);

-- Insert sample tasks
INSERT INTO tasks (title, description, priority, status, due_date, assignee_id, reporter_id, project_id, order_index, created_at, updated_at, version, deleted)
VALUES
    ('Set up CI/CD pipeline', 'Configure GitHub Actions for automated testing and deployment', 'HIGH', 'IN_PROGRESS', CURRENT_DATE + INTERVAL '7 days', 2, 1, 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, FALSE),
    ('Design database schema', 'Create PostgreSQL database schema with proper indexes and constraints', 'CRITICAL', 'DONE', CURRENT_DATE - INTERVAL '2 days', 1, 1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, FALSE),
    ('Implement user authentication', 'JWT-based authentication with refresh tokens', 'HIGH', 'IN_REVIEW', CURRENT_DATE + INTERVAL '3 days', 3, 1, 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, FALSE),
    ('Create REST API endpoints', 'Implement all CRUD operations for projects, tasks, and users', 'CRITICAL', 'IN_PROGRESS', CURRENT_DATE + INTERVAL '5 days', 1, 2, 1, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, FALSE),
    ('Write unit tests', 'Achieve 80% code coverage for service layer', 'MEDIUM', 'TODO', CURRENT_DATE + INTERVAL '14 days', 3, 2, 1, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, FALSE),
    ('Set up Redis caching', 'Implement cache-aside pattern for frequently accessed data', 'LOW', 'TODO', CURRENT_DATE + INTERVAL '10 days', 2, 1, 1, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, FALSE),
    ('Configure Kafka messaging', 'Set up event-driven architecture with Kafka producers and consumers', 'MEDIUM', 'TODO', CURRENT_DATE + INTERVAL '12 days', 1, 2, 1, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, FALSE),
    ('Deploy to production', 'Deploy the application to production environment', 'HIGH', 'TODO', CURRENT_DATE + INTERVAL '21 days', 1, 1, 1, 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, FALSE);

-- Insert sample comments
INSERT INTO comments (content, author_id, task_id, created_at, updated_at, deleted)
VALUES
    ('Started working on the CI/CD pipeline configuration.', 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
    ('The database schema looks great. Nice indexes!', 3, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
    ('JWT implementation needs review for security best practices.', 1, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);
