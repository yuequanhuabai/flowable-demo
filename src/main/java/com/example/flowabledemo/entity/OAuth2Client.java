package com.example.flowabledemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

/**
 * OAuth2客戶端實體類
 * 簡化版實現，包含核心字段
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("oauth2_client")
public class OAuth2Client {

    /**
     * 主鍵ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 客戶端ID (唯一標識)
     */
    @TableField("client_id")
    private String clientId;

    /**
     * 客戶端密鑰
     */
    @TableField("client_secret")
    private String clientSecret;

    /**
     * 客戶端名稱
     */
    @TableField("client_name")
    private String clientName;

    /**
     * 重定向URI
     */
    @TableField("redirect_uri")
    private String redirectUri;

    /**
     * 權限範圍 (逗號分隔)
     */
    @TableField("scopes")
    private String scopes;

    /**
     * 授權類型 (逗號分隔)
     */
    @TableField("grant_types")
    private String grantTypes;

    /**
     * 客戶端描述
     */
    @TableField("client_description")
    private String clientDescription;

    /**
     * 是否激活
     */
    @TableField("is_active")
    private Boolean isActive;

    /**
     * 創建時間
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新時間
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // 便捷方法：將scopes字符串轉換為List
    public List<String> getScopesList() {
        if (scopes == null || scopes.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.asList(scopes.split(","));
    }

    // 便捷方法：將grantTypes字符串轉換為List
    public List<String> getGrantTypesList() {
        if (grantTypes == null || grantTypes.trim().isEmpty()) {
            return List.of("authorization_code");
        }
        return Arrays.asList(grantTypes.split(","));
    }

    // 便捷方法：設置scopes
    public void setScopesList(List<String> scopesList) {
        if (scopesList != null && !scopesList.isEmpty()) {
            this.scopes = String.join(",", scopesList);
        }
    }

    // 便捷方法：設置grantTypes
    public void setGrantTypesList(List<String> grantTypesList) {
        if (grantTypesList != null && !grantTypesList.isEmpty()) {
            this.grantTypes = String.join(",", grantTypesList);
        }
    }
}