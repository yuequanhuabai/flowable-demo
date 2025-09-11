package com.example.flowabledemo.service;

import com.example.flowabledemo.entity.OAuth2Client;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * OAuth2验证服务
 * 专注于客户端验证、权限验证、令牌验证等核心验证逻辑
 */
@Service
public class OAuth2ValidationService {

    // 🔐 安全配置
    private final String issuer = "http://localhost:8080";
    private final String tokenSigningKey = "jwt-signing-secret-key";
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(14);

    // ⏱️ 令牌配置 (单位：秒)
    private final int accessTokenExpiration = 3600;      // 1小时
    private final int refreshTokenExpiration = 86400;     // 24小时  
    private final int authorizationCodeExpiration = 600;  // 10分钟

    // 🌐 端点配置
    private final String authorizationEndpoint = "/oauth2/authorize";
    private final String tokenEndpoint = "/oauth2/token";
    private final String userInfoEndpoint = "/oauth2/userinfo";

    // 🎭 支持的功能
    private final List<String> supportedGrantTypes = Arrays.asList(
        "authorization_code", "refresh_token", "client_credentials"
    );
    private final List<String> supportedScopes = Arrays.asList(
        "read", "write", "user:profile", "user:email"
    );
    private final List<String> supportedResponseTypes = Arrays.asList(
        "code", "token"
    );

    // ================================
    // 核心业务方法：客户端验证
    // ================================

    /**
     * 🔍 验证客户端身份凭据
     */
    public boolean authenticateClient(OAuth2Client client, String providedSecret) {
        if (client == null || providedSecret == null) {
            return false;
        }
        
        // 检查客户端状态
        if (!client.getIsActive()) {
            return false;
        }
        
        // 验证密钥 (BCrypt比较)
        return passwordEncoder.matches(providedSecret, client.getClientSecret());
    }

    /**
     * 🎯 验证客户端权限范围
     */
    public boolean validateClientScope(OAuth2Client client, String requestedScope) {
        if (client == null || requestedScope == null) {
            return false;
        }
        
        // 检查客户端是否有该权限
        List<String> clientScopes = client.getScopesList();
        
        // 检查请求的权限是否被服务器支持
        if (!supportedScopes.contains(requestedScope)) {
            return false;
        }
        
        // 检查客户端是否被授权该权限
        return clientScopes.contains(requestedScope);
    }

    /**
     * 📋 验证授权类型
     */
    public boolean validateGrantType(String grantType) {
        return grantType != null && supportedGrantTypes.contains(grantType);
    }

    /**
     * 🌐 验证回调地址
     */
    public boolean validateRedirectUri(OAuth2Client client, String providedUri) {
        if (client == null || providedUri == null) {
            return false;
        }
        
        // 精确匹配客户端注册的回调地址
        return client.getRedirectUri().equals(providedUri);
    }

    // ================================
    // 核心业务方法：令牌管理
    // ================================

    // ================================
    // 核心业务方法：令牌验证 (生成方法已移至专门的服务)
    // ================================

    /**
     * ⏰ 检查令牌是否过期 (简化版本)
     */
    public boolean isTokenExpired(String token, LocalDateTime createdAt) {
        if (createdAt == null) return true;
        
        LocalDateTime expirationTime = createdAt.plusSeconds(accessTokenExpiration);
        return LocalDateTime.now().isAfter(expirationTime);
    }

    // ================================
    // 核心业务方法：授权流程
    // ================================

    /**
     * 🚀 完整的客户端验证流程
     */
    public AuthenticationResult authenticateForAuthorization(
            OAuth2Client client, 
            String clientSecret,
            String grantType,
            String scope,
            String redirectUri) {
        
        AuthenticationResult result = new AuthenticationResult();
        
        // 1. 验证客户端身份
        if (!authenticateClient(client, clientSecret)) {
            result.setSuccess(false);
            result.setError("invalid_client");
            result.setErrorDescription("Client authentication failed");
            return result;
        }
        
        // 2. 验证授权类型
        if (!validateGrantType(grantType)) {
            result.setSuccess(false);
            result.setError("unsupported_grant_type");
            result.setErrorDescription("Grant type not supported: " + grantType);
            return result;
        }
        
        // 3. 验证权限范围
        if (scope != null && !validateClientScope(client, scope)) {
            result.setSuccess(false);
            result.setError("invalid_scope");
            result.setErrorDescription("Invalid or unauthorized scope: " + scope);
            return result;
        }
        
        // 4. 验证回调地址 (仅authorization_code需要)
        if ("authorization_code".equals(grantType) && !validateRedirectUri(client, redirectUri)) {
            result.setSuccess(false);
            result.setError("invalid_redirect_uri");
            result.setErrorDescription("Redirect URI mismatch");
            return result;
        }
        
        // 5. 验证成功
        result.setSuccess(true);
        result.setClientId(client.getClientId());
        result.setScope(scope);
        return result;
    }

    // ================================
    // 配置信息getter方法
    // ================================

    public String getIssuer() { return issuer; }
    public int getAccessTokenExpiration() { return accessTokenExpiration; }
    public int getRefreshTokenExpiration() { return refreshTokenExpiration; }
    public String getAuthorizationEndpoint() { return authorizationEndpoint; }
    public String getTokenEndpoint() { return tokenEndpoint; }
    public String getUserInfoEndpoint() { return userInfoEndpoint; }
    public List<String> getSupportedGrantTypes() { return supportedGrantTypes; }
    public List<String> getSupportedScopes() { return supportedScopes; }
    public List<String> getSupportedResponseTypes() { return supportedResponseTypes; }

    // ================================
    // 内部类：认证结果
    // ================================
    
    public static class AuthenticationResult {
        private boolean success;
        private String clientId;
        private String scope;
        private String error;
        private String errorDescription;

        // getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getClientId() { return clientId; }
        public void setClientId(String clientId) { this.clientId = clientId; }
        
        public String getScope() { return scope; }
        public void setScope(String scope) { this.scope = scope; }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        
        public String getErrorDescription() { return errorDescription; }
        public void setErrorDescription(String errorDescription) { this.errorDescription = errorDescription; }
    }
}