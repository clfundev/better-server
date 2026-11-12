package io.github.clfundev.better.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI betterOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Better API")
                        .version("1.0"));
    }
}