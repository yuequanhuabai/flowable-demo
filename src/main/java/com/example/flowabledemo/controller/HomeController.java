package com.example.flowabledemo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 首页控制器
 * 提供基本的欢迎页面和用户信息展示
 */
@Controller
public class HomeController {

    /**
     * 首页 - 显示当前登录用户信息
     */
    @GetMapping("/")
    @ResponseBody
    public String home() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return String.format("""
                <h1>OAuth2 演示项目</h1>
                <h2>欢迎, %s!</h2>
                <p>您已成功登录系统。</p>
                
                <h3>可用功能:</h3>
                <ul>
                    <li><a href="/oauth2/client/test_client_001">查看测试客户端信息</a></li>
                    <li><a href="/logout">退出登录</a></li>
                </ul>
                
                <h3>用户信息:</h3>
                <ul>
                    <li>用户名: %s</li>
                    <li>权限: %s</li>
                    <li>认证状态: %s</li>
                </ul>
                
                <hr>
                <p><small>这是一个OAuth2客户端注册和授权演示项目</small></p>
                """, 
                auth.getName(), 
                auth.getName(),
                auth.getAuthorities(),
                auth.isAuthenticated() ? "已认证" : "未认证"
            );
        } else {
            return "<h1>OAuth2 演示项目</h1><p>请先<a href='/login'>登录</a></p>";
        }
    }

    /**
     * 用户信息API端点
     */
    @GetMapping("/api/user")
    @ResponseBody
    public Object userInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return new UserInfo(auth.getName(), auth.getAuthorities().toString(), true);
        } else {
            return new UserInfo("anonymous", "[]", false);
        }
    }

    /**
     * 用户信息响应对象
     */
    public static class UserInfo {
        private String username;
        private String authorities;
        private boolean authenticated;

        public UserInfo(String username, String authorities, boolean authenticated) {
            this.username = username;
            this.authorities = authorities;
            this.authenticated = authenticated;
        }

        // Getters
        public String getUsername() { return username; }
        public String getAuthorities() { return authorities; }
        public boolean isAuthenticated() { return authenticated; }
    }
}