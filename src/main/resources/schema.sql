-- Drop table if it exists
DROP TABLE IF EXISTS T_USER;

-- Create user table
CREATE TABLE T_USER (
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    USERNAME VARCHAR(255) NOT NULL UNIQUE,
    PASSWORD VARCHAR(255) NOT NULL
);

-- Insert sample users
-- Passwords are 'password' encoded with BCrypt.
-- You can generate your own here: https://www.bcryptcalculator.com/
INSERT INTO T_USER (USERNAME, PASSWORD) VALUES ('user1', '$2a$10$Y.OTQx.gC.xik2xSjA/9b.LhN4y.Skc2A4iZRfH6vj3aL3i/i/dOS');
INSERT INTO T_USER (USERNAME, PASSWORD) VALUES ('user2', '$2a$10$Y.OTQx.gC.xik2xSjA/9b.LhN4y.Skc2A4iZRfH6vj3aL3i/i/dOS');
INSERT INTO T_USER (USERNAME, PASSWORD) VALUES ('manager', '$2a$10$Y.OTQx.gC.xik2xSjA/9b.LhN4y.Skc2A4iZRfH6vj3aL3i/i/dOS');
