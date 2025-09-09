package com.example.flowabledemo.controller;

import com.example.flowabledemo.dto.ClientRegistrationRequest;
import com.example.flowabledemo.dto.ClientRegistrationResponse;
import com.example.flowabledemo.entity.OAuth2Client;
import com.example.flowabledemo.service.OAuth2ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OAuth2客戶端註冊控制器
 * 簡化版實現，提供基本的CRUD操作
 */
@Slf4j
@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Validated
public class OAuth2ClientController {

    private final OAuth2ClientService oauth2ClientService;

    /**
     * 註冊新的OAuth2客戶端
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerClient(@Valid @RequestBody ClientRegistrationRequest request) {
        log.info("接收客戶端註冊請求: {}", request.getClientName());
        
        try {
            ClientRegistrationResponse response = oauth2ClientService.registerClient(request);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "客戶端註冊成功");
            result.put("data", response);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
            
        } catch (Exception e) {
            log.error("客戶端註冊失敗: {}", e.getMessage(), e);
            
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "客戶端註冊失敗: " + e.getMessage());
            errorResult.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
        }
    }

    /**
     * 查詢所有活躍的客戶端
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllClients() {
        try {
            List<OAuth2Client> clients = oauth2ClientService.findAllActiveClients();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "查詢成功");
            result.put("data", clients);
            result.put("total", clients.size());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("查詢客戶端列表失敗: {}", e.getMessage(), e);
            
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "查詢失敗: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    /**
     * 根據客戶端ID查詢客戶端
     */
    @GetMapping("/{clientId}")
    public ResponseEntity<Map<String, Object>> getClientById(@PathVariable String clientId) {
        try {
            OAuth2Client client = oauth2ClientService.findByClientId(clientId);
            
            if (client == null) {
                Map<String, Object> notFoundResult = new HashMap<>();
                notFoundResult.put("success", false);
                notFoundResult.put("message", "客戶端不存在: " + clientId);
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundResult);
            }
            
            // 隱藏敏感信息
            client.setClientSecret("***");
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "查詢成功");
            result.put("data", client);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("查詢客戶端失敗: {}", e.getMessage(), e);
            
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "查詢失敗: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    /**
     * 更新客戶端信息
     */
    @PutMapping("/{clientId}")
    public ResponseEntity<Map<String, Object>> updateClient(
            @PathVariable String clientId,
            @Valid @RequestBody ClientRegistrationRequest request) {
        
        try {
            ClientRegistrationResponse response = oauth2ClientService.updateClient(clientId, request);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "客戶端更新成功");
            result.put("data", response);
            
            return ResponseEntity.ok(result);
            
        } catch (RuntimeException e) {
            log.error("客戶端更新失敗: {}", e.getMessage(), e);
            
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
        } catch (Exception e) {
            log.error("客戶端更新失敗: {}", e.getMessage(), e);
            
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "更新失敗: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    /**
     * 刪除客戶端（軟刪除）
     */
    @DeleteMapping("/{clientId}")
    public ResponseEntity<Map<String, Object>> deleteClient(@PathVariable String clientId) {
        try {
            boolean deleted = oauth2ClientService.deleteByClientId(clientId);
            
            if (!deleted) {
                Map<String, Object> notFoundResult = new HashMap<>();
                notFoundResult.put("success", false);
                notFoundResult.put("message", "客戶端不存在或已被刪除: " + clientId);
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundResult);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "客戶端刪除成功");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("刪除客戶端失敗: {}", e.getMessage(), e);
            
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "刪除失敗: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    /**
     * 驗證客戶端憑證
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateCredentials(
            @RequestParam String clientId,
            @RequestParam String clientSecret) {
        
        try {
            boolean valid = oauth2ClientService.validateClientCredentials(clientId, clientSecret);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("valid", valid);
            result.put("message", valid ? "憑證驗證成功" : "憑證驗證失敗");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("憑證驗證失敗: {}", e.getMessage(), e);
            
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("valid", false);
            errorResult.put("message", "驗證失敗: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
}