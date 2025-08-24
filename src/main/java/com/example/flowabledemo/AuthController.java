package com.example.flowabledemo;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest, HttpSession session) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        System.out.println("Login attempt for user: " + username);
        System.out.println("Session ID: " + session.getId());

        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            if (passwordEncoder.matches(password, userDetails.getPassword())) {
                // Store user info in session
                session.setAttribute("username", username);
                session.setAttribute("roles", userDetails.getAuthorities());
                
                // 设置session超时时间为30分钟
                session.setMaxInactiveInterval(30 * 60);
                
                System.out.println("Login successful for user: " + username);
                System.out.println("Session ID after login: " + session.getId());
                System.out.println("Session attributes set: " + session.getAttribute("username"));
                System.out.println("Session max inactive interval: " + session.getMaxInactiveInterval());
                
                // 存储到session恢复映射中
                SimpleSessionRecoveryFilter.storeUserSession(session.getId(), username);
                
                Map<String, Object> response = new HashMap<>();
                response.put("username", username);
                response.put("roles", userDetails.getAuthorities());
                response.put("message", "Login successful");
                response.put("sessionId", session.getId()); // 帮助调试
                
                return ResponseEntity.ok(response);
            } else {
                System.out.println("Invalid password for user: " + username);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid credentials");
                return ResponseEntity.badRequest().body(errorResponse);
            }
        } catch (Exception e) {
            System.out.println("User not found: " + username);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        if (session != null) {
            // 清理session恢复映射
            SimpleSessionRecoveryFilter.removeUserSession(session.getId());
            session.invalidate();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/current-user")
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpSession session) {
        String username = (String) session.getAttribute("username");


        System.out.println("Getting current user - Session ID: " + (session != null ? session.getId() : "null"));
        System.out.println("Username in session: " + username);
        
        if (username != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("roles", session.getAttribute("roles"));
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, String>>> getUsers() {
        // Return available users for login
        List<Map<String, String>> users = List.of(
            Map.of("username", "user1", "role", "Employee"),
            Map.of("username", "user2", "role", "Employee"), 
            Map.of("username", "manager", "role", "Manager")
        );
        
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/debug-session")
    public ResponseEntity<Map<String, Object>> debugSession(HttpSession session, 
                                                             jakarta.servlet.http.HttpServletRequest request) {
        Map<String, Object> debug = new HashMap<>();
        debug.put("sessionId", session != null ? session.getId() : "null");
        debug.put("sessionValid", session != null && session.getAttribute("username") != null);
        debug.put("username", session != null ? session.getAttribute("username") : "null");
        debug.put("requestedSessionId", request.getRequestedSessionId());
        debug.put("sessionFromCookie", request.isRequestedSessionIdFromCookie());
        debug.put("sessionFromUrl", request.isRequestedSessionIdFromURL());
        debug.put("sessionIdValid", request.isRequestedSessionIdValid());
        debug.put("cookieHeader", request.getHeader("Cookie"));
        
        System.out.println("=== Session Debug Info ===");
        debug.forEach((key, value) -> System.out.println(key + ": " + value));
        System.out.println("=========================");
        
        return ResponseEntity.ok(debug);
    }
}