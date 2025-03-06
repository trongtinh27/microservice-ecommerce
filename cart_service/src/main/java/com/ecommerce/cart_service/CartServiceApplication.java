package com.ecommerce.cart_service;

import com.ecommerce.security.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableRedisRepositories
@EnableFeignClients
@Import({JwtService.class, PreFilter.class, SecurityConfig.class, AuthenticationRequestInterceptor.class})
public class CartServiceApplication {

	public static void main(String[] args) {
		// Load .env file and set variables into system properties
		Dotenv dotenv = Dotenv.configure().load();
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

		// Start Spring Boot application
		SpringApplication.run(CartServiceApplication.class, args);
	}

}
