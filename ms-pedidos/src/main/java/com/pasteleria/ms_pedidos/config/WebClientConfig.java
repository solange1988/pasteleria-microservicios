package com.pasteleria.ms_pedidos.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class WebClientConfig {

    @Value("${ms.productos.url}")
    private String productosUrl;

    @Value("${ms.usuarios.url}")
    private String usuariosUrl;

    @Bean(name = "productosClient")
    public WebClient productosClient() {
        return WebClient.builder()
                .baseUrl(productosUrl)
                .build();
    }

    @Bean(name = "usuariosClient")
    public WebClient usuariosClient() {
        return WebClient.builder()
                .baseUrl(usuariosUrl)
                .build();
    }
}
