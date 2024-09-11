package com.example.owetracker.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Disable CSRF for testing purposes (not recommended for production)
                .authorizeRequests()
                //.antMatchers("/api/users/register", "/api/users/login").permitAll() // Allow public access to register and login
                //.anyRequest().authenticated() // Require authentication for other endpoints
                .anyRequest().permitAll();

        return http.build();
    }
}
