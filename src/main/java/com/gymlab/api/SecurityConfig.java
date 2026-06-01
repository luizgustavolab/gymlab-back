package com.gymlab.api;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
    .requestMatchers("/").permitAll() // Adicione esta linha
    .requestMatchers("/api/lgpd/**").permitAll()
    .requestMatchers(HttpMethod.GET, "/api/exercicios").permitAll()
    .anyRequest().authenticated()
)
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .decoder(jwtDecoder())
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
            "http://localhost:4200", 
            "https://gymlab-front.vercel.app"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        try {
            String jwkSetJson = System.getenv("SUPABASE_PUBLIC_KEY");
            if (jwkSetJson == null || jwkSetJson.isEmpty()) {
                jwkSetJson = System.getProperty("SUPABASE_PUBLIC_KEY");
            }

            JWKSet jwkSet = JWKSet.parse(jwkSetJson);
            
            DefaultJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
            JWSVerificationKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(
                Collections.singleton(JWSAlgorithm.ES256),
                (jwkSelector, securityContext) -> jwkSelector.select(jwkSet)
            );
            jwtProcessor.setJWSKeySelector(keySelector);

            return new NimbusJwtDecoder(jwtProcessor);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao inicializar o JwtDecoder: " + e.getMessage());
        }
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String role = jwt.getClaimAsString("role");
            if (role == null) {
                role = jwt.getClaimAsString("aud");
            }
            if (role != null) {
                return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
            }
            return Collections.emptyList();
        });
        return converter;
    }
}