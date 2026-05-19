package com.pasteleria.ms_reportes.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class WebClientConfig {

    @Value("${ms.pedidos.url}")
    private String pedidosUrl;

    @Value("${ms.pagos.url}")
    private String pagosUrl;

    @Value("${ms.productos.url}")
    private String productosUrl;

    @Bean(name = "pedidosClient")
    public WebClient pedidosClient() {
        return WebClient.builder()
                .baseUrl(pedidosUrl)
                .build();
    }

    @Bean(name = "pagosClient")
    public WebClient pagosClient() {
        return WebClient.builder()
                .baseUrl(pagosUrl)
                .build();
    }

    @Bean(name = "productosClient")
    public WebClient productosClient() {
        return WebClient.builder()
                .baseUrl(productosUrl)
                .build();
    }




}
