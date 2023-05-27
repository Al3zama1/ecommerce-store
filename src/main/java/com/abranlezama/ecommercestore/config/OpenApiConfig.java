package com.abranlezama.ecommercestore.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ecommerceAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(servers())
                .tags(tags())
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer").bearerFormat("JWT")));
    }

    @Bean
    public Info apiInfo() {
        return new Info()
                .title("Ecommerce API")
                .version("1.0")
                .description("Ecommerce application REST API documentation")
                .contact(contact());
    }

    @Bean
    public Contact contact() {
        return new Contact()
                .name("Abran Lezama")
                .email("lezama.abran@gmail.com")
                .url("https://abranlezama.com/");
    }

    @Bean
    public List<Server> servers() {
        return List.of(
                new Server().url("http://localhost:8080").description("Local development server.")
        );
    }

    @Bean
    public List<Tag> tags() {
        return List.of(
                new Tag().name("Public Products").description("Public product endpoints."),
                new Tag().name("Customer Authentication").description("Customer authentication endpoints."),
                new Tag().name("Customer Cart").description("Customer shopping cart endpoints.")
        );
    }
}
