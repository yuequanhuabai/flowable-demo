package com.example.flowabledemo.service;

import com.example.flowabledemo.entity.Role;
import com.example.flowabledemo.entity.User;
import com.example.flowabledemo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定義用戶認證服務
 * 使用 MyBatis Plus 實現
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 從數據庫查找用戶
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用戶不存在: " + username);
        }

        // 查找用戶的角色
        List<Role> roles = userMapper.findRolesByUserId(user.getId());
        
        // 構建 Spring Security 用戶對象
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                        .collect(Collectors.toList()))
                .build();
    }
}