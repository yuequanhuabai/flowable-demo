package com.example.flowabledemo.dto;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 客戶端註冊響應DTO
 */
@Data
@Builder
public class ClientRegistrationResponse {

    /**
     * 客戶端ID
     */
    private String clientId;

    /**
     * 客戶端密鑰
     */
    private String clientSecret;

    /**
     * 客戶端名稱
     */
    private String clientName;

    /**
     * 重定向URI
     */
    private String redirectUri;

    /**
     * 權限範圍列表
     */
    private List<String> scopes;

    /**
     * 授權類型列表
     */
    private List<String> grantTypes;

    /**
     * 客戶端描述
     */
    private String clientDescription;

    /**
     * 創建時間
     */
    private LocalDateTime createdAt;

    /**
     * 是否激活
     */
    private Boolean isActive;
}