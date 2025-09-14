package com.example.flowabledemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码编码器配置
 * 独立配置避免循环依赖
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * 密码编码器Bean
     * 使用BCrypt算法，强度为10
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}