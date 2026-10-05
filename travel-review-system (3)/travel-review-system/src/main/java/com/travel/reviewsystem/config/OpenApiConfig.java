package com.travel.reviewsystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the title/description shown at the top of the Swagger UI
 * dashboard (http://localhost:8080/swagger-ui.html) instead of the
 * generic springdoc defaults.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI reviewSystemOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Travel Platform — Review & Rating API")
                        .description("Hotel & flight reviews: ratings, photos, replies, "
                                + "flagging, and moderator review of flagged content.")
                        .version("v1.0.0")
                        .contact(new Contact().name("Internship Task")));
    }
}
