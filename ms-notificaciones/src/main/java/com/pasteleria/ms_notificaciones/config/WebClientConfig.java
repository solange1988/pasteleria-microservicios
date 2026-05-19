package com.pasteleria.ms_notificaciones.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${ms.usuarios.url}")
    private String usuariosUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(usuariosUrl)
                .build();
    }
}
