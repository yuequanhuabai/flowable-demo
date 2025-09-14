package com.example.flowabledemo.service;

import com.example.flowabledemo.entity.User;
import com.example.flowabledemo.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户管理服务
 * OAuth2演示项目的简化用户认证服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // ================================
    // 用户查询方法
    // ================================

    /**
     * 根据用户名查找用户
     */
    public User findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            log.debug("Username is null or empty");
            return null;
        }
        
        try {
            User user = userMapper.findByUsername(username.trim());
            log.debug("Found user: {}", user != null ? user.getUsername() : "null");
            return user;
        } catch (Exception e) {
            log.error("Error finding user by username: {}", username, e);
            return null;
        }
    }

    /**
     * 根据用户名查找启用的用户
     */
    public User findEnabledUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            log.debug("Username is null or empty");
            return null;
        }
        
        try {
            User user = userMapper.findByUsernameAndEnabled(username.trim());
            log.debug("Found enabled user: {}", user != null ? user.getUsername() : "null");
            return user;
        } catch (Exception e) {
            log.error("Error finding enabled user by username: {}", username, e);
            return null;
        }
    }

    /**
     * 检查用户名是否存在
     */
    public boolean existsByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        
        try {
            return userMapper.existsByUsername(username.trim());
        } catch (Exception e) {
            log.error("Error checking username existence: {}", username, e);
            return false;
        }
    }

    // ================================
    // 用户认证方法
    // ================================

    /**
     * 验证用户登录
     * 核心认证方法，用于OAuth2流程中的用户身份验证
     */
    public boolean validateUser(String username, String password) {
        if (username == null || password == null || 
            username.trim().isEmpty() || password.trim().isEmpty()) {
            log.debug("Username or password is null/empty");
            return false;
        }

        try {
            // 1. 查找启用的用户
            User user = findEnabledUserByUsername(username);
            if (user == null) {
                log.debug("User not found or disabled: {}", username);
                return false;
            }

            // 2. 验证密码
            log.debug("验证密码 - 用户: {}", username);
            log.debug("输入密码长度: {}", password.length());
            log.debug("数据库密码哈希: {}", user.getPassword());
            log.debug("PasswordEncoder类型: {}", passwordEncoder.getClass().getSimpleName());
            
            boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
            log.info("用户 {} 密码验证结果: {}", username, passwordMatches ? "SUCCESS" : "FAILED");
            
            return passwordMatches;
            
        } catch (Exception e) {
            log.error("Error validating user: {}", username, e);
            return false;
        }
    }

    /**
     * 获取用户认证详情
     * 返回用户信息（不包含密码），用于认证后的用户会话
     */
    public UserAuthenticationDetails getUserAuthenticationDetails(String username) {
        User user = findEnabledUserByUsername(username);
        if (user == null) {
            return null;
        }

        return new UserAuthenticationDetails(
            user.getId(),
            user.getUsername(),
            user.getEnabled(),
            user.getCreatedAt()
        );
    }

    // ================================
    // 用户注册方法
    // ================================

    /**
     * 注册新用户
     */
    public boolean registerUser(String username, String password) {
        if (username == null || password == null ||
            username.trim().isEmpty() || password.trim().isEmpty()) {
            log.debug("Username or password is null/empty for registration");
            return false;
        }

        try {
            // 1. 检查用户名是否已存在
            if (existsByUsername(username)) {
                log.debug("Username already exists: {}", username);
                return false;
            }

            // 2. 验证用户名格式（简单验证）
            if (username.length() < 3 || username.length() > 20) {
                log.debug("Invalid username length: {}", username);
                return false;
            }

            // 3. 验证密码强度（简单验证）
            if (password.length() < 6) {
                log.debug("Password too short");
                return false;
            }

            // 4. 创建新用户
            User newUser = new User();
            newUser.setUsername(username.trim());
            newUser.setPassword(passwordEncoder.encode(password));
            newUser.setEnabled(true);
            newUser.setCreatedAt(java.time.LocalDateTime.now());
            newUser.setUpdatedAt(java.time.LocalDateTime.now());

            // 5. 保存到数据库
            int result = userMapper.insert(newUser);

            if (result > 0) {
                log.info("Successfully registered new user: {}", username);
                return true;
            } else {
                log.error("Failed to insert new user: {}", username);
                return false;
            }

        } catch (Exception e) {
            log.error("Error registering user: {}", username, e);
            return false;
        }
    }

    /**
     * 验证注册信息
     */
    public String validateRegistrationData(String username, String password, String confirmPassword) {
        // 验证用户名
        if (username == null || username.trim().isEmpty()) {
            return "用户名不能为空";
        }

        if (username.length() < 3 || username.length() > 20) {
            return "用户名长度必须在3-20字符之间";
        }

        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            return "用户名只能包含字母、数字和下划线";
        }

        if (existsByUsername(username)) {
            return "用户名已存在";
        }

        // 验证密码
        if (password == null || password.isEmpty()) {
            return "密码不能为空";
        }

        if (password.length() < 6 || password.length() > 50) {
            return "密码长度必须在6-50字符之间";
        }

        // 验证确认密码
        if (!password.equals(confirmPassword)) {
            return "两次输入的密码不一致";
        }

        return null; // 无错误
    }

    // ================================
    // 系统统计方法
    // ================================

    /**
     * 获取启用用户数量
     */
    public long getEnabledUserCount() {
        try {
            return userMapper.countEnabledUsers();
        } catch (Exception e) {
            log.error("Error counting enabled users", e);
            return 0;
        }
    }

    // ================================
    // 内部类：用户认证详情
    // ================================

    /**
     * 用户认证详情
     * 用于认证后的用户会话信息（不包含敏感数据）
     */
    public static class UserAuthenticationDetails {
        private final Long id;
        private final String username;
        private final Boolean enabled;
        private final java.time.LocalDateTime createdAt;

        public UserAuthenticationDetails(Long id, String username, Boolean enabled, java.time.LocalDateTime createdAt) {
            this.id = id;
            this.username = username;
            this.enabled = enabled;
            this.createdAt = createdAt;
        }

        // Getters
        public Long getId() { return id; }
        public String getUsername() { return username; }
        public Boolean getEnabled() { return enabled; }
        public java.time.LocalDateTime getCreatedAt() { return createdAt; }

        @Override
        public String toString() {
            return "UserAuthenticationDetails{" +
                    "id=" + id +
                    ", username='" + username + '\'' +
                    ", enabled=" + enabled +
                    ", createdAt=" + createdAt +
                    '}';
        }
    }
}