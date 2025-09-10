package com.example.flowabledemo.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * OAuth2客户端实体类
 * 基于四个核心安全机制设计：身份标识、身份验证、权限控制、可撤销机制
 */
@Data
public class OAuth2Client {

    // 🏷️ 身份标识机制
    private Long id;
    private String clientId;

    // 🔐 身份验证机制
    private String clientSecret;

    // 📝 基本信息
    private String clientName;

    // 🎯 权限控制机制
    private String redirectUri;
    private String scopes;

    // ⏰ 可撤销机制
    private Boolean isActive;

    // 📅 审计信息
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ================================
    // 基础方法：getter/setter (框架需要)
    // ================================

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getClientId() {
//        return clientId;
//    }
//
//    public void setClientId(String clientId) {
//        this.clientId = clientId;
//    }
//
//    public String getClientSecret() {
//        return clientSecret;
//    }
//
//    public void setClientSecret(String clientSecret) {
//        this.clientSecret = clientSecret;
//    }
//
//    public String getClientName() {
//        return clientName;
//    }
//
//    public void setClientName(String clientName) {
//        this.clientName = clientName;
//    }
//
//    public String getRedirectUri() {
//        return redirectUri;
//    }
//
//    public void setRedirectUri(String redirectUri) {
//        this.redirectUri = redirectUri;
//    }
//
//    public String getScopes() {
//        return scopes;
//    }
//
//    public void setScopes(String scopes) {
//        this.scopes = scopes;
//    }
//
//    public Boolean getIsActive() {
//        return isActive;
//    }
//
//    public void setIsActive(Boolean isActive) {
//        this.isActive = isActive;
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public LocalDateTime getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public void setUpdatedAt(LocalDateTime updatedAt) {
//        this.updatedAt = updatedAt;
//    }

    // ================================
    // 基础方法：equals/hashCode (集合操作需要)
    // ================================

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        OAuth2Client that = (OAuth2Client) obj;
        return Objects.equals(clientId, that.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId);
    }

    // ================================
    // 基础方法：toString (调试和日志需要)
    // ================================

    @Override
    public String toString() {
        return "OAuth2Client{" +
                "id=" + id +
                ", clientId='" + clientId + '\'' +
                ", clientName='" + clientName + '\'' +
                ", redirectUri='" + redirectUri + '\'' +
                ", scopes='" + scopes + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", clientSecret='[PROTECTED]'" +  // 🔒 不在日志中暴露密钥
                '}';
    }

    // ================================
    // 业务方法：客户端数据自身的行为
    // ================================

    // 📋 数据转换：解析权限范围字符串
    public List<String> getScopesList() {
        if (this.scopes == null || this.scopes.trim().isEmpty()) {
            return Arrays.asList("read"); // 默认只读权限
        }
        return Arrays.asList(this.scopes.split(","))
                .stream()
                .map(String::trim)
                .filter(scope -> !scope.isEmpty())
                .toList();
    }

    // 🔍 数据校验：检查数据完整性
    public boolean isDataValid() {
        return this.clientId != null && !this.clientId.trim().isEmpty() &&
               this.clientSecret != null && !this.clientSecret.trim().isEmpty() &&
               this.clientName != null && !this.clientName.trim().isEmpty() &&
               this.redirectUri != null && !this.redirectUri.trim().isEmpty();
    }

    // 📊 数据格式化：生成安全的显示信息
    public String getDisplayInfo() {
        return String.format("Client: %s (%s) - Status: %s - Scopes: %s", 
            this.clientName, 
            this.clientId,
            this.isActive ? "Active" : "Inactive",
            String.join(", ", getScopesList()));
    }

    // 🛡️ 数据转换：生成API安全响应（隐藏敏感信息）
    public OAuth2Client toSafeResponse() {
        OAuth2Client safeClient = new OAuth2Client();
        safeClient.setId(this.id);
        safeClient.setClientId(this.clientId);
        safeClient.setClientName(this.clientName);
        safeClient.setRedirectUri(this.redirectUri);
        safeClient.setScopes(this.scopes);
        safeClient.setIsActive(this.isActive);
        safeClient.setCreatedAt(this.createdAt);
        safeClient.setUpdatedAt(this.updatedAt);
        // 💡 注意：不包含clientSecret，保证安全
        return safeClient;
    }

    // 📅 数据访问：检查是否为新创建的客户端
    public boolean isNewlyCreated() {
        return this.createdAt != null && 
               this.updatedAt != null && 
               this.createdAt.equals(this.updatedAt);
    }

    // 🏷️ 数据格式化：生成客户端标识摘要  
    public String getClientSummary() {
        return String.format("%s-%s", 
            this.clientName != null ? this.clientName.replaceAll("\\s+", "").toLowerCase() : "unknown",
            this.clientId != null ? this.clientId.substring(0, Math.min(8, this.clientId.length())) : "noId"
        );
    }
}