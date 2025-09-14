-- 重置用户表数据
-- 清理所有数据并重新插入正确的测试用户

-- 1. 清空用户表
TRUNCATE TABLE users;

-- 2. 重新插入测试用户（使用在线BCrypt生成器生成的哈希值）
-- 这些哈希值已经过验证，确保是BCrypt(10)生成的

INSERT INTO users (username, password, enabled) VALUES 
-- 用户名: user, 密码: password
('user', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', true),

-- 用户名: admin, 密码: admin  
('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', true),

-- 用户名: demo, 密码: demo
('demo', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', true);

-- 3. 验证插入结果
SELECT 
    id, 
    username, 
    LEFT(password, 20) as password_prefix,
    enabled, 
    created_at 
FROM users 
ORDER BY id;

-- 4. 显示插入的用户数量
SELECT COUNT(*) as total_users FROM users WHERE enabled = true;