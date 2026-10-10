-- ============================================================
-- V2__seed_roles_and_admin.sql
-- Seed mandatory reference data.
-- This script runs after V1 and populates the three core roles
-- and creates one initial administrator account.
--
-- IMPORTANT: Change the admin password immediately after first
-- login. This is a development seed only.
-- Password below is BCrypt hash of: Admin@123456
-- ============================================================

-- Seed the three core roles
INSERT INTO roles (name) VALUES ('STUDENT');
INSERT INTO roles (name) VALUES ('INSTRUCTOR');
INSERT INTO roles (name) VALUES ('ADMIN');

-- Seed the initial administrator account
-- Email: admin@optout.edu
-- Password: Admin@123456  (BCrypt, cost 12)
-- CHANGE THIS IMMEDIATELY IN ANY NON-DEV ENVIRONMENT
INSERT INTO users (email, password_hash, first_name, last_name, status, email_verified_at)
VALUES (
    'admin@optout.edu',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj/RK.PvhN1e',
    'System',
    'Administrator',
    'ACTIVE',
    CURRENT_TIMESTAMP(6)
);

-- Assign ADMIN role to the seeded admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'admin@optout.edu'
  AND r.name = 'ADMIN';

-- Record this in the audit log
INSERT INTO audit_events (actor_email, action, resource_type, details, occurred_at)
VALUES (
    'system',
    'SEED_ADMIN_ACCOUNT',
    'USER',
    'Initial admin account created by database seed script V2',
    CURRENT_TIMESTAMP(6)
);
