package com.example.flowabledemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.flowabledemo.entity.OAuth2Client;
import com.example.flowabledemo.mapper.OAuth2ClientMapper;
import com.example.flowabledemo.service.OAuth2ClientService;
import com.example.flowabledemo.dto.ClientRegistrationRequest;
import com.example.flowabledemo.dto.ClientRegistrationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * OAuth2客戶端服務實現類
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2ClientServiceImpl extends ServiceImpl<OAuth2ClientMapper, OAuth2Client> implements OAuth2ClientService {

    private final OAuth2ClientMapper oauth2ClientMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ClientRegistrationResponse registerClient(ClientRegistrationRequest request) {
        log.info("開始註冊OAuth2客戶端: {}", request.getClientName());

        // 生成唯一的客戶端ID
        String clientId = generateClientId(request.getClientName());
        
        // 檢查客戶端ID是否已存在
        while (existsByClientId(clientId)) {
            clientId = generateClientId(request.getClientName());
        }

        // 生成客戶端密鑰
        String clientSecret = generateClientSecret();
        String encodedClientSecret = passwordEncoder.encode(clientSecret);

        // 創建客戶端實體
        OAuth2Client client = new OAuth2Client();
        client.setClientId(clientId);
        client.setClientSecret(encodedClientSecret);
        client.setClientName(request.getClientName());
        client.setRedirectUri(request.getRedirectUri());
        client.setScopesList(request.getScopes());
        client.setGrantTypesList(request.getGrantTypes());
        client.setClientDescription(request.getClientDescription());
        client.setIsActive(true);
        client.setCreatedAt(LocalDateTime.now());
        client.setUpdatedAt(LocalDateTime.now());

        // 保存到資料庫
        boolean saved = save(client);
        if (!saved) {
            throw new RuntimeException("客戶端註冊失敗");
        }

        log.info("OAuth2客戶端註冊成功: clientId={}", clientId);

        // 返回註冊響應
        return ClientRegistrationResponse.builder()
                .clientId(clientId)
                .clientSecret(clientSecret) // 明文返回，後續不可再獲取
                .clientName(client.getClientName())
                .redirectUri(client.getRedirectUri())
                .scopes(client.getScopesList())
                .grantTypes(client.getGrantTypesList())
                .clientDescription(client.getClientDescription())
                .createdAt(client.getCreatedAt())
                .isActive(client.getIsActive())
                .build();
    }

    @Override
    public OAuth2Client findByClientId(String clientId) {
        return oauth2ClientMapper.findByClientId(clientId);
    }

    @Override
    public List<OAuth2Client> findAllActiveClients() {
        return oauth2ClientMapper.findAllActiveClients();
    }

    @Override
    public boolean existsByClientId(String clientId) {
        Integer count = oauth2ClientMapper.countByClientId(clientId);
        return count != null && count > 0;
    }

    @Override
    @Transactional
    public boolean deleteByClientId(String clientId) {
        OAuth2Client client = findByClientId(clientId);
        if (client == null) {
            return false;
        }
        
        client.setIsActive(false);
        client.setUpdatedAt(LocalDateTime.now());
        
        return updateById(client);
    }

    @Override
    @Transactional
    public ClientRegistrationResponse updateClient(String clientId, ClientRegistrationRequest request) {
        OAuth2Client existingClient = findByClientId(clientId);
        if (existingClient == null) {
            throw new RuntimeException("客戶端不存在: " + clientId);
        }

        // 更新客戶端信息
        existingClient.setClientName(request.getClientName());
        existingClient.setRedirectUri(request.getRedirectUri());
        existingClient.setScopesList(request.getScopes());
        existingClient.setGrantTypesList(request.getGrantTypes());
        existingClient.setClientDescription(request.getClientDescription());
        existingClient.setUpdatedAt(LocalDateTime.now());

        boolean updated = updateById(existingClient);
        if (!updated) {
            throw new RuntimeException("客戶端更新失敗");
        }

        return ClientRegistrationResponse.builder()
                .clientId(existingClient.getClientId())
                .clientSecret("***") // 不返回密鑰
                .clientName(existingClient.getClientName())
                .redirectUri(existingClient.getRedirectUri())
                .scopes(existingClient.getScopesList())
                .grantTypes(existingClient.getGrantTypesList())
                .clientDescription(existingClient.getClientDescription())
                .createdAt(existingClient.getCreatedAt())
                .isActive(existingClient.getIsActive())
                .build();
    }

    @Override
    public boolean validateClientCredentials(String clientId, String clientSecret) {
        OAuth2Client client = findByClientId(clientId);
        if (client == null || !client.getIsActive()) {
            return false;
        }
        
        return passwordEncoder.matches(clientSecret, client.getClientSecret());
    }

    /**
     * 生成客戶端ID
     */
    private String generateClientId(String clientName) {
        // 簡化版：使用客戶端名稱前綴 + UUID後8位
        String prefix = clientName.toLowerCase()
                .replaceAll("[^a-z0-9]", "")
                .substring(0, Math.min(clientName.length(), 8));
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return prefix + "_" + suffix;
    }

    /**
     * 生成客戶端密鑰
     */
    private String generateClientSecret() {
        return UUID.randomUUID().toString().replace("-", "") + 
               UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}