package ru.enikeev.stolplusapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {

    /**
     * Конфигурация OpenAPI документации
     * После запуска приложения документация будет доступна по адресу:
     * http://localhost:8091/docs
     */
    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("Stol Plus App")
                        .version("1.0.1")
                        .description("API for Stol Plus App"));
    }

}
