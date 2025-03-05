package com.ecommerce.shop_service;

import com.ecommerce.security.JwtService;
import com.ecommerce.security.PreFilter;
import com.ecommerce.security.SecurityConfig;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({SecurityConfig.class, PreFilter.class, JwtService.class})
@EnableFeignClients
public class ShopServiceApplication {
	public static void main(String[] args) {
		// Load .env file and set variables into system properties
		Dotenv dotenv = Dotenv.configure().load();
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

		// Start Spring Boot application

		SpringApplication.run(ShopServiceApplication.class, args);
	}

}
