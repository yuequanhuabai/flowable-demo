package com.example.flowabledemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.flowabledemo.entity.OAuth2Client;
import com.example.flowabledemo.dto.ClientRegistrationRequest;
import com.example.flowabledemo.dto.ClientRegistrationResponse;

import java.util.List;

/**
 * OAuth2客戶端服務接口
 */
public interface OAuth2ClientService extends IService<OAuth2Client> {

    /**
     * 註冊新的OAuth2客戶端
     */
    ClientRegistrationResponse registerClient(ClientRegistrationRequest request);

    /**
     * 根據客戶端ID查詢客戶端
     */
    OAuth2Client findByClientId(String clientId);

    /**
     * 查詢所有活躍的客戶端
     */
    List<OAuth2Client> findAllActiveClients();

    /**
     * 檢查客戶端ID是否存在
     */
    boolean existsByClientId(String clientId);

    /**
     * 軟刪除客戶端
     */
    boolean deleteByClientId(String clientId);

    /**
     * 更新客戶端信息
     */
    ClientRegistrationResponse updateClient(String clientId, ClientRegistrationRequest request);

    /**
     * 驗證客戶端憑證
     */
    boolean validateClientCredentials(String clientId, String clientSecret);
}