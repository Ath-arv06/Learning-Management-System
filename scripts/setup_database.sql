-- ============================================================
-- OptOut LMS — Database Setup Script
-- Run this ONCE in MySQL Workbench or MySQL CLI as a user with
-- CREATE DATABASE and GRANT privileges (typically root).
--
-- After running this script:
--   DB name:  lms_db
--   App user: lms_user  (password: change before production)
--   Root should NOT be used by the application.
-- ============================================================

-- Create the database
CREATE DATABASE IF NOT EXISTS lms_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Create a least-privilege application user
-- CHANGE THE PASSWORD before any non-development use
CREATE USER IF NOT EXISTS 'lms_user'@'localhost' IDENTIFIED BY 'LmsDevPass2024!';

-- Grant only necessary permissions to the application user
-- Flyway needs ALTER, CREATE, DROP, INDEX, REFERENCES for migrations
-- The application at runtime needs SELECT, INSERT, UPDATE, DELETE
GRANT SELECT, INSERT, UPDATE, DELETE,
      CREATE, ALTER, DROP, INDEX, REFERENCES
ON lms_db.* TO 'lms_user'@'localhost';

FLUSH PRIVILEGES;

-- Verify setup
SELECT User, Host FROM mysql.user WHERE User = 'lms_user';
SHOW GRANTS FOR 'lms_user'@'localhost';

-- ============================================================
-- Optional: separate migration user (more secure)
-- If you want to separate migration credentials from app credentials:
--
-- CREATE USER 'lms_migrator'@'localhost' IDENTIFIED BY 'MigratorPass2024!';
-- GRANT ALL ON lms_db.* TO 'lms_migrator'@'localhost';
-- Then use lms_migrator only in Flyway config, lms_user for app.
-- ============================================================
