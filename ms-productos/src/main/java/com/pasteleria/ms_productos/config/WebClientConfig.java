package com.pasteleria.ms_productos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class WebClientConfig {

    @Value("${ms.categorias.url}")
    private String categoriasUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(categoriasUrl)
                .build();
    }


}

