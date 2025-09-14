package com.example.flowabledemo.service;

import com.example.flowabledemo.entity.OAuth2Client;
import com.example.flowabledemo.mapper.OAuth2ClientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * OAuth2客户端管理服务
 * 负责客户端注册、查询、更新等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class OAuth2ClientService {

    private final OAuth2ClientMapper clientMapper;
    private final ScopeValidationService scopeValidationService;
    private final PasswordEncoder passwordEncoder;

    // ================================
    // 客户端注册相关方法
    // ================================

    /**
     * 注册新的OAuth2客户端
     */
    public ClientRegistrationResult registerClient(ClientRegistrationRequest request) {
        ClientRegistrationResult result = new ClientRegistrationResult();

        try {
            // 1. 验证注册请求
            ValidationResult validation = validateRegistrationRequest(request);
            if (!validation.isValid()) {
                result.setSuccess(false);
                result.setError("invalid_request");
                result.setErrorDescription(validation.getErrorMessage());
                return result;
            }

            // 2. 生成客户端ID和密钥
            String clientId = generateClientId(request.getClientName());
            String clientSecret = generateClientSecret();
            String hashedSecret = passwordEncoder.encode(clientSecret);

            // 3. 创建客户端实体
            OAuth2Client client = new OAuth2Client();
            client.setClientId(clientId);
            client.setClientSecret(hashedSecret);
            client.setClientName(request.getClientName());
            client.setRedirectUri(request.getRedirectUri());
            client.setScopes(String.join(",", request.getScopes()));
            client.setIsActive(true);
            client.setCreatedAt(LocalDateTime.now());
            client.setUpdatedAt(LocalDateTime.now());

            // 4. 保存到数据库
            clientMapper.insert(client);

            // 5. 构建成功响应（包含客户端密钥）
            result.setSuccess(true);
            result.setClientId(clientId);
            result.setClientSecret(clientSecret); // 明文密钥只在注册时返回
            result.setClientName(request.getClientName());
            result.setRedirectUri(request.getRedirectUri());
            result.setScopes(request.getScopes());
            
            String message = "Client registered successfully";
            if (validation.getWarning() != null) {
                message += ". Warning: " + validation.getWarning();
            }
            result.setMessage(message);

            return result;

        } catch (Exception e) {
            result.setSuccess(false);
            result.setError("server_error");
            result.setErrorDescription("Failed to register client: " + e.getMessage());
            return result;
        }
    }

    /**
     * 根据客户端ID查找客户端
     */
    public OAuth2Client findByClientId(String clientId) {
        return clientMapper.findByClientId(clientId);
    }

    /**
     * 检查客户端是否存在且活跃
     */
    public boolean isClientActive(String clientId) {
        OAuth2Client client = findByClientId(clientId);
        return client != null && client.getIsActive();
    }

    // ================================
    // 私有辅助方法
    // ================================

    /**
     * 验证客户端注册请求
     */
    private ValidationResult validateRegistrationRequest(ClientRegistrationRequest request) {
        ValidationResult result = new ValidationResult();

        // 检查必填字段
        if (request.getClientName() == null || request.getClientName().trim().isEmpty()) {
            result.setValid(false);
            result.setErrorMessage("Client name is required");
            return result;
        }

        if (request.getRedirectUri() == null || request.getRedirectUri().trim().isEmpty()) {
            result.setValid(false);
            result.setErrorMessage("Redirect URI is required");
            return result;
        }

        // 检查重定向URI格式
        if (!isValidRedirectUri(request.getRedirectUri())) {
            result.setValid(false);
            result.setErrorMessage("Invalid redirect URI format");
            return result;
        }

        // 检查客户端名称是否已存在
        if (clientMapper.existsByClientName(request.getClientName())) {
            result.setValid(false);
            result.setErrorMessage("Client name already exists");
            return result;
        }

        // 验证和过滤权限范围
        ScopeValidationService.ScopeValidationResult scopeResult = 
            scopeValidationService.validateScopes(request.getScopes(), "confidential"); // 简化版，实际应该从请求中获取客户端类型
        
        if (!scopeResult.isValid()) {
            result.setValid(false);
            result.setErrorMessage("Invalid scopes: " + scopeResult.getMessage());
            return result;
        }
        
        // 如果有被拒绝的权限，给出警告信息但仍然允许注册
        if (scopeResult.hasRejectedScopes()) {
            // 注意：这里可以选择拒绝注册或者给出警告
            // 当前实现：允许注册但记录被拒绝的权限
            result.setWarning("Some requested scopes were rejected: " + scopeResult.getRejectedScopes());
        }
        
        // 更新请求中的权限为已批准的权限
        request.setScopes(scopeResult.getApprovedScopes());

        result.setValid(true);
        return result;
    }

    /**
     * 生成客户端ID
     */
    private String generateClientId(String clientName) {
        int length = clientName.toLowerCase().replaceAll("[^a-z0-9]", "").length();
        String prefix = clientName.toLowerCase().replaceAll("[^a-z0-9]", "").substring(0, Math.min(8, length));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String clientId = prefix + "_" + uuid;

        // 确保ID唯一性
        while (clientMapper.existsByClientId(clientId)) {
            uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            clientId = prefix + "_" + uuid;
        }

        return clientId;
    }

    /**
     * 生成客户端密钥
     */
    private String generateClientSecret() {
        return UUID.randomUUID().toString().replace("-", "") + 
               UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    /**
     * 验证重定向URI格式
     */
    private boolean isValidRedirectUri(String redirectUri) {
        try {
            // 简单的URL格式验证
            return redirectUri.startsWith("http://") || redirectUri.startsWith("https://");
        } catch (Exception e) {
            return false;
        }
    }

    // ================================
    // 内部类：请求和响应对象
    // ================================

    public static class ClientRegistrationRequest {
        @jakarta.validation.constraints.NotBlank(message = "Client name is required")
        @jakarta.validation.constraints.Size(min = 2, max = 200, message = "Client name must be between 2 and 200 characters")
        private String clientName;
        
        @jakarta.validation.constraints.NotBlank(message = "Redirect URI is required")
        @jakarta.validation.constraints.Pattern(regexp = "^https?://.*", message = "Redirect URI must start with http:// or https://")
        private String redirectUri;
        
        private java.util.List<String> scopes = new java.util.ArrayList<>();
        
        @jakarta.validation.constraints.Size(max = 500, message = "Description must not exceed 500 characters")
        private String description;

        // Getters and Setters
        public String getClientName() { return clientName; }
        public void setClientName(String clientName) { this.clientName = clientName; }

        public String getRedirectUri() { return redirectUri; }
        public void setRedirectUri(String redirectUri) { this.redirectUri = redirectUri; }

        public java.util.List<String> getScopes() { return scopes; }
        public void setScopes(java.util.List<String> scopes) { this.scopes = scopes; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class ClientRegistrationResult {
        private boolean success;
        private String clientId;
        private String clientSecret;
        private String clientName;
        private String redirectUri;
        private java.util.List<String> scopes;
        private String message;
        private String error;
        private String errorDescription;

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getClientId() { return clientId; }
        public void setClientId(String clientId) { this.clientId = clientId; }

        public String getClientSecret() { return clientSecret; }
        public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }

        public String getClientName() { return clientName; }
        public void setClientName(String clientName) { this.clientName = clientName; }

        public String getRedirectUri() { return redirectUri; }
        public void setRedirectUri(String redirectUri) { this.redirectUri = redirectUri; }

        public java.util.List<String> getScopes() { return scopes; }
        public void setScopes(java.util.List<String> scopes) { this.scopes = scopes; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }

        public String getErrorDescription() { return errorDescription; }
        public void setErrorDescription(String errorDescription) { this.errorDescription = errorDescription; }
    }

    private static class ValidationResult {
        private boolean valid;
        private String errorMessage;
        private String warning;

        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }

        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        
        public String getWarning() { return warning; }
        public void setWarning(String warning) { this.warning = warning; }
    }
}