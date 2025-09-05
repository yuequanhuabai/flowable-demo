package com.example.flowabledemo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ApiController {

    @GetMapping("/public/info")
    public ResponseEntity<Map<String, Object>> getPublicInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("message", "這是公開 API，無需認證");
        info.put("timestamp", Instant.now().toString());
        info.put("status", "success");
        return ResponseEntity.ok(info);
    }

    @GetMapping("/user/profile")
    @PreAuthorize("hasAuthority('SCOPE_read')")
    public ResponseEntity<Map<String, Object>> getUserProfile(Authentication authentication) {
        Map<String, Object> profile = new HashMap<>();
        
        if (authentication instanceof JwtAuthenticationToken jwtToken) {
            Jwt jwt = jwtToken.getToken();
            profile.put("username", jwt.getSubject());
            profile.put("scopes", jwt.getClaimAsStringList("scope"));
            profile.put("clientId", jwt.getClaimAsString("client_id"));
            profile.put("issuedAt", jwt.getIssuedAt());
            profile.put("expiresAt", jwt.getExpiresAt());
        }
        
        profile.put("message", "用戶資料獲取成功");
        profile.put("authorities", authentication.getAuthorities());
        
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/admin/action")
    @PreAuthorize("hasAuthority('SCOPE_write')")
    public ResponseEntity<Map<String, Object>> performAdminAction(
            @RequestBody(required = false) Map<String, Object> request,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "管理操作執行成功");
        response.put("performedBy", authentication.getName());
        response.put("timestamp", Instant.now().toString());
        response.put("requestData", request);
        response.put("authorities", authentication.getAuthorities());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/token/info")
    public ResponseEntity<Map<String, Object>> getTokenInfo(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtToken) {
            Jwt jwt = jwtToken.getToken();
            
            Map<String, Object> tokenInfo = new HashMap<>();
            tokenInfo.put("subject", jwt.getSubject());
            tokenInfo.put("issuer", jwt.getIssuer());
            tokenInfo.put("audience", jwt.getAudience());
            tokenInfo.put("issuedAt", jwt.getIssuedAt());
            tokenInfo.put("expiresAt", jwt.getExpiresAt());
            tokenInfo.put("scopes", jwt.getClaimAsStringList("scope"));
            tokenInfo.put("authorities", authentication.getAuthorities());
            tokenInfo.put("claims", jwt.getClaims());
            
            return ResponseEntity.ok(tokenInfo);
        }
        
        return ResponseEntity.badRequest().body(Map.of("error", "不是 JWT 令牌"));
    }
}