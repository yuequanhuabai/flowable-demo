//package com.example.flowabledemo.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * 密码测试控制器
// * 用于调试密码编码和验证问题
// */
//@RestController
//@RequestMapping("/test")
//public class PasswordTestController {
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private com.example.flowabledemo.service.CustomUserDetailsService userDetailsService;
//
//    /**
//     * 测试密码编码
//     */
//    @GetMapping("/encode")
//    public String encodePassword(@RequestParam String password) {
//        String encoded = passwordEncoder.encode(password);
//        return String.format("原密码: %s<br>编码后: %s", password, encoded);
//    }
//
//    /**
//     * 测试密码验证
//     */
//    @GetMapping("/verify")
//    public String verifyPassword(
//            @RequestParam String rawPassword,
//            @RequestParam String encodedPassword) {
//
//        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
//
//        return String.format("""
//            原密码: %s<br>
//            哈希值: %s<br>
//            验证结果: %s<br>
//            编码器类型: %s
//            """,
//            rawPassword,
//            encodedPassword,
//            matches ? "✅ 匹配" : "❌ 不匹配",
//            passwordEncoder.getClass().getSimpleName()
//        );
//    }
//
//    /**
//     * 测试数据库中的密码
//     */
//    @GetMapping("/test-db-password")
//    public String testDbPassword() {
//        String dbHash = "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";
//        String[] testPasswords = {"admin", "password", "123456", "demo"};
//
//        StringBuilder result = new StringBuilder();
//        result.append("<h3>测试数据库中的密码哈希</h3>");
//        result.append("数据库哈希值: ").append(dbHash).append("<br><br>");
//
//        for (String password : testPasswords) {
//            boolean matches = passwordEncoder.matches(password, dbHash);
//            result.append(String.format("测试密码 '%s': %s<br>",
//                password,
//                matches ? "✅ 匹配" : "❌ 不匹配"
//            ));
//        }
//
//        return result.toString();
//    }
//
//    /**
//     * 测试UserDetailsService
//     */
//    @GetMapping("/test-userdetails")
//    public String testUserDetailsService(@RequestParam(defaultValue = "admin") String username) {
//        try {
//            var userDetails = userDetailsService.loadUserByUsername(username);
//
//            return String.format("""
//                <h3>UserDetailsService测试结果</h3>
//                用户名: %s<br>
//                密码哈希: %s<br>
//                启用状态: %s<br>
//                权限: %s<br>
//                账户未过期: %s<br>
//                账户未锁定: %s<br>
//                凭证未过期: %s
//                """,
//                userDetails.getUsername(),
//                userDetails.getPassword(),
//                userDetails.isEnabled(),
//                userDetails.getAuthorities(),
//                userDetails.isAccountNonExpired(),
//                userDetails.isAccountNonLocked(),
//                userDetails.isCredentialsNonExpired()
//            );
//        } catch (Exception e) {
//            return String.format("错误: %s<br>详情: %s", e.getClass().getSimpleName(), e.getMessage());
//        }
//    }
//
//    /**
//     * 完整的认证测试
//     */
//    @GetMapping("/test-authentication")
//    public String testAuthentication(
//            @RequestParam(defaultValue = "admin") String username,
//            @RequestParam(defaultValue = "admin") String password) {
//
//        StringBuilder result = new StringBuilder();
//        result.append("<h3>完整认证流程测试</h3>");
//
//        try {
//            // 1. 测试UserDetailsService
//            var userDetails = userDetailsService.loadUserByUsername(username);
//            result.append("✅ UserDetailsService加载用户成功<br>");
//            result.append("数据库密码哈希: ").append(userDetails.getPassword()).append("<br>");
//
//            // 2. 测试密码验证
//            boolean matches = passwordEncoder.matches(password, userDetails.getPassword());
//            result.append("✅ 密码验证结果: ").append(matches ? "SUCCESS" : "FAILED").append("<br>");
//
//            if (matches) {
//                result.append("<br><strong style='color: green;'>🎉 认证流程完全正常！</strong>");
//            } else {
//                result.append("<br><strong style='color: red;'>❌ 密码验证失败</strong>");
//            }
//
//        } catch (Exception e) {
//            result.append("❌ 错误: ").append(e.getMessage());
//        }
//
//        return result.toString();
//    }
//
//    /**
//     * 测试当前数据库哈希对应什么密码
//     */
//    @GetMapping("/reverse-test")
//    public String reverseTestPassword() {
//        String dbHash = "$2a$10$5OpTX3vjdsb3F6EaBk6YQ.x2hDPxl2xgKFCG/GGc0JCvEBmKxQo8a";
//        String[] commonPasswords = {
//            "admin", "password", "123456", "demo", "test", "user",
//            "admin123", "password123", "abc123", "qwerty", "letmein"
//        };
//
//        StringBuilder result = new StringBuilder();
//        result.append("<h3>反向密码测试 - 当前admin用户哈希</h3>");
//        result.append("哈希值: ").append(dbHash).append("<br><br>");
//
//        for (String pwd : commonPasswords) {
//            boolean matches = passwordEncoder.matches(pwd, dbHash);
//            if (matches) {
//                result.append(String.format("🎉 <strong style='color: green;'>找到匹配密码: '%s'</strong><br>", pwd));
//            } else {
//                result.append(String.format("❌ 密码 '%s': 不匹配<br>", pwd));
//            }
//        }
//
//        return result.toString();
//    }
//
//    /**
//     * 生成标准测试用户的BCrypt哈希
//     */
//    @GetMapping("/generate-test-hashes")
//    public String generateTestHashes() {
//        StringBuilder result = new StringBuilder();
//        result.append("<h3>生成测试用户BCrypt哈希值</h3>");
//        result.append("<p>复制以下SQL语句到数据库执行:</p>");
//        result.append("<pre style='background: #f5f5f5; padding: 10px;'>");
//
//        String userHash = passwordEncoder.encode("password");
//        String adminHash = passwordEncoder.encode("admin");
//        String demoHash = passwordEncoder.encode("demo");
//
//        result.append("-- 清空并重新插入用户数据\n");
//        result.append("TRUNCATE TABLE users;\n\n");
//        result.append("INSERT INTO users (username, password, enabled) VALUES\n");
//        result.append(String.format("('user', '%s', true),\n", userHash));
//        result.append(String.format("('admin', '%s', true),\n", adminHash));
//        result.append(String.format("('demo', '%s', true);\n", demoHash));
//        result.append("\n-- 验证插入\n");
//        result.append("SELECT username, LEFT(password, 25) as hash_prefix, enabled FROM users;");
//
//        result.append("</pre>");
//
//        // 验证生成的哈希
//        result.append("<h4>验证生成的哈希:</h4>");
//        result.append(String.format("user/password: %s<br>", passwordEncoder.matches("password", userHash) ? "✅" : "❌"));
//        result.append(String.format("admin/admin: %s<br>", passwordEncoder.matches("admin", adminHash) ? "✅" : "❌"));
//        result.append(String.format("demo/demo: %s<br>", passwordEncoder.matches("demo", demoHash) ? "✅" : "❌"));
//
//        return result.toString();
//    }
//}