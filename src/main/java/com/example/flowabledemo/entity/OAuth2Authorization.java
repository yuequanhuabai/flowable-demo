package com.example.flowabledemo.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * OAuth2授权码实体类
 * 最简化设计，仅实现基本功能
 */
@Data
public class OAuth2Authorization {

    // 🏷️ 主键标识
    private Long id;

    // 🎫 授权码机制 (核心)
    private String authorizationCode;

    // 🏷️ 关联标识 (绑定客户端和用户)
    private String clientId;
    private String userId;

    // ⏰ 时效控制 (防止重放攻击)
    private LocalDateTime expiresAt;
    private Boolean isUsed;

    // 📅 审计信息
    private LocalDateTime createdAt;

    // ================================
    // 基础方法：equals/hashCode (集合操作需要)
    // ================================

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        OAuth2Authorization that = (OAuth2Authorization) obj;
        return Objects.equals(authorizationCode, that.authorizationCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorizationCode);
    }

    // ================================
    // 基础方法：toString (调试和日志需要)
    // ================================

    @Override
    public String toString() {
        return "OAuth2Authorization{" +
                "id=" + id +
                ", clientId='" + clientId + '\'' +
                ", userId='" + userId + '\'' +
                ", expiresAt=" + expiresAt +
                ", isUsed=" + isUsed +
                ", createdAt=" + createdAt +
                ", authorizationCode='[PROTECTED]'" +  // 🔒 不在日志中暴露授权码
                '}';
    }

    // ================================
    // 业务方法：授权码自身的行为
    // ================================

    // 🔍 数据校验：检查数据完整性
    public boolean isDataValid() {
        return this.authorizationCode != null && !this.authorizationCode.trim().isEmpty() &&
               this.clientId != null && !this.clientId.trim().isEmpty() &&
               this.userId != null && !this.userId.trim().isEmpty() &&
               this.expiresAt != null;
    }

    // ⏰ 时效检查：是否已过期
    public boolean isExpired() {
        return this.expiresAt != null && this.expiresAt.isBefore(LocalDateTime.now());
    }

    // 📋 状态检查：是否可以使用
    public boolean canBeUsed() {
        return !isExpired() && 
               (this.isUsed == null || !this.isUsed) && 
               isDataValid();
    }

    // 🚫 标记使用：防止重复使用
    public void markAsUsed() {
        this.isUsed = true;
    }

    // 📊 数据格式化：生成安全的显示信息
    public String getDisplayInfo() {
        return String.format("Authorization: %s (Client: %s, User: %s) - Status: %s - Expires: %s",
            this.authorizationCode != null ? this.authorizationCode.substring(0, Math.min(8, this.authorizationCode.length())) + "***" : "N/A",
            this.clientId,
            this.userId,
            canBeUsed() ? "Valid" : (isExpired() ? "Expired" : (Boolean.TRUE.equals(this.isUsed) ? "Used" : "Invalid")),
            this.expiresAt);
    }

    // 🛡️ 数据转换：生成API安全响应（隐藏敏感信息）
    public OAuth2Authorization toSafeResponse() {
        OAuth2Authorization safeAuth = new OAuth2Authorization();
        safeAuth.setId(this.id);
        safeAuth.setClientId(this.clientId);
        safeAuth.setUserId(this.userId);
        safeAuth.setExpiresAt(this.expiresAt);
        safeAuth.setIsUsed(this.isUsed);
        safeAuth.setCreatedAt(this.createdAt);
        // 💡 注意：不包含authorizationCode，保证安全
        return safeAuth;
    }

    // 🏷️ 数据格式化：生成授权摘要
    public String getAuthorizationSummary() {
        return String.format("auth-%s-%s",
            this.clientId != null ? this.clientId.substring(0, Math.min(6, this.clientId.length())) : "unknown",
            this.userId != null ? this.userId.substring(0, Math.min(6, this.userId.length())) : "noUser"
        );
    }

    // ⏳ 计算剩余有效时间（分钟）
    public long getRemainingMinutes() {
        if (this.expiresAt == null || isExpired()) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), this.expiresAt).toMinutes();
    }
}