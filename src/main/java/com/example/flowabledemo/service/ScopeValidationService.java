package com.example.flowabledemo.service;

import org.springframework.stereotype.Service;

import java.util.*;

/**
 * OAuth2权限范围验证服务
 * 负责验证客户端申请的权限是否合理和安全
 */
@Service
public class ScopeValidationService {

    // ================================
    // 权限范围定义
    // ================================
    
    /**
     * 公开权限 - 任何客户端都可以申请
     */
    public static final Set<String> PUBLIC_SCOPES = Set.of(
        "read",           // 基础读取权限
        "user:basic"      // 用户基本信息
    );
    
    /**
     * 敏感权限 - 需要审核的权限
     */
    public static final Set<String> SENSITIVE_SCOPES = Set.of(
        "write",          // 写入权限
        "user:profile",   // 用户详细信息
        "user:email",     // 用户邮箱
        "user:phone"      // 用户手机号
    );
    
    /**
     * 特权权限 - 仅内部可信应用可申请
     */
    public static final Set<String> PRIVILEGED_SCOPES = Set.of(
        "admin",          // 管理员权限
        "user:delete",    // 删除用户
        "system:config",  // 系统配置
        "all"            // 全部权限
    );
    
    /**
     * 所有有效权限
     */
    public static final Set<String> ALL_VALID_SCOPES = new HashSet<String>() {{
        addAll(PUBLIC_SCOPES);
        addAll(SENSITIVE_SCOPES);  
        addAll(PRIVILEGED_SCOPES);
    }};

    // ================================
    // 权限验证方法
    // ================================
    
    /**
     * 验证并过滤客户端申请的权限
     * @param requestedScopes 客户端申请的权限
     * @param clientType 客户端类型（简化版，实际应该从数据库读取）
     * @return 验证结果
     */
    public ScopeValidationResult validateScopes(List<String> requestedScopes, String clientType) {
        ScopeValidationResult result = new ScopeValidationResult();
        
        if (requestedScopes == null || requestedScopes.isEmpty()) {
            // 默认给予基础读取权限
            result.setApprovedScopes(List.of("read"));
            result.setValid(true);
            result.setMessage("Granted default scope: read");
            return result;
        }
        
        List<String> approvedScopes = new ArrayList<>();
        List<String> rejectedScopes = new ArrayList<>();
        List<String> invalidScopes = new ArrayList<>();
        
        for (String scope : requestedScopes) {
            if (!ALL_VALID_SCOPES.contains(scope)) {
                // 无效的权限范围
                invalidScopes.add(scope);
                continue;
            }
            
            if (PUBLIC_SCOPES.contains(scope)) {
                // 公开权限，直接批准
                approvedScopes.add(scope);
            } else if (SENSITIVE_SCOPES.contains(scope)) {
                // 敏感权限，根据客户端类型决定
                if (canGrantSensitiveScope(clientType)) {
                    approvedScopes.add(scope);
                } else {
                    rejectedScopes.add(scope);
                }
            } else if (PRIVILEGED_SCOPES.contains(scope)) {
                // 特权权限，仅可信客户端可申请
                if (canGrantPrivilegedScope(clientType)) {
                    approvedScopes.add(scope);
                } else {
                    rejectedScopes.add(scope);
                }
            }
        }
        
        // 构建结果
        result.setApprovedScopes(approvedScopes);
        result.setRejectedScopes(rejectedScopes);
        result.setInvalidScopes(invalidScopes);
        result.setValid(!approvedScopes.isEmpty());
        
        // 构建消息
        StringBuilder message = new StringBuilder();
        if (!approvedScopes.isEmpty()) {
            message.append("Approved scopes: ").append(approvedScopes);
        }
        if (!rejectedScopes.isEmpty()) {
            if (message.length() > 0) message.append("; ");
            message.append("Rejected scopes: ").append(rejectedScopes).append(" (insufficient privileges)");
        }
        if (!invalidScopes.isEmpty()) {
            if (message.length() > 0) message.append("; ");
            message.append("Invalid scopes: ").append(invalidScopes);
        }
        
        result.setMessage(message.toString());
        return result;
    }
    
    /**
     * 检查是否可以授予敏感权限
     */
    private boolean canGrantSensitiveScope(String clientType) {
        // 简化实现：目前允许所有客户端申请敏感权限
        // 实际实现中应该检查客户端的验证状态、信誉等
        return !"public".equalsIgnoreCase(clientType);
    }
    
    /**
     * 检查是否可以授予特权权限
     */
    private boolean canGrantPrivilegedScope(String clientType) {
        // 仅内部可信应用可以申请特权权限
        return "trusted".equalsIgnoreCase(clientType) || "internal".equalsIgnoreCase(clientType);
    }
    
    /**
     * 获取权限描述
     */
    public String getScopeDescription(String scope) {
        return switch (scope) {
            case "read" -> "基础数据读取权限";
            case "write" -> "数据写入权限";
            case "user:basic" -> "用户基本信息访问权限";
            case "user:profile" -> "用户详细资料访问权限";
            case "user:email" -> "用户邮箱访问权限";
            case "user:phone" -> "用户手机号访问权限";
            case "admin" -> "管理员权限";
            case "user:delete" -> "用户删除权限";
            case "system:config" -> "系统配置权限";
            case "all" -> "全部权限";
            default -> "未知权限: " + scope;
        };
    }
    
    // ================================
    // 结果类
    // ================================
    
    public static class ScopeValidationResult {
        private boolean valid;
        private List<String> approvedScopes = new ArrayList<>();
        private List<String> rejectedScopes = new ArrayList<>();
        private List<String> invalidScopes = new ArrayList<>();
        private String message;
        
        // Getters and Setters
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        
        public List<String> getApprovedScopes() { return approvedScopes; }
        public void setApprovedScopes(List<String> approvedScopes) { this.approvedScopes = approvedScopes; }
        
        public List<String> getRejectedScopes() { return rejectedScopes; }
        public void setRejectedScopes(List<String> rejectedScopes) { this.rejectedScopes = rejectedScopes; }
        
        public List<String> getInvalidScopes() { return invalidScopes; }
        public void setInvalidScopes(List<String> invalidScopes) { this.invalidScopes = invalidScopes; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public boolean hasRejectedScopes() {
            return rejectedScopes != null && !rejectedScopes.isEmpty();
        }
        
        public boolean hasInvalidScopes() {
            return invalidScopes != null && !invalidScopes.isEmpty();
        }
    }
}