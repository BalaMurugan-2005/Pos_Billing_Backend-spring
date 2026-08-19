package com.pos.system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:5173,http://localhost:5174}")
    private String allowedOriginsRaw;

    @Value("${cors.allowed-methods:GET,POST,PUT,PATCH,DELETE,OPTIONS}")
    private String[] allowedMethods;

    @Value("${cors.allow-credentials:true}")
    private boolean allowCredentials;

    private List<String> getResolvedOriginPatterns() {
        List<String> patterns = new ArrayList<>();
        
        // Common deployment domains
        patterns.add("https://*.onrender.com");
        patterns.add("https://*.vercel.app");
        patterns.add("https://*.netlify.app");
        patterns.add("http://localhost:*");
        patterns.add("http://127.0.0.1:*");

        // Parse custom origins from properties/env vars
        if (allowedOriginsRaw != null && !allowedOriginsRaw.isBlank()) {
            for (String origin : allowedOriginsRaw.split(",")) {
                String trimmed = origin.trim();
                if (!trimmed.isEmpty()) {
                    patterns.add(trimmed);
                }
            }
        }

        // Fallback catch-all pattern for flexibility with credentials
        patterns.add("https://*");
        
        return patterns;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        List<String> patterns = getResolvedOriginPatterns();
        for (String pattern : patterns) {
            config.addAllowedOriginPattern(pattern);
        }

        for (String method : allowedMethods) {
            config.addAllowedMethod(method.trim());
        }

        config.addAllowedHeader("*");
        config.addExposedHeader("Authorization");
        config.addExposedHeader("Content-Disposition");
        config.setAllowCredentials(allowCredentials);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        List<String> patterns = getResolvedOriginPatterns();
        registry.addMapping("/**")
                .allowedOriginPatterns(patterns.toArray(new String[0]))
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "Content-Disposition")
                .allowCredentials(true)
                .maxAge(3600);
    }
}