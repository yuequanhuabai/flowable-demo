package com.example.flowabledemo.config;

import com.example.flowabledemo.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security配置
 * OAuth2演示项目的安全配置
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;


    /**
     * 安全过滤链配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 配置请求授权
            .authorizeHttpRequests(authz -> authz
                // 公开的API端点 - 客户端注册相关
                .requestMatchers("/oauth2/client/**").permitAll()
                
                // 测试端点放行（开发阶段）
                .requestMatchers("/test/**").permitAll()
                
                // 静态资源和公共端点放行
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/error").permitAll()
                
                // 其他所有请求需要认证
                .anyRequest().authenticated()
            )
            
            // 使用Spring Security默认表单登录
            .formLogin(form -> form
                .defaultSuccessUrl("/", true)
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