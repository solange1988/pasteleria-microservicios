package com.pasteleria.ms_inventario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;



@EnableDiscoveryClient
@SpringBootApplication
public class MsInventarioApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsInventarioApplication.class, args);
	}

}
