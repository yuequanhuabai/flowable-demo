package com.example.flowabledemo.config;

import com.example.flowabledemo.entity.OAuth2Client;
import com.example.flowabledemo.service.OAuth2ClientService;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.jdbc.core.JdbcTemplate;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * OAuth2 授权服务器配置
 * 负责配置OAuth2授权服务器的核心组件
 */
@Configuration
@RequiredArgsConstructor
public class OAuth2AuthorizationServerConfig {

    private final OAuth2ClientService oauth2ClientService;
    private final JdbcTemplate jdbcTemplate;

    /**
     * 注册客户端仓库
     * 集成现有的OAuth2ClientService，从数据库读取客户端信息
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        return new RegisteredClientRepository() {
            @Override
            public void save(RegisteredClient registeredClient) {
                // 暂不实现，使用现有的OAuth2ClientService管理客户端
                throw new UnsupportedOperationException("使用OAuth2ClientService管理客户端注册");
            }

            @Override
            public RegisteredClient findById(String id) {
                OAuth2Client client = oauth2ClientService.findByClientId(id);
                return client != null ? convertToRegisteredClient(client) : null;
            }

            @Override
            public RegisteredClient findByClientId(String clientId) {
                OAuth2Client client = oauth2ClientService.findByClientId(clientId);
                return client != null ? convertToRegisteredClient(client) : null;
            }
        };
    }

    /**
     * 将数据库中的OAuth2Client转换为Spring Security的RegisteredClient
     */
    private RegisteredClient convertToRegisteredClient(OAuth2Client client) {
        return RegisteredClient.withId(client.getClientId())
                .clientId(client.getClientId())
                .clientSecret(client.getClientSecret()) // 已经是BCrypt加密的
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri(client.getRedirectUri())
                .postLogoutRedirectUri("http://localhost:3000")
                .scopes(scopes -> {
                    // 解析客户端的权限范围
                    if (client.getScopes() != null && !client.getScopes().isEmpty()) {
                        String[] scopeArray = client.getScopes().split(",");
                        for (String scope : scopeArray) {
                            scopes.add(scope.trim());
                        }
                    }
                    // 添加OIDC标准scope
                    scopes.add(OidcScopes.OPENID);
                    scopes.add(OidcScopes.PROFILE);
                })
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(true) // 需要用户确认授权
                        .requireProofKey(false) // 暂不强制PKCE
                        .build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(1)) // 访问令牌1小时过期
                        .refreshTokenTimeToLive(Duration.ofDays(1)) // 刷新令牌1天过期
                        .reuseRefreshTokens(false) // 不重用刷新令牌
                        .build())
                .build();
    }

    /**
     * JWT 密钥源配置
     * 生成RSA密钥对用于JWT令牌签名和验证
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();

        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    /**
     * 生成RSA密钥对
     */
    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    /**
     * JWT解码器配置
     * 用于验证和解析JWT令牌
     */
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    /**
     * 授权服务器设置
     * 配置OAuth2授权服务器的各种端点和设置
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer("http://localhost:8080") // 令牌颁发者
                .authorizationEndpoint("/oauth2/authorize") // 授权端点
                .deviceAuthorizationEndpoint("/oauth2/device_authorization") // 设备授权端点
                .deviceVerificationEndpoint("/oauth2/device_verification") // 设备验证端点
                .tokenEndpoint("/oauth2/token") // 令牌端点
                .jwkSetEndpoint("/oauth2/jwks") // JWK Set端点
                .tokenRevocationEndpoint("/oauth2/revoke") // 令牌撤销端点
                .tokenIntrospectionEndpoint("/oauth2/introspect") // 令牌内省端点
                .oidcClientRegistrationEndpoint("/connect/register") // OIDC客户端注册端点
                .oidcUserInfoEndpoint("/oauth2/userinfo") // OIDC用户信息端点
                .oidcLogoutEndpoint("/connect/logout") // OIDC登出端点
                .build();
    }

    /**
     * OAuth2 授权服务
     * 管理OAuth2授权信息的存储和检索
     *
     * 使用数据库存储确保：
     * - 授权码、访问令牌等信息持久化
     * - 支持应用重启后的状态恢复和多实例部署
     * - 生产环境就绪
     */
    @Bean
    public OAuth2AuthorizationService authorizationService(RegisteredClientRepository clientRepository) {
        return new JdbcOAuth2AuthorizationService(jdbcTemplate, clientRepository);
    }

    /**
     * OAuth2 授权同意服务
     * 管理用户授权同意信息的存储和检索
     *
     * 使用数据库存储确保：
     * - 用户的授权同意记录永久保存
     * - 用户不需要重复授权，提升用户体验
     * - 支持多实例部署的数据一致性
     */
    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(RegisteredClientRepository clientRepository) {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, clientRepository);
    }
}