package com.example.flowabledemo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 用户实体类
 * OAuth2演示项目的简化用户模型
 */
@TableName("users")
@Data
public class User {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名（唯一）
     */
    @TableField("username")
    private String username;

    /**
     * 密码（BCrypt加密存储）
     */
    @TableField("password")
    private String password;

    /**
     * 账户是否启用
     */
    @TableField("enabled")
    private Boolean enabled;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    // ================================
    // 业务方法
    // ================================

    /**
     * 检查用户是否可用（启用且存在）
     */
    public boolean isAvailable() {
        return this.enabled != null && this.enabled && 
               this.username != null && !this.username.trim().isEmpty();
    }

    /**
     * 获取安全的显示信息（不包含密码）
     */
    public String getDisplayInfo() {
        return String.format("User: %s (ID: %d, Status: %s)", 
                this.username, 
                this.id, 
                this.enabled ? "Active" : "Inactive");
    }

    /**
     * 创建安全的用户副本（不包含密码）
     */
    public User toSafeUser() {
        User safeUser = new User();
        safeUser.setId(this.id);
        safeUser.setUsername(this.username);
        safeUser.setEnabled(this.enabled);
        safeUser.setCreatedAt(this.createdAt);
        safeUser.setUpdatedAt(this.updatedAt);
        // 注意：不复制password字段
        return safeUser;
    }

    // ================================
    // 基础方法重写
    // ================================

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", enabled=" + enabled +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", password='[PROTECTED]'" +  // 🔒 不在日志中暴露密码
                '}';
    }
}