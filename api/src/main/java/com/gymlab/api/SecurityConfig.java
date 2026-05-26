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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desabilita CSRF já que usamos tokens stateless
            .cors(cors -> cors.configure(http)) // Acopla as regras de CORS que definiremos
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/api/exercicios").permitAll() // Catálogo é público
                .anyRequest().authenticated() // Qualquer outro endpoint (como salvar treinos) exige login
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {})); // Ativa a validação automática do JWT do Supabase

        return http.build();
    }
}