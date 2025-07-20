package com.finsight.app.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info =
        @Info(
            title = "FinSight API",
            description = "Personal Finance Management API",
            version = "1.0.0",
            contact = @Contact(name = "FinSight Team", email = "support@finsight.com")),
    servers = {
      @Server(url = "http://localhost:8080", description = "Development server"),
      @Server(url = "https://api.finsight.com", description = "Production server")
    })
public class OpenApiConfig {}
