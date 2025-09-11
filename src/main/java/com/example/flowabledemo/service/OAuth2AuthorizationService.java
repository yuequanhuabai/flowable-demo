package com.example.flowabledemo.service;

import com.example.flowabledemo.entity.OAuth2Authorization;
import com.example.flowabledemo.entity.OAuth2Client;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * OAuth2授权码管理服务
 * 专门负责授权码的生成、存储、验证和管理
 */
@Service
public class OAuth2AuthorizationService {

    // ⏱️ 授权码配置
    private final int authorizationCodeExpiration = 600; // 10分钟

    // ================================
    // 核心业务方法：授权码生成
    // ================================

    /**
     * 📝 生成授权码
     */
    public String generateAuthorizationCode(OAuth2Client client, String scope) {
        return "auth_code_" + client.getClientId() + "_" +
               UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    /**
     * 🎫 生成访问令牌
     */
    public String generateAccessToken(OAuth2Client client, String scope) {
        // 简化版本：实际应该使用JWT
        return "access_token_" + client.getClientId() + "_" + 
               UUID.randomUUID().toString().replace("-", "").substring(0, 16) +
               "_" + System.currentTimeMillis();
    }

    /**
     * 🔄 生成刷新令牌  
     */
    public String generateRefreshToken(OAuth2Client client) {
        return "refresh_token_" + client.getClientId() + "_" +
               UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    // ================================
    // 核心业务方法：授权码管理
    // ================================

    /**
     * 🚀 创建授权码实体
     */
    public OAuth2Authorization createAuthorization(OAuth2Client client, String userId, String scope) {
        OAuth2Authorization authorization = new OAuth2Authorization();
        authorization.setAuthorizationCode(generateAuthorizationCode(client, scope));
        authorization.setClientId(client.getClientId());
        authorization.setUserId(userId);
        authorization.setExpiresAt(LocalDateTime.now().plusSeconds(authorizationCodeExpiration));
        authorization.setIsUsed(false);
        authorization.setCreatedAt(LocalDateTime.now());
        
        return authorization;
    }

    /**
     * 🔍 验证授权码
     */
    public boolean validateAuthorizationCode(OAuth2Authorization authorization, String providedCode) {
        if (authorization == null || providedCode == null) {
            return false;
        }

        // 检查授权码是否匹配
        if (!providedCode.equals(authorization.getAuthorizationCode())) {
            return false;
        }

        // 检查是否可以使用 (未过期且未使用)
        return authorization.canBeUsed();
    }

    /**
     * 🚫 使用授权码 (标记为已使用)
     */
    public boolean consumeAuthorizationCode(OAuth2Authorization authorization, String providedCode) {
        if (!validateAuthorizationCode(authorization, providedCode)) {
            return false;
        }

        // 标记为已使用
        authorization.markAsUsed();
        return true;
    }

    // ================================
    // 核心业务方法：令牌交换
    // ================================

    /**
     * 🔄 授权码换取访问令牌
     */
    public TokenResponse exchangeCodeForToken(OAuth2Authorization authorization, OAuth2Client client, String providedCode) {
        TokenResponse response = new TokenResponse();

        // 验证并消费授权码
        if (!consumeAuthorizationCode(authorization, providedCode)) {
            response.setSuccess(false);
            response.setError("invalid_grant");
            response.setErrorDescription("Invalid or expired authorization code");
            return response;
        }

        // 验证客户端匹配
        if (!client.getClientId().equals(authorization.getClientId())) {
            response.setSuccess(false);
            response.setError("invalid_client");
            response.setErrorDescription("Client ID mismatch");
            return response;
        }

        // 生成令牌
        String accessToken = generateAccessToken(client, "read,write");
        String refreshToken = generateRefreshToken(client);

        // 构建成功响应
        response.setSuccess(true);
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setTokenType("Bearer");
        response.setExpiresIn(3600); // 1小时
        response.setScope("read,write");

        return response;
    }

    // ================================
    // 配置信息getter方法
    // ================================

    public int getAuthorizationCodeExpiration() {
        return authorizationCodeExpiration;
    }

    // ================================
    // 内部类：令牌响应
    // ================================

    public static class TokenResponse {
        private boolean success;
        private String accessToken;
        private String refreshToken;
        private String tokenType;
        private int expiresIn;
        private String scope;
        private String error;
        private String errorDescription;

        // getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

        public String getTokenType() { return tokenType; }
        public void setTokenType(String tokenType) { this.tokenType = tokenType; }

        public int getExpiresIn() { return expiresIn; }
        public void setExpiresIn(int expiresIn) { this.expiresIn = expiresIn; }

        public String getScope() { return scope; }
        public void setScope(String scope) { this.scope = scope; }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }

        public String getErrorDescription() { return errorDescription; }
        public void setErrorDescription(String errorDescription) { this.errorDescription = errorDescription; }
    }
}