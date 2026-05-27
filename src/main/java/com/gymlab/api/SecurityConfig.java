package com.gymlab.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .cors(cors -> cors.configure(http))

            .authorizeHttpRequests(auth -> auth

                // =====================================================
                // ENDPOINTS PÚBLICOS
                // =====================================================

                .requestMatchers(
                    "/api/lgpd/**"
                ).permitAll()

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/exercicios"
                ).permitAll()

                // =====================================================
                // ENDPOINTS PROTEGIDOS
                // =====================================================

                .anyRequest().authenticated()
            )

            // =====================================================
            // JWT SUPABASE
            // =====================================================

            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt -> {})
            );

        return http.build();
    }
}