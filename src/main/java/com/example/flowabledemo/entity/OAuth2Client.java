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
}