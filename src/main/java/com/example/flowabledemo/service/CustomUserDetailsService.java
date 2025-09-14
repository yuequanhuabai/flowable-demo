package com.example.flowabledemo.service;

import com.example.flowabledemo.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

/**
 * Spring Security用户详情服务
 * 从数据库加载用户信息用于认证
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user details for username: {}", username);
        
        // 从数据库查找用户
        User user = userService.findEnabledUserByUsername(username);
        if (user == null) {
            log.debug("User not found or disabled: {}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }

        log.debug("Successfully loaded user: {}", username);
        
        // 返回Spring Security的UserDetails对象
        return new CustomUserDetails(user);
    }

    /**
     * 自定义UserDetails实现
     * 包装我们的User实体以符合Spring Security要求
     */
    public static class CustomUserDetails implements UserDetails {
        
        private final User user;

        public CustomUserDetails(User user) {
            this.user = user;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            // 简化权限：所有用户都有USER权限
            // 在OAuth2演示中，权限控制主要通过scopes实现
            return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        }

        @Override
        public String getPassword() {
            return user.getPassword();
        }

        @Override
        public String getUsername() {
            return user.getUsername();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true; // 简化实现：账户不过期
        }

        @Override
        public boolean isAccountNonLocked() {
            return true; // 简化实现：账户不锁定
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true; // 简化实现：密码不过期
        }

        @Override
        public boolean isEnabled() {
            return user.getEnabled();
        }

        // 获取原始用户对象的方法
        public User getUser() {
            return user;
        }

        public Long getUserId() {
            return user.getId();
        }
    }
}