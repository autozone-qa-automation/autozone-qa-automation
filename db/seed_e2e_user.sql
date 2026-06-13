-- ==========================================================================
-- Autozone QA - Seed E2E test user (Selenium / Cucumber)
-- ==========================================================================
--
-- The E2E suite (CucumberTest, testng.xml) logs in before every scenario
-- using the credentials configured in .env.properties:
--
--   test.username=carlos.mendoza@autozone.com
--   test.password=Password123!
--
-- The backend has no public endpoint that creates a user able to log in
-- (passwords sent through the API are not guaranteed to be hashed the same
-- way), so this script inserts the user directly with a BCrypt hash
-- (cost factor 4 - matches BCryptPasswordEncoder(4, ...) used by the
-- backend, see UserManagementConfig.java).
--
-- Hash below corresponds to: Password123!
--
-- Usage:
--   mysql -u <user> -p <database_name> < db/seed_e2e_user.sql
--
-- The script is idempotent: it can be run multiple times without creating
-- duplicate rows.
-- ==========================================================================

INSERT INTO roles (idRole, permission)
SELECT 1, 'ADMIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE idRole = 1);

INSERT INTO users (email, isActive, lastName, name, hashPassword, idRoles)
SELECT 'carlos.mendoza@autozone.com', 1, 'Mendoza', 'Carlos', '$2b$04$zreRC5iZoxSp5dtwzKA1hONKLU.hJN/WPmq8oZb7jGirMRiTlSKRa', 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'carlos.mendoza@autozone.com');
