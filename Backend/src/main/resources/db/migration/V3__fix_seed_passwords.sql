-- V3: Fix seed data password hashes (V2 used an incorrect BCrypt hash)
-- The hash $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- does not correspond to the intended passwords.

UPDATE users SET password = '$2a$10$StSvQTv4ojdpcHPR19vC7.EDZf9WVO3Na8NeqCy3Zb9n82Fl.n2tW'
WHERE email = 'admin@taskmanagement.com';

UPDATE users SET password = '$2a$10$n7XqEXdhaJD5KxRRXPRWjOksZv60CEepLy0u9cW9PnBqqU/QFeCFK'
WHERE email = 'manager@taskmanagement.com';

UPDATE users SET password = '$2a$10$Uj70rfLq9P5woKkcA184GunSyUzp3O8SJkFaJDGQn0wHV4vy9OsCe'
WHERE email = 'employee@taskmanagement.com';
