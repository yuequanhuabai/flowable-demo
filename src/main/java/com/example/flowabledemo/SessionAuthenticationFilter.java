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
        if (path.equals("/api/login") || path.equals("/api/users")) {
            filterChain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        
        System.out.println("SessionAuthenticationFilter - Path: " + path);
        System.out.println("SessionAuthenticationFilter - Session: " + (session != null ? session.getId() : "null"));
        
        if (session != null) {
            System.out.println("SessionAuthenticationFilter - Username in session: " + session.getAttribute("username"));
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
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Authentication required\"}");
        }
    }
}