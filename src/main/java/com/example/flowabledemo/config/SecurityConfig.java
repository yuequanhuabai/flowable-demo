package com.example.flowabledemo.config;

import com.example.flowabledemo.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

/**
 * Spring Security配置
 * OAuth2演示项目的安全配置，支持OAuth2授权服务器
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    /**
     * OAuth2 授权服务器安全过滤链配置
     * 处理OAuth2相关的端点请求
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        // 应用OAuth2授权服务器的默认安全配置
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http
            // 配置OAuth2授权服务器
            .getConfigurer(OAuth2AuthorizationServerConfigurer.class)
            .authorizationEndpoint(authorizationEndpoint ->
                authorizationEndpoint.consentPage("/oauth2/consent") // 自定义授权确认页面
            )
            .oidc(Customizer.withDefaults()); // 启用OIDC 1.0支持

        http
            // 异常处理 - 未认证时重定向到登录页面
            .exceptionHandling(exceptions ->
                exceptions.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
            )
            // 配置JWT资源服务器（用于令牌验证）
            .oauth2ResourceServer(resourceServer ->
                resourceServer.jwt(Customizer.withDefaults())
            );

        return http.build();
    }

    /**
     * 默认安全过滤链配置
     * 处理表单登录和其他常规请求
     */
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            // 配置请求授权
            .authorizeHttpRequests(authz -> authz
                // OAuth2相关端点会被上面的过滤链处理，这里不需要重复配置

                // 公开的API端点 - 客户端注册相关
                .requestMatchers("/oauth2/client/**").permitAll()

                // 测试端点放行（开发阶段）
                .requestMatchers("/test/**").permitAll()
                .requestMatchers("/oauth2-test").permitAll()

                // 静态资源和公共端点放行
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/error").permitAll()

                // 注册相关端点放行
                .requestMatchers("/register").permitAll()

                // 其他所有请求需要认证
                .anyRequest().authenticated()
            )

            // 使用自定义表单登录页面
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )

            // 配置登出
            .logout(logout -> logout.permitAll())

            // 配置用户详情服务
            .userDetailsService(userDetailsService)

            // 暂时禁用CSRF（开发阶段）
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}