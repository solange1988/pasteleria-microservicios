package com.pasteleria.ms_productos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;




@EnableDiscoveryClient
@SpringBootApplication
public class MsProductosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsProductosApplication.class, args);
	}

}
