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
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() &&
            !auth.getPrincipal().equals("anonymousUser")) {
            model.addAttribute("username", auth.getName());
        }

        return "home";
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