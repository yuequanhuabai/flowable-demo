package com.example.flowabledemo.controller;

import com.example.flowabledemo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 认证相关页面控制器
 * 处理登录、注册等认证界面
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * 自定义登录页面
     */
    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        log.debug("访问登录页面 - error: {}, logout: {}", error, logout);

        // 检查是否已经登录
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() &&
            !auth.getPrincipal().equals("anonymousUser")) {
            log.debug("用户已登录，重定向到首页");
            return "redirect:/";
        }

        // 添加错误和成功消息到模型
        if (error != null) {
            model.addAttribute("errorMessage", "用户名或密码错误，请重试");
            log.debug("添加登录错误消息");
        }

        if (logout != null) {
            model.addAttribute("successMessage", "您已成功登出");
            log.debug("添加登出成功消息");
        }

        // 返回自定义登录页面
        return "login";
    }


    /**
     * 用户注册页面
     */
    @GetMapping("/register")
    public String register(
            @RequestParam(value = "success", required = false) String success,
            Model model) {

        log.debug("访问注册页面");

        // 检查是否已经登录
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() &&
            !auth.getPrincipal().equals("anonymousUser")) {
            log.debug("用户已登录，重定向到首页");
            return "redirect:/";
        }

        if (success != null) {
            model.addAttribute("successMessage", "注册成功！请使用新账号登录。");
            log.debug("显示注册成功消息");
        }

        return "register";
    }

    /**
     * 处理用户注册
     */
    @PostMapping("/register")
    public String doRegister(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {

        log.debug("处理用户注册请求: {}", username);

        try {
            // 1. 验证输入数据
            String validationError = userService.validateRegistrationData(username, password, confirmPassword);
            if (validationError != null) {
                model.addAttribute("errorMessage", validationError);
                model.addAttribute("username", username); // 保留用户输入
                log.debug("注册数据验证失败: {}", validationError);
                return "register";
            }

            // 2. 注册新用户
            boolean success = userService.registerUser(username, password);
            if (success) {
                log.info("用户注册成功: {}", username);
                return "redirect:/register?success=true";
            } else {
                model.addAttribute("errorMessage", "注册失败，请稍后再试");
                model.addAttribute("username", username);
                log.error("用户注册失败: {}", username);
                return "register";
            }

        } catch (Exception e) {
            log.error("注册过程中发生异常", e);
            model.addAttribute("errorMessage", "系统错误，请稍后再试");
            model.addAttribute("username", username);
            return "register";
        }
    }

    /**
     * OAuth2 客户端管理页面
     */
    @GetMapping("/clients")
    public String clients(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth != null ? auth.getName() : "anonymous");
        return "clients";
    }

    /**
     * OAuth2 授权流程测试页面
     */
    @GetMapping("/oauth2-test")
    public String oauth2Test(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth != null ? auth.getName() : "anonymous");
        log.debug("显示OAuth2测试页面，当前用户: {}", auth != null ? auth.getName() : "anonymous");
        return "oauth2-test";
    }
}