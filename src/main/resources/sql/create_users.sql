-- 用户表创建脚本
-- OAuth2演示项目 - 简化用户认证系统

DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(200) NOT NULL COMMENT 'BCrypt加密存储的密码',
    enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '账户是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 性能优化索引
    INDEX idx_username (username),
    INDEX idx_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表-OAuth2演示用';

-- 插入测试用户数据
-- 密码使用BCrypt加密（strength=10）
-- 先删除现有数据
DELETE FROM users;

-- 插入新的正确哈希数据
INSERT INTO users (username, password) VALUES
('user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM7lFd9Nl.BftEyU5OxS'),    -- 密码: password  
('admin', '$2a$10$5OpTX3vjdsb3F6EaBk6YQ.x2hDPxl2xgKFCG/GGc0JCvEBmKxQo8a'),   -- 密码: admin
('demo', '$2a$10$EblZqNptyYdQfmFK7QwkNeBXrxbI8dX7lK4QEMqx6V9ug2HYKCqna');    -- 密码: demo

-- 验证数据插入
SELECT id, username, enabled, created_at FROM users;