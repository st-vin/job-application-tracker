package org.alvin.jobapplicationtracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF protection for development
                .csrf(csrf -> csrf.disable())
                // Permit all requests without authentication
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                // Disable frame options so the H2 console can render
                .headers(headers -> headers.frameOptions().disable());

        return http.build();
    }
}
