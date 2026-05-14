package com.hampcoders.electrolink.shared.infrastructure.documentation.openapi.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI configuration class for the Learning Platform application.
 * This class sets up the OpenAPI documentation with general information about the API
 * including title, description, and version.
 */
@Configuration
public class OpenApiConfiguration {

  /**
   * Configures the OpenAPI documentation for the Learning Platform application.
   *
   * @return an OpenAPI object with the configured API documentation settings.
   */
  @Bean
  public OpenAPI learningPlatformOpenApi() {
    // General configuration
    var openApi = new OpenAPI();
    openApi
        .info(new Info()
            .title("Learning Platform API")
            .description("Learning Platform application REST API documentation.")
            .version("v1.0.0")
            .license(new License().name("Apache 2.0")
                .url("https://springdoc.org")))
        .externalDocs(new ExternalDocumentation()
            .description("Learning Platform Documentation")
            .url("https://github.com/upc-is-si729/daos-language-reference"));

    // Add security scheme
    final String securitySchemeName = "bearerAuth";

    openApi.addSecurityItem(new SecurityRequirement()
            .addList(securitySchemeName))
        .components(new Components()
            .addSecuritySchemes(securitySchemeName,
                new SecurityScheme()
                    .name(securitySchemeName)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")));

    // Return OpenAPI configuration object with all the settings
    return openApi;
  }
}