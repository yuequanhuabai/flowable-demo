-- OAuth2 客戶端註冊表
-- 簡化版設計，包含核心字段

DROP TABLE IF EXISTS oauth2_client;

CREATE TABLE oauth2_client (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主鍵ID',
    client_id VARCHAR(100) NOT NULL UNIQUE COMMENT '客戶端ID',
    client_secret VARCHAR(255) NOT NULL COMMENT '客戶端密鑰',
    client_name VARCHAR(200) NOT NULL COMMENT '客戶端名稱',
    redirect_uri VARCHAR(500) NOT NULL COMMENT '重定向URI',
    scopes VARCHAR(500) DEFAULT 'read,write' COMMENT '權限範圍，逗號分隔',
    grant_types VARCHAR(200) DEFAULT 'authorization_code,refresh_token' COMMENT '授權類型，逗號分隔',
    client_description TEXT COMMENT '客戶端描述',
    is_active TINYINT DEFAULT 1 COMMENT '是否激活 0-否 1-是',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '創建時間',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
    INDEX idx_client_id (client_id),
    INDEX idx_client_name (client_name),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OAuth2客戶端註冊表';

-- Insert test data
INSERT INTO oauth2_client (client_id, client_secret, client_name, redirect_uri, scopes, client_description) VALUES 
('test_client_001', '{bcrypt}$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFVdpms7OOUlJNlf6/WdOOG', 'Test Client App', 'http://localhost:3000/callback', 'read,write,user:profile', 'Sample client for testing OAuth2 flow'),
('demo_webapp_002', '{bcrypt}$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFVdpms7OOUlJNlf6/WdOOG', 'Demo Web App', 'http://localhost:8080/oauth2/callback', 'read,write', 'Demo web application client');