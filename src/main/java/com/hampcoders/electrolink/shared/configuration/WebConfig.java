package com.hampcoders.electrolink.shared.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebConfig is a configuration class that sets up Cross-Origin Resource Sharing (CORS)
 * for the application. It allows requests from the specified origin
 * (http://localhost:8081) and permits various HTTP methods and headers.
 * This configuration is essential for enabling communication between the frontend
 * (e.g., an Angular application) and the backend API without encountering CORS issues.
 */
@Configuration
public class WebConfig {

  /**
   * Defines a WebMvcConfigurer bean that configures CORS mappings for the application.
   *
   * @return a WebMvcConfigurer instance that defines CORS mappings for the application.
   */
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:8081/swagger-ui/index.html")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*");
      }
    };
  }
}