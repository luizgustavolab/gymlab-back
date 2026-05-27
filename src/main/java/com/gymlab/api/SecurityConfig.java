package com.gymlab.api;

import jakarta.servlet.http.HttpServletRequest;

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

            // =========================================
            // CSRF
            // =========================================

            .csrf(csrf -> csrf.disable())

            // =========================================
            // CORS
            // =========================================

            .cors(cors -> {})

            // =========================================
            // LOG TOKEN
            // =========================================

            .addFilterBefore(

                (request, response, chain) -> {

                    HttpServletRequest req =
                        (HttpServletRequest) request;

                    String auth =
                        req.getHeader(
                            "Authorization"
                        );

                    System.out.println(
                        "====================================="
                    );

                    System.out.println(
                        "AUTH HEADER:"
                    );

                    System.out.println(auth);

                    System.out.println(
                        "====================================="
                    );

                    chain.doFilter(
                        request,
                        response
                    );
                },

                org.springframework.security.web
                    .authentication
                    .UsernamePasswordAuthenticationFilter.class
            )

            // =========================================
            // ROTAS
            // =========================================

            .authorizeHttpRequests(auth -> auth

                // públicas

                .requestMatchers(
                    "/api/lgpd/**"
                ).permitAll()

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/exercicios"
                ).permitAll()

                // protegidas

                .anyRequest()
                .authenticated()
            )

            // =========================================
            // JWT SUPABASE
            // =========================================

            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt -> {})
            );

        return http.build();
    }
}