package gyt.auth_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

@Configuration
public class OpenApiConfig {

    @Value("${springdoc.swagger-ui.servers}")
    private String servers;

    @Bean
    public OpenAPI authServiceAPI() {
        List<Server> serverList = Arrays.stream(servers.split(","))
                .map(url -> new Server().url(url.trim()))
                .collect(Collectors.toList());

        return new OpenAPI()
                .servers(serverList)
                .info(new Info()
                        .title("Auth Service API")
                        .description("Authentication and Authorization API for GYT Movie Booking System")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("GYT Team")
                                .email("support@gyt.com")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token")));
    }
}
