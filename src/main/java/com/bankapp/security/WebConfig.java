package com.bankapp.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    public WebMvcConfigurer corsConfigurer(){
        String frontEndOrigin = "http://localhost:4200";
        return new WebMvcConfigurer(){
          @Override
          public void addCorsMappings(CorsRegistry corsRegistry){
              corsRegistry.addMapping("/api/v1/**")
                      .allowedOrigins(frontEndOrigin)
                      .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                      .allowedHeaders("*")
                      .allowCredentials(true);
          }
        };
    }
}
