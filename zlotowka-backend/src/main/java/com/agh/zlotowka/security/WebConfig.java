package com.agh.zlotowka.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// TODO: probably that will need to be changed after adding spring security (filter chain)
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")  // Wszystkie originy
                .allowedMethods("*")  // Wszystkie metody HTTP
                .allowedHeaders("*")  // Wszystkie nagłówki
                .allowCredentials(false)  // Musi być false gdy allowedOrigins("*")
                .maxAge(3600);
    }
}