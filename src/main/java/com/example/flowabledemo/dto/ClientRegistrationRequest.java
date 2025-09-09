package com.example.flowabledemo.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 客戶端註冊請求DTO
 */
@Data
public class ClientRegistrationRequest {

    /**
     * 客戶端名稱
     */
    @NotBlank(message = "客戶端名稱不能為空")
    @Size(min = 2, max = 200, message = "客戶端名稱長度必須在2-200個字符之間")
    private String clientName;

    /**
     * 重定向URI
     */
    @NotBlank(message = "重定向URI不能為空")
    @Pattern(regexp = "^https?://.+", message = "重定向URI必須是有效的HTTP/HTTPS地址")
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
    @Size(max = 500, message = "客戶端描述不能超過500個字符")
    private String clientDescription;

    // 設置默認值
    public List<String> getScopes() {
        return scopes != null ? scopes : List.of("read", "write");
    }

    public List<String> getGrantTypes() {
        return grantTypes != null ? grantTypes : List.of("authorization_code", "refresh_token");
    }
}