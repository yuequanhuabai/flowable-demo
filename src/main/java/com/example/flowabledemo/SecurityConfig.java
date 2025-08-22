package com.example.flowabledemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        // Create a UserDetails object for each user.
        // The password must be encoded here.
        UserDetails user1 = User.builder()
            .username("user1")
            .password(passwordEncoder.encode("password"))
            .roles("USER")
            .build();

        UserDetails user2 = User.builder()
            .username("user2")
            .password(passwordEncoder.encode("password"))
            .roles("USER")
            .build();

        UserDetails manager = User.builder()
            .username("manager")
            .password(passwordEncoder.encode("password"))
            .roles("USER", "MANAGER")
            .build();

        // InMemoryUserDetailsManager is a simple implementation that stores users in memory.
        return new InMemoryUserDetailsManager(user1, user2, manager);
    }
}
