package com.example.flowabledemo;

import org.flowable.common.engine.impl.interceptor.CommandInterceptor;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Command监控配置 - 监控taskService.complete()内部执行过程
 */
@Configuration
public class CommandMonitorConfig {
    
    @Bean
    public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> commandInterceptorConfigurer() {
        return processEngineConfiguration -> {
            System.out.println("=== 配置Command执行监控器 ===");
            
            // 获取现有的前置拦截器列表
            List<CommandInterceptor> preInterceptors = processEngineConfiguration.getCustomPreCommandInterceptors();
            if (preInterceptors == null) {
                preInterceptors = new ArrayList<>();
            }
            
            // 添加我们的Command调试拦截器到最前面
            preInterceptors.add(0, new CommandDebugInterceptor());
            processEngineConfiguration.setCustomPreCommandInterceptors(preInterceptors);
            
            System.out.println("✅ Command监控器已注册到拦截器链的最前端");
            System.out.println("🎯 将监控所有Task相关的Command执行过程");
            System.out.println("=== Command监控器配置完成 ===");
        };
    }
}