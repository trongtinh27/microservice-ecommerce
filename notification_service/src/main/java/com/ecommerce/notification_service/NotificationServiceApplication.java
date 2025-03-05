package com.ecommerce.notification_service;

import com.ecommerce.security.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({JwtService.class, PreFilter.class, SecurityConfig.class, AuthenticationRequestInterceptor.class})
@EnableFeignClients
public class NotificationServiceApplication {

	public static void main(String[] args) {
		// Load .env file and set variables into system properties
		Dotenv dotenv = Dotenv.configure().load();
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

		// Start Spring Boot application

		SpringApplication.run(NotificationServiceApplication.class, args);
	}

}
