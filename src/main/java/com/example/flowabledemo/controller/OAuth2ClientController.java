package com.example.flowabledemo.controller;

import com.example.flowabledemo.service.OAuth2ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

/**
 * OAuth2客户端注册控制器
 * 提供客户端注册和管理的REST API端点
 */
@RestController
@RequestMapping("/oauth2/client")
@RequiredArgsConstructor
public class OAuth2ClientController {

    private final OAuth2ClientService clientService;

    /**
     * 注册新的OAuth2客户端
     * POST /oauth2/client/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerClient(@Valid @RequestBody OAuth2ClientService.ClientRegistrationRequest request) {
        try {
            OAuth2ClientService.ClientRegistrationResult result = clientService.registerClient(request);

            if (result.isSuccess()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(result);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("server_error", "Registration failed: " + e.getMessage()));
        }
    }

    /**
     * 获取客户端信息（不包含密钥）
     * GET /oauth2/client/{clientId}
     */
    @GetMapping("/{clientId}")
    public ResponseEntity<?> getClient(@PathVariable String clientId) {
        try {
            var client = clientService.findByClientId(clientId);
            if (client != null) {
                return ResponseEntity.ok(client.toSafeResponse());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("client_not_found", "Client not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("server_error", "Failed to retrieve client: " + e.getMessage()));
        }
    }

    /**
     * 检查客户端状态
     * GET /oauth2/client/{clientId}/status
     */
    @GetMapping("/{clientId}/status")
    public ResponseEntity<?> getClientStatus(@PathVariable String clientId) {
        try {
            boolean isActive = clientService.isClientActive(clientId);
            return ResponseEntity.ok(new ClientStatusResponse(clientId, isActive));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("server_error", "Failed to check status: " + e.getMessage()));
        }
    }

    /**
     * 处理验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errorMessage.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ")
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("validation_error", errorMessage.toString().trim()));
    }

    // ================================
    // 响应对象类
    // ================================

    static class ErrorResponse {
        private String error;
        private String errorDescription;

        public ErrorResponse(String error, String errorDescription) {
            this.error = error;
            this.errorDescription = errorDescription;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getErrorDescription() {
            return errorDescription;
        }

        public void setErrorDescription(String errorDescription) {
            this.errorDescription = errorDescription;
        }
    }

    static class ClientStatusResponse {
        private String clientId;
        private boolean active;

        public ClientStatusResponse(String clientId, boolean active) {
            this.clientId = clientId;
            this.active = active;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }
}