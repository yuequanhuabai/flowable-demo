# OAuth2 用户认证系统实施方案

## 方案背景

基于OAuth2演示项目的核心目标，采用**简化优先**策略：
- 🎯 **聚焦OAuth2流程** - 避免复杂的用户管理系统
- 🚀 **快速原型验证** - 优先实现核心功能
- 📝 **保持项目简洁** - 避免过度设计

## 当前策略分析

### ✅ 用户表创建 - 立即执行

#### 简化的数据库设计
```sql
-- 简化的用户表设计（去除角色复杂度）
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
INSERT INTO users (username, password) VALUES
('user', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW'),   -- 密码: password
('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG');  -- 密码: admin
```

#### 后端实现需求
1. **创建 User 实体类**
   ```java
   @TableName("users")
   @Data
   public class User {
       private Long id;
       private String username;
       private String password;
       private Boolean enabled;
       private LocalDateTime createdAt;
       private LocalDateTime updatedAt;
   }
   ```

2. **创建 UserMapper 接口**
   ```java
   @Mapper
   public interface UserMapper extends BaseMapper<User> {
       User findByUsername(String username);
       boolean existsByUsername(String username);
   }
   ```

3. **创建 UserService 服务类**
   ```java
   @Service
   public class UserService {
       // 用户认证验证
       public boolean validateUser(String username, String password);
       
       // 根据用户名查找用户
       public User findByUsername(String username);
   }
   ```

### ❌ 前端登录界面 - 暂缓执行

#### 为什么暂时不需要自定义前端登录界面？

1. **Spring Security 默认支持**
   - 自动提供 `/login` 登录页面
   - 内置表单认证处理
   - 足够满足OAuth2演示需求

2. **OAuth2 流程特点**
   - 用户认证通常在授权服务器内部完成
   - 客户端应用不直接处理用户密码
   - 标准OAuth2流程中，登录页面由授权服务器提供

3. **开发效率考虑**
   - 先让OAuth2后端流程完全跑通
   - 验证整个授权码流程无误
   - 最后再进行UI美化

## 实施计划

### 第一阶段：用户认证基础设施 ⚡
**立即执行任务：**
1. 创建用户表SQL脚本
2. 实现User实体类、Mapper、Service
3. 集成Spring Security基础认证
4. **使用默认 `/login` 页面进行测试**

### 第二阶段：OAuth2授权服务器 🔐
**下一步任务：**
1. 启用pom.xml中的OAuth2依赖
2. 实现 `/oauth2/authorize` 授权端点
3. 实现 `/oauth2/token` 令牌端点
4. **使用Spring Security默认授权确认页面**

### 第三阶段：完整流程验证 🧪
**验证任务：**
1. 测试完整OAuth2授权码流程
2. 验证客户端注册 → 用户登录 → 授权 → 获取令牌
3. 确保安全性和正确性

### 第四阶段：UI优化（可选）🎨
**美化任务：**
1. 自定义登录页面设计
2. 自定义授权确认页面
3. 完善前端用户体验

## 技术实现细节

### 用户认证策略
```java
// 简化的用户认证逻辑
@Service
public class UserService {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public boolean validateUser(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null || !user.getEnabled()) {
            return false;
        }
        return passwordEncoder.matches(password, user.getPassword());
    }
}
```

### Spring Security 配置策略
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/oauth2/client/**").permitAll()  // 客户端注册API
                .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults())  // 使用默认登录页面
            .oauth2AuthorizationServer(Customizer.withDefaults());  // 启用OAuth2授权服务器
        return http.build();
    }
}
```

## 关键优势

### 🎯 聚焦核心功能
- **避免过度工程化**：不实现复杂的用户管理系统
- **OAuth2为中心**：所有实现围绕OAuth2演示目标

### ⚡ 快速迭代
- **利用框架默认**：最大化使用Spring Security内置功能
- **渐进式开发**：先跑通流程，再优化体验

### 🛡️ 安全可靠
- **标准实现**：遵循OAuth2规范和Spring Security最佳实践
- **简单可维护**：减少自定义代码，降低bug风险

## 下一步行动

**建议立即开始第一阶段**：
1. ✅ 创建用户表
2. ✅ 实现User相关的实体、Mapper、Service
3. ✅ 配置基础Spring Security认证

**验收标准**：
- 用户表创建成功，包含测试数据
- 能够通过用户名/密码验证用户身份
- Spring Security默认 `/login` 页面可正常工作

---

*文档版本：Step 2*  
*创建时间：2025-09-13*  
*核心理念：简化优先，OAuth2为中心*