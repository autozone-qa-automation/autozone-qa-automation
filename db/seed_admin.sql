-- ==========================================================================
-- Autozone QA - Seed ADMIN user for API integration tests
-- ==========================================================================
--
-- The backend has no working API path to bootstrap an ADMIN account:
-- POST /api/v1/users requires an authenticated ADMIN caller and stores the
-- supplied password unhashed, so it cannot be used to create a user that is
-- able to log in. This script inserts the ADMIN role (if missing) and one
-- ADMIN user directly into the database with a BCrypt-hashed password.
--
-- Credentials seeded by this script:
--   email:    admin@autozone.com
--   password: Qa!Admin2026   (hashed below with BCrypt, cost factor 4 -
--                              matches BCryptPasswordEncoder(4, ...) used
--                              by the backend)
--
-- These must match src/test/resources/env.properties (api.username /
-- api.password). If you change either value here, regenerate the hash and
-- update env.properties accordingly.
--
-- Usage:
--   mysql -u <user> -p <database_name> < db/seed_admin.sql
--
-- The script is idempotent: it can be run multiple times without creating
-- duplicate rows.
-- ==========================================================================

INSERT INTO roles (idRole, permission)
SELECT 1, 'ADMIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE idRole = 1);

INSERT INTO users (email, isActive, lastName, name, hashPassword, idRoles)
SELECT 'admin@autozone.com', 1, 'Admin', 'QA', '$2a$04$2Ortr5niNPuU1.RyUYx7uuvG7Egh7Qa16TqPSPrYBdWdZRcLgO0zC', 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@autozone.com');
