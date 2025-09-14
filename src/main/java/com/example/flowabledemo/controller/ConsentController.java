package com.example.flowabledemo.controller;

import com.example.flowabledemo.entity.OAuth2Client;
import com.example.flowabledemo.service.OAuth2ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.*;

/**
 * OAuth2 授权确认控制器
 * 处理用户授权确认页面，提供类似Google/WeChat的授权体验
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class ConsentController {

    private final RegisteredClientRepository registeredClientRepository;
    private final OAuth2AuthorizationService authorizationService;
    private final OAuth2AuthorizationConsentService authorizationConsentService;
    private final OAuth2ClientService oauth2ClientService;

    /**
     * 显示授权确认页面
     * 当第三方应用请求用户授权时显示此页面
     */
    @GetMapping("/oauth2/consent")
    public String consent(
            Principal principal,
            Model model,
            @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
            @RequestParam(OAuth2ParameterNames.SCOPE) String scope,
            @RequestParam(OAuth2ParameterNames.STATE) String state) {

        log.debug("显示授权确认页面 - clientId: {}, scope: {}, user: {}",
                 clientId, scope, principal.getName());

        // 1. 获取客户端信息
        RegisteredClient registeredClient = registeredClientRepository.findByClientId(clientId);
        if (registeredClient == null) {
            log.error("未找到客户端: {}", clientId);
            model.addAttribute("errorMessage", "无效的客户端应用");
            return "error";
        }

        // 2. 获取自定义客户端信息（用于显示友好名称）
        OAuth2Client oauth2Client = oauth2ClientService.findByClientId(clientId);
        String clientName = oauth2Client != null ? oauth2Client.getClientName() : registeredClient.getClientId();

        // 3. 解析请求的权限范围
        Set<String> scopesToApprove = StringUtils.commaDelimitedListToSet(scope);
        Set<String> previouslyApprovedScopes = new HashSet<>();

        // 4. 检查之前已授权的权限
        OAuth2AuthorizationConsent existingConsent =
            authorizationConsentService.findById(registeredClient.getId(), principal.getName());
        if (existingConsent != null) {
            previouslyApprovedScopes.addAll(existingConsent.getScopes());
        }

        // 5. 计算需要新授权的权限
        Set<String> scopesToApproveFiltered = new HashSet<>();
        for (String requestedScope : scopesToApprove) {
            if (!previouslyApprovedScopes.contains(requestedScope)) {
                scopesToApproveFiltered.add(requestedScope);
            }
        }

        log.debug("权限分析 - 请求权限: {}, 已授权权限: {}, 需要确认权限: {}",
                 scopesToApprove, previouslyApprovedScopes, scopesToApproveFiltered);

        // 6. 如果所有权限都已授权，直接跳转
        if (scopesToApproveFiltered.isEmpty()) {
            log.debug("所有权限已授权，直接跳转");
            return "redirect:/oauth2/authorize?" +
                   "client_id=" + clientId +
                   "&response_type=code" +
                   "&scope=" + scope +
                   "&state=" + state;
        }

        // 7. 准备页面数据
        model.addAttribute("clientId", clientId);
        model.addAttribute("clientName", clientName);
        model.addAttribute("state", state);
        model.addAttribute("username", principal.getName());

        // 8. 权限信息
        model.addAttribute("requestedScopes", scopesToApprove);
        model.addAttribute("previouslyApprovedScopes", previouslyApprovedScopes);
        model.addAttribute("scopesToApprove", scopesToApproveFiltered);
        model.addAttribute("scopeDescriptions", getScopeDescriptions());

        // 9. 应用信息
        if (oauth2Client != null) {
            model.addAttribute("clientDescription", "第三方应用 " + clientName + " 请求访问您的账户");
            model.addAttribute("redirectUri", oauth2Client.getRedirectUri());
        } else {
            model.addAttribute("clientDescription", "第三方应用请求访问您的账户");
        }

        return "oauth2-consent";
    }

    /**
     * 处理用户的授权决定
     */
    @PostMapping("/oauth2/consent")
    public String handleConsent(
            Principal principal,
            @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
            @RequestParam(OAuth2ParameterNames.STATE) String state,
            @RequestParam(value = "scope", required = false) Set<String> approvedScopes,
            @RequestParam("action") String action) {

        log.debug("处理授权决定 - clientId: {}, action: {}, approvedScopes: {}, user: {}",
                 clientId, action, approvedScopes, principal.getName());

        // 1. 用户拒绝授权
        if (!"approve".equals(action)) {
            log.info("用户拒绝授权 - clientId: {}, user: {}", clientId, principal.getName());
            return "redirect:/oauth2/authorize?error=access_denied&state=" + state;
        }

        // 2. 用户同意授权但没有选择任何权限
        if (approvedScopes == null || approvedScopes.isEmpty()) {
            log.debug("用户未选择任何权限");
            return "redirect:/oauth2/authorize?error=invalid_scope&state=" + state;
        }

        // 3. 保存用户授权决定
        try {
            RegisteredClient registeredClient = registeredClientRepository.findByClientId(clientId);
            if (registeredClient == null) {
                log.error("未找到客户端: {}", clientId);
                return "redirect:/oauth2/authorize?error=invalid_client&state=" + state;
            }

            // 4. 创建或更新授权同意记录
            OAuth2AuthorizationConsent.Builder consentBuilder = OAuth2AuthorizationConsent
                .withId(registeredClient.getId(), principal.getName());

            // 5. 添加之前已授权的权限
            OAuth2AuthorizationConsent existingConsent =
                authorizationConsentService.findById(registeredClient.getId(), principal.getName());
            if (existingConsent != null) {
                for (String existingScope : existingConsent.getScopes()) {
                    consentBuilder.scope(existingScope);
                }
            }

            // 6. 添加新授权的权限
            for (String scope : approvedScopes) {
                consentBuilder.scope(scope);
            }

            OAuth2AuthorizationConsent consent = consentBuilder.build();
            authorizationConsentService.save(consent);

            log.info("保存用户授权决定成功 - clientId: {}, user: {}, scopes: {}",
                    clientId, principal.getName(), approvedScopes);

            // 7. 重定向回授权端点继续OAuth2流程
            String scopeParam = String.join(" ", approvedScopes);
            return "redirect:/oauth2/authorize?" +
                   "client_id=" + clientId +
                   "&response_type=code" +
                   "&scope=" + scopeParam +
                   "&state=" + state;

        } catch (Exception e) {
            log.error("保存授权决定时发生错误", e);
            return "redirect:/oauth2/authorize?error=server_error&state=" + state;
        }
    }

    /**
     * 权限范围描述映射
     * 将技术性的scope转换为用户友好的描述
     */
    private Map<String, String> getScopeDescriptions() {
        Map<String, String> descriptions = new HashMap<>();
        descriptions.put("read", "读取您的基本信息");
        descriptions.put("write", "修改您的基本信息");
        descriptions.put("openid", "验证您的身份");
        descriptions.put("profile", "访问您的个人资料");
        descriptions.put("email", "访问您的邮箱地址");
        descriptions.put("phone", "访问您的手机号码");
        descriptions.put("address", "访问您的地址信息");
        descriptions.put("offline_access", "在您离线时访问数据");
        return descriptions;
    }
}