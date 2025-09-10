-- OAuth2 客户端注册表
-- 基于四个核心安全机制设计

DROP TABLE IF EXISTS oauth2_client;

CREATE TABLE oauth2_client (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- 🏷️ 身份标识机制 (防止冒充)
    client_id VARCHAR(100) NOT NULL UNIQUE COMMENT '客户端唯一标识符',
    
    -- 🔐 身份验证机制 (防止伪造请求)
    client_secret VARCHAR(255) NOT NULL COMMENT '客户端密钥，BCrypt加密存储',
    
    -- 📝 基本信息 (便于管理和追踪)
    client_name VARCHAR(200) NOT NULL COMMENT '客户端应用名称',
    
    -- 🎯 权限控制机制 (防止越权和劫持)
    redirect_uri VARCHAR(500) NOT NULL COMMENT '授权回调地址，防止授权劫持',
    scopes VARCHAR(200) DEFAULT 'read' COMMENT '权限范围，逗号分隔，防止越权访问',
    
    -- ⏰ 可撤销机制支持 (应对突发安全事件)
    is_active BOOLEAN DEFAULT true COMMENT '是否启用，支持快速禁用客户端',
    
    -- 📅 审计和追踪信息
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    
    -- 🚀 性能优化索引
    INDEX idx_client_id (client_id),
    INDEX idx_client_name (client_name),
    INDEX idx_is_active (is_active),
    INDEX idx_created_at (created_at)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OAuth2客户端注册表';

-- 插入一个测试数据
INSERT INTO oauth2_client (client_id, client_secret, client_name, redirect_uri, scopes) VALUES 
('test_client_001', '$2a$10$example.bcrypt.hash.for.testing.purposes', 'Test Application', 'http://localhost:3000/callback', 'read,write');

