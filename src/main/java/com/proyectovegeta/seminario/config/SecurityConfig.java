package com.proyectovegeta.seminario.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

@Configuration
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                            CorsConfiguration configuration = new CorsConfiguration();
                            configuration.applyPermitDefaultValues();
                            configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
                            return configuration;
                        }
                ))
                .authorizeHttpRequests((authorizeRequests) -> authorizeRequests
                        .requestMatchers("/**").permitAll()
                );
        return http.build();

    }
}