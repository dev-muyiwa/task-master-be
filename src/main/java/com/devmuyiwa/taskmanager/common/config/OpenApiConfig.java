package com.devmuyiwa.taskmanager.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Map;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI taskManagerOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Task Manager API")
                        .description("""
                            API documentation for the Task Manager application.
                       
                            ## Authentication
                            Most endpoints require JWT authentication. After logging in or registering,
                            include the JWT token in the Authorization header for all subsequent requests.
                            
                            ## Workspace Context
                            Most endpoints require a workspace context. Include the `X-Workspace-ID` header with your workspace UUID.
                            """)
                        .version("1.0")
                        .contact(new Contact()
                                .name("Moyosoreoluwa Odusola")
                                .email("devmuyiwa@gmail.com")
                                .url("https://github.com/dev-muyiwa"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                    new Server()
                        .url("http://localhost:8080")
                        .description("Local development server")
                ))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token obtained from /auth/login or /auth/register")))
                // Add bearer auth as a global security requirement
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
    }

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            // Add security requirements to all operations
            openApi.getPaths().forEach((path, pathItem) ->
                pathItem.readOperations().forEach(operation -> {
                    // Add bearer auth to all operations except /auth/**
                    if (!path.startsWith("/auth/")) {
                        operation.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
                    }
                })
            );
        };
    }
}