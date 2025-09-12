# OAuth2客户端注册验证步骤

## 前置准备

### 1. 数据库准备
```sql
-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS oauth2 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. 使用数据库
USE oauth2;

-- 3. 执行建表脚本
-- 运行 src/main/resources/sql/create_oauth2.sql 文件
SOURCE D:/software/developmentTools/Git/gitee/newpap/demo/flowable-demo/src/main/resources/sql/create_oauth2.sql;
```

### 2. 应用启动
```bash
# 在项目根目录执行
mvn spring-boot:run

# 或者使用IDE直接运行 FlowableDemoApplication.java
```

### 3. 确认服务启动成功
访问: http://localhost:8080
看到Spring Boot默认错误页面或其他响应即表示启动成功。

---

## 客户端注册验证步骤

### 步骤1: 成功注册客户端

**请求方式:** POST  
**请求地址:** http://localhost:8080/oauth2/client/register  
**请求头:**
```
Content-Type: application/json
```

**请求体:**
```json
{
    "clientName": "测试应用001",
    "redirectUri": "http://localhost:3000/callback",
    "scopes": ["read", "write", "user:profile"],
    "description": "这是一个测试OAuth2客户端应用"
}
```

**使用curl命令:**
```bash
curl -X POST http://localhost:8080/oauth2/client/register \
  -H "Content-Type: application/json" \
  -d '{
    "clientName": "测试应用001",
    "redirectUri": "http://localhost:3000/callback", 
    "scopes": ["read", "write", "user:profile"],
    "description": "这是一个测试OAuth2客户端应用"
  }'
```

**预期成功响应:**
```json
{
    "success": true,
    "clientId": "测试应用_a1b2c3d4",
    "clientSecret": "abc123def456ghi789jkl012mno345pqr678stu901vwx234yz567",
    "clientName": "测试应用001", 
    "redirectUri": "http://localhost:3000/callback",
    "scopes": ["read", "write", "user:profile"],
    "message": "Client registered successfully"
}
```

**❗重要提醒:** 
- `clientSecret` 只在注册时返回一次，请立即保存！
- `clientId` 是自动生成的唯一标识符

---

### 步骤2: 验证参数校验功能

#### 2.1 测试缺少必填字段
```bash
curl -X POST http://localhost:8080/oauth2/client/register \
  -H "Content-Type: application/json" \
  -d '{
    "redirectUri": "http://localhost:3000/callback"
  }'
```

**预期错误响应:**
```json
{
    "error": "validation_error",
    "errorDescription": "clientName: Client name is required;"
}
```

#### 2.2 测试无效的重定向URI
```bash
curl -X POST http://localhost:8080/oauth2/client/register \
  -H "Content-Type: application/json" \
  -d '{
    "clientName": "测试应用002",
    "redirectUri": "invalid-uri"
  }'
```

**预期错误响应:**
```json
{
    "error": "validation_error", 
    "errorDescription": "redirectUri: Redirect URI must start with http:// or https://;"
}
```

#### 2.3 测试重复的客户端名称
```bash
# 重复提交相同的clientName
curl -X POST http://localhost:8080/oauth2/client/register \
  -H "Content-Type: application/json" \
  -d '{
    "clientName": "测试应用001",
    "redirectUri": "http://localhost:4000/callback"
  }'
```

**预期错误响应:**
```json
{
    "success": false,
    "error": "invalid_request", 
    "errorDescription": "Client name already exists"
}
```

---

### 步骤3: 查询已注册的客户端

#### 3.1 查询客户端信息（不含密钥）
```bash
# 使用步骤1返回的clientId
curl -X GET http://localhost:8080/oauth2/client/{clientId}
```

**例如:**
```bash
curl -X GET http://localhost:8080/oauth2/client/测试应用_a1b2c3d4
```

**预期成功响应:**
```json
{
    "id": 1,
    "clientId": "测试应用_a1b2c3d4",
    "clientName": "测试应用001",
    "redirectUri": "http://localhost:3000/callback",
    "scopes": "read,write,user:profile", 
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
}
```

**注意:** 响应中不包含 `clientSecret`，这是安全设计！

#### 3.2 检查客户端状态
```bash
curl -X GET http://localhost:8080/oauth2/client/测试应用_a1b2c3d4/status
```

**预期响应:**
```json
{
    "clientId": "测试应用_a1b2c3d4",
    "active": true
}
```

#### 3.3 查询不存在的客户端
```bash
curl -X GET http://localhost:8080/oauth2/client/nonexistent_client
```

**预期错误响应:**
```json
{
    "error": "client_not_found",
    "errorDescription": "Client not found" 
}
```

---

### 步骤4: 数据库验证

#### 4.1 检查客户端表数据
```sql
-- 查询所有已注册的客户端
SELECT id, client_id, client_name, redirect_uri, scopes, is_active, created_at 
FROM oauth2_client;

-- 验证密钥是否被正确加密存储
SELECT client_id, client_secret 
FROM oauth2_client 
WHERE client_id = '测试应用_a1b2c3d4';
-- client_secret应该是BCrypt加密后的字符串，如: $2a$14$...
```

#### 4.2 验证索引和约束
```sql
-- 检查唯一约束
SHOW INDEX FROM oauth2_client WHERE Key_name = 'client_id';

-- 测试唯一约束（应该失败）
INSERT INTO oauth2_client (client_id, client_secret, client_name, redirect_uri) 
VALUES ('测试应用_a1b2c3d4', 'test', 'duplicate', 'http://test.com');
-- 预期错误: Duplicate entry for key 'client_id'
```

---

## 常见问题排查

### 问题1: 应用启动失败
**症状:** 启动时报错连接数据库失败  
**解决:** 
1. 检查MySQL服务是否启动
2. 确认数据库连接配置 (application.yml)
3. 确认数据库 `oauth2` 是否创建

### 问题2: 客户端注册失败
**症状:** 返回500错误  
**排查:**
1. 查看控制台日志
2. 检查数据库表是否创建成功
3. 确认MyBatis配置是否正确

### 问题3: 验证注解不生效
**症状:** 无效请求没有被拦截  
**解决:** 确认使用的是 `jakarta.validation.Valid` 而非 `javax.validation.Valid`

### 问题4: 中文乱码
**症状:** 客户端名称显示乱码  
**解决:** 
1. 确认数据库字符集为 utf8mb4
2. 确认API请求使用UTF-8编码

---

## 验证成功标准

✅ **所有测试步骤都应该:**
1. 正常客户端注册返回201状态码和完整信息
2. 无效请求返回400状态码和具体错误信息  
3. 客户端查询返回正确的信息（不含密钥）
4. 数据库中正确存储客户端信息和加密密钥
5. 重复注册被正确拒绝
6. 参数验证功能正常工作

**恭喜！** 如果所有步骤都通过，说明OAuth2客户端注册功能实现成功！

---

## 下一步

客户端注册完成后，可以继续实现：
1. 授权码流程 (Authorization Code Flow)
2. 访问令牌交换
3. 用户认证和授权
4. 资源服务器保护