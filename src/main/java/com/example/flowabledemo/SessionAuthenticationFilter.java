package com.example.flowabledemo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SessionAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        // Skip filter for login and user endpoints
        String path = request.getRequestURI();
        if (path.equals("/api/login") || path.equals("/api/users") || path.equals("/api/current-user")) {
            filterChain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        
        // 如果session为null，但有sessionId，尝试重新获取或创建
        if (session == null && request.getRequestedSessionId() != null) {
            System.out.println("SessionAuthenticationFilter - Session is null but sessionId exists, trying to get session...");
            session = request.getSession(false);  // 再次尝试
            
            if (session == null) {
                System.out.println("SessionAuthenticationFilter - Session still null, sessionId may be expired or invalid");
            }
        }
        
        System.out.println("SessionAuthenticationFilter - Path: " + path);
        System.out.println("SessionAuthenticationFilter - Session: " + (session != null ? session.getId() : "null"));
        System.out.println("SessionAuthenticationFilter - Request Cookie: " + request.getHeader("Cookie"));
        System.out.println("SessionAuthenticationFilter - User-Agent: " + request.getHeader("User-Agent"));
        
        if (session != null) {
            System.out.println("SessionAuthenticationFilter - Username in session: " + session.getAttribute("username"));
            System.out.println("SessionAuthenticationFilter - Session max inactive interval: " + session.getMaxInactiveInterval());
            System.out.println("SessionAuthenticationFilter - Session last accessed time: " + session.getLastAccessedTime());
        }
        
        if (session != null && session.getAttribute("username") != null) {
            String username = (String) session.getAttribute("username");
            @SuppressWarnings("unchecked")
            Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) session.getAttribute("roles");
            
            if (authorities == null) {
                authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
            }
            
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(username, null, authorities);
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            System.out.println("SessionAuthenticationFilter - Authentication set for: " + username);
            filterChain.doFilter(request, response);
        } else {
            // No valid session, return 401
            System.out.println("SessionAuthenticationFilter - No valid session, returning 401");
            System.out.println("SessionAuthenticationFilter - Available sessions: " + 
                (request.getRequestedSessionId() != null ? request.getRequestedSessionId() : "none"));
            
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.getWriter().write("{\"error\":\"Authentication required\",\"sessionExpired\":true}");
        }
    }
}