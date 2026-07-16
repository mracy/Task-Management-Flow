package com.taskmanagement.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI taskFlowOpenAPI() {
        final String securitySchemeName = "Bearer Token";

        return new OpenAPI()
                .info(new Info()
                        .title("TaskFlow - Task Management Platform API")
                        .description("""
                                ## TaskFlow API
                                A production-ready task management platform with full CRUD operations for
                                projects, tasks, comments, notifications, and dashboard analytics.

                                ### Authentication
                                1. Register or Login to get JWT tokens
                                2. Use the **access token** in the `Authorization` header as `Bearer <token>`
                                3. Tokens expire after 15 minutes — use `/auth/refresh` with your refresh token

                                ### Roles
                                | Role | Permissions |
                                |------|------------|
                                | ADMIN | Full access to all resources |
                                | MANAGER | Manage projects, tasks, and members |
                                | EMPLOYEE | View and work on assigned tasks |

                                ### Default Test Accounts
                                | Email | Password | Role |
                                |-------|----------|------|
                                | admin@taskmanagement.com | admin123456 | ADMIN |
                                | manager@taskmanagement.com | manager123456 | MANAGER |
                                | employee@taskmanagement.com | employee123456 | EMPLOYEE |
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("TaskFlow Team")
                                .email("support@taskflow.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development"),
                        new Server().url("https://api.taskflow.com").description("Production")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your JWT access token")));
    }
}
