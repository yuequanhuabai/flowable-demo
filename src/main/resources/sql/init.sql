-- OAuth2 PKCE 演示項目數據庫初始化腳本
-- 數據庫: test

-- 用戶表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(200) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 角色表
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用戶角色關聯表
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- OAuth2 客戶端註冊表
CREATE TABLE IF NOT EXISTS oauth2_registered_client (
    id varchar(100) PRIMARY KEY,
    client_id varchar(100) NOT NULL UNIQUE,
    client_id_issued_at timestamp DEFAULT CURRENT_TIMESTAMP,
    client_secret varchar(200),
    client_secret_expires_at timestamp DEFAULT NULL,
    client_name varchar(200) NOT NULL,
    client_authentication_methods varchar(1000) NOT NULL,
    authorization_grant_types varchar(1000) NOT NULL,
    redirect_uris varchar(1000),
    post_logout_redirect_uris varchar(1000),
    scopes varchar(1000) NOT NULL,
    client_settings varchar(2000) NOT NULL,
    token_settings varchar(2000) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- OAuth2 授權信息表
CREATE TABLE IF NOT EXISTS oauth2_authorization (
    id varchar(100) PRIMARY KEY,
    registered_client_id varchar(100) NOT NULL,
    principal_name varchar(200) NOT NULL,
    authorization_grant_type varchar(100) NOT NULL,
    authorized_scopes varchar(1000),
    attributes blob,
    state varchar(500),
    authorization_code_value blob,
    authorization_code_issued_at timestamp,
    authorization_code_expires_at timestamp,
    authorization_code_metadata blob,
    access_token_value blob,
    access_token_issued_at timestamp,
    access_token_expires_at timestamp,
    access_token_metadata blob,
    access_token_type varchar(100),
    access_token_scopes varchar(1000),
    oidc_id_token_value blob,
    oidc_id_token_issued_at timestamp,
    oidc_id_token_expires_at timestamp,
    oidc_id_token_metadata blob,
    refresh_token_value blob,
    refresh_token_issued_at timestamp,
    refresh_token_expires_at timestamp,
    refresh_token_metadata blob,
    user_code_value blob,
    user_code_issued_at timestamp,
    user_code_expires_at timestamp,
    user_code_metadata blob,
    device_code_value blob,
    device_code_issued_at timestamp,
    device_code_expires_at timestamp,
    device_code_metadata blob
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- OAuth2 用戶同意表
CREATE TABLE IF NOT EXISTS oauth2_authorization_consent (
    registered_client_id varchar(100) NOT NULL,
    principal_name varchar(200) NOT NULL,
    authorities varchar(1000) NOT NULL,
    PRIMARY KEY (registered_client_id, principal_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入初始角色數據
INSERT IGNORE INTO roles (name, description) VALUES 
('USER', '普通用戶'),
('ADMIN', '管理員');

-- 插入初始用戶數據 (密碼已使用 BCrypt 加密)
-- user 密碼: password
-- admin 密碼: admin
INSERT IGNORE INTO users (username, password) VALUES 
('user', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW'),
('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG');

-- 分配用戶角色
INSERT IGNORE INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r 
WHERE u.username = 'user' AND r.name = 'USER';

INSERT IGNORE INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r 
WHERE u.username = 'admin' AND r.name IN ('USER', 'ADMIN');