package com.example.flowabledemo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简化的Session恢复过滤器 - 避免循环依赖
 */
@Component
public class SimpleSessionRecoveryFilter extends OncePerRequestFilter {
    
    // 简单的内存session存储
    private static final ConcurrentHashMap<String, String> sessionUserMap = new ConcurrentHashMap<>();
    
    public static void storeUserSession(String sessionId, String username) {
        sessionUserMap.put(sessionId, username);
        System.out.println("SimpleSessionRecoveryFilter - Stored session: " + sessionId + " for user: " + username);
    }
    
    public static void removeUserSession(String sessionId) {
        sessionUserMap.remove(sessionId);
        System.out.println("SimpleSessionRecoveryFilter - Removed session: " + sessionId);
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        // 只处理需要认证的API请求
        if (path.startsWith("/api/") && 
            !path.equals("/api/login") && 
            !path.equals("/api/users") && 
            !path.equals("/api/current-user") && 
            !path.equals("/api/debug-session")) {
            
            HttpSession session = request.getSession(false);
            String requestedSessionId = request.getRequestedSessionId();
            
            // 如果session为null但有sessionId，尝试恢复
            if (session == null && requestedSessionId != null && sessionUserMap.containsKey(requestedSessionId)) {
                System.out.println("SimpleSessionRecoveryFilter - Attempting to recover session: " + requestedSessionId);
                
                String username = sessionUserMap.get(requestedSessionId);
                System.out.println("SimpleSessionRecoveryFilter - Found stored user: " + username);
                
                // 创建新session并恢复用户信息
                session = request.getSession(true);
                session.setAttribute("username", username);
                
                // 简单设置角色 - 根据用户名判断
                if ("manager".equals(username)) {
                    session.setAttribute("roles", java.util.List.of(
                        new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"),
                        new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_MANAGER")
                    ));
                } else {
                    session.setAttribute("roles", java.util.List.of(
                        new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER")
                    ));
                }
                
                session.setMaxInactiveInterval(30 * 60); // 30分钟
                
                // 更新存储的sessionId
                sessionUserMap.remove(requestedSessionId);
                sessionUserMap.put(session.getId(), username);
                
                System.out.println("SimpleSessionRecoveryFilter - Session recovered successfully");
                System.out.println("SimpleSessionRecoveryFilter - New session ID: " + session.getId());
            }
        }
        
        filterChain.doFilter(request, response);
    }
}